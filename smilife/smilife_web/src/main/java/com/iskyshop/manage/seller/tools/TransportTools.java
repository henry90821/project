package com.iskyshop.manage.seller.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.domain.virtual.CglibBean;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.ExpressCompanyCommon;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.Transport;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.virtual.OrderFreightInfo;
import com.iskyshop.foundation.domain.virtual.FreightGoods;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IExpressCompanyCommonService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.ITransportService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: TransportTools.java
 * </p>
 * 
 * <p>
 * Description:运费模板工具类，用来处理运费模板相关信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-11-14
 * 
 * @version iskyshop_b2b2c 2015
 */
@Component
public class TransportTools {
	@Autowired
	private ITransportService transportService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IExpressCompanyCommonService expressCompanyCommonService;
	@Autowired
	private IGroupService groupService;

	/**
	 * 根据某个运费模板（json数据），查询此模板数据中针对“全国”的运费模板的指定字段(mark)的值
	 * 
	 * @param json
	 * @param mark
	 * @return
	 */
	public String query_transprot(String json, String mark) {
		String ret = "";
		List<Map> list = Json.fromJson(ArrayList.class, CommUtil.null2String(json));
		if (list != null && list.size() > 0) {
			for (Map map : list) {
				if ("-1".equals(CommUtil.null2String(map.get("city_id")))) {
					ret = CommUtil.null2String(map.get(mark));
					break;
				}
			}
		}
		return ret;
	}

	
	/**
	 * 解析出运费模板列表
	 * 
	 * @param json
	 *            运费json数据
	 * @param type
	 *            0为解析所有信息（包含全国配送），1为解析所有区域配送信息
	 * @return 运费模板列表信息
	 * @throws ClassNotFoundException
	 */
	public List<CglibBean> query_all_transprot(String json, int type) throws ClassNotFoundException {
		List<CglibBean> cbs = new ArrayList<CglibBean>();
		List<Map> list = Json.fromJson(ArrayList.class, CommUtil.null2String(json));
		if (list != null && list.size() > 0) {
			if (type == 0) {
				for (Map map : list) {
					HashMap propertyMap = new HashMap();
					propertyMap.put("city_id", Class.forName("java.lang.String"));
					propertyMap.put("city_name", Class.forName("java.lang.String"));
					propertyMap.put("trans_weight", Class.forName("java.lang.String"));
					propertyMap.put("trans_fee", Class.forName("java.lang.String"));
					propertyMap.put("trans_add_weight", Class.forName("java.lang.String"));
					propertyMap.put("trans_add_fee", Class.forName("java.lang.String"));
					CglibBean cb = new CglibBean(propertyMap);
					cb.setValue("city_id", CommUtil.null2String(map.get("city_id")));
					cb.setValue("city_name", CommUtil.null2String(map.get("city_name")));
					cb.setValue("trans_weight", CommUtil.null2String(map.get("trans_weight")));
					cb.setValue("trans_fee", CommUtil.null2String(map.get("trans_fee")));
					cb.setValue("trans_add_weight", CommUtil.null2String(map.get("trans_add_weight")));
					cb.setValue("trans_add_fee", CommUtil.null2String(map.get("trans_add_fee")));
					cbs.add(cb);
				}
			}
			if (type == 1) {
				for (Map map : list) {
					if (!"-1".equals(CommUtil.null2String(map.get("city_id")))) {
						HashMap propertyMap = new HashMap();
						propertyMap.put("city_id", Class.forName("java.lang.String"));
						propertyMap.put("city_name", Class.forName("java.lang.String"));
						propertyMap.put("trans_weight", Class.forName("java.lang.String"));
						propertyMap.put("trans_fee", Class.forName("java.lang.String"));
						propertyMap.put("trans_add_weight", Class.forName("java.lang.String"));
						propertyMap.put("trans_add_fee", Class.forName("java.lang.String"));
						CglibBean cb = new CglibBean(propertyMap);
						cb.setValue("city_id", CommUtil.null2String(map.get("city_id")));
						cb.setValue("city_name", CommUtil.null2String(map.get("city_name")));
						cb.setValue("trans_weight", CommUtil.null2String(map.get("trans_weight")));
						cb.setValue("trans_fee", CommUtil.null2String(map.get("trans_fee")));
						cb.setValue("trans_add_weight", CommUtil.null2String(map.get("trans_add_weight")));
						cb.setValue("trans_add_fee", CommUtil.null2String(map.get("trans_add_fee")));
						cbs.add(cb);
					}
				}
			}
		}
		return cbs;
	}

	
	/**
	 * 根据指定模板，计算指定单位数量（件数、重量、体积）的商品发送到指定城市的运费。若在模板中没有找到指定城市的运费模板，则取“全国”的运费模板进入计算
	 * @param trans_id 模板id，必须确保此id对应一个模板，否则出错
	 * @param type 用于从运费模板中取出指定发货方式的模板内容，可取值："mail"、"express"、"ems"。必须确保模板中有对应的发货方式的模板内容
	 * @param count 购买的商品数量（单位：个）
	 * @param weight 购买的商品的重量(单位：kg)
	 * @param volume 购买的商品的体积（单位：立方米）
	 * @param city_name 城市名，如“深圳市”，为二级地名
	 * @return 返回运费
	 */
	public float cal_goods_trans_fee(String trans_id, String type, Integer count, String weight, String volume, String city_name) {
		Transport trans = this.transportService.getObjById(CommUtil.null2Long(trans_id));
		String json = "";		
		if ("mail".equals(type)) {
			json = trans.getTrans_mail_info();
		} else if ("express".equals(type)) {
			json = trans.getTrans_express_info();
		} else if ("ems".equals(type)) {
			json = trans.getTrans_ems_info();
		}
		
		return this.calGoodsTransFee(json, trans.getTrans_type(), CommUtil.null2Float(weight), CommUtil.null2Float(volume), city_name, count,null);
	}

	
	/**
	 * 根据指定的模板(如平邮模板、快递模板和EMS模板)（即指定的运送方式）和运费计算类型（trans_type）计算商品发到目的地的运费
	 * @param trans_json  待处理的商品关联的运费模板(如平邮模板、快递模板和EMS模板)的内容，不能为空或null，否则计算返回0
	 * @param trans_type  指定trans_json是哪种类型的模板。0：按照件数计算运费， 1：按照重量计算运费用，2：按照体积计算运费用
	 * @param totalGoodsweight  商品总重量
	 * @param totalGoodsvolume  商品总体积
	 * @param city_name  目的地名称（第二级地址，如深圳市）
	 * @param totalGoodsCount  商品总数量
	 * @param orderFreightInfo
	 * @return  返回运费
	 */
	private float calGoodsTransFee(String trans_json, int trans_type, Float totalGoodsweight, Float totalGoodsvolume, String city_name, Integer totalGoodsCount,OrderFreightInfo orderFreightInfo) {
		float fee = 0;
		boolean cal_flag = false;
		if (!StringUtils.isNullOrEmpty(trans_json)) {
			List<Map> list = Json.fromJson(ArrayList.class, trans_json);
			if (list != null && list.size() > 0) {
				for (Map map : list) {
					String[] city_list = CommUtil.null2String(map.get("city_name")).split("、");
					for (String city : city_list) {
						if (city_name.indexOf(city) >= 0) {// 城市匹配，如“沈阳市”和“沈阳”是一致的
							cal_flag = true;
							float trans_weight = CommUtil.null2Float(map.get("trans_weight"));
							float trans_fee = CommUtil.null2Float(map.get("trans_fee"));
							float trans_add_weight = CommUtil.null2Float(map.get("trans_add_weight"));
							float trans_add_fee = CommUtil.null2Float(map.get("trans_add_fee"));
							
							//begin dengyuqi 2016-3-4
							if(null != orderFreightInfo){
								orderFreightInfo.setTrans_weight((int)trans_weight);
								orderFreightInfo.setTrans_fee(trans_fee);
								orderFreightInfo.setTrans_add_weight((int)trans_add_weight);
								orderFreightInfo.setTrans_add_fee(trans_add_fee);
							}
							//end
							
							fee = trans_fee;
							float other_price = 0;
							if (trans_type == 0 && totalGoodsCount > (int) trans_weight && trans_add_weight > 0) {// 按照件数计算运费
								other_price = trans_add_fee * Math.round(Math.ceil((totalGoodsCount - (int)trans_weight) / trans_add_weight));
							} else if (trans_type == 1 && totalGoodsweight > trans_weight && trans_add_weight > 0) {// 按照重量计算运费用
								other_price = trans_add_fee * Math.round(Math.ceil((totalGoodsweight - trans_weight) / trans_add_weight));
							} else if (trans_type == 2 && totalGoodsvolume > trans_weight && trans_add_weight > 0) {// 按照体积计算运费用
								other_price = trans_add_fee * Math.round(Math.ceil((totalGoodsvolume - trans_weight) / trans_add_weight));
							}
							fee += other_price;
							
							break;
						}
					}
					if(cal_flag) {
						break;
					}
				}
				if (!cal_flag) {// 如果没有找到配置所在的区域运费信息，则使用全国价格进行计算
					for (Map map : list) {
						String[] city_list = CommUtil.null2String(map.get("city_name")).split("、");
						for (String city : city_list) {
							if ("全国".equals(city)) {
								cal_flag = true;
								float trans_weight = CommUtil.null2Float(map.get("trans_weight"));
								float trans_fee = CommUtil.null2Float(map.get("trans_fee"));
								float trans_add_weight = CommUtil.null2Float(map.get("trans_add_weight"));
								float trans_add_fee = CommUtil.null2Float(map.get("trans_add_fee"));
								
								//begin dengyuqi 2016-3-4
								if(null != orderFreightInfo){
									orderFreightInfo.setTrans_weight((int)trans_weight);
									orderFreightInfo.setTrans_fee(trans_fee);
									orderFreightInfo.setTrans_add_weight((int)trans_add_weight);
									orderFreightInfo.setTrans_add_fee(trans_add_fee);
								}
								//end
								
								fee = trans_fee;
								float other_price = 0;
								if (trans_type == 0 && totalGoodsCount > (int) trans_weight && trans_add_weight > 0) {// 按照件数计算运费
									other_price = trans_add_fee * Math.round(Math.ceil((totalGoodsCount - (int)trans_weight) / trans_add_weight));
								} else if (trans_type == 1 && totalGoodsweight > trans_weight && trans_add_weight > 0) {// 按照重量计算运费用
									other_price = trans_add_fee * Math.round(Math.ceil((totalGoodsweight - trans_weight) / trans_add_weight));
								} else if (trans_type == 2 && totalGoodsvolume > trans_weight && trans_add_weight > 0) {// 按照体积计算运费用
									other_price = trans_add_fee * Math.round(Math.ceil((totalGoodsvolume - trans_weight) / trans_add_weight));
								}
								fee += other_price;
								break;
							}
						}
						if(cal_flag) {
							break;
						}
					}
				}
			}
		}
		return fee;
	}

	
	/**
	 * 计算属于某一商家的所购物车中的商品发到目的地址都支持的运送方式及对应运费(若购物车中的所有商品所启用的运送方式没有交集，则返回包含一个SysMap对象的列表，其key为“商品没有公共的运送方式，请联系商家！”，对应value为“9999.0”)。团购商品免邮费
	 * 计算运费的原则：
	 * 1、过滤出公共运送方式；----通常情况下，某种运送方式对于商家来说，要么所有商品都支持要么所有商品都不支持，所以只返回购物车中所有商品都支持的运送方式。
	 * 2、按运费模板分组商品（所有使用静态模板的商品划为一类（取其中最大运费值作为这类商品的最终运费），使用动态模板的商品则挂在同一动态模板下的商品被划为一类并进行统一计算运费）
	 * 3、静态运费模板不受商品属性（个数/重量/体积）影响。
	 * 4、静态运费模板默认对三种运送方式（平邮、快递和EMS）都是支持的。
	 * @param carts  属于同一商家的购物车
	 * @param area_id  收货地址，第三级Area的id，如福田。请确保此参数正确，否则返回包含一个SysMap对象的列表，其key=“请设置一个收货地址！”，value为“0”
	 * @param freeShipFeeGoodsCartIds  免运费的购物车id列表
	 * @param freeShipFeeCountsForGoodsCart  对应freeShipFeeGoodsCartIds的每个购物车中可以免运费的商品数量
	 * @param freightInfoMaps 发货方式(key)及对应的运费信息(value)列表
	 * @return 返回所有商品都支持的发货方式(key)及对应的运费(value)，如：key=“平邮[10.0元]”, value=10.0
	 */
	public List<SysMap> calStoreCartsTransFee(List<GoodsCart> carts, String area_id, List<Long> freeShipFeeGoodsCartIds, List<Integer> freeShipFeeCountsForGoodsCart,Map<String,List<OrderFreightInfo>> freightInfoMaps) {
		List<SysMap> trans = new ArrayList<SysMap>();
		if (!StringUtils.isNullOrEmpty(area_id)) {
			Area area = this.areaService.getObjById(CommUtil.null2Long(area_id)).getParent();
			String city_name = area.getAreaName();
			
			float mail_fee = 0, express_fee = 0, ems_fee = 0;//各运送方式最终对应的总运费
			float static_mail_max = 0, static_express_max = 0, static_ems_max = 0;//所有静态运费模板中对应各运送方式的最高运费			
			
			boolean isMailEnabled = true, isExpressEnabled = true, isEMSEnabled = true;//默认三种运送方式都支持，只要购物车中有一个商品不支持某种运送方式，则购物车中所有商品也都不支持
			Map<Transport, List<Map<Integer, GoodsCart>>> gcsGroupByTranport = new HashMap<Transport, List<Map<Integer, GoodsCart>>>();//按运费模板进行分组
			
			//begin dengyuqi 2016-3-4
			List<OrderFreightInfo> mailFreightInfos = new ArrayList<OrderFreightInfo>(); //平邮运费信息
			List<OrderFreightInfo> expressFreightInfos = new ArrayList<OrderFreightInfo>(); //快递运费信息
			List<OrderFreightInfo> emsFreightInfos = new ArrayList<OrderFreightInfo>(); //EMS运费信息
			//end
			
			for(GoodsCart gc: carts) {//对购物车进行过滤(过滤掉不需要运费的购物车)和分组
				Goods goods = gc.getGoods();
				if (goods.getGoods_transfee() == 0) {// 买家承担费用					
					if(goods.getGroup_buy() == 2 && goods.getGroup() != null) {//团购商品免邮费
						Group group = this.groupService.getObjById(goods.getGroup().getId());
						if(group.getStatus() == 0 && group.getBeginTime().before(new Date())) {
							continue;
						}
					}
					Integer realCount = gc.getCount();//购物车中实际需要支付运费的商品数量
					if(freeShipFeeGoodsCartIds != null) {
						int idx = freeShipFeeGoodsCartIds.indexOf(gc.getId());
						if(idx >= 0) {
							realCount -= freeShipFeeCountsForGoodsCart.get(idx);
							if(realCount <= 0) {
								continue;
							}
						}						
					}
					
					if(goods.getTransport() == null) {//当前购物车中的商品使用静态运费模板
						//begin dengyuqi 2016-3-4
						OrderFreightInfo mailStaticFreightInfo = null;
						for(OrderFreightInfo freightInfo : mailFreightInfos){
							if(freightInfo.getTrans_id() == 0){
								mailStaticFreightInfo = freightInfo;
								break;
							}
						}
						if(null == mailStaticFreightInfo){
							mailStaticFreightInfo = new OrderFreightInfo(0);
						}
						List<FreightGoods> mailFreightGoods = mailStaticFreightInfo.getGoods_info();
						if(null == mailFreightGoods){
							mailFreightGoods = new ArrayList<FreightGoods>();
						}
						FreightGoods mailFreightGood = new FreightGoods(goods.getId());
						mailFreightGood.setPrice(goods.getMail_trans_fee().doubleValue());
						mailFreightGoods.add(mailFreightGood);
						mailStaticFreightInfo.setGoods_info(mailFreightGoods);
						if(!mailFreightInfos.contains(mailStaticFreightInfo)){
							mailFreightInfos.add(mailStaticFreightInfo);
						}
						
						OrderFreightInfo expressStaticFreightInfo = null;
						for(OrderFreightInfo freightInfo : expressFreightInfos){
							if(freightInfo.getTrans_id() == 0){
								expressStaticFreightInfo = freightInfo;
								break;
							}
						}
						if(null == expressStaticFreightInfo){
							expressStaticFreightInfo = new OrderFreightInfo(0);
						}
						List<FreightGoods> expressFreightGoods = expressStaticFreightInfo.getGoods_info();
						if(null == expressFreightGoods){
							expressFreightGoods = new ArrayList<FreightGoods>();
						}
						FreightGoods expressFreightGood = new FreightGoods(goods.getId());
						expressFreightGood.setPrice(goods.getExpress_trans_fee().doubleValue());
						expressFreightGoods.add(expressFreightGood);
						expressStaticFreightInfo.setGoods_info(expressFreightGoods);
						if(!expressFreightInfos.contains(expressStaticFreightInfo)){
							expressFreightInfos.add(expressStaticFreightInfo);
						}
						
						OrderFreightInfo emsStaticFreightInfo = null;
						for(OrderFreightInfo freightInfo : emsFreightInfos){
							if(freightInfo.getTrans_id() == 0){
								emsStaticFreightInfo = freightInfo;
								break;
							}
						}
						if(null == emsStaticFreightInfo){
							emsStaticFreightInfo = new OrderFreightInfo(0);
						}
						List<FreightGoods> emsFreightGoods = emsStaticFreightInfo.getGoods_info();
						if(null == emsFreightGoods){
							emsFreightGoods = new ArrayList<FreightGoods>();
						}
						FreightGoods emsFreightGood = new FreightGoods(goods.getId());
						emsFreightGood.setPrice(goods.getEms_trans_fee().doubleValue());
						emsFreightGoods.add(emsFreightGood);
						emsStaticFreightInfo.setGoods_info(emsFreightGoods);
						if(!emsFreightInfos.contains(emsStaticFreightInfo)){
							emsFreightInfos.add(emsStaticFreightInfo);
						}
						//end
						
						if(goods.getMail_trans_fee().floatValue() > static_mail_max) {
							static_mail_max = goods.getMail_trans_fee().floatValue();
							
//							//begin dengyuqi 2016-3-4
//							OrderFreightInfo staticFreightInfo = null;
//							for(OrderFreightInfo freightInfo : mailFreightInfos){
//								if(freightInfo.getTrans_id() == 0){
//									staticFreightInfo = freightInfo;
//									break;
//								}
//							}
//							if(null == staticFreightInfo){
//								staticFreightInfo = new OrderFreightInfo(0);
//							}
//							
//							staticFreightInfo.setTrans_fee(static_mail_max);
//							List<FreightGoods> freightGoods = new ArrayList<FreightGoods>();
//							FreightGoods freightGood = new FreightGoods(goods.getId(),realCount);
//							freightGoods.add(freightGood);
//							staticFreightInfo.setGoods_info(freightGoods);
//							mailFreightInfos.add(staticFreightInfo);
//							//end
						}
						if(goods.getExpress_trans_fee().floatValue() > static_express_max) {
							static_express_max = goods.getExpress_trans_fee().floatValue();
							
//							//begin dengyuqi 2016-3-4
//							OrderFreightInfo staticFreightInfo = null;
//							for(OrderFreightInfo freightInfo : expressFreightInfos){
//								if(freightInfo.getTrans_id() == 0){
//									staticFreightInfo = freightInfo;
//									break;
//								}
//							}
//							if(null == staticFreightInfo){
//								staticFreightInfo = new OrderFreightInfo(0);
//							}
//							
//							staticFreightInfo.setTrans_fee(static_express_max);
//							List<FreightGoods> freightGoods = new ArrayList<FreightGoods>();
//							FreightGoods freightGood = new FreightGoods(goods.getId(),realCount);
//							freightGoods.add(freightGood);
//							staticFreightInfo.setGoods_info(freightGoods);
//							expressFreightInfos.add(staticFreightInfo);
//							//end
						}
						if(goods.getEms_trans_fee().floatValue() > static_ems_max) {
							static_ems_max = goods.getEms_trans_fee().floatValue();
							
//							//begin dengyuqi 2016-3-4
//							OrderFreightInfo staticFreightInfo = null;
//							for(OrderFreightInfo freightInfo : emsFreightInfos){
//								if(freightInfo.getTrans_id() == 0){
//									staticFreightInfo = freightInfo;
//									break;
//								}
//							}
//							if(null == staticFreightInfo){
//								staticFreightInfo = new OrderFreightInfo(0);
//							}
//							
//							staticFreightInfo.setTrans_fee(static_ems_max);
//							List<FreightGoods> freightGoods = new ArrayList<FreightGoods>();
//							FreightGoods freightGood = new FreightGoods(goods.getId(),realCount);
//							freightGoods.add(freightGood);
//							staticFreightInfo.setGoods_info(freightGoods);
//							emsFreightInfos.add(staticFreightInfo);
//							//end
						}						
					} else {//当前购物车中的商品使用动态运费模板
						Transport tran = goods.getTransport();
						List<Map<Integer, GoodsCart>> gcCounts = gcsGroupByTranport.get(tran);
						if(gcCounts == null) {
							gcCounts = new ArrayList<Map<Integer, GoodsCart>>();
							gcsGroupByTranport.put(tran, gcCounts);	
							
							if(isMailEnabled && !tran.isTrans_mail()) {
								isMailEnabled = false;
							}
							if(isExpressEnabled && !tran.isTrans_express()) {
								isExpressEnabled = false;
							}
							if(isEMSEnabled && !tran.isTrans_ems()) {
								isEMSEnabled = false;
							}
						}
						Map<Integer, GoodsCart> count = new HashMap<Integer, GoodsCart>();
						count.put(realCount, gc);
						gcCounts.add(count);						
					}
				}
			}
			if(!isMailEnabled && !isExpressEnabled && !isEMSEnabled) {
				trans.add(new SysMap("商品没有公共的运送方式，请联系商家！", (float)9999.0));
				return trans;
			}
			//计算动态模板的运费
			Map  dynamicTranFees = this.calDynamicTranFees(gcsGroupByTranport, isMailEnabled, isExpressEnabled, isEMSEnabled, city_name,mailFreightInfos,expressFreightInfos,emsFreightInfos);
			if(isMailEnabled) {
				mail_fee = static_mail_max + (float)dynamicTranFees.get("mail");
				trans.add(new SysMap("平邮[" + mail_fee + "元]", mail_fee));
				
				//begin dengyuqi 2016-3-4
				if(null != freightInfoMaps){
					freightInfoMaps.put("平邮[" + mail_fee + "元]_freightInfos", mailFreightInfos);
				}
				//end
			}
			if(isExpressEnabled) {
				express_fee = static_express_max + (float)dynamicTranFees.get("express");
				trans.add(new SysMap("快递[" + express_fee + "元]", express_fee));
				
				//begin dengyuqi 2016-3-4
				if(null != freightInfoMaps){
					freightInfoMaps.put("快递[" + express_fee + "元]_freightInfos", expressFreightInfos);
				}
				//end
			} 
			if(isEMSEnabled) {
				ems_fee = static_ems_max + (float)dynamicTranFees.get("EMS");
				trans.add(new SysMap("EMS[" + ems_fee + "元]", ems_fee));
				
				//begin dengyuqi 2016-3-4
				if(null != freightInfoMaps){
					freightInfoMaps.put("EMS[" + ems_fee + "元]_freightInfos", emsFreightInfos);
				}
				//end
			}
			if (mail_fee == 0 && express_fee == 0 && ems_fee == 0) {
				trans.clear();
				trans.add(new SysMap("商家承担", (float)0));
			}
		} else {
			trans.add(new SysMap("请设置一个收货地址！", (float)0));
			return trans;
		} 
		
		return trans;
	}
	
	
//	/**
//	 * 计算属于某一商家的所购物车中的商品发到目的地址都支持的运送方式及对应运费(若购物车中的所有商品所启用的运送方式没有交集，则返回包含一个SysMap对象的列表，其key为“商品没有公共的运送方式，请联系商家！”，对应value为“9999.0”)。团购商品免邮费
//	 * 计算运费的原则：
//	 * 1、过滤出公共运送方式；----通常情况下，某种运送方式对于商家来说，要么所有商品都支持要么所有商品都不支持，所以只返回购物车中所有商品都支持的运送方式。
//	 * 2、按运费模板分组商品（所有使用静态模板的商品划为一类（取其中最大运费值作为这类商品的最终运费），使用动态模板的商品则挂在同一动态模板下的商品被划为一类并进行统一计算运费）
//	 * 3、静态运费模板不受商品属性（个数/重量/体积）影响。
//	 * 4、静态运费模板默认对三种运送方式（平邮、快递和EMS）都是支持的。
//	 * @param carts  属于同一商家的购物车
//	 * @param er_goods 需要计算运费的满减购物车中的商品
//	 * @param ac_goods 需要计算运费的满赠购物车中的商品（不包括赠品）
//	 * @param area_id  收货地址，第三级Area的id，如福田。请确保此参数正确，否则返回包含一个SysMap对象的列表，其key=“请设置一个收货地址！”，value为“0”
//	 * @param freeShipFeeGoodsCartIds  免运费的购物车id列表
//	 * @param freeShipFeeCountsForGoodsCart  对应freeShipFeeGoodsCartIds的每个购物车中可以免运费的商品数量
//	 * @return 返回所有商品都支持的发货方式(key)及对应的运费(value)，如：key=“平邮[10.0元]”, value=10.0
//	 */
//	public List<SysMap> calStoreCartsTransFee(List<GoodsCart> carts, Map<Long, List<GoodsCart>> er_goods, Map<Goods, List<GoodsCart>> ac_goods, String area_id,  List<Long> freeShipFeeGoodsCartIds, List<Integer> freeShipFeeCountsForGoodsCart){
//		List<GoodsCart> totalCarts = new ArrayList<GoodsCart>();
//		totalCarts.addAll(carts);
//		
//		if (er_goods != null) {
//			for (Long id : er_goods.keySet()) {
//				List<GoodsCart> list = er_goods.get(id);
//				totalCarts.addAll(list);
//			}
//		}
//		if (ac_goods != null) {
//			for (Goods id : ac_goods.keySet()) {
//				List<GoodsCart> list = ac_goods.get(id);
//				totalCarts.addAll(list);
//			}
//		}		
//		return this.calStoreCartsTransFee(totalCarts, area_id, freeShipFeeGoodsCartIds, freeShipFeeCountsForGoodsCart);
//	}
	
	
//	private void generateStaticFreightInfo(List<OrderFreightInfo> freightInfos,Double fee,
//			GoodsInfo goodsInfo) {
//		if(null == freightInfos){
//			freightInfos = new ArrayList<OrderFreightInfo>();
//		}
//		OrderFreightInfo freightInfo = null;
//		for(OrderFreightInfo orderFreightInfo : freightInfos){
//			if(orderFreightInfo.getTrans_id() == 0){
//				freightInfo = orderFreightInfo;
//				break;
//			}
//		}
//		if(freightInfo == null){
//			freightInfo = new OrderFreightInfo();
//		}
//		
//		freightInfo.setTrans_id(0);
//		freightInfo.setTrans_fee(fee);
//		List<GoodsInfo> goodsInfos = freightInfo.getGoods_info();
//		goodsInfos.clear();
//		goodsInfos.add(goodsInfo);
//		freightInfo.setGoods_info(goodsInfos);
//		freightInfos.add(freightInfo);
//	}

