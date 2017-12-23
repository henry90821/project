package com.iskyshop.smilife.payCenter;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.iskyshop.core.annotation.Token;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.smilife.common.Constants;
import com.iskyshop.smilife.common.Result;

/**
 * 支付中心接口
 * 
 * @author chuzhisheng
 * @version 1.0
 * @date 2016年4月6日 上午11:28:37
 */
@Controller
@RequestMapping(value = "/api/app")
public class PayCenterAction {
	/** 日志 */
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IpayCenterService payCenterService;
	/** 回调返回 */
	public static final String SUCCESS = "success";

	/**
	 * pc和H5支付下单接口
	 * 
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年4月6日 下午1:29:02
	 * @param request
	 * @param user
	 *            下单用户
	 * @param orderId
	 *            支付订单号
	 * @param openId
	 *            微信公众号支付时必填
	 * @param channel
	 *            支付渠道APP、WX、WEB、WAP
	 * @param orderType
	 *            BUY_TICKET("购票", "1"),SHOPPING("购物", "2"),CHARGE("充值", "3"),INTEGRAL("积分兑换","4")
	 * @return
	 */
	@RequestMapping(value = "/mall2301PlaceOrder.htm", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall2301PlaceOrder(HttpServletRequest request, User user, String orderId, String openId, String channel,
			String orderType, String title, String returnUrl) {
		return payCenterService.placeOrder(user, orderId, openId, channel, orderType, title, returnUrl);
	}

	/**
	 * 
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年4月6日 下午5:54:06
	 * @param request
	 * @param user
	 *            下单用户
	 * @param orderId
	 *            支付订单号
	 * @param payType
	 *            支付方式： (必填)WX(微信支付，均可使用)ALI(支付宝支付，均可使用)、YE(余额支付，购票、购物使用)、SBH(随变花)
	 * @param payPwd
	 *            支付密码
	 * @param orderType
	 *            BUY_TICKET("购票", "1"),SHOPPING("购物", "2"),CHARGE("充值", "3"),INTEGRAL("积分兑换","4");
	 * @param instNumber
	 *            随变花分期数(随变花支付必填)
	 * @return
	 */
	@RequestMapping(value = "/mall2302AppPlaceOrder.htm", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall2302AppPlaceOrder(HttpServletRequest request, User user, String orderId, String payType,
			String payPwd, String orderType, String title, Integer instNumber) {
		return payCenterService.appPlaceOrder(user, orderId, payType, payPwd, orderType, title, instNumber);
	}

	/**
	 * 退款接口
	 * 
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年4月7日 下午1:36:40
	 * @param refundFee
	 *            退款金额
	 * @param refundNo
	 *            退款单号，向支付中心下退款单时生成
	 * @param originalNo
	 *            原订单号
	 * @param orderType
	 *            0:商品退款，1:消费码退款，2:订单退款
	 * @param refundType
	 *            1:部分退 2：全部退
	 * @param refundId
	 *            如果是商品退款传ReturnGoodsLog的主键；如果是团购消费码就传GroupInfo的主键id ；如果是订单退款，则传待退款订单的主键id
	 * @return
	 */
	@RequestMapping(value = "/mall2303refund.htm", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Object mall2303refund(String refundFee, String originalNo, String returnRemark, String orderType,
			String refundType, String refundId) {
		return payCenterService.refund(refundFee, originalNo, returnRemark, orderType, refundType, refundId);
	}

	/**
	 * 支付回调接口
	 * 
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年4月9日 下午1:37:44
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/mall2304payCallBack.htm", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Object payCallBack(HttpServletRequest request, Integer code, String msg, String errorDetail, String tradeNo,
			String thirdNo, String billNo, String billType, String payType, String channel) {
		Result result = payCenterService.payCallBack(request, code, msg, errorDetail, tradeNo, thirdNo, billNo, billType,
				payType, channel);
		if (Constants.ONE.equals(result.getCode())) {// 处理成功
			logger.info("支付回调成功，返回：" + SUCCESS);
			return SUCCESS;
		}
		return result.getMsg();
	}

	/**
	 * 退款回调接口
	 * 
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年4月9日 下午1:38:41
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/mall2305refundCallBack.htm", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Object refundCallBack(HttpServletRequest request, Integer code, String msg, String errorDetail, String refundNo,
			String tradeNo, String thirdNo, String billNo, String billType) {
		Result result = payCenterService
				.refundCallBack(code, msg, errorDetail, refundNo, tradeNo, thirdNo, billNo, billType);
		if (Constants.ONE.equals(result.getCode())) {// 处理成功
			logger.info("退款回调成功，返回：" + SUCCESS);
			return SUCCESS;
		}
		return result.getMsg();
	}
}
