package com.iskyshop.smilife.order;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.iskyshop.core.annotation.Token;
import com.iskyshop.foundation.domain.User;

/**
 * 订单接口
 * @author chuzhisheng
 * @version 1.0
 * @date 2016年3月22日 下午4:52:38
 */
@Controller
@RequestMapping(value="/api/app")
public class OrderAction {
	@Autowired
	private IOrderService orderService;

   /**
    * 通过类型获取订单列表信息
    *  1、全部：null(不传为全部)、
    *  2、待付款：10、
    *  3、待发货:20、
    *  4、待收货:30、
    *  5、已收货:40
    * @author chuzhisheng
    * @version 1.0
    * @date 2016年3月22日 下午5:08:05
    * @param request
    * @param id
    * @param type
    * @param currPage
    * @param pageSize
    * @return
    */
	@RequestMapping(value="/mall1501GetOrders.htm",method=RequestMethod.POST,produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall0901Charge(HttpServletRequest request,User user,String status,String currentPage,String pageSize) {
		return orderService.getOrders(user,status,currentPage,pageSize);
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
	@RequestMapping(value="/mall1502GetOrdersDetail.htm",method=RequestMethod.POST,produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall1502GetOrdersDetail(HttpServletRequest request,User user,String orderId) {
		return orderService.getOrdersDetail(user,orderId);
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
	@RequestMapping(value="/mall1503GetLogisticsInfo.htm",method=RequestMethod.POST,produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall1503GetLogisticsInfo(HttpServletRequest request,User user,String orderId) {
		return orderService.getLogisticsInfo(user,orderId);
	}
	
	/**
	 * 取消订单
	 * @author tianbotao
	 * stateInfo取消订单原因
	 */
	@RequestMapping(value="/mall1504CancelOrder.htm",method=RequestMethod.POST,produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall1504CancelOrder(HttpServletRequest request, User user) {
		String id = request.getParameter("id");
		String stateInfo = request.getParameter("stateInfo");
		return orderService.cancelOrder(request, user, id, stateInfo);
	}
	
	/**
	 * 确认收货订单
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年4月15日 下午5:53:50
	 * @param request
	 * @param user
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value="/mall1505confirmOrder.htm",method=RequestMethod.POST,produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall1505confirmOrder(HttpServletRequest request, User user,String orderId) {
		return orderService.confirmOrder(request, user,orderId);
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
	@RequestMapping(value="/mall1506GetChildOrderDetail.htm",method=RequestMethod.POST,produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall1502GetChildOrdersDetail(HttpServletRequest request,User user,String orderId) {
		return orderService.getChildOrdersDetail(user,orderId);
	}

	/**
	 * 订单评价
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年4月18日 下午1:58:26
	 * @param request
	 * @param user
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/mall1507OrderDiscussSave.htm", method = RequestMethod.POST ,produces={"application/json"} )
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Token
	public Object mall1312OrderDiscussSave(HttpServletRequest request,User user,String id,String evaluates) {
		return orderService.saveOrderDiscuss(request,user,id,evaluates);
	}
}
