package com.iskyshop.bcp.service;

import java.util.List;

import com.iskyshop.foundation.domain.GoldRecord;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.User;
import com.smilife.bcp.dto.request.Commodity;
import com.smilife.bcp.dto.request.CommodityCancle;
import com.smilife.bcp.dto.response.ResultDTO;
/**
 * 计费service
 * @author liz
 * 2015-9-24
 */
public interface IFeeManageservice {
	
	
	/**
	 * 支付（购买商品支付）
	 * @param user
	 * @param order
	 * @param order_total_price
	 * @return
	 */
	public ResultDTO payment(User user,OrderForm order,double order_total_price);
	
	/**
	 * 支付（兑换金币支付）
	 * @param user
	 * @param order
	 * @param order_total_price
	 * @return
	 */
	public ResultDTO payment(User user,GoldRecord obj);
	
	/**
	 * 支付取消（商品退款）
	 * @param order
	 * @param rgl
	 * @return
	 */
	public ResultDTO orderCancle(OrderForm order,ReturnGoodsLog rgl,String amount);
	
	/**
	 * 支付取消（团购退款）
	 * @param order
	 * @param rgl
	 * @return
	 */
	public ResultDTO orderCancle(OrderForm order,GroupInfo gi,String amount);
	
	/**
	 * 解析订单商品及运费为commodity
	 * 
	 * @param order
	 * @return
	 */
	public List<Commodity> getOrderCommodities(OrderForm order);
	
	/**
	 * 设置订单取消报文
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年4月20日 下午4:01:23
	 * @param commoditys
	 * @param order
	 * @param amount
	 */
	public double assembleDatagramData(List<CommodityCancle> commoditys, OrderForm order, String amount);
	
	/**
	 * 构造商品CommodityCancle
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年4月22日 下午10:06:37
	 * @param order
	 * @param commoditys
	 * @param gi
	 * @param amount
	 */
	public void buildGroupInfoCommodity(OrderForm order,List<CommodityCancle> commoditys,GroupInfo gi,String amount);
	
	/**
	 * 构造商品CommodityCancle
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年4月22日 下午10:06:21
	 * @param commoditys
	 * @param rgl
	 * @param amount
	 */
	public void buildGoodsCommodity(List<CommodityCancle> commoditys,ReturnGoodsLog rgl,String amount);
	
	/**
	 * 支付取消（订单退款）
	 * @param order
	 * @param rgl
	 * @return
	 */
	public ResultDTO orderCancle(OrderForm order,String amount);

}
