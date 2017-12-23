package com.iskyshop.foundation.service;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.DeliveryAddress;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;

public interface IOrderFormService {
	/**
	 * 保存一个OrderForm，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(OrderForm instance);
	
	/**
	 * 执行原始sql
	 * @param sql
	 * @return
	 */
	public List executeNativeQuery(String sql);

	/**
	 * 根据一个ID得到OrderForm
	 * 
	 * @param id
	 * @return
	 */
	OrderForm getObjById(Long id);

	/**
	 * 删除一个OrderForm
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除OrderForm
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到OrderForm
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个OrderForm
	 * 
	 * @param id
	 *            需要更新的OrderForm的id
	 * @param dir
	 *            需要更新的OrderForm
	 */
	boolean update(OrderForm instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<OrderForm> query(String query, Map params, int begin, int max);

	/**
	 * 自定义查询语句，可以查询指定字段并返回，如select id from OrderForm obj where xxxx 返回id集合
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List queryFromOrderForm(String query, Map params, int begin, int max);

	/**
	 * 校验用户是否已购买过指定的秒杀商品
	 * @param userId
	 * @param goodsId 秒杀商品id
	 * @return
	 */
	Boolean validateSeckillOrderForm(Long userId, Long goodsId);


	/**
	 * 批量保存订单
	 * 
	 * @Title: saveOrderForm
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param orderForms 参数
	 * @return void 返回类型
	 * @throws
	 */
	void saveOrderForm(List<Map<String, Object>> orderForms);

	/**
	 * 根据
	 * 
	 * @Title: getObjByProperty
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param construct
	 * @param @param propertyName
	 * @param @param value
	 * @param @return 参数
	 * @return User 返回类型	
	 * @throws
	 */
	OrderForm getObjByProperty(String construct, String propertyName, String value);

	/**
	 * 根据订单主键ID值获取待同步的订单数据集合
	 * 
	 * @param id
	 *            {@link OrderForm}中的主键ID值
	 * @return 返回待同步的订单数据对象集合，如果没有需要同步的订单则返回的集合列表size为0，返回的结果集不会为Null值
	 */
	List<OrderForm> getUnSynchronizeOrders(Long id);


	/**
	 * 确认购物车
	 * @param request
	 * @param response
	 * @param gcs 购物车id集
	 * @param giftids 赠品id集
	 * @param addr_id 选中的收货地址
	 * @param isWap
	 * @return
	 * @throws Exception
	 */
	public Map goodsCart2(HttpServletRequest request, HttpServletResponse response, String gcs, String giftids, 
			Long addr_id, Boolean isWap) throws Exception;



	/**
	 * 提交订单
	 * @param request
	 * @param user
	 * @param gift_ids 赠品id集
	 * @param addr 收货地址
	 * @param gcIds 购物车id集
	 * @param delivery_time 配送时间
	 * @param deliveryAddr 自提点地址
	 * @param payType 支付方式
	 * @param xmStore 到店自提门店
	 * @param isWap
	 * @param giftCartMapping
	 * @param hxGoods
	 * @param transports
	 * @param storeIds
	 * @param storeCart
	 * @return
	 * @throws Exception
	 */
	public Map goodsCart3(HttpServletRequest request, User user,String[] gift_ids,
			Address addr, String[] gcIds, String delivery_time, DeliveryAddress deliveryAddr, 
			String payType, ShipAddress xmStore, Boolean isWap, Map<Object, Map<Long, 
			Set<Long>>> giftCartMapping, Map<Object, Map> hxGoods, Map<Object, Map> transports, 
			Set<Object> storeIds, Map<Object, Set<Long>> storeCart) throws Exception;


	
	/**
	 * 计算用户在店铺store_id中购买的商品发到收货地址addr_id所支持的运送方式
	 * @param request
	 * @param response
	 * @param store_id
	 * @param addr_id
	 * @return
	 */
	public List<SysMap> calStoreShipTransports(HttpServletRequest request,
			HttpServletResponse response, String store_id, String addr_id);


	/**
	 * 将店铺中的原始订单按商品来源再进行拆单
	 * 
	 * @param request
	 * @param sid
	 *            店铺id，可能为Long类型，也可能为String类型的"self"
	 * @param store
	 *            若sid为“self”，则此参数为null，否则为一个Store类型的对象
	 * @param buyer
	 *            当前的用户
	 * @param storeOf
	 *            当前店铺的原始订单(可能会被修改)
	 * @param carts
	 *            用户在当前店铺中所购买的商品对应的购物车
	 * @param isSelfPickup
	 *            是否为到店自提
	 * @param hxGoodsDispached
	 *            当前店铺的海信商品选择门店的情况(可能为一个size()==0的列表)
	 * @param receiverAddr
	 *            用户的收货地址
	 * @param enoughReduceInfo
	 * 			  
	 * @param couponInfo
	 * 
	 * @return
	 */
	public List<Map<String, Object>> refineOrderForm(HttpServletRequest request, Object sid, Store store, User buyer,
			OrderForm storeOf, List<GoodsCart> carts, boolean isSelfPickup, List<Map> hxGoodsDispached, Address receiverAddr, String enoughReduceInfo,String couponInfo);


	/**
	 * 获取非海信订单退款总额(若同一商品存在于不同子订单中,该接口不可用)
	 * @param orderId 订单号
	 * @return
	 */
	public Map<String,Double> getOrderRefundAmount(String orderId);

	public boolean modifyEnoughReduceInfo(OrderForm orderForm);
	public boolean modifyFreightInfo(OrderForm orderForm);
	public boolean modifyOrderStatus(String orderId);
	public boolean modifyActivityAmount(OrderForm orderForm);
	public BigDecimal getPayoffAmount(OrderForm orderForm);//订单总结算金额
	
	public Map getDiscountAmounts(OrderForm orderForm);

	public String getOriginalNo(OrderForm of);
	public String getRefundType(OrderForm of, String type);
	public Object orderPay(HttpServletRequest request, HttpServletResponse response, User user, Object order, String channel, String orderType, String returnUrl);
}
