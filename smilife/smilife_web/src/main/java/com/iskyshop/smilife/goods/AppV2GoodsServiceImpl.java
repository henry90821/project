package com.iskyshop.smilife.goods;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.lucene.search.SortField;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.ip.IPSeeker;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.FootPoint;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsInventory;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.ProductMapping;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.Subject;
import com.iskyshop.foundation.domain.Transport;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.EvaluateQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.domain.query.ProductMappingQueryObject;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.ICombinPlanService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IFootPointService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsInventoryService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IProductMappingService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.ISubjectService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lsolr.entity.SortWrapper;
import com.iskyshop.lucene.LuceneResult;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.GoodsTools;
import com.iskyshop.manage.admin.tools.SubjectTools;
import com.iskyshop.module.weixin.manage.coupon.activity.CouponActivityComm;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;
import com.iskyshop.view.web.tools.ActivityViewTools;
import com.iskyshop.view.web.tools.ConsultViewTools;
import com.iskyshop.view.web.tools.GoodsClassViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;
import com.smi.tools.kits.StrKit;
import com.smilife.bcp.service.UserManageConnector;
import com.tydic.framework.util.PropertyUtil;

/**
 * 商城的商品接口实现类
 * 
 * @author hanhua
 * @version 1.0
 * @date 2016-3-19 下午2:25:04
 */
@Service
@Transactional
public class AppV2GoodsServiceImpl implements IAppV2GoodsService {
    /**日志*/
	private Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    private IGoodsClassService goodsClassService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IEvaluateService evaluateService;

    @Autowired
    private ICouponService couponService;

    @Autowired
    private ICouponInfoService couponInfoService;
    
    @Autowired
    private IShipAddressService shipAddressService;
    
    @Autowired
	private IGoodsInventoryService goodsInventoryService;
    
	@Autowired
	private IProductMappingService productMappingService;
	
	@Autowired
	private UserManageConnector manageConnector;
	
	@Autowired
	private IFootPointService footPointService;
	
	@Autowired
	private ISysConfigService sysConfigService;
	
	@Autowired
	private GoodsViewTools goodsViewTools;
	
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private ConsultViewTools consultViewTools;
	@Autowired
	private ActivityViewTools activityViewTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISubjectService subjectService;
	@Autowired
	private SubjectTools subjectTools;
	@Autowired
	private IntegralViewTools integralViewTools; 
	@Autowired
	private GoodsClassViewTools goodsClassViewTools;
	@Autowired
	private GoodsTools goodsTools;

    /**
     * 获取商品分类列表
     * 
     * @author hanhua
     * @method getGoodsClassList
     * @param id
     * @return
     * @date 2016年3月22日 下午3:21:09
     * @return List<GoodsClass>
     */
    public List<GoodsClass> getGoodsClassList(String id) {
        // 商品分类
        List<GoodsClass> goodsClassList = null;
        // 获取当前id的分类所属
        if (StringUtils.isNullOrEmpty(id)) {
            Map params = new HashMap();
            params.put("display", true);
            goodsClassList = this.goodsClassService.query(
                    "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc", params, -1, -1);
        } else {
            GoodsClass goodsClass = this.goodsClassService.getObjById(CommUtil.null2Long(id));
            // 获取当前所属分类的所有子列表分类
            goodsClassList = new ArrayList<GoodsClass>(goodsClass.getChilds());

        }
        return goodsClassList;
    }

    /**
     * 获取分类下商品列表
     * 
     * @author hanhua
     * @method getClassGoods
     * @param gc_id
     * @param orderBy
     * @param orderType
     * @return
     * @date 2016年3月22日 下午3:21:01
     * @return List<Goods>
     */
    public List<Goods> getClassGoods(String gc_id, String orderBy, String orderType) {
        return this.getClassGoods(gc_id, null, null, orderBy, orderType, "0", "12");
    }

    /**
     * 获滚动鼠标加载分类商品列表
     * 
     * @author hanhua
     * @method getClassGoods
     * @param gc_id
     * @param store_id
     * @param type
     * @param orderBy
     * @param orderType
     * @param beginCount
     * @param count
     * @return
     * @date 2016年3月22日 下午3:20:51
     * @return List<Goods>
     */
    public List<Goods> getClassGoods(String gc_id, String store_id, String type, String orderBy, String orderType, String beginCount, String count) {
        // 商品结果集合
        List<Goods> goods_list = null;
        // 预处理条件
        Map<String, Object> preParams = new HashMap<String, Object>();
        String querySql = "select obj from Goods obj " + "where obj.goods_status=:goods_status and obj.gc.id =:gc_id $otherCondition"
                + " order by $orderBy $orderType";
        String otherCondition = "and 1 = 1";
        // 排序条件 默认 orderBy:goods_salenum,orderType: desc
        orderBy = StringUtils.isNullOrEmpty(orderBy) ? "goods_salenum" : orderBy;
        orderType = StringUtils.isNullOrEmpty(orderType) ? "desc" : orderType;
        // 处理类型排序
        switch (orderBy.toLowerCase()) {
        case "goods_collect":
        case "goods_salenum":// 人气,销量
            // 排序只能为降序
            orderType = "desc";
            break;
        case "store_price":// 价格
            break;
        default:
            // 默认按价格排序
            orderType = "store_price";
        }
        otherCondition += StringUtils.isNullOrEmpty(store_id) ? "" : " and obj.goods_store.id =:store_id";
        switch (StringUtils.isNullOrEmpty(type) ? "" : type.toLowerCase()) {
        case "mobile_hot":
            otherCondition += " and obj.mobile_hot=1";
            break;
        case "mobile_recommend":
            otherCondition += " and obj.mobile_recommend=1";
            break;
        default:
            break;
        }
        preParams.put("orderBy", orderBy);
        preParams.put("orderType", orderType);
        preParams.put("otherCondition", otherCondition);
        // 替换占位符
        querySql = pretreatmentSql(querySql, preParams);
        // 查询条件
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("goods_status", 0);
        params.put("gc_id", CommUtil.null2Long(gc_id));
        if (!StringUtils.isNullOrEmpty(store_id)) {
            params.put("store_id", CommUtil.null2Long(store_id));
        }
        goods_list = this.goodsService.query(querySql, params, CommUtil.null2Int(beginCount), CommUtil.null2Int(count));
        return goods_list;
    }