	/**
	 * 计算使用动态运费模板的所有购物车的运送方式及对应运费
	 * @param gcsGroupByTranport  按运费模板进行分组的所有购物车
	 * @param isMailEnabled  商品是否支持平邮（由gcsGroupByTranport中的所有商品确定）
	 * @param isExpressEnabled 商品是否支持快递（由gcsGroupByTranport中的所有商品确定）
	 * @param isEMSEnabled 商品是否支持EMS（由gcsGroupByTranport中的所有商品确定）
	 * @param city_name 第二级城市名，如:深圳
	 * @param mailFreightInfos
	 * @param expressFreightInfos
	 * @param emsFreightInfos
	 * @return 返回所有商品都支持的发货方式(key，可能值：mail、express和EMS)及对应的运费(value,float类型)，如：key=“mail”, value=10.0
	 */
	private Map calDynamicTranFees(Map<Transport, List<Map<Integer, GoodsCart>>> gcsGroupByTranport, boolean isMailEnabled, boolean isExpressEnabled, boolean isEMSEnabled, String city_name,
			List<OrderFreightInfo> mailFreightInfos,List<OrderFreightInfo> expressFreightInfos,List<OrderFreightInfo> emsFreightInfos) {
		Map trans = new HashMap();
		float mailFee = 0, expressFee = 0, emsFee = 0;
		
		for(Transport transport: gcsGroupByTranport.keySet()) {
			//begin dengyuqi 2016-3-4
			OrderFreightInfo mailDynamicFreightInfo = new OrderFreightInfo(transport.getId());
			OrderFreightInfo expressDynamicFreightInfo = new OrderFreightInfo(transport.getId());
			OrderFreightInfo emsDynamicFreightInfo = new OrderFreightInfo(transport.getId());
			List<FreightGoods> mailGoodsInfos = new ArrayList<FreightGoods>();
			List<FreightGoods> expressGoodsInfos = new ArrayList<FreightGoods>();
			List<FreightGoods> emsGoodsInfos = new ArrayList<FreightGoods>();
			//end
				
			Integer totalCount = 0;
			float totalWeight = 0, totalVolume = 0;	//这些变量有些不起作用，起不起作用由运费模板的	trans_type的值决定
			
			//计算运费模板下所有购物车的总数量、总重量和总体积
			for(Map<Integer,GoodsCart> cartEntry: gcsGroupByTranport.get(transport)) {
				for(Integer count: cartEntry.keySet()) {//只会循环一次
					Goods goods = cartEntry.get(count).getGoods();
					BigDecimal cnt = new BigDecimal(count);
					if(transport.getTrans_type() == 0) {//按件数
						totalCount += count;
						
						//begin dengyuqi 2016-3-4
						FreightGoods mailGgoodsInfo = new FreightGoods(goods.getId());
						mailGgoodsInfo.setCount(count);
						FreightGoods expressGoodsInfo = new FreightGoods(goods.getId());
						expressGoodsInfo.setCount(count);
						FreightGoods emsGoodsInfo = new FreightGoods(goods.getId());
						emsGoodsInfo.setCount(count);
						mailGoodsInfos.add(mailGgoodsInfo);
						expressGoodsInfos.add(expressGoodsInfo);
						emsGoodsInfos.add(emsGoodsInfo);
						//end
					} else if(transport.getTrans_type() == 1) {//按重量。若使用按重量计算运费的商品的重量为null,则认为其重量为0
						if(goods.getGoods_weight() != null) {
							totalWeight += goods.getGoods_weight().multiply(cnt).floatValue();
						}
						
						//begin dengyuqi 2016-3-4
						FreightGoods mailGgoodsInfo = new FreightGoods(goods.getId());
						mailGgoodsInfo.setCount(CommUtil.mul(goods.getGoods_weight(), cnt));
						FreightGoods expressGoodsInfo = new FreightGoods(goods.getId());
						expressGoodsInfo.setCount(CommUtil.mul(goods.getGoods_weight(), cnt));
						FreightGoods emsGoodsInfo = new FreightGoods(goods.getId());
						emsGoodsInfo.setCount(CommUtil.mul(goods.getGoods_weight(), cnt));
						mailGoodsInfos.add(mailGgoodsInfo);
						expressGoodsInfos.add(expressGoodsInfo);
						emsGoodsInfos.add(emsGoodsInfo);
						//end
					} else if(transport.getTrans_type() == 2) {//按体积。若使用按体积计算运费的商品的体积为null,则认为其体积为0
						if(goods.getGoods_volume() != null) {
							totalVolume += goods.getGoods_volume().multiply(cnt).floatValue();
						}
						
						//begin dengyuqi 2016-3-4
						FreightGoods mailGoodsInfo = new FreightGoods(goods.getId());
						mailGoodsInfo.setCount(CommUtil.mul(goods.getGoods_weight(), cnt));
						FreightGoods expressGoodsInfo = new FreightGoods(goods.getId());
						expressGoodsInfo.setCount(CommUtil.mul(goods.getGoods_weight(), cnt));
						FreightGoods emsGoodsInfo = new FreightGoods(goods.getId());
						emsGoodsInfo.setCount(CommUtil.mul(goods.getGoods_weight(), cnt));
						mailGoodsInfos.add(mailGoodsInfo);
						expressGoodsInfos.add(expressGoodsInfo);
						emsGoodsInfos.add(emsGoodsInfo);
						//end
					}
				}
			}
			
			if(isMailEnabled) {
				//begin dengyuqi 2016-3-4
				mailDynamicFreightInfo.setGoods_info(mailGoodsInfos);
				mailFreightInfos.add(mailDynamicFreightInfo);
				//end
				
				mailFee += this.calGoodsTransFee(transport.getTrans_mail_info(), transport.getTrans_type(), totalWeight, totalVolume, city_name, totalCount,mailDynamicFreightInfo);
			}
			if(isExpressEnabled) {
				//begin dengyuqi 2016-3-4
				expressDynamicFreightInfo.setGoods_info(expressGoodsInfos);
				expressFreightInfos.add(expressDynamicFreightInfo);
				//end
				
				expressFee += this.calGoodsTransFee(transport.getTrans_express_info(), transport.getTrans_type(), totalWeight, totalVolume, city_name, totalCount,expressDynamicFreightInfo);
			}
			if(isEMSEnabled) {
				//begin dengyuqi 2016-3-4
				emsDynamicFreightInfo.setGoods_info(emsGoodsInfos);
				emsFreightInfos.add(emsDynamicFreightInfo);
				//end
				
				emsFee += this.calGoodsTransFee(transport.getTrans_ems_info(), transport.getTrans_type(), totalWeight, totalVolume, city_name, totalCount,emsDynamicFreightInfo);
			}
		}
		if(isMailEnabled) {
			trans.put("mail", mailFee);
		}
		if(isExpressEnabled) {
			trans.put("express", expressFee);
		}
		if(isEMSEnabled) {
			trans.put("EMS", emsFee);
		}
		
		return trans;
	}

	
	/**
	 * 查询当前快递是否为商家常用物流
	 * 
	 * @param id
	 *            快递id
	 * @return 是否为常用物流公司
	 */
	public int query_common_ec(String id) {
		int ret = 0;
		if (!"".equals(CommUtil.null2String(id))) {
			Map params = new HashMap();
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			List<ExpressCompanyCommon> eccs = new ArrayList<ExpressCompanyCommon>();
			if (store != null && user.getUserRole().indexOf("SELLER") >= 0) {// 商家
				params.put("ecc_type", 0);
				params.put("ecc_store_id", store.getId());
				eccs = this.expressCompanyCommonService.query(
						"select obj from ExpressCompanyCommon obj where obj.ecc_type=:ecc_type and obj.ecc_store_id=:ecc_store_id",
						params, -1, -1);
				for (ExpressCompanyCommon ecc : eccs) {
					if (ecc.getEcc_ec_id().equals(CommUtil.null2Long(id))) {
						ret = 1;
					}
				}
			} else {// 平台
				params.put("ecc_type", 1);
				eccs = this.expressCompanyCommonService
						.query("select obj from ExpressCompanyCommon obj where obj.ecc_type=:ecc_type", params, -1, -1);
				for (ExpressCompanyCommon ecc : eccs) {
					if (ecc.getEcc_ec_id().equals(CommUtil.null2Long(id))) {
						ret = 1;
					}
				}
			}
		}
		return ret;
	}
	
}
