package com.iskyshop.smilife.order;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jasig.cas.client.util.CommonUtils;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.smilife.common.Constants;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;
import com.iskyshop.smilife.enums.OrderStatus;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.smilife.bcp.service.UserManageConnector;

/**
 * 订单接口
 * @author chuzhisheng
 * @version 1.0
 * @date 2016年3月22日 下午4:56:06
 */
@Service
@Transactional
public class OrderServiceImpl implements IOrderService{
    /**日志*/
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private UserManageConnector manageConnector;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private IPayoffLogService payoffLogservice;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IStorePointService storePointService;
	/**
	 * 通过类型获取订单信息
	 *  1、全部：null(不传为全部)、
	 *  2、待付款：10、
	 *  3、待发货:20、
	 *  4、待收货:30、
	 *  5、已收货:40
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年3月22日 下午5:03:36
	 * @param id
	 * @param type
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	@Override
	public Result getOrders(User user, String status, String currentPage,String pageSize) {
		Result result=new Result();
		try{
			//设置每页条数，默认设置为10
			if(StringUtils.isNullOrEmpty(pageSize)){
				pageSize=Constants.TEN;
			}
			//设置每页条数，默认设置为8
			if(StringUtils.isNullOrEmpty(currentPage)){
				currentPage=Constants.ONE;
			}
			//新建查询实体
			OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, new ModelAndView(), "addTime", "desc");
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("obj.order_id in(")
	 		 .append(" select distinct b.mainOrderId from OrderForm b where b.user_id =:user_id and b.order_cat !=:order_cat");
			//查询参数
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("user_id", user.getId().toString());
			params.put("order_cat", 2);
			if (!StringUtils.isNullOrEmpty(status)) {
				params.put("order_status",  CommUtil.null2Int(status));
				sqlBuffer.append(" and b.order_status =:order_status");
			}
			sqlBuffer.append(") ");
			ofqo.addQuery(sqlBuffer.toString(), params);
			//设置每页条数
			ofqo.setPageSize(CommUtil.null2Int(pageSize));
			//查询订单信息
			IPageList pList = this.orderFormService.list(ofqo);
			//构建返回集合
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> orderList=this.buildOrderList(pList.getResult());
			Map<String, Object> map=new HashMap<String, Object>();
			map.put(Constants.TOTAL_COUNT,new Integer(pList.getRowCount()));
	    	map.put(Constants.CURRENT_PAGE,new Integer(pList.getCurrentPage()));
	    	map.put(Constants.PAGE_SIZE, pList.getPageSize());
	    	map.put(Constants.PAGE_COUNT, new Integer(pList.getPages()));
	    	map.put(Constants.LIST, orderList);
	    	//如果传入的当前页比总页数大则返回空集合
	    	if(Integer.parseInt(currentPage)>new Integer(pList.getPages())){
	    		map.put(Constants.LIST, new ArrayList<Object>());
	    	}
	    	//设置返回数据
	    	result.setData(map);
		}catch(Exception e){
			 result.set(ErrorEnum.SYSTEM_ERROR);
	         logger.error("[com.iskyshop.smilife.order] 查询订单接口异常:"+e);
	         e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据订单id获取物流信息
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年3月23日 上午10:54:24
	 * @param request
	 * @param id
	 * @param orderId
	 * @return
	 */
   public Result getLogisticsInfo(User user,String orderId){
	   Result result=new Result();
		try{
		   //物流信息集合
		   List<TransInfo> transInfo_list = new ArrayList<TransInfo>();
		   //根据订单id查询订单信息
		   OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(orderId));
		   //如果查询不到订单直接返回
		   if(order==null){
			   return  result.set(ErrorEnum.REQUEST_ERROR).setMsg("查询不到订单信息");
		   }
		   //主订单的物流信息
		   TransInfo transInfo = this.orderFormTools.query_ship_getData(CommUtil.null2String(orderId));
			if (transInfo != null) {
				transInfo.setExpress_company_name(
						this.orderFormTools.queryExInfo(order.getExpress_info(), "express_company_name"));
				transInfo.setExpress_ship_code(order.getShipCode());
				transInfo_list.add(transInfo);
			}
			//子订单的物流信息
			List<String> childOrderIds=this.getChildOrderIds(order.getChild_order_detail()); //获取子订单id
            if(childOrderIds!=null){
            	 for(String id:childOrderIds){
            		  //查询子订单物流信息
            		  TransInfo logistics = this.orderFormTools.query_ship_getData(CommUtil.null2String(id)); 
            		  transInfo_list.add(logistics);
            	 }
            }
			//设置接口返回数据
			result.setData(transInfo_list);
		}catch(Exception e){
			 result.set(ErrorEnum.SYSTEM_ERROR);
	         logger.error("[com.iskyshop.smilife.order] 获取物流信息接口异常:"+e);
	         e.printStackTrace();
		}
	   return result;
   }
   
	/**
	 * 根据订单id获取订单详情
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年3月23日 下午5:07:22
	 * @param request
	 * @param id
	 * @param orderId
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
  public Result getOrdersDetail(User user,String orderId){
	   Result result=new Result();
		try{
			if (!StringUtils.isNullOrEmpty(orderId)) {
			   //构建返回数据	
	           result.setData(this.buildOrderResult(orderId));
			}
		}catch(Exception e){
			 result.set(ErrorEnum.SYSTEM_ERROR);
	         logger.error("[com.iskyshop.smilife.order] 获取订单详情接口异常:"+e);
	         e.printStackTrace();
		}
	   return result;
  }

/**
 * 通过订单id，构建返回数据
 * @author chuzhisheng
 * @version 1.0
 * @date 2016年4月18日 上午11:07:14
 * @param orderId
 * @return
 */
  private Map<String, Object>  buildOrderResult(String orderId){
	  Map<String, Object> map=new HashMap<String, Object>();
	  //查询订单信息
	  OrderForm order= this.orderFormService.getObjById(CommUtil.null2Long(orderId));
	  if(order!=null){
			//商品信息
			map.put("goodsInfo", JSON.parse(order.getGoods_info()));
			//订单号
			map.put("orderNo", order.getOrder_id());
			//订单id
			map.put("orderId", order.getId());
			//订单状态
			map.put("orderStatus", order.getOrder_status());
			//订单价格
		    map.put("orderPrice", orderFormTools.query_order_price(orderId));
			//map.put("orderPrice", order.getTotalPrice());
			//运费金额
			map.put("orderShipPrice", orderFormTools.query_ship_price(orderId));
			//收货人
			map.put("receiverName", order.getReceiver_Name());
			//手机号
			map.put("receiverMobile", order.getReceiver_mobile());
			//身份证号
			map.put("receiverCard", order.getReceiver_card());
			//收货详细地址
			map.put("receiverAreaInfo",order.getReceiver_area()+" "+order.getReceiver_area_info());
			//商品金额
			map.put("goodsPrice", order.getGoods_amount());
			//优惠券
			String couponOrderAmount="0.00";
			if(order.getCoupon_info()!=null){
				Object o=JSON.parseObject(order.getCoupon_info()).get("coupon_amount");
				couponOrderAmount=o==null?"0.00":o.toString();
			}
			map.put("coupons",couponOrderAmount);
			//满减金额
			map.put("enoughReduce", order.getEnough_reduce_amount());
			//商品图标
			map.put("goodsIconUrl",order.getGoodsConfig()==null?"":order.getGoodsConfig().getIconUrl());
			//物流单号
			map.put("shipCode",order.getShipCode());
			//子订单信息
			map.put("childOrderDetail",JSON.parse(order.getChild_order_detail()));
			//物流公司
			String expressCompany="";
			if(order.getExpress_info()!=null){
				Object o=JSON.parseObject(order.getExpress_info()).get("express_company_name");
				expressCompany=o==null?"":o.toString();
			}
			map.put("expressCompany",expressCompany);
			//虚拟商品信息
			map.put("orderSellerIntro",JSON.parse(order.getOrder_seller_intro()));
			//赠品信息
			map.put("giftInfos",JSON.parse(order.getGift_infos()));
			//商品数量
			map.put("goodsCount",orderFormTools.query_goods_count(order.getId().toString()));
			// 配送方式，0为快递配送，1为自提点自提     2为店铺自提 ,3为星美直送
			map.put("deliveryType", order.getDelivery_type());
	  }
	  return map;
  }
  /**
   * 解析子订单json串。获取子订单id
   * @author chuzhisheng
   * @version 1.0
   * @date 2016年4月18日 上午11:17:08
   * @param orderDetailJson
   * @return
   */
  private List<String> getChildOrderIds(String orderDetailJson){
	  List<String> list=null;
	  if(!StringUtils.isNullOrEmpty(orderDetailJson)){
		  JSONArray jsonList=JSON.parseArray(orderDetailJson);
		  if(jsonList!=null){
			  list=new ArrayList<String>();
			  for(int i=0;i<jsonList.size();i++){
				  JSONObject obj=jsonList.getJSONObject(i);
				  list.add(obj.getString("order_id"));
			  }
		  }
	  }
	  return list;  
  }
  
  /**
   * 构建商品集合
   * @author chuzhisheng
   * @version 1.0
   * @date 2016年3月23日 下午4:11:21
   * @param list
   * @return
   */
  private List<Map<String,Object>> buildOrderList(List<OrderForm> list){
  	List<Map<String,Object>> orderList=null;
  	if(list!=null){
  		orderList=new ArrayList<Map<String,Object>>();
  		for(OrderForm obj:list){
  			Map<String,Object> map=new HashMap<String, Object>();
  			if(obj!=null){
  			    //订单id
  				map.put("orderId", obj.getId());
  			    //订单号
  				map.put("orderNo",obj.getOrder_id() );
  			    //订单状态
  				map.put("orderStatus",obj.getOrder_status());
  			    //订单状态描述
  				map.put("orderStatusName", OrderStatus.getDescr(obj.getOrder_status()));
  			    //订单价格
  				map.put("orderPrice",orderFormTools.query_order_price(obj.getId().toString()));
  			    //商品信息，json格式
  				map.put("goodsInfo", JSON.parse(obj.getGoods_info()));
  			    //商品数量
  				map.put("goodsCount",orderFormTools.query_goods_count(obj.getId().toString()));
  			    //商品图标
  				map.put("goodsIconUrl",obj.getGoodsConfig()==null?"":obj.getGoodsConfig().getIconUrl());
  				// 配送方式，0为快递配送，1为自提点自提     2为店铺自提 ,3为星美直送
  				map.put("deliveryType", obj.getDelivery_type());
  			}
  			orderList.add(map);
  		}
  	}
  	return orderList;
  }
  	
  	
  	/**
  	 * 取消订单
	 * @author tianbotao
	 * stateInfo取消订单原因
  	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Result cancelOrder(HttpServletRequest request, User user, String id, String stateInfo){
		 // 接口返回对象
        Result result = new Result();
        try {
			// 检验必填参数项
	        if (CommonUtils.isEmpty(id) || CommonUtils.isEmpty(stateInfo)) {
	            return result.set(ErrorEnum.REQUEST_ERROR).setMsg("参数异常");
	        }
	        List<OrderForm> objs = new ArrayList<OrderForm>();
			OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
			objs.add(obj);
			boolean all_verify = true;
			if (obj != null && obj.getUser_id().equals(CommUtil.null2String(user.getId()))) {
				if (obj.getOrder_main() == 1 && obj.getChild_order_detail() != null) {
					List<Map> maps = (List<Map>) Json.fromJson(CommUtil.null2String(obj.getChild_order_detail()));
					if (maps != null) {
						for (Map map : maps) {
							logger.info(map.get("order_id"));
							OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(map.get("order_id")));
							objs.add(child_order);
						}
					}
				}
				for (OrderForm of : objs) {
					if (of.getOrder_status() >= 20) {
						all_verify = false;
					}
				}
			}
			if (all_verify) {
				if (obj != null && obj.getUser_id().equals(CommUtil.null2String(user.getId()))) {
					if (obj.getOrder_main() == 1) {
						List<Map> maps = (List<Map>) Json.fromJson(CommUtil.null2String(obj.getChild_order_detail()));
						if (maps != null) {
							for (Map map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil.null2Long(map.get("order_id")));
								child_order.setOrder_status(0);
								this.orderFormService.update(child_order);
								if (child_order.getOrder_form() == 0) {
									Store store = this.storeService.getObjById(CommUtil.null2Long(child_order.getStore_id()));
									Map<String, Object> map2 = new HashMap<String, Object>();
									if (store != null) {
										map2.put("seller_id", store.getUser().getId().toString());
									}
									map2.put("order_id", obj.getId().toString());
									this.msgTools.sendEmailCharge(CommUtil.getURL(request),
											"email_toseller_order_cancel_notify", store.getUser().getEmail(), map2, null,
											CommUtil.null2String(store.getId()));
									this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_order_cancel_notify",
											store.getUser().getMobile(), map2, null, CommUtil.null2String(store.getId()));
								}

							}
						}
					}
					int old_status = obj.getOrder_status();
					obj.setOrder_status(0);
					this.orderFormService.update(obj);
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("取消订单。原订单状态为：" + old_status);
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(obj);
					ofl.setState_info(stateInfo);
					this.orderFormLogService.save(ofl);
					Store store = this.storeService.getObjById(CommUtil.null2Long(obj.getStore_id()));
					Map<String, Object> map = new HashMap<String, Object>();
					if (store != null) {
						map.put("seller_id", store.getUser().getId().toString());
					}
					map.put("order_id", obj.getId().toString());
					if (obj.getOrder_form() == 0) {
						this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_toseller_order_cancel_notify", store
								.getUser().getEmail(), map, null, CommUtil.null2String(store.getId()));
						this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_order_cancel_notify", store
								.getUser().getMobile(), map, null, CommUtil.null2String(store.getId()));
					}
					//还原商品库存
					this.goodsService.recover_goods_inventory(obj, null);
				}
			}
        } catch (Exception e) {
            result.set(ErrorEnum.SYSTEM_ERROR);
            logger.error(e);
            e.printStackTrace();
        }
        return result;
	}
	
	/**
	 * 确认收货订单
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年4月15日 下午5:53:50
	 * @param request
	 * @param user
	 * @param orderId 订单id
	 * @return
	 */
   @SuppressWarnings("static-access")
