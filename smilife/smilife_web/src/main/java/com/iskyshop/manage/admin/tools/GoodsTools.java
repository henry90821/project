package com.iskyshop.manage.admin.tools;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.qrcode.QRCodeUtil;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.AddressUtil;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsInventory;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.ProductMapping;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IGoodsInventoryLogService;
import com.iskyshop.foundation.service.IGoodsInventoryService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.ftp.tools.FTPServerTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

@Component
public class GoodsTools {
	private static Logger logger = Logger.getLogger(GoodsTools.class);
	public static ReadWriteLock hxInventoryRWLock = new ReentrantReadWriteLock();//海信商品库存读写锁，用于同步更新海信商品的库存的定时任务与查询海信商品库存
	
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private FTPServerTools FTPTools;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private IGoodsInventoryService goodsInventoryService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IGoodsInventoryLogService goodsInventoryLogService;
	
	/**
	 * 生成商品二维码(在新建商品时会调用此方法来生成商品二维码，在已创建的商品上生成二维码时则没有调用此方法)
	 * 
	 * @param request
	 * @param obj
	 * @param uploadFilePath
	 */
	public void createGoodsQR(HttpServletRequest request, Goods obj, String uploadFilePath) {
		try {
			String destPath = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache";
			if (!CommUtil.fileExist(destPath)) {
				CommUtil.createFolder(destPath);
			}
			destPath = destPath + File.separator + obj.getId() + "_qr.jpg";

			String logoPath = "";

			if (obj.getGoods_main_photo() != null) {
				if (1 == obj.getGoods_type()) {
					this.FTPTools.userDownloadImg(obj.getGoods_main_photo(),
							String.valueOf(obj.getGoods_store().getUser().getId()));
				} else {
					this.FTPTools.systemDownloadImg(obj.getGoods_main_photo());
				}
				logoPath = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache"
						+ File.separator + obj.getGoods_main_photo().getName();

			} else {// 取默认的商品图片(默认的商品图片可能是项目中的资源文件，也可能是用户上传上去的新图片)
				Accessory defaulImg = this.configService.getSysConfig().getGoodsImage();
				String imgPath = defaulImg.getPath();
				if (imgPath.startsWith("resources/style/common/images"))
					logoPath = CommUtil.getServerRealPathFromSystemProp() + imgPath + File.separator
							+ this.configService.getSysConfig().getGoodsImage().getName();
				else {
					logoPath = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache"
							+ File.separator + defaulImg.getName();
					File defaultGoodsImg = new File(logoPath);
					if (!defaultGoodsImg.exists()) {
						this.FTPTools.systemDownloadImg(defaulImg);
					}
				}
			}

			QRCodeUtil.encode(configService.getSysConfig().getGoodsH5Url() + "?goodsId=" + obj.getId(), logoPath, destPath, true);

			// 将二维码图片上传ftp
			String url = this.FTPTools.systemUpload(obj.getId() + "_qr.jpg", "/goods_qr");
			obj.setQr_img_path(url + "/" + obj.getId() + "_qr.jpg");
			this.goodsService.update(obj);

			this.FTPTools.DeleteWebImg(obj.getGoods_main_photo());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 返回商品当前的单价和库存等信息。若此商品参加了团购或秒杀活动，则返回此商品的团购价或秒杀价及对应库存；若此商品为全局库存商品，则返回全局库存的单价和库存；若此商品为规格库存商品，则返回对应gspIds参数的单价和库存（
	 * 若gspIds为空或为错误的规格属性id列表，则使用默认的规格属性列表）；若此商品参加了商城活动且当前用户已登录，则会返回此商品折扣后的价格（是前面的价格基础上再折扣的价格））
	 * 
	 * @param goods
	 *            待查询的商品对象，不能为null
	 * @param gspIds
	 *            商品规格属性的id列表，以逗号分隔。若商品为全局库存商品或商品参加了团购和秒杀活动，则gspIds不起作用；若商品为规格库存商品，此参数若为空或错误的规格属性id列表，则使用默认的规格属性的id列表
	 * @param userId
	 *            用户id，若为空串或null，则表示使用当前登录用户的id。此参数对返回参加了商城活动的商品的价格有影响
	 * @return key=price：商品折扣前的单价(Double类型)；
	 *         key=count：库存(若返回的库存和价格为规格库存和价格，则存在key=spec；若返回的为全局的库存和价格，则存在key=all；若使用的是默认的规格属性id列表
	 *         ，则存在key=useDefaultGsps，其对应的value为默认的规格属性的id数组(类型：String[]))；若商品为团购商品
	 *         ，则存在key=group，此key对应的value为团购活动的id；若商品为秒杀商品，则存在key=seckill，此key对应的value为秒杀活动的id
	 *         ；若此商品参加了商城活动且userId或当前用户已登录，则还存在以下key： rate（当前用户的折扣率）、level_name（当前用户的等级）、act_price(商品折扣后的价格，Double类型)。
	 */
	public Map getGoodsPriceAndInventory(Goods goods, String gspIds, String userId) {
		double price = 0;
		int count = 0;
		Map map = new HashMap();
		if (goods.getGroup() != null && goods.getGroup_buy() == 2) {// 团购商品统一按照团购价格处理
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					count = gg.getGg_count();
					price = CommUtil.null2Double(gg.getGg_price());
					map.put("group", gg.getGroup().getId());
					break;
				}
			}
		} else if (goods.getSeckill_buy() == 2) {// 秒杀商品
			Map params2 = new HashMap();
			params2.put("goods_id", goods.getId());

			String jpql = "select obj from SeckillGoods obj where obj.gg_goods.id=:goods_id and obj.gg_status=2";

			SeckillGoods seckillGoods = seckillGoodsService.query(jpql, params2, 0, 1).get(0);
			count = seckillGoods.getGg_count();
			price = CommUtil.null2Double(seckillGoods.getGg_price());
			map.put("seckill", seckillGoods.getId());
		} else {
			if ("spec".equals(goods.getInventory_type())) {
				boolean found = false;
				List<HashMap> list = Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
				Map defaultGsps = list.get(0);//不要判断list是否为null或size是否大于0。因为若出现此情况，则说明业务上有问题
				
				if (!StringUtils.isNullOrEmpty(gspIds)) {					
					String[] gsp_ids = gspIds.split(",");
					Arrays.sort(gsp_ids);
					for (Map temp : list) {
						String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
						Arrays.sort(temp_ids);
						if (Arrays.equals(gsp_ids, temp_ids)) {
							count = CommUtil.null2Int(temp.get("count"));
							price = CommUtil.null2Double(temp.get("price"));
							found = true;							
							break;
						}
					}
				}
				if (!found) {
					count = CommUtil.null2Int(defaultGsps.get("count"));
					price = CommUtil.null2Double(defaultGsps.get("price"));
					map.put("useDefaultGsps", CommUtil.null2String(defaultGsps.get("id")).split("_"));
				}
				map.put("spec", "spec");
			} else {// 全局库存
				count = goods.getGoods_inventory();
				price = CommUtil.null2Double(goods.getStore_price());
				map.put("all", "all");
			}
		}
		if (goods.getActivity_status() == 2) {// 如果是促销商品，根据规格配置价格计算相应配置的促销价格
			if (StringUtils.isNullOrEmpty(userId) && SecurityUserHolder.getCurrentUser() != null) {
				userId = CommUtil.null2String(SecurityUserHolder.getCurrentUser().getId());
			}
			if (!StringUtils.isNullOrEmpty(userId)) {
				ActivityGoods actGoods = this.actgoodsService.getObjById(goods.getActivity_goods_id());
				// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
				BigDecimal rebate = BigDecimal.valueOf(0.00);
				String level_name = "铜牌会员";
				int level = this.integralViewTools.query_user_level(userId);
				if (level == 0) {
					rebate = actGoods.getAct().getAc_rebate();
				} else if (level == 1) {
					level_name = "银牌会员";
					rebate = actGoods.getAct().getAc_rebate1();
				} else if (level == 2) {
					level_name = "金牌会员";
					rebate = actGoods.getAct().getAc_rebate2();
				} else if (level == 3) {
					level_name = "超级会员";
					rebate = actGoods.getAct().getAc_rebate3();
				}
				map.put("rate", rebate);
				map.put("level_name", level_name);
				map.put("act_price", Double.parseDouble(CommUtil.formatMoney(CommUtil.mul(rebate, price))));
			}
		}
		map.put("price", Double.parseDouble(CommUtil.formatMoney(price)));
		map.put("count", count);
		return map;
	}

