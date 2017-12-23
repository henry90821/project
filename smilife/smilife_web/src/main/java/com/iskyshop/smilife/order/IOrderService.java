package com.iskyshop.smilife.order;

import javax.servlet.http.HttpServletRequest;

import com.iskyshop.foundation.domain.User;
import com.iskyshop.smilife.common.Result;

/**
 * 订单接口
 * @author chuzhisheng
 * @version 1.0
 * @date 2016年3月22日 下午4:54:42
 */
public interface IOrderService {
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
   public Result getOrders(User user,String status,String currentPage,String pageSize);
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
   public Result getLogisticsInfo(User user,String orderId);
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
   public Result getOrdersDetail(User user,String orderId);
   
   /**
    * 取消订单
	* @author tianbotao
    * @param request
    * @param user
    * @param id
    * @param stateInfo
    * @return
    */
   public Result cancelOrder(HttpServletRequest request, User user, String id, String stateInfo);
	/**
	 * 确认订单
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年4月15日 下午5:53:50
	 * @param request
	 * @param user
	 * @param orderId
	 * @return
	 */
   public Result confirmOrder(HttpServletRequest request, User user,String orderId);
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
   public Result getChildOrdersDetail(User user,String orderId);
   /**
    * 订单评价
    * @author chuzhisheng
    * @version 1.0
    * @date 2016年4月18日 下午2:00:25
    * @param user
    * @param id
    * @return
    */
   public Result saveOrderDiscuss(HttpServletRequest request,User user,String id,String evaluates);
}