public Result confirmOrder(HttpServletRequest request, User user,String orderId){
	   // 接口返回对象
       Result result = new Result();
       try{
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(orderId));
		//订单为本人的，且状态状态是待收货
		if (obj != null && obj.getUser_id().equals(CommUtil.null2String(user.getId()))) {
			if(obj.getOrder_status()!=30){
			    return result.set(ErrorEnum.REQUEST_ERROR).setMsg("订单状态为"+OrderStatus.getDescr(obj.getOrder_status())+"的订单不能确认收货！");
			}
			obj.setOrder_status(40);
			Calendar ca = Calendar.getInstance();
			ca.add(ca.DATE, this.configService.getSysConfig().getAuto_order_return());
			obj.setReturn_shipTime(ca.getTime());
			obj.setConfirmTime(new Date()); // 设置确认收货时间
			boolean ret = this.orderFormService.update(obj);
			if (obj.getPayType() != null && "payafter".equals(obj.getPayType())) { // 如果买家支付方式为货到付款，买家确认收货时更新商品库存
				this.update_goods_inventory(obj); // 更新商品库存
			}
			if (ret) { // 订单状态更新成功，更新相关信息
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("确认收货");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
				// 主订单生成商家结算日志
				if (obj.getOrder_form() == 0) {
					Store store = this.storeService.getObjById(CommUtil.null2Long(obj.getStore_id()));
					PayoffLog plog = new PayoffLog();
					if (obj.getPayType() != null && "payafter".equals(obj.getPayType())) {
						plog.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
						plog.setPl_info("货到付款");
						plog.setAddTime(new Date());
						plog.setSeller(store.getUser());
						plog.setO_id(CommUtil.null2String(obj.getId()));
						plog.setOrder_id(obj.getOrder_id().toString());
						plog.setCommission_amount(obj.getCommission_amount()); // 该订单总佣金费用
						plog.setGoods_info(obj.getGoods_info());
						plog.setOrder_total_price(obj.getGoods_amount()); // 该订单总商品金额
						plog.setTotal_amount(BigDecimal.valueOf(CommUtil.add(
								CommUtil.subtract(0, obj.getCommission_amount()), obj.getShip_price()))); // 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
						plog.setShip_price(obj.getShip_price());
					} else {
						plog.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
						plog.setPl_info("确认收货");
						plog.setAddTime(new Date());
						plog.setSeller(store.getUser());
						plog.setO_id(CommUtil.null2String(obj.getId()));
						plog.setOrder_id(obj.getOrder_id().toString());
						plog.setCommission_amount(obj.getCommission_amount()); // 该订单总佣金费用
						plog.setGoods_info(obj.getGoods_info());
						plog.setOrder_total_price(obj.getGoods_amount()); // 该订单总商品金额
						plog.setTotal_amount(BigDecimal.valueOf(CommUtil.add(
								CommUtil.subtract(obj.getGoods_amount(), obj.getCommission_amount()), obj.getShip_price()))); // 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
						plog.setShip_price(obj.getShip_price());
					}
					this.payoffLogservice.save(plog);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(obj.getGoods_amount(),
							store.getStore_sale_amount()))); // 店铺本次结算总销售金额
					store.setStore_commission_amount(BigDecimal.valueOf(CommUtil.add(obj.getCommission_amount(),
							store.getStore_commission_amount()))); // 店铺本次结算总佣金
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(plog.getTotal_amount(),
							store.getStore_payoff_amount()))); // 店铺本次结算总佣金
					this.storeService.update(store);
					// 增加系统总销售金额、总佣金
					SysConfig sc = this.configService.getSysConfig();
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(obj.getGoods_amount(), sc.getPayoff_all_sale())));
					sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil.add(obj.getCommission_amount(),
							sc.getPayoff_all_commission())));
					this.configService.update(sc);
				}
			}
		}
       }catch (Exception e) {
           result.set(ErrorEnum.SYSTEM_ERROR);
           logger.error(e);
           e.printStackTrace();
       }
       return result;
   }
	@SuppressWarnings({ "rawtypes", "static-access", "unchecked" })
	private void update_goods_inventory(OrderForm order) {
		// 付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(CommUtil.null2String(order.getId()));
		// 更新订单中组合套装商品信息
		List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
		for (Map map_combin : maps) {
			if (map_combin.get("combin_suit_info") != null) {
				Map suit_info = Json.fromJson(Map.class, CommUtil.null2String(map_combin.get("combin_suit_info")));
				int combin_count = CommUtil.null2Int(suit_info.get("suit_count"));
				List<Map> combin_goods = this.orderFormTools.query_order_suitgoods(suit_info);
				for (Map temp_goods : combin_goods) { // 更新组合套装中其他商品信息，将套装主商品排除，主商品在普通商品更新中更新信息
					for (Goods temp : goods_list) {
						if (!CommUtil.null2String(temp_goods.get("id")).equals(temp.getId().toString())) {
							Goods goods = this.goodsService.getObjById(CommUtil.null2Long(temp_goods.get("id")));
							goods.setGoods_salenum(goods.getGoods_salenum() + combin_count);
							this.goodsService.update(goods);
						}
					}
				}
			}
		}
		// 普通商品更新信息
		for (Goods goods : goods_list) {
			int goods_count = this.orderFormTools.queryOfGoodsCount(CommUtil.null2String(order.getId()),
					CommUtil.null2String(goods.getId()));
			if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						gg.setGg_selled_count(gg.getGg_selled_count() + goods_count);
						this.groupGoodsService.update(gg);
						// 更新lucene索引
						String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
								+ File.separator + "groupgoods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGroupGoodsIndex(gg));
					}
				}
			}
			List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools.queryOfGoodsGsps(
					CommUtil.null2String(order.getId()), CommUtil.null2String(goods.getId()));
			String spectype = "";
			for (GoodsSpecProperty gsp : temp_gsp_list) {
				spectype += gsp.getSpec().getName() + ":" + gsp.getValue() + " ";
			}
			goods.setGoods_salenum(goods.getGoods_salenum() + goods_count);

			GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(goods.getId());
			todayGoodsLog.setGoods_salenum(todayGoodsLog.getGoods_salenum() + goods_count);

			Map<String, Integer> logordermap = (Map<String, Integer>) Json.fromJson(todayGoodsLog.getGoods_order_type());
			String ordertype = order.getOrder_type();
			if (logordermap.containsKey(ordertype)) {
				logordermap.put(ordertype, logordermap.get(ordertype) + goods_count);
			} else {
				logordermap.put(ordertype, goods_count);
			}
			todayGoodsLog.setGoods_order_type(Json.toJson(logordermap, JsonFormat.compact()));

			Map<String, Integer> logspecmap = (Map<String, Integer>) Json.fromJson(todayGoodsLog.getGoods_sale_info());

			if (logspecmap.containsKey(spectype)) {
				logspecmap.put(spectype, logspecmap.get(spectype) + goods_count);
			} else {
				logspecmap.put(spectype, goods_count);
			}
			todayGoodsLog.setGoods_sale_info(Json.toJson(logspecmap, JsonFormat.compact()));

			this.goodsLogService.update(todayGoodsLog);

			this.goodsService.update(goods);
			// 更新lucene索引
			String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
					+ "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
		}
	}
	
	/**
	 * 根据主订单id获取子订单详情
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年3月23日 下午5:07:22
	 * @param request
	 * @param id
	 * @param orderId
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
    public Result getChildOrdersDetail(User user,String orderId){
	   Result result=new Result();
	   try{
		   //定义子订单集合
		   List<Map<String,Object>> childOrderList=null;
		   //查询主订单信息
		   OrderForm order= this.orderFormService.getObjById(CommUtil.null2Long(orderId));
		   //订单为主订单即该订单有子订单信息
		   if(order!=null&&order.getOrder_main()==1){
			   //获取子订单id
			   List<String> childOrderIds=this.getChildOrderIds(order.getChild_order_detail());
			   if(childOrderIds!=null){
				   childOrderList=new ArrayList<Map<String,Object>>();
				   for(int i=0;i<childOrderIds.size();i++){
					   //将查询出的子订单信息放入子订单集合
					   childOrderList.add(this.buildOrderResult(childOrderIds.get(i)));
				   }
				   //设置返回数据
				   result.setData(childOrderList); 
			   }
		   }else{
			   result.setData(new ArrayList<Object>()); 
		   }
	   }catch(Exception e){
			 result.set(ErrorEnum.SYSTEM_ERROR);
	         logger.error("[com.iskyshop.smilife.order.getChildOrdersDetail] 查询子订单详情接口异常:"+e);
	         e.printStackTrace();
		}
	   return result;
   }
   
   /**
    * 订单评价
    * @author chuzhisheng
    * @version 1.0
    * @date 2016年4月18日 下午2:00:25
    * @param user
    * @param id
    * @return
    */
   @SuppressWarnings({ "rawtypes", "unchecked", "static-access", "unused" })
   public Result saveOrderDiscuss(HttpServletRequest request,User user,String orderIds,String evaluates){
	   Result result=new Result();
	   try{
		   if(!StringUtils.isNullOrEmpty(orderIds)){	
			    String[] ids = orderIds.split(",");
				List<Map> evaluatesMap = CommUtil.json2List(evaluates);
				for (String id : ids) {
					OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
					if (obj != null && obj.getUser_id().equals(user.getId().toString())) {
						if (obj.getOrder_status() == 40) {
							obj.setOrder_status(50);
							this.orderFormService.update(obj);
							OrderFormLog ofl = new OrderFormLog();
							ofl.setAddTime(new Date());
							ofl.setLog_info("评价订单");
							ofl.setLog_user(user);
							ofl.setOf(obj);
							this.orderFormLogService.save(ofl);
							List<Map> json = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
							for (Map map : json) {
								map.put("orderForm", obj.getId());
							}
							for (Map map : json) {
								Evaluate eva = new Evaluate();
								Goods goods = this.goodsService.getObjById(CommUtil.null2Long(map.get("goods_id")));
								eva.setAddTime(new Date());
								eva.setEvaluate_goods(goods);
								eva.setGoods_num(CommUtil.null2Int(map.get("goods_count")));
								eva.setGoods_price(map.get("goods_price").toString());
								eva.setGoods_spec(map.get("goods_gsp_val").toString());
								
								for(Map evaluateMap : evaluatesMap){
									if(goods.getId().intValue() == CommUtil.null2Int(evaluateMap.get("goodsId"))){
										eva.setEvaluate_info((String)evaluateMap.get("evaluate_info"));
										int eva_val = CommUtil.null2Int(evaluateMap.get("evaluate_buyer_val"));							
										eva.setEvaluate_buyer_val(eva_val);
										BigDecimal desc_eva_val = BigDecimal.valueOf(CommUtil.null2Double(evaluateMap.get("description_evaluate")));
										eva.setDescription_evaluate(desc_eva_val);
										eva.setService_evaluate(BigDecimal.valueOf(
												CommUtil.null2Double(evaluateMap.get("service_evaluate"))));
										eva.setShip_evaluate(BigDecimal
												.valueOf(CommUtil.null2Double(evaluateMap.get("ship_evaluate"))));
										
										//保存商品中的评价相关信息
										BigDecimal eva_count = BigDecimal.valueOf(goods.getEvaluate_count());
						                BigDecimal desc_eva_total = goods.getDescription_evaluate().multiply(eva_count);
										eva_count = eva_count.add(BigDecimal.valueOf(1));
										goods.setEvaluate_count(eva_count.intValue());
										goods.setDescription_evaluate(BigDecimal.valueOf(CommUtil.div(desc_eva_total.add(desc_eva_val), eva_count)));
										if(eva_val == 1) {//好评
											goods.setWell_evaluate(goods.getWell_evaluate() + 1);
										} else if(eva_val == 0) {//中评
											goods.setMiddle_evaluate(goods.getMiddle_evaluate() + 1);
										} else {//差评
											goods.setBad_evaluate(goods.getBad_evaluate() + 1);
										}		
										break;
									}
								}
								eva.setEvaluate_type("goods");
								eva.setEvaluate_user(user);
								//542 前台：个人中心-我的评价追加评论失败 原因是因为没有添加交易完成时间，导致追加和修改评价是出错
								OrderForm ord = this.orderFormService.getObjById(CommUtil.null2Long(map.get("orderForm")));
								ord.setFinishTime(new Date());
								this.orderFormService.update(ord);
								eva.setOf(ord);
								this.evaluateService.save(eva);
								
								
								this.goodsService.update(goods);	
	
								// 更新lucene索引
								String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
										+ "goods";
								File file = new File(goods_lucene_path);
								if (!file.exists()) {
									CommUtil.createFolder(goods_lucene_path);
								}
								LuceneUtil lucene = LuceneUtil.instance();
								lucene.setIndex_path(goods_lucene_path);
								lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
								
								Map params = new HashMap();
								User sp_user = this.userService.getObjById(obj.getEva_user_id());
								params.put("user_id", user.getId().toString());
								List<Evaluate> evas = this.evaluateService
										.query("select obj from Evaluate obj where obj.of.user_id=:user_id", params, -1, -1);
								double user_evaluate1 = 0;
								double user_evaluate1_total = 0;
								double description_evaluate = 0;
								double description_evaluate_total = 0;
								double service_evaluate = 0;
								double service_evaluate_total = 0;
								double ship_evaluate = 0;
								double ship_evaluate_total = 0;
								DecimalFormat df = new DecimalFormat("0.0");
								for (Evaluate eva1 : evas) {
									user_evaluate1_total = user_evaluate1_total + eva1.getEvaluate_buyer_val();
									description_evaluate_total = description_evaluate_total
											+ CommUtil.null2Double(eva1.getDescription_evaluate());
									service_evaluate_total = service_evaluate_total
											+ CommUtil.null2Double(eva1.getService_evaluate());
									ship_evaluate_total = ship_evaluate_total + CommUtil.null2Double(eva1.getShip_evaluate());
								}
								user_evaluate1 = CommUtil.null2Double(df.format(user_evaluate1_total / evas.size()));
								description_evaluate = CommUtil.null2Double(df.format(description_evaluate_total / evas.size()));
								service_evaluate = CommUtil.null2Double(df.format(service_evaluate_total / evas.size()));
								ship_evaluate = CommUtil.null2Double(df.format(ship_evaluate_total / evas.size()));
								params.clear();
								params.put("user_id", obj.getEva_user_id());
								List<StorePoint> sps = this.storePointService
										.query("select obj from StorePoint obj where obj.user.id=:user_id", params, -1, -1);
								StorePoint point = null;
								if (sps.size() > 0) {
									point = sps.get(0);
								} else {
									point = new StorePoint();
								}
								point.setAddTime(new Date());
								point.setUser(sp_user);
								point.setDescription_evaluate(
										BigDecimal.valueOf(description_evaluate > 5 ? 5 : description_evaluate));
								point.setService_evaluate(BigDecimal.valueOf(service_evaluate > 5 ? 5 : service_evaluate));
								point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate > 5 ? 5 : ship_evaluate));
								if (sps.size() > 0) {
									this.storePointService.update(point);
								} else {
									this.storePointService.save(point);
								}
								// 增加用户积分和消费金额
								user.setIntegral(user.getIntegral() + this.configService.getSysConfig().getIndentComment());
								user.setUser_goods_fee(
										BigDecimal.valueOf(CommUtil.add(user.getUser_goods_fee(), obj.getTotalPrice())));
								this.userService.update(user);
							}
						}else{
							 result.set(ErrorEnum.REQUEST_ERROR).setMsg("订单状态为："+OrderStatus.getDescr(obj.getOrder_status())+"的订单不能评价！");
						}
						if (obj.getOrder_form() == 0) {
							Store store = this.storeService.getObjById(CommUtil.null2Long(obj.getStore_id()));
							Map map = new HashMap();
							map.put("seller_id", store.getUser().getId().toString());
							map.put("order_id", obj.getId().toString());
							this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_toseller_evaluate_ok_notify",
									store.getUser().getEmail(), map, null, CommUtil.null2String(store.getId()));
						}
					}
				}
		    }else{
		    	 result.set(ErrorEnum.REQUEST_ERROR).setMsg("传入订单id为空");
		    }
	   }catch(Exception e){
			 result.set(ErrorEnum.SYSTEM_ERROR);
	         logger.error("[com.iskyshop.smilife.order.saveOrderDiscuss] 订单评价接口异常:"+e);
	         e.printStackTrace();
	   }
	   return result;
   }
}