    /**
     * 替换占位符
     * 
     * @author hanhua
     * @method pretreatmentSql
     * @param sql
     * @param params
     * @return
     * @date 2016年3月22日 下午3:22:05
     * @return String
     */
    private String pretreatmentSql(String sql, Map<String, Object> params) {
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            String obj = String.valueOf(params.get(key));
            sql = sql.replace("$" + key, obj);
        }
        return sql;
    }

    /**
     * 搜索接口
     * 
     * @author hanhua
     * @method searchGoods
     * @param keyword
     * @return
     * @date 2016年3月22日 下午3:20:43
     * @return List<LuceneVo>
     */
    public LuceneResult searchGoods(String keyword, String orderType, String type, String currentPage, String pageSize) {
    	 //默认查询第一页
        if(StringUtils.isNullOrEmpty(currentPage)){
        	currentPage = "1";
        }
        //默认查询十条数据
        if(StringUtils.isNullOrEmpty(pageSize)){
        	pageSize = "10";
        }
        // 从System的属性中获取当前应用的工作根目录
        String path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator + "goods";
        // lucene搜索工具类,用来写入索引，搜索数据
        LuceneUtil lucene = LuceneUtil.instance();
        lucene.setIndex_path(path);
        List temp_list = this.goodsClassService.query("select obj.id from GoodsClass obj", null, -1, -1);
        lucene.setGc_size(temp_list.size());
        boolean order_type = true;
		String order_by = "";
		SortWrapper sort = null;
		LuceneResult pList;
		// 排序条件 默认 orderBy:goods_salenum,orderType: desc
        //type = StringUtils.isNullOrEmpty(type) ? "goods_collect" : type;
        orderType = StringUtils.isNullOrEmpty(orderType) ? "desc" : orderType;
        if ("asc".equals(CommUtil.null2String(orderType))) {
			order_type = false;
		}
        // 处理排序方式
		if ("goods_salenum".equals(CommUtil.null2String(type))) {
			order_by = "goods_salenum";//销量goods_salenum
			sort = new SortWrapper(order_by, SortField.Type.INT, order_type);
		}
		if ("goods_collect".equals(CommUtil.null2String(type))) {
			order_by = "goods_collect";//收藏
			sort = new SortWrapper(order_by, SortField.Type.INT, order_type);
		}
		if ("well_evaluate".equals(CommUtil.null2String(type))) {
			order_by = "well_evaluate";//好评
			sort = new SortWrapper(order_by, SortField.Type.DOUBLE, order_type);
		}
		if ("store_price".equals(CommUtil.null2String(type))) {
			order_by = "store_price";//价格
			sort = new SortWrapper(order_by, SortField.Type.DOUBLE, order_type);
		}
		if (sort != null) {
			pList = lucene.search(keyword, CommUtil.null2Int(currentPage), CommUtil.null2Int(pageSize), null, null, null,
					null, null, sort, null, null, null);
		} else {
			pList = lucene.search(keyword, CommUtil.null2Int(currentPage), CommUtil.null2Int(pageSize), pageSize, null, null, null, null, null, null, null, null);
		}
        return pList;
    }

    /**
     * 商品评价
     * 
     * @author hanhua
     * @method getGoodsEva
     * @param keyword
     * @return
     * @date 2016年3月22日 下午3:20:27
     * @return List<Evaluate>
     */
    public List<Evaluate> getGoodsEva(String goodsId, String type) {
        String querySql = "select obj from Evaluate obj where obj.evaluate_goods.id =:goods_id and obj.evaluate_type = 'goods' and obj.evaluate_status = '0'";
        // 判断评价类型
        switch (!StringUtils.isNullOrEmpty(type) ? type.toLowerCase() : "all") {
        case "well":
            querySql += " and obj.evaluate_buyer_val = 1";
            break;
        case "middle":
            querySql += " and obj.evaluate_buyer_val = 0";
            break;
        case "bad":
            querySql += " and obj.evaluate_buyer_val = -1";
            break;
        }
        // 查询条件
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("goods_id", CommUtil.null2Long(goodsId));
        List<Evaluate> list = evaluateService.query(querySql, params, 0, 10);
        return list;
    }

    /**
     * 用户领取优惠券
     * 
     * @author hanhua
     * @method getGoodsStoreCoupon
     * @param goodsId
     *            商品Id
     * @param couponId
     *            优惠券id
     * @return
     * @date 2016年3月22日 下午5:07:56
     * @return List<CouponInfo>
     */
    public String getGoodsStoreCoupon(User user, String goodsId, String couponId) {
        String couponFlag="0";
        Coupon coupon = this.couponService.getObjById(Long.parseLong(couponId));
        String querySql = "select obj from CouponInfo obj where obj.coupon.id=:coupon_id and obj.user.id=:user_id";
        Map<String, Object> couponParams = new HashMap<String, Object>();
        couponParams.put("coupon_id", Long.parseLong(couponId));
        couponParams.put("user_id", user.getId());
        List<CouponInfo> listCouponInfo = this.couponInfoService.query(querySql, couponParams, -1, -1);
        if (!CollectionUtils.isEmpty(listCouponInfo)) {
            couponFlag="1";//此优惠券已领过
        }else{
            CouponInfo info = new CouponInfo();
            info.setAddTime(new Date());
            info.setCoupon(coupon);
            info.setCoupon_sn(UUID.randomUUID().toString());
            info.setUser(user);
            this.couponInfoService.save(info);
        }
        return couponFlag;
    }
    /**
     * 通过影院Id获取影院两公里商品列表
     * @author chuzhisheng
     * @version 1.0
     * @date 2016年3月23日 下午2:05:57
     * @param request
     * @param id
     * @param cinemaId
     * @return
     */
    @SuppressWarnings("static-access")
	public Result getCenimaGoods(String cinemaId,String currentPage,String pageSize){
       Result result=new Result();
		try{
			if(StringUtils.isNullOrEmpty(currentPage)){
				currentPage="1";
			}
			if(StringUtils.isNullOrEmpty(pageSize)){
				pageSize="10";
			}
			//通过cinemaId查询sa_code，cinemaId和sa_code为一对一关系，该数据在后台    自营-》发货地址     配置
			ShipAddress shipAddress = this.shipAddressService.getObjByProperty(null, "cinema_id",cinemaId);
			if(shipAddress==null){
				 result.set(ErrorEnum.REQUEST_ERROR).setMsg("一大波影片，好货正在上线，再等等就有了！");
				 return result;
			}
			String hql = "select obj from GoodsInventory obj where obj.shipAddress.sa_code =:sa_code and obj.inventory>0";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sa_code",shipAddress.getSa_code() );
			//海信商品库存信息
			List<GoodsInventory> goodsInventoryTmp = null;
			int maxCheckCnt = 10;
			goodsTools.hxInventoryRWLock.readLock().lock();
			while(maxCheckCnt-- > 0) {
				try {
					goodsInventoryTmp = this.goodsInventoryService.query(hql, params, -1, -1);
				} catch (Exception e) {
					logger.error("查找海信商品在各门店中的库存时出现异常", e);
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
			
			goodsTools.hxInventoryRWLock.readLock().unlock();
			
			if(goodsInventoryTmp!=null){
				Set<String> goodsCodeList = this.genericGoodsCode(goodsInventoryTmp);
				if(goodsCodeList.size()>0){
				// 通过goods_code到ProductMapping表中查找goods_id;
				Map<String, Object> paramsMap = new HashMap<String, Object>();
				paramsMap.put("goods_code", goodsCodeList);
				paramsMap.put("goods_status", 0);//已上架
				paramsMap.put("configCode", "hx");
				//新建查询实体
			    ProductMappingQueryObject ofqo = new ProductMappingQueryObject(currentPage, new ModelAndView(), "goods.goods_salenum", "desc");
			    StringBuffer sqlBuffer = new StringBuffer();
				sqlBuffer.append("obj.goodsCode in (:goods_code) and obj.goodsConfig.configCode=:configCode and obj.goods.goods_status=:goods_status");
				ofqo.addQuery(sqlBuffer.toString(), paramsMap);
				//设置每页条数
				ofqo.setPageSize(CommUtil.null2Int(pageSize));
				//查询订单信息
				IPageList pList = this.productMappingService.list(ofqo);
				//组装goods集合返回
				@SuppressWarnings("unchecked")
				List<Map<String,Object>> goodsList=this.buildGoodsList(pList.getResult());
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("list", goodsList);
				map.put("totalCount",new Integer(pList.getRowCount()));
		    	map.put("currentPage",new Integer(pList.getCurrentPage()));
		    	map.put("pageSize", pList.getPageSize());
		    	map.put("pageCount", new Integer(pList.getPages()));
				result.setData(map);
				}
			}
		}catch(Exception e){
			 result.set(ErrorEnum.SYSTEM_ERROR);
	         logger.error("[com.iskyshop.smilife.goods] 获取影院两公里商品列表接口异常:"+e);
		}
  	   return result;
    }
    
    /**
     * 构建商品集合
     * @author chuzhisheng
     * @version 1.0
     * @date 2016年3月23日 下午4:11:21
     * @param list
     * @return
     */
    private List<Map<String,Object>> buildGoodsList(List<ProductMapping> list){
    	List<Map<String,Object>> goodsList=null;
    	if(list!=null){
    		goodsList=new ArrayList<Map<String,Object>>();
    		for(ProductMapping obj:list){
    			Map<String,Object> map=new HashMap<String, Object>();
    			if(obj.getGoods()!=null){
    				map.put("goodId", obj.getGoods().getId());
    				map.put("goodName", obj.getGoods().getGoods_name());
    				if(obj.getGoods().getGoods_price() != null){
    					map.put("originalPrice", obj.getGoods().getGoods_price());
    				}else{
    					map.put("originalPrice", "");
    				}
    				map.put("goodPicUrl", obj.getGoods().getGoods_main_photo().getPath()+"/"+obj.getGoods().getGoods_main_photo().getName());
    				map.put("goodPrice", obj.getGoods().getGoods_current_price());
    				map.put("goodInventory", obj.getGoods().getGoods_inventory());
    				map.put("goodsSalenum", obj.getGoods().getGoods_salenum());
    				map.put("evaluateCount", obj.getGoods().getEvaluate_count());
    				map.put("goodDetailDesp", StrKit.isEmpty(obj.getGoods().getGoods_details_mobile())?obj.getGoods().getGoods_details():obj.getGoods().getGoods_details_mobile());
    			}
    			goodsList.add(map);
    		}
    	}
    	return goodsList;
    }
    
    /**
     * 封装商品编码
     * @author chuzhisheng
     * @version 1.0
     * @date 2016年3月23日 下午3:41:24
     * @param goodsInventoryTmp
     * @return
     */
    private Set<String> genericGoodsCode(List<GoodsInventory> goodsInventoryTmp){
    	Set<String> goodsCodes = new HashSet<String>();
		for (GoodsInventory child : goodsInventoryTmp) {
			if(!StringUtils.isNullOrEmpty(child.getGoodsCode())){
				goodsCodes.add(child.getGoodsCode());
			}
		}
		return goodsCodes;
    }
    /**
     * 获取分页的开始位置和查询条数
     * @author chuzhisheng
     * @version 1.0
     * @date 2016年3月23日 下午3:50:47
     * @param currentPage
     * @param pageSize
     * @return
     */
    private Map<String,Integer> getPageMap(String currentPage,String pageSize){
    	Map<String,Integer> map=new HashMap<String, Integer>();
    	int begin=0;
    	int max=10;
    	if(!StringUtils.isNullOrEmpty(pageSize)) max=Integer.parseInt(pageSize);
    	if(!StringUtils.isNullOrEmpty(currentPage)){
    		begin=(Integer.parseInt(currentPage)-1)*max;
    	}else{
    		currentPage="1";
    		begin=begin*max;
    	}
    	map.put("begin", begin);
    	map.put("max", max);
    	return map;
    }

    /**
     * 商品评价列表、成交记录列表接口
     * @author tianbotao
     */
	@Override
	public IPageList getGoodsEva(String goodsId, String type, String currentPage, String pageSize){
        //默认查询第一页
        if(StringUtils.isNullOrEmpty(currentPage)){
        	currentPage = "1";
        }
        //默认查询十条数据
        if(StringUtils.isNullOrEmpty(pageSize)){
        	pageSize = "10";
        }
        EvaluateQueryObject ofqo = new EvaluateQueryObject(currentPage, new ModelAndView(), "addTime", "desc");
        StringBuffer sqlString = new StringBuffer();
        sqlString.append(" obj.evaluate_goods.id =:goods_id and obj.evaluate_type = 'goods' and obj.evaluate_status = '0' ");
        // 判断评价类型
        switch (!StringUtils.isNullOrEmpty(type) ? type.toLowerCase() : "all") {
        case "well":
            sqlString.append(" and obj.evaluate_buyer_val = 1 ");
            break;
        case "middle":
            sqlString.append(" and obj.evaluate_buyer_val = 0 ");
            break;
        case "bad":
            sqlString.append(" and obj.evaluate_buyer_val = -1 ");
            break;
        }
        // 查询条件
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("goods_id", CommUtil.null2Long(goodsId));
        ofqo.addQuery(sqlString.toString(), params);
		//设置每页条数
		ofqo.setPageSize(CommUtil.null2Int(pageSize));
		//查询订单信息
		IPageList pList = this.evaluateService.list(ofqo);
        return pList;
	}

	/**
	 *  获取商品详情接口
     * @author tianbotao
     * @version 1.0
     * @date 2016年3月24日 下午3:50:47
	 * 
	 */
	@Override
	public Map<String, Object> getGoodsDetails(HttpServletRequest request, User user, String goodsId){
		Map<String, Object> entity = new HashMap<String, Object>();
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goodsId));
		boolean adminBoolean = false;
		SeckillGoods seckillGoods = null;
		Map<String, String> genericEvaluateMap = new HashMap<String, String>();
		if(goods != null){
			if(user != null){
				// 登录用户记录浏览足迹信息
				saveFootPoint(request, user, goods);
				//判断用户是否为超级管理员，超级管理员可以查看未审核得到商品信息
				if ("ADMIN".equals(user.getUserRole())) {
					adminBoolean = true;
				}
			}
			// 记录商品点击日志
			saveGoodsLog(request, goods);
			//查看商家商品或超级管理查看为审核商品
			if(goods != null && goods.getGoods_status() == 0 || adminBoolean){
				//检查团购商品
				checkGroupGoods(goods);
				//检查组合商品是否过期
				checkCombinPlan(goods);
				//检查满就送
				Map<String, Boolean> buyGiftMap = checkBuyGift(goods);
				if (goods.getGoods_type() == 0) { // 平台自营商品
					//检查秒杀商品
					seckillGoods = checkSeckillGoods(goods);
				}
				this.goodsService.update(goods);
				//查满减
				EnoughReduce enoughReduce = checkEnoughReduce(goods);
				//查商品规格
				List<GoodsSpecProperty> goodsSpecPropertyList = checkGoodsSpecProperty(goods);
				//通过用户ip查询区域
				String currentCity = checkCurrentIp(request);
				// 查询运费地区
				List<Area> areas = this.areaService.query(
						"select obj from Area obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
				//优惠券活动
				List<Coupon> couponList = checkCouponList(goods);
				if (goods.getGoods_type() == 1) { //商家
					//查询店铺评分信息
					genericEvaluateMap = checkGenericEvaluate(goods.getGoods_store());
				}
				//查看收藏
				String mark = checkFavorite(goods);
				//轮播图
				goods.getGoods_photos();
				List<Accessory> goods_photos = goods.getGoods_photos();
				
				//组装数据
				entity = assemblyData(goods, goods_photos, user, buyGiftMap, enoughReduce, seckillGoods, couponList, goodsSpecPropertyList, areas, genericEvaluateMap, mark);
			}else{
				logger.info("查看失败！");
			}
		}
		return entity;
	}

	/**
	 * 组装数据
	 * @author tianbotao
	 * @param goods_photos
	 * @return
	 */
	private Map<String, Object> assemblyData(Goods goods, List<Accessory> goods_photos, User user, Map<String, Boolean> buyGiftMap, EnoughReduce enoughReduce, SeckillGoods sg, List<Coupon> couponList, 
			List<GoodsSpecProperty> goodsSpecPropertyList, List<Area> areaList, Map<String, String> genericEvaluateMap, String mark){
		Map<String, Object> entity = new HashMap<String, Object>();
		Map<String, String> photoMap;
		Map<String, String> userMap;
		List<Map<String, String>> photoList = new ArrayList<Map<String, String>>();
		if(goods_photos != null && !goods_photos.isEmpty()){
			for(Accessory accessory : goods_photos){
				photoMap = new HashMap<String, String>();
				String url = accessory.getPath() + "/" + accessory.getName();
				photoMap.put("url", url);
				photoList.add(photoMap);
			}
		}
		//主图url
		if(goods.getGoods_main_photo() != null){
			entity.put("main_pic_url", goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName());
		}
		entity.put("goods_photos", photoList);//轮播图路径列表
		if(goods.getGoods_price() != null){
			entity.put("goods_price", CommUtil.null2String(goods.getGoods_price()));//商品原价
		}else{
			entity.put("goods_price", "");
		}
		entity.put("goods_current_price", CommUtil.null2String(goods.getGoods_current_price()));//商品当前价格
		if(user != null){
			userMap = new HashMap<String, String>();
			userMap.put("id", CommUtil.null2String(user.getId()));
			userMap.put("name", user.getUsername());
			entity.put("user", userMap);//用户信息
			if(goods.getActivity_status() == 2){
				String level_name = this.goodsViewTools.query_user_level_name(CommUtil.null2String(user.getId()));
				entity.put("level_name", level_name);//用户级别
				if(!StringUtils.isNullOrEmpty(level_name)){
					Map map = this.activityViewTools.getActivityGoodsInfo(CommUtil.null2String(goods.getId()), CommUtil.null2String(user.getId()), null);
					//entity.put("act_price", map.get("rate_price").toString());//实际价格 根据用户级别等算出
					entity.put("activity_map", map);//实际价格和折扣
				}
			}
		}
		entity.put("goods_name", goods.getGoods_name());//商品名称
		entity.put("goods_salenum", goods.getGoods_salenum());//商品已售数量
		entity.put("group_buy", CommUtil.null2String(goods.getGroup_buy()));//团购 2代表团购
		entity.put("buyGift_id", CommUtil.null2String(goods.getBuyGift_id()));//礼物id
		entity.put("order_enough_give_status", CommUtil.null2String(goods.getOrder_enough_give_status()));//是否可以满送礼物 ?1为可以 
		entity.put("isGift", CommUtil.null2String(buyGiftMap.get("isGift")));//是否有礼物
		entity.put("isGive", CommUtil.null2String(buyGiftMap.get("isGive")));//是否有赠品
		entity.put("buyGift_amount", goods.getBuyGift_amount());//满多少钱给赠品
		entity.put("order_enough_if_give", goods.getOrder_enough_if_give());//是否可以满赠 1为可以
		entity.put("mark", mark);//判断是否收藏
		if(enoughReduce != null){//满减
			entity.put("ertag", enoughReduce.getErtag());
		}
		entity.put("seckill_buy", goods.getSeckill_buy());//秒杀 2，4秒杀
		if(sg != null){
			SeckillGoods seckillGoods = new SeckillGoods();//秒杀实体
			seckillGoods.setBeginTime(sg.getBeginTime());
			seckillGoods.setBeginTime(sg.getEndTime());
			seckillGoods.setGg_price(sg.getGg_price());
			seckillGoods.setGg_count(sg.getGg_count());
			seckillGoods.setGg_selled_count(sg.getGg_selled_count());
			seckillGoods.setGg_status(sg.getGg_status());
			entity.put("seckillGoods", seckillGoods);
			//秒杀截止时间，单位秒
			long restTime=(sg.getEndTime().getTime()-(new Date()).getTime())/1000;
			entity.put("seckillGoods_resttime",restTime);
		}
		entity.put("nowTime", CommUtil.formatLongDate(new Date()));//当前时间
		if(couponList != null && !couponList.isEmpty()){
			List<Map<String, Object>> allReceiveCouponList = new ArrayList<Map<String, Object>>();
			Map<String, Object> map;
			for(Coupon coupon : couponList){
				map = new HashMap<String, Object>();
				map.put("id", coupon.getId());
				map.put("coupon_amount", coupon.getCoupon_amount());//金额（格式化00.00）
				map.put("coupon_order_amount", coupon.getCoupon_order_amount());//满多少可用（格式化00.00）
				map.put("coupon_begin_time", coupon.getCoupon_begin_time());//生效时间（MM-dd）
				map.put("coupon_end_time", coupon.getCoupon_end_time());//失效时间（MM-dd）
				String couponFlag = "0";
				if(user != null){
					String querySql = "select obj from CouponInfo obj where obj.coupon.id=:coupon_id and obj.user.id=:user_id";
			        Map<String, Object> couponParams = new HashMap<String, Object>();
			        couponParams.put("coupon_id", coupon.getId());
			        couponParams.put("user_id", user.getId());
			        List<CouponInfo> listCouponInfo = this.couponInfoService.query(querySql, couponParams, -1, -1);
			        if (!CollectionUtils.isEmpty(listCouponInfo)) {
			            couponFlag="1";//此优惠券已领过
			        }
				}else{
					couponFlag = "2";//没有登录
				}
				map.put("couponFlag", couponFlag);
				allReceiveCouponList.add(map);
			}
			entity.put("allReceiveCouponList", allReceiveCouponList);
		}
		entity.put("goods_id", goods.getId());//商品id
		entity.put("suit_length", this.goodsViewTools.getCombinPlans(CommUtil.null2String(goods.getId()), "suit"));//组合套装数量
		entity.put("parts_length", this.goodsViewTools.getCombinPlans(CommUtil.null2String(goods.getId()), "parts"));//组合配件数量
		List<GoodsSpecification> goodsSpecificationList = this.goodsViewTools.generic_spec(CommUtil.null2String(goods.getId()));
		if(goodsSpecificationList != null && !goodsSpecificationList.isEmpty()){
			List<Map<String, Object>> generic_spec = new ArrayList<Map<String, Object>>();
			Map<String, Object> m;
			for(GoodsSpecification goodsSpecification : goodsSpecificationList){
				m = new HashMap<String, Object>();
				m.put("name", goodsSpecification.getName());
				m.put("id", goodsSpecification.getId());
				generic_spec.add(m);
			}
			entity.put("generic_spec", generic_spec);//商品的规格属性
		}
		if(goodsSpecPropertyList != null && !goodsSpecPropertyList.isEmpty()){
			List<Map<String, Object>> gspsCopy = new ArrayList<Map<String, Object>>();
			Map<String, Object> map;
			GoodsSpecification goodsSpecification;
			for(GoodsSpecProperty goodsSpecProperty : goodsSpecPropertyList){
				map = new HashMap<String, Object>();
				List<Map<String, Object>> spec = new ArrayList<Map<String, Object>>();
				Map<String, Object> m;
				goodsSpecification = goodsSpecProperty.getSpec();
				if(goodsSpecification != null){
					m = new HashMap<String, Object>();
					m.put("id", goodsSpecification.getId());
					spec.add(m);
				}
				map.put("spec", spec);
				map.put("value", goodsSpecProperty.getValue());
				map.put("id", goodsSpecProperty.getId());
				gspsCopy.add(map);
			}
			entity.put("gspsCopy", gspsCopy);
		}
		entity.put("goods_inventory", goods.getGoods_inventory());//库存
		if(areaList != null && !areaList.isEmpty()){//送货至
			List<Map<String, Object>> areas = new ArrayList<Map<String, Object>>();
			Map<String, Object> map;
			for(Area area : areaList){
				map = new HashMap<String, Object>();
				map.put("id", area.getId());
				map.put("areaName", area.getAreaName());
				areas.add(map);
			}
			entity.put("areas", areas);
		}
		entity.put("goods_transfee", goods.getGoods_transfee());//1卖家承担运费
		if(goods.getTransport() != null){
			Transport transport = new Transport();
			transport.setTrans_mail(goods.getTransport().isTrans_mail());//是否可以平邮
			transport.setTrans_express(goods.getTransport().isTrans_express());//是否快递
			transport.setTrans_ems(goods.getTransport().isTrans_ems());//是否ems
			entity.put("transport", transport);
		}
		entity.put("mail_trans_fee", goods.getMail_trans_fee());//平邮运费
		entity.put("express_trans_fee", goods.getExpress_trans_fee());//快递费用
		entity.put("ems_trans_fee", goods.getEms_trans_fee());//ems费用
		entity.put("consul_count", this.consultViewTools.queryByType(null, goods.getId().toString()).size());//在线咨询数量
		entity.put("evaluates_count", goods.getEvaluate_count());//商品评价数量
		if(goods.getGoods_store() != null){
			Store store = new Store();
			Accessory store_logo = new Accessory();
			Store temStore= goods.getGoods_store();
			store.setStore_name(temStore.getStore_name());
			store.setId(temStore.getId());
			store_logo.setPath(temStore.getStore_logo()==null?"":temStore.getStore_logo().getPath());
			store_logo.setName(temStore.getStore_logo()==null?"":temStore.getStore_logo().getName());
			store.setStore_logo(store_logo);
			entity.put("store", store);
		}
		//组长评论数据和对应的用户评价
		assemblyData(entity, goods);
		return entity;
	}

	/**
	 * 组长评论数据
	 * @author tianbotao
	 * @param entity
	 * @param genericEvaluateMap
	 */
	private void assemblyData(Map<String, Object> entity, Goods goods){
		entity.put("well_evaluate", goods.getWell_evaluate());
		entity.put("evaluate_count", goods.getEvaluate_count());
		entity.put("middle_evaluate", goods.getMiddle_evaluate());
		entity.put("bad_evaluate", goods.getBad_evaluate());
		entity.put("well", CommUtil.div(goods.getWell_evaluate(), goods.getEvaluate_count()));
		entity.put("middle", CommUtil.div(goods.getMiddle_evaluate(), goods.getEvaluate_count()));
		entity.put("bad", CommUtil.div(goods.getBad_evaluate(), goods.getEvaluate_count()));
		//对应的用户评价
		List<Evaluate> evaluateList = goods.getEvaluates();
		List<Map<String, Object>> getEvaluates = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		User evaluate_user;
		Accessory photo;
		if(evaluateList != null && !evaluateList.isEmpty()){
			for(Evaluate evaluate : evaluateList){
				map = new HashMap<String, Object>();
				map.put("evaluate_info", evaluate.getEvaluate_info());
				map.put("addTime", evaluate.getAddTime());
				if(evaluate.getEvaluate_user() != null){
					evaluate_user = new User();
					evaluate_user.setUserName(CommUtil.fuzzy(evaluate.getEvaluate_user().getUsername()));
					map.put("addTime", evaluate.getAddTime());
					if(evaluate.getEvaluate_user().getPhoto() != null){
						photo = new Accessory();
						photo.setPath(evaluate.getEvaluate_user().getPhoto().getPath());
						photo.setName(evaluate.getEvaluate_user().getPhoto().getName());
						evaluate_user.setPhoto(photo);
					}
					map.put("evaluate_user", evaluate_user);
				}
				getEvaluates.add(map);
			}
			entity.put("getEvaluates", getEvaluates);
		}
	}

	/**
	 * 查看收藏
	 * @author tianbotao
	 * @param goods
	 * @return
	 */
	private String checkFavorite(Goods goods){
		String mark = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map map = new HashMap();
			map.put("gid", goods.getId());
			map.put("uid", SecurityUserHolder.getCurrentUser().getId());
			List<Favorite> favorites = this.favoriteService
					.query("select obj from Favorite obj where obj.goods_id=:gid and obj.user_id=:uid", map, -1, -1);
			if (favorites.size() > 0) {
				mark = "1";
			}
		}
		return mark;
	}

	/**
	 * 优惠券活动
	 * @author tianbotao
	 * @param goods
	 * @return
	 */
	private List<Coupon> checkCouponList(Goods goods){
		//优惠券活动
		Map allCouponParams = new HashMap();
		allCouponParams.put("current_time", CommUtil.formatDate(CouponActivityComm.getCurDate()));
		allCouponParams.put("acType", 1);// 活动类型，0为商品活动，1为优惠券活动
		allCouponParams.put("ac_status", 1);// 活动状态，0为关闭，1为启动
		allCouponParams.put("ag_status", 1);// 活动商品审核状态，0为待审核，1为审核通过，-1为审核拒绝,-2为已经到期关闭
		allCouponParams.put("activity_id",CommUtil.null2Long(PropertyUtil.getProperty("couponActivityId")));//8为生产环境中对应的id，不可修改，否则生产环境对应功能会出错
		List<Coupon> allCouponList = new ArrayList<Coupon>();
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append(" select obj from ActivityGoods obj where obj.act.ac_status=:ac_status ");
		sqlBuff.append(" and obj.act.id=:activity_id and obj.acType=:acType and obj.ag_status=:ag_status ");
		sqlBuff.append(" and obj.act.ac_begin_time<=:current_time ");
		sqlBuff.append(" and obj.act.ac_end_time>=:current_time ");
		sqlBuff.append(" and obj.coupon.coupon_begin_time<=:current_time ");
		sqlBuff.append(" and obj.coupon.coupon_end_time>=:current_time ");
		sqlBuff.append(" and obj.coupon.coupon_type=:coupon_type ");
		if (goods.getGoods_type() == 0) { // 平台自营商品
			allCouponParams.put("coupon_type", 0);// 优惠券类型，0为平台优惠券，抵消自营商品订单金额，1为商家优惠券，抵消订单中该商家商品部分金额
			sqlBuff.append(" and obj.coupon.store is null ");
		}else{
			allCouponParams.put("coupon_type", 1);
			allCouponParams.put("storeId", goods.getGoods_store().getId());
			sqlBuff.append(" and obj.coupon.store.id =:storeId ");
		}
		sqlBuff.append(" order by obj.coupon.coupon_amount ");
		List<ActivityGoods> allActivityGoodsList = this.activityGoodsService.query(sqlBuff.toString(), allCouponParams, -1, -1);
		for (ActivityGoods allActivityGoods : allActivityGoodsList) {
			allCouponList.add(allActivityGoods.getCoupon());
		}
		List<Coupon> allReceiveCouponList = new ArrayList<Coupon>();
		for (Coupon allCoupons : allCouponList) {
			List<CouponInfo> allCouponInfoList = allCoupons.getCouponinfos();
			int allCouponInfoLen = 0;
			if (allCouponInfoList != null && allCouponInfoList.size() > 0) {
				allCouponInfoLen = allCouponInfoList.size();
			}
			if (allCoupons.getCoupon_count() == 0 || allCouponInfoLen < allCoupons.getCoupon_count()) {
				allReceiveCouponList.add(allCoupons);
			}
		}
		return allReceiveCouponList;
	}

	/**
	 * 查询店铺评分信息
     * @author tianbotao
	 * @param goods_store
	 * @return
	 */
	private Map<String, String> checkGenericEvaluate(Store store){
		Map<String, String> genericEvaluateMap = new HashMap<String, String>();
		double description_result = 0;
		double service_result = 0;
		double ship_result = 0;
		GoodsClass gc = this.goodsClassService.getObjById(store.getGc_main_id());
		if (store != null && gc != null && store.getPoint() != null) {
			float description_evaluate = CommUtil.null2Float(gc.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(gc.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());

			float store_description_evaluate = CommUtil.null2Float(store.getPoint().getDescription_evaluate());
			float store_service_evaluate = CommUtil.null2Float(store.getPoint().getService_evaluate());
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint().getShip_evaluate());
			// 计算和同行比较结果
			description_result = CommUtil.div(store_description_evaluate - description_evaluate, description_evaluate);
			service_result = CommUtil.div(store_service_evaluate - service_evaluate, service_evaluate);
			ship_result = CommUtil.div(store_ship_evaluate - ship_evaluate, ship_evaluate);
		}
		if (description_result > 0) {
			genericEvaluateMap.put("description_css", "value_strong");
			genericEvaluateMap.put("description_result", CommUtil.null2String(
							CommUtil.mul(description_result, 100) > 100 ? 100 : CommUtil.mul(description_result, 100))
					+ "%");
		}
		if (description_result == 0) {
			genericEvaluateMap.put("description_css", "value_normal");
			genericEvaluateMap.put("description_result", "-----");
		}
		if (description_result < 0) {
			genericEvaluateMap.put("description_css", "value_light");
			genericEvaluateMap.put("description_result", CommUtil.null2String(CommUtil.mul(-description_result, 100)) + "%");
		}
		if (service_result > 0) {
			genericEvaluateMap.put("service_css", "value_strong");
			genericEvaluateMap.put("service_result", CommUtil.null2String(CommUtil.mul(service_result, 100)) + "%");
		}
		if (service_result == 0) {
			genericEvaluateMap.put("service_css", "value_normal");
			genericEvaluateMap.put("service_result", "-----");
		}
		if (service_result < 0) {
			genericEvaluateMap.put("service_css", "value_light");
			genericEvaluateMap.put("service_result", CommUtil.null2String(CommUtil.mul(-service_result, 100)) + "%");
		}
		if (ship_result > 0) {
			genericEvaluateMap.put("ship_css", "value_strong");
			genericEvaluateMap.put("ship_result", CommUtil.null2String(CommUtil.mul(ship_result, 100)) + "%");
		}
		if (ship_result == 0) {
			genericEvaluateMap.put("ship_css", "value_normal");
			genericEvaluateMap.put("ship_result", "-----");
		}
		if (ship_result < 0) {
			genericEvaluateMap.put("ship_css", "value_light");
			genericEvaluateMap.put("ship_result", CommUtil.null2String(CommUtil.mul(-ship_result, 100)) + "%");
		}
		return genericEvaluateMap;
	}

	/**
	 * 通过用户ip查询区域
     * @author tianbotao
	 * @param goods
	 * @return
	 */
	private String checkCurrentIp(HttpServletRequest request){
		// 计算当期访问用户的IP地址
		String currentCity = null;
		String current_ip = CommUtil.getIpAddr(request); // 获得本机IP
		if (CommUtil.isIp(current_ip)) {
			IPSeeker ip = new IPSeeker(null, null);
			currentCity = ip.getIPLocation(current_ip).getCountry();
		} else {
			currentCity = "未知地区";
		}
		return currentCity;
	}

	/**
	 * 查商品规格
     * @author tianbotao
	 * @param goods
	 * @return
	 */
	private List<GoodsSpecProperty> checkGoodsSpecProperty(Goods goods){
		List<Map> gsps = (List<Map>) CommUtil.json2List(goods.getGoods_specs_info());
		List<GoodsSpecProperty> gspsBase = goods.getGoods_specs();
		List<GoodsSpecProperty> gspsBaseCopy = new ArrayList<GoodsSpecProperty>();
		for (GoodsSpecProperty gsp : gspsBase) {
			GoodsSpecProperty s = new GoodsSpecProperty();
			s.setId(gsp.getId());
			s.setSpecImage(gsp.getSpecImage());
			s.setValue(gsp.getValue());
			s.setSpec(gsp.getSpec());
			for (Map m : gsps) {
				if (s.getId().toString().equals(m.get("id"))) {
					s.setValue(CommUtil.null2String(m.get("name")));
				}
			}
			gspsBaseCopy.add(s);
		}
		return gspsBaseCopy;
	}

	/**
	 * 检查满就送
     * @author tianbotao
	 * @param goods
	 */
	private EnoughReduce checkEnoughReduce(Goods goods){
		if (goods.getEnough_reduce() == 1) { // 如果是满就减商品，未到活动时间不作处理，活动时间显示满减信息，已过期则删除满减信息
			EnoughReduce er = this.enoughReduceService
					.getObjById(CommUtil.null2Long(goods.getOrder_enough_reduce_id()));
			if (er.getErstatus() == 10 && er.getErbegin_time().before(new Date())
					&& er.getErend_time().after(new Date())) { // 正在进行
				return er;
			}
		}
		return null;
	}

	/**
	 * 检查满就送
     * @author tianbotao
	 * @param goods
	 */
	private Map<String, Boolean> checkBuyGift(Goods goods){
		Map<String, Boolean> buyGiftMap = new HashMap<String, Boolean>();
		if (goods.getOrder_enough_give_status() == 1) {
			BuyGift bg = this.buyGiftService.getObjById(goods.getBuyGift_id());
			if (bg != null && bg.getEndTime().before(new Date())) {
				bg.setGift_status(20);
				List<Map> maps = Json.fromJson(List.class, bg.getGift_info());
				maps.addAll(Json.fromJson(List.class, bg.getGoods_info()));
				for (Map map : maps) {
					Goods g = this.goodsService.getObjById(CommUtil.null2Long(map.get("goods_id")));
					if (g != null) {
						g.setOrder_enough_give_status(0);
						g.setOrder_enough_if_give(0);
						g.setBuyGift_id(null);
						this.goodsService.update(g);
					}
				}
				this.buyGiftService.update(bg);
			}
			if (bg != null && bg.getGift_status() == 10) {
				buyGiftMap.put("isGift", true);
			}
		}
		if (goods.getOrder_enough_if_give() == 1) {
			BuyGift bg = this.buyGiftService.getObjById(goods.getBuyGift_id());
			if (bg != null && bg.getGift_status() == 10) {
				buyGiftMap.put("isGive", true);
			}
		}
		return buyGiftMap;
	}

	/**
	 * 检查组合商品是否过期
     * @author tianbotao
	 * @param goods
	 */
	private SeckillGoods checkSeckillGoods(Goods goods){
		Map params = new HashMap();
		SeckillGoods seckillGoods = null;
		String jpql = "select obj from SeckillGoods obj where obj.gg_goods.id=:goods_id and obj.gg_status=:gg_status";
		if (goods.getSeckill_buy() == 4) { // 检查秒杀是否开始
			params.clear();
			params.put("goods_id", goods.getId());
			params.put("gg_status", 1);
			seckillGoods = seckillGoodsService.query(jpql, params, 0, 1).get(0);
			if (seckillGoods.getBeginTime().before(new Date())) {
				seckillGoods.setGg_status(2);
				seckillGoodsService.update(seckillGoods);
				goods.setSeckill_buy(2);
				goods.setGoods_current_price(seckillGoods.getGg_price());
				updateLucene(goods);
			}
			//mv.addObject("seckillGoods", seckillGoods);
		} else if(goods.getSeckill_buy() == 2) { // 检查秒杀是否过期
			params.clear();
			params.put("goods_id", goods.getId());
			params.put("gg_status", 2);
			seckillGoods = seckillGoodsService.query(jpql, params, 0, 1).get(0);
			if (seckillGoods.getEndTime().before(new Date())) {
				seckillGoods.setGg_status(3);
				seckillGoodsService.update(seckillGoods);
				goods.setSeckill_buy(0);
				goods.setGoods_inventory(
						goods.getGoods_inventory() + (seckillGoods.getGg_count() - seckillGoods.getGg_selled_count()));
				goods.setGoods_current_price(goods.getStore_price());
				updateLucene(goods);
			}
			//mv.addObject("seckillGoods", seckillGoods);
		}
		return seckillGoods;
	}
	
	/**
	 * 更新lucene索引
	 * @author tianbotao
	 * @param goods
	 */
	private void updateLucene(Goods goods){
		String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
				+ "goods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneUtil lucene = LuceneUtil.instance();
		LuceneUtil.setIndex_path(goods_lucene_path);
		lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
	}

	/**
	 * 检查组合商品是否过期
     * @author tianbotao
	 * @param goods
	 */
	private void checkCombinPlan(Goods goods){
		if (goods.getCombin_status() == 1) {
			Map params = new HashMap();
			params.put("endTime", new Date());
			params.put("main_goods_id", goods.getId());
			List<CombinPlan> combins = this.combinplanService.query(
					"select obj from CombinPlan obj where obj.endTime<=:endTime and obj.main_goods_id=:main_goods_id",
					params, -1, -1);
			if (combins.size() > 0) {
				for (CombinPlan com : combins) {
					if (com.getCombin_type() == 0) {
						if (goods.getCombin_suit_id().equals(com.getId())) {
							goods.setCombin_suit_id(null);
						}
					} else {
						if (goods.getCombin_parts_id().equals(com.getId())) {
							goods.setCombin_parts_id(null);
						}
					}
					goods.setCombin_status(0);
				}
			}
		}
	}

	/**
	 * 检查团购商品
     * @author tianbotao
	 * @param goods
	 */
	private void checkGroupGoods(Goods goods){
		goods.setGoods_click(goods.getGoods_click() + 1);
		if (this.sysConfigService.getSysConfig().isZtc_status() && goods.getZtc_status() == 2) {
			goods.setZtc_click_num(goods.getZtc_click_num() + 1);
		}
		if (goods.getGroup() != null && goods.getGroup_buy() == 2) { // 如果是团购商品，检查团购是否过期
			Group group = goods.getGroup();
			if (group.getEndTime().before(new Date())) {
				goods.setGroup(null);
				goods.setGroup_buy(0);
				goods.setGoods_current_price(goods.getStore_price());
			}
		}
	}

	/**
	 * 记录商品点击日志
     * @author tianbotao
	 * @param goods
	 */
	private void saveGoodsLog(HttpServletRequest request, Goods goods){
		GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(goods.getId());
		if(todayGoodsLog != null){
			todayGoodsLog.setGoods_click(todayGoodsLog.getGoods_click() + 1);
			String click_from_str = todayGoodsLog.getGoods_click_from();
			Map<String, Integer> clickmap = (!StringUtils.isNullOrEmpty(click_from_str))
					? (Map<String, Integer>) Json.fromJson(click_from_str) : new HashMap<String, Integer>();
			String from = CommUtil.null2String(request.getParameter("from"));
			if("search".equals(from)){
				from = "搜索";
			}else if("floor".equals(from)){
				from = "首页楼层";
			}else if("gcase".equals(from)){
				from = "橱窗";
			}else{
				from = "其他";
			}
			if (!StringUtils.isNullOrEmpty(from)) {
				if (clickmap.containsKey(from)) {
					clickmap.put(from, clickmap.get(from) + 1);
				} else {
					clickmap.put(from, 1);
				}
			} else {
				if (clickmap.containsKey("unknow")) {
					clickmap.put("unknow", clickmap.get("unknow") + 1);
				} else {
					clickmap.put("unknow", 1);
				}
			}
			todayGoodsLog.setGoods_click_from(Json.toJson(clickmap, JsonFormat.compact()));
			boolean goodsLogBoolean = this.goodsLogService.update(todayGoodsLog);
			if(goodsLogBoolean){
				logger.info("记录日志成功！");
			}else{
				logger.info("记录日志失败！");
			}
		}
	}

	/**
	 * 登录用户记录浏览足迹信息
	 * @author tianbotao
	 * @param user
	 */
	private void saveFootPoint(HttpServletRequest request, User user, Goods goods){
		if(user != null){
			Map mapFootPoint = new HashMap();
			Map params = new HashMap();
			FootPoint fp = null;
			params.put("fp_date", CommUtil.formatDate(CommUtil.formatShortDate(new Date())));
			params.put("fp_user_id", user.getId());
			//查询该用户当天足迹
			List<FootPoint> fps = this.footPointService.query(
					"select obj from FootPoint obj where obj.fp_date=:fp_date and obj.fp_user_id=:fp_user_id", params, -1, -1);
			if(fps != null && !fps.isEmpty()){
				fp = fps.get(0);
				List<Map> list = Json.fromJson(List.class, fp.getFp_goods_content());
				boolean add = true;
				for (Map map : list) { // 排除重复的商品足迹
					if (CommUtil.null2Long(map.get("goods_id")).equals(goods.getId())){
						add = false;
					}
				}
				if(add){
					//组装footPoint对象
					modleFootPoint(request, goods, mapFootPoint);
					list.add(0, mapFootPoint); // 后浏览的总是插入最前面
					fp.setFp_goods_count(list.size());
					fp.setFp_goods_content(Json.toJson(list, JsonFormat.compact()));
					this.footPointService.update(fp);
					logger.info("商品足迹更新成功！");
				}else{
					logger.info("商品足迹已存在！");
				}
			}else{
				List<Map> mList = new ArrayList<Map>();
				fp = new FootPoint();
				//组装footPoint对象
				modleFootPoint(request, goods, mapFootPoint);
				fp.setAddTime(new Date());
				fp.setFp_date(new Date());
				fp.setFp_user_id(user.getId());
				fp.setFp_user_name(user.getUsername());
				fp.setFp_goods_count(1);
				mList.add(mapFootPoint);
				fp.setFp_goods_content(Json.toJson(mList, JsonFormat.compact()));
				this.footPointService.save(fp);
				logger.info("商品足迹添加成功！");
			}
		}
	}
	
	/**
	 * 组装footPoint对象
	 * @author tianbotao
	 * @param footPoint
	 */
	private void modleFootPoint(HttpServletRequest request, Goods goods, Map mapFootPoint){
		mapFootPoint.put("goods_id", goods.getId());
		mapFootPoint.put("goods_name", goods.getGoods_name());
		mapFootPoint.put("goods_sale", goods.getGoods_salenum());
		mapFootPoint.put("goods_time", CommUtil.formatLongDate(new Date()));
		mapFootPoint.put("goods_img_path",goods.getGoods_main_photo() != null ? (goods.getGoods_main_photo().getPath() + "/" 
				+ goods.getGoods_main_photo().getName()) : (this.sysConfigService.getSysConfig()
				.getGoodsImage().getPath() + "/"
				+ this.sysConfigService.getSysConfig().getGoodsImage().getName()));
		mapFootPoint.put("goods_price", goods.getGoods_current_price());
		mapFootPoint.put("goods_class_id", CommUtil.null2Long(goods.getGc().getId()));
		mapFootPoint.put("goods_class_name", CommUtil.null2String(goods.getGc().getClassName()));
	}

	/**
	 * 分页查询分类下子分类下所有商品
	 * @author tianbotao
	 */
	@Override
	public IPageList getClassGoodsList(String classId, String store_id, String type, String orderBy, String orderType, String currentPage, String pageSize){
		ModelAndView mv = new ModelAndView();
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(classId));

        orderType = StringUtils.isNullOrEmpty(orderType) ? "desc" : orderType;

        GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        gqo.setPageSize(CommUtil.null2Int(pageSize)); // 设定分页查询
        Set<Long> ids = null;
		if (gc != null) {
			ids = goodsClassViewTools.genericIds(gc.getId(), false, false);
			ids.add(gc.getId());
		}
		if (ids != null && ids.size() > 0) {
			Map paras = new HashMap();
			paras.put("ids", ids);
			gqo.addQuery("obj.gc.id in (:ids)", paras);
		}
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		switch (StringUtils.isNullOrEmpty(type) ? "" : type.toLowerCase()) {
		      	case "mobile_hot":
		            gqo.addQuery("obj.mobile_hot", new SysMap("mobile_hot", 1), "=");
		            break;
		        case "mobile_recommend":
		            gqo.addQuery("obj.mobile_recommend", new SysMap("mobile_recommend", 1), "=");
		            break;
		        default:
		            break;
		  }
		if (!StringUtils.isNullOrEmpty(store_id)) {
            gqo.addQuery("obj.store_id", new SysMap("store_id", CommUtil.null2Long(store_id)), "=");
        }
		IPageList pList = this.goodsService.list(gqo);
		return pList;
	}
	
		
	/**
	 * 获取专题详情接口
	 * @author tianbotao 
	 */
	@Override
	public List<Map<String, Object>> getSubjectDetails(String subjectId){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null; 
		Subject subject = this.subjectService.getObjById(CommUtil.null2Long(subjectId));
		if(subject != null && subject.getSubject_detail() != null){
			List<Map> objs = (List<Map>) Json.fromJson(subject.getSubject_detail());
			if(objs != null && !objs.isEmpty()){
				int seq = 1;
				boolean isHeaderImgGot = false;
				for(Map m : objs){
					String type = CommUtil.null2String(m.get("type"));
					if("goods".equals(type)){
						String goodsIds = CommUtil.null2String(m.get("goods_ids"));
						if(!StringUtils.isNullOrEmpty(goodsIds)){
							String[] arry = goodsIds.split(",");
							for(String id: arry){
								if(!StringUtils.isNullOrEmpty(id)) {
									Goods goods = this.goodsService.getObjById(Long.parseLong(id));
									if(goods != null && goods.getGoods_status() == 0) {
										map = new HashMap<String, Object>();
										map.put("id", goods.getId());
										map.put("type", "goods");
										map.put("goodsName",goods.getGoods_name());
										map.put("goodsPrice", goods.getGoods_current_price());
										if(goods.getGoods_price() != null){
											map.put("originalPrice", goods.getGoods_price());
										}else{
											map.put("originalPrice", "");
										}
										map.put("picUrl", goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName());
										map.put("seq", seq++);
										list.add(map);
									}									
								}								
							}
						}
					}else if(!isHeaderImgGot && "img".equals(type)){
						map = new HashMap<String, Object>();
						map.put("id", m.get("id"));
						map.put("type", "img");
						map.put("picUrl", m.get("img_url"));
						map.put("detailUrl", m.get("img_href"));
						if(!StringUtils.isNullOrEmpty(m.get("areaInfo"))){
							List<Map> areaInfo = this.subjectTools.getAreaInfo(CommUtil.null2String(m.get("areaInfo")));
							map.put("areaInfo", areaInfo);
						}
						map.put("seq", seq++);
						list.add(map);
						isHeaderImgGot = true;
					}
				}
			}
		}
		return list;
	}

	/**
	 * 客户端商品详细介绍
	 * @author tianbotao 
	 */
	@Override
	public Map<String, Object> goodsIntroduce(String goodsId){
		Map<String, Object> entity = new HashMap<String, Object>();
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goodsId));
		if(goods != null){
			entity.put("goodsId", goods.getGoods_name());//商品id
			entity.put("goodsName", goods.getGoods_name());//商品名称
			entity.put("goodsDetailsMobile", goods.getGoods_details_mobile());// 详细说明app
			entity.put("goodsDetails", goods.getGoods_details());//详细说明pc网页
			entity.put("goodsSellerTime", goods.getGoods_seller_time());//商品上架时间
			GoodsBrand goodsBrand = goods.getGoods_brand();
			if(goodsBrand != null){
				entity.put("goodsBrandName", goodsBrand.getName());//品牌名称
			}
			String json = goods.getGoods_property();
			if(json != null && !"[]".equals(json)){
				List<Map> list = (List<Map>) Json.fromJson(json);
				if(list != null && !list.isEmpty()){
					entity.put("goodsProperty", list);
				}
			}
		}
		return entity;
	}

	/**
	 * 根据店铺id查询商品列表
	 * @author tianbotao 
	 */
	@Override
	public IPageList getGoodsForStoreId(String storeId, String type, String orderType, String currentPage, String pageSize){
		ModelAndView mv = new ModelAndView();
		GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, type, orderType);
	    gqo.setPageSize(CommUtil.null2Int(pageSize)); // 设定分页查询
		Map params = new HashMap();
	    StringBuffer sql = new StringBuffer();
	    sql.append(" obj.goods_status=:goods_status");
	    params.put("goods_status", 0);
		if (!StringUtils.isNullOrEmpty(storeId)) {
			sql.append(" and obj.goods_store.id=:store_id");
			params.put("store_id", CommUtil.null2Long(storeId));
        }else{
        	sql.append(" and obj.goods_store.id is null");
        }
		gqo.addQuery(sql.toString(), params);
		IPageList pList = this.goodsService.list(gqo);
		return pList;
	}
	
	/**
	 * 查看商品规格库存
	 * @author tianbotao
	 */
	@Override
	public Result loadGoodsGsp(String id, String gsp, User user){
		Result result = new Result();
		try{
			if(StringUtils.isNullOrEmpty(id) || StringUtils.isNullOrEmpty(gsp)){
				result.set(ErrorEnum.REQUEST_ERROR);
				return result;
			}
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			Map<String, Object> map = new HashMap<String, Object>();
			int count = 0;
			double price = 0;
			double act_price = 0;
			if (goods.getGroup() != null && goods.getGroup_buy() == 2) { // 团购商品统一按照团购价格处理
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						count = gg.getGg_count();
						price = CommUtil.null2Double(gg.getGg_price());
					}
				}
			} else {
				count = goods.getGoods_inventory();
				price = CommUtil.null2Double(goods.getStore_price());
				if ("spec".equals(goods.getInventory_type())) {
					List<HashMap> list = Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
					String[] gsp_ids = gsp.split(",");
					for (Map temp : list) {
						String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
						Arrays.sort(gsp_ids);
						Arrays.sort(temp_ids);
						if (Arrays.equals(gsp_ids, temp_ids)) {
							count = CommUtil.null2Int(temp.get("count"));
							price = CommUtil.null2Double(temp.get("price"));
						}
					}
				}
			}
			if (goods.getActivity_status() == 2 && user != null) { // 如果是促销商品，并且用户已登录，根据规格配置价格计算相应配置的促销价格
				ActivityGoods actGoods = this.activityGoodsService.getObjById(goods.getActivity_goods_id());
				// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
				BigDecimal rebate = BigDecimal.valueOf(0.00);
				int level = this.integralViewTools
						.query_user_level(CommUtil.null2String(user.getId()));
				if (level == 0) {
					rebate = actGoods.getAct().getAc_rebate();
				} else if (level == 1) {
					rebate = actGoods.getAct().getAc_rebate1();
				} else if (level == 2) {
					rebate = actGoods.getAct().getAc_rebate2();
				} else if (level == 3) {
					rebate = actGoods.getAct().getAc_rebate3();
				}
				act_price = CommUtil.mul(rebate, price);
			}
			map.put("count", count);
			map.put("price", CommUtil.formatMoney(price));
			if (act_price != 0) {
				map.put("act_price", CommUtil.formatMoney(act_price));
			}
			result.setData(map);
		}catch(Exception e){
			result.set(ErrorEnum.SYSTEM_ERROR);
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	/**
     * 查询最新上架商品
     * 
     * @author tianbotao
     */
    public IPageList getNewGoods(String currentPage, String pageSize) {
        ModelAndView mv = new ModelAndView();
        GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, "goods_seller_time", "desc");
        gqo.setPageSize(CommUtil.null2Int(pageSize)); // 设定分页查询
        Map params = new HashMap();
        StringBuffer sql = new StringBuffer();
        sql.append(" obj.goods_status=:goods_status");
        params.put("goods_status", 0);
        gqo.addQuery(sql.toString(), params);
        IPageList pList = this.goodsService.list(gqo);
        return pList;
    }
}