	/**
	 * 返回商品指定规格属性值集对应的价格及库存信息(返回商品的规格库存字段goods_inventory_detail中的指定Map)
	 * 
	 * @param goods
	 * @param gspIds
	 * @return 返回null则表示goods为null或对应商品不是规格库存商品或未找到与gspIds匹配的规格属性集
	 */
	public Map getGSPsMap(Goods goods, String gspIds) {
		if (goods != null) {
			if ("spec".equals(goods.getInventory_type()) && !StringUtils.isNullOrEmpty(gspIds)) {
				List<HashMap> list = Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
				String[] gsp_ids = gspIds.split(",");
				Arrays.sort(gsp_ids);
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
					Arrays.sort(temp_ids);
					if (Arrays.equals(gsp_ids, temp_ids)) {
						return temp;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 为指定的商品选择发货的星美门店。（注：当前海信商品都为全局库存商品）。选址原则：根据门店与目的地的距离来选择门店
	 * 
	 * @param goodsCarts
	 *            星美商品对应的购物车列表，请确保传入的商品都为海信商品（本函数中不再检查）
	 * @param dstAddr
	 *            发货的目的地址，不得为null
	 * @return 
	 *         返回一个Map对象:key=flag（int型，0:表示成功；1表示失败）；若flag=1，则存在key=result：对应失败的描述(String)，key=errCode：错误编码（int型，1:计算收货地址的经纬度失败
	 *         ;2:未找到购买商品的库存信息;3:部分商品库存不足;4:参数goodsCarts为空）；若flag=0，则存在key=result：对应一个List<Map>对象，其中每一个元素对应一个门店相关信息(key=
	 *         address
	 *         :门店地址对象（ShipAddress）；key=distance：门店距离目的地址的直线距离(double型，单位：米)；key=goodscart_list：要从此门店发出的购物车id列表(类型：List
	 *         <Long>)；key=goods_count_list：对应goodscart_list要发出的各商品的数量（List<Integer>）)
	 */
	public Map selectCinemaStore(List<GoodsCart> goodsCarts, Address dstAddr) {
		Map result = new HashMap();

		if (goodsCarts.size() == 0) {
			String errMsg = "购物车(参数：goodsCarts)不能为空。";
			result.put("flag", 1);
			result.put("errCode", 4);
			result.put("result", errMsg);
			return result;
		}

		// 计算发货的目的地址的经纬度
		Double longitude = dstAddr.getLongitude();		
		if (longitude == null || longitude.isInfinite() || longitude.isNaN()) {
			logger.info("开始获取地址(id=" + dstAddr.getId()+ ")的经纬度");
			AddressUtil.getLngAndLat(dstAddr);
			if (dstAddr.getLongitude() == null) {
				String errMsg = "向百度地图获取地址（地址id=" + dstAddr.getId() + "）的经纬度失败。";
				result.put("flag", 1);
				result.put("errCode", 1);
				result.put("result", errMsg);
				return result;
			} else {
				addressService.update(dstAddr);// 保存地址的经纬度
				logger.info("获取地址(id=" + dstAddr.getId()+ ")的经纬度成功！");
			}
		}

		Map<String, Goods> goodsCodeMaping = new HashMap<String, Goods>();// key:海信商品编码， value：对应的Goods对象
		Map<String, Map<GoodsCart, Integer>> toBuy = new HashMap<String, Map<GoodsCart, Integer>>();// key:海信商品编码,
																									// value:待买的商品。为Map:key=购物车对象,value=对应购物车中待购商品的数量（会变化，初始化为对应购物车中的数量）
		Map<String, Integer> goodsInventory = new HashMap<String, Integer>();// key=海信商品编码， value:对应商品的库存
		Map<Long, Map> stores = new HashMap<Long, Map>();// 每个门店的商品库存。key=门店地址的id，value=代表一个Map类型的门店，其中存在以下键：key=商品的编码，value=店铺中对应商品的库存;
															// key=address,value=门店地址对象;
															// key=distance,value=店铺到发货目的地址的距离(单位：米)

		for (int i = 0; i < goodsCarts.size(); i++) {
			Goods g = goodsCarts.get(i).getGoods();
			ProductMapping pm = g.getProductMapping();
			goodsCodeMaping.put(pm.getGoodsCode(), g);

			Map<GoodsCart, Integer> toBuyCounts = toBuy.get(pm.getGoodsCode());
			if (toBuyCounts == null) {
				toBuyCounts = new HashMap<GoodsCart, Integer>();
				toBuy.put(pm.getGoodsCode(), toBuyCounts);
			}
			toBuyCounts.put(goodsCarts.get(i), goodsCarts.get(i).getCount());
		}

		String hql = "select obj from GoodsInventory obj where obj.goodsCode in (:goodsCodes)";
		Map params = new HashMap();
		params.put("goodsCodes", toBuy.keySet());
		List<GoodsInventory> goodsInventoryTmp = null;
		int maxCheckCnt = 10;
		hxInventoryRWLock.readLock().lock();
		while(maxCheckCnt-- > 0) {
			try {
				goodsInventoryTmp = this.goodsInventoryService.query(hql, params, -1, -1);
			} catch (Exception e) {
				logger.error("查找海信商品在各门店中的库存时出现异常",e);
			}
			if(goodsInventoryTmp != null && goodsInventoryTmp.size() > 0) {
				break;
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error("线程Sleep异常", e);
				}
			}
		}
		hxInventoryRWLock.readLock().unlock();

		if (goodsInventoryTmp == null || goodsInventoryTmp.size() == 0) {
			String errMsg = "查找海信商品在各门店中的库存时失败：未找到指定海信商品的库存信息";
			result.put("flag", 1);
			result.put("errCode", 2);
			result.put("result", errMsg);
			return result;
		}		

		for (GoodsInventory gi : goodsInventoryTmp) {
			ShipAddress addr = gi.getShipAddress();
			Map store = stores.get(addr.getId());
			if (store == null) {
				store = new HashMap();
				store.put("address", addr);
				stores.put(addr.getId(), store);
			}
			store.put(gi.getGoodsCode(), gi.getInventory());

			Integer inventory = goodsInventory.get(gi.getGoodsCode());
			if (inventory == null) {
				inventory = 0;
			}
			goodsInventory.put(gi.getGoodsCode(), inventory + gi.getInventory());
		}

		// 检查库存是否满足需求
		String errMsg = "";
		for (String gcode : toBuy.keySet()) {
			Map<GoodsCart, Integer> gcCounts = toBuy.get(gcode);
			int count = 0;
			for (Integer c : gcCounts.values()) {
				count += c;
			}
			Integer inventory = goodsInventory.get(gcode);
			if (inventory == null || count > inventory) {
				errMsg += "商品（" + goodsCodeMaping.get(gcode).getGoods_name() + "）的库存（<font style=\"color:red\"><strong>"
						+ CommUtil.null2Int(inventory) + "</strong></font>）不能满足需要购买的数量(<font style=\"color:red\"><strong>"
						+ count + "</strong></font>);<br/>";
			}
		}
		if (!"".equals(errMsg)) {
			result.put("flag", 1);
			result.put("errCode", 3);
			result.put("result", errMsg);
			return result;
		}

		List<Map> resultList = this.goodsDispatcher(goodsCodeMaping, toBuy, stores, dstAddr);

		result.put("flag", 0);
		result.put("result", resultList);		

		return result;
	}

	/**
	 * 计算各门店要发的商品
	 * 
	 * @param goodsCodeMaping
	 *            要购买的所有商品的商品编码与商品id的映射
	 * @param toBuy
	 *            要购买的购物车详情。key=要购买的商品的编码，value=为Map（key=购物车对象，value= 对应购物车中的购买数量）。注：此参数中的值在本函数中最终会被清空
	 * @param stores
	 *            要购买的商品的库存（注意：要购买的商品在此参数中可能没有数据）
	 * @param dstAddr
	 *            目的收货地址
	 * @return 
	 *         返回一个List对象，其中每一个元素对应一个Map类型包装的门店信息(key=address:门店地址对象(ShipAddress)；key=distance：门店距离目的地址的直线距离(double型，单位m)；key=
	 *         goodscart_list：要从此门店发出的购物车id列表(List<Long>)；key=goods_count_list(类型：List<Integer>)：对应goodscart_list要发出的各商品的数量)
	 */
	private List<Map> goodsDispatcher(Map<String, Goods> goodsCodeMaping, Map<String, Map<GoodsCart, Integer>> toBuy,
			Map<Long, Map> stores, Address dstAddr) {
		// 计算各门店到发货目的地址的距离并按距离升序排列
		List<Map> storesSortedByDistance = new ArrayList<Map>();
		for (Map store : stores.values()) {
			ShipAddress sa = (ShipAddress) store.get("address");
			store.put(
					"distance",
					AddressUtil.GetShortDistance(sa.getLongitude(), sa.getLatitude(), dstAddr.getLongitude(),
							dstAddr.getLatitude()));
			storesSortedByDistance.add(store);
		}
		Collections.sort(storesSortedByDistance, new Comparator<Map>() {
			@Override
			public int compare(Map o1, Map o2) {
				double d1 = (Double) o1.get("distance");
				double d2 = (Double) o2.get("distance");
				if (d1 > d2)
					return 1;
				else if (d1 < d2)
					return -1;
				else
					return 0;
			}
		});

		// 按序从每个门店中取货，直到要的货全部取到
		List<Map> result = new ArrayList<Map>();
		logger.debug("海信商品分发情况(收货地址: " + dstAddr.getArea_info() + ",对应地址id: " + dstAddr.getId() + ")");
		for (Map store : storesSortedByDistance) {
			if (toBuy.size() == 0) {// 商品都已全部买完
				break;
			}
			List<Long> goodscart_list = new ArrayList<Long>();// 当前门店要发的购物车的id列表(购物车中的商品可能会被分到不同的店铺中去发货)
			List<Integer> goods_count_list = new ArrayList<Integer>(); // 对应goodscart_list的各商品的实际购买数量
			Set<String> toBuyKeysCopy = new HashSet<String>(toBuy.keySet()); // 因为下面的for中要对toBuy进行remove操作，所以不能对toBuy.keySet()进行for操作

			for (String gcode : toBuyKeysCopy) {
				if (store.containsKey(gcode) && ((Integer) store.get(gcode)) > 0) {
					Map<GoodsCart, Integer> goodsCounts = toBuy.get(gcode);
					Set<GoodsCart> goodsCountsKeysCopy = new HashSet<GoodsCart>(goodsCounts.keySet());

					for (GoodsCart gc : goodsCountsKeysCopy) {
						int inventory = (Integer) store.get(gcode);
						if (inventory <= 0) {// 在为第二个购物车购买商品时，库存数可能已为0了
							break;
						}
						int count = goodsCounts.get(gc);
						int countSel = count;
						if (inventory < count) {
							countSel = inventory;
						}
						goodscart_list.add(gc.getId());
						goods_count_list.add(countSel);
						
						ShipAddress sa_debug = (ShipAddress)store.get("address");
						Double distance_debug = (Double)store.get("distance");
						logger.debug(countSel + "个商品(name=" + gc.getGoods().getGoods_name() + ", id=" + gc.getGoods().getId() + ")将从距离收货地址" + distance_debug.toString() + "米远的星美门店(name=" + sa_debug.getSa_name() + ",id=" + sa_debug.getId()+ ")发货。");

						int remainCount = count - countSel;
						if (remainCount == 0) {// 当前购物车中的商品已买完
							goodsCounts.remove(gc);
							if (goodsCounts.size() == 0) {// gcode对应的商品已全部买完
								toBuy.remove(gcode);
							}
						} else {
							goodsCounts.put(gc, remainCount);// 修改要购买的商品的数量
						}
						store.put(gcode, inventory - countSel);// 修改库存的数量，库存可能变为0
					}
				}
			}
			if (goodscart_list.size() > 0) {// 当前门店中有商品要发
				Map goodsResult = new HashMap();
				goodsResult.put("address", store.get("address"));
				goodsResult.put("distance", store.get("distance"));
				goodsResult.put("goodscart_list", goodscart_list);
				goodsResult.put("goods_count_list", goods_count_list);
				result.add(goodsResult);				
			}
		}

		return result;
	}


	/**
	 * 获取商品的规格价格。若商品参加了团购或秒杀活动，则返回团购或秒杀的价格(若商品参加了商城活动且当前用户已登录，则返回的价格都是已折扣后的价格)。若商品为全局库存商品，则返回的价格为全局价格
	 * 
	 * @param gsp
	 *            当商品为规格库存时，若此参数为空或错误则使用默认的规格属性列表；若商品参加了团购或秒杀或为全局库存商品，则此参数无效
	 * @param goodId
	 * @return 返回null则表示商品不存在
	 */
	public String generGspgoodsPrice(String gsp, String goodId) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goodId));
		Double price = null;
		if (goods != null) {
			Map ret = this.getGoodsPriceAndInventory(goods, gsp, null);

			if (ret.containsKey("act_price")) {
				price = (Double) ret.get("act_price");
			} else {
				price = (Double) ret.get("price");
			}
			return CommUtil.null2String(CommUtil.formatMoney(price));
		}

		return null;
	}
	
}
