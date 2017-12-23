package com.iskyshop.smilife.payCenter;

import javax.servlet.http.HttpServletRequest;

import com.iskyshop.foundation.domain.User;
import com.iskyshop.smilife.common.Result;

/**
 * 支付中心接口
 * 
 */
public interface IpayCenterService {
	
	/**
	 * web和 h5统一支付接口：向支付中心提交订单信息，即所谓的支付下单
	 * 
	 * @param user
	 *            下单用户.对于充值订单，user参数不会被使用
	 * @param orderId
	 *            支付订单号，如购物订单的order_id字段的值，充值订单的pd_sn字段的值，积分兑换订单的igo_order_sn字段的值
	 * @param openId
	 *            微信公众号支付时必填。若不填，则应传空串，而不是null，否则会出现签名错误
	 * @param channel
	 *            支付渠道（不是订单的下单渠道），如：ChannelEnum.APP.name()
	 * @param orderType
	 *            订单类型，如：OrderTypeEnum.SHOPPING.getIndex()
	 * @param title
	 *            下单标题（暂时无用，传空串）
	 * @param returnUrl
	 *            充值成功后，支付中心将跳转到的业务系统页面地址，如支付成功后跳转到业务系统的订单列表页面
	 * @return 
	 * 			若成功，则在返回json字符串中会包含支付中心内置的支付页面的url，如：{"code":0,"msg":"OK","url":"https://xmpay.smi170.com/webpay.do?order_id=44524"}
	 */
	public Result placeOrder(User user, String orderId, String openId, String channel, String orderType, String title, String returnUrl);


	
	/**
	 * APP 支付下单接口
	 * 
	 * @param payType
	 *            支付方式，必填。如：PayTypeEnum.ALI.name()。WX(微信支付，均可使用)、ALI(支付宝支付，均可使用)、YE(余额支付，购票、购物使用)
	 * @param payPwd
	 *            支付密码
	 * @param user
	 *            下单用户
	 * @param orderId
	 *            支付订单号
	 * @param orderType
	 *            订单类型，如：OrderTypeEnum.SHOPPING.getIndex()
	 * @param title
	 *            下单标题（暂时无用，传空串）
	 * @param instNumber
	 *            分期数(随变花支付必填)
	 * @return
	 */
	public Result appPlaceOrder(User user, String orderId, String payType, String payPwd, String orderType, String title, Integer instNumber);



	/**
	 * 退款接口
	 *
	 * @param refundFee
	 *            退款金额
	 * @param refundNo
	 *            退款单号，向支付中心下退款单时生成
	 * @param originalNo
	 *            原订单号，即OrderForm的order_id字段的值
	 * @param refundType
	 *            退款类型，如：RefundTypeEnum.GoodsRefund.getCode()
	 * @param refundMode
	 *            退款方式，部分退和全额退。如：RefundModeEnum.Full.getCode()
	 * @param refundId
	 *            如果是商品退款传ReturnGoodsLog的主键；如果是团购消费码就传GroupInfo的主键id ；如果是订单退款，则传待退款订单的主键id
	 * @return
	 */
	public Result refund(String refundFee, String originalNo, String returnRemark, String refundType, String refundMode, String refundId);

	
	
	/**
	 * 支付回调
	 * 
	 * @param code
	 *            ：0表示成功
	 * @param msg
	 *            ：成功信息
	 * @param errDetail
	 *            ：错误信息
	 * @param tradeNo
	 *            ：支付中心对应的流水号，如：1000420160901001522223270560
	 * @param thirdNo
	 *            ：第三方支付的订单号，如2016090121001004870224570589
	 * @param billNo
	 *            ：当前业务系统中的订单号，如：20160901001522223270560(购物订单的订单号)，pd20160901000717632213599(充值订单的订单号)
	 * @param billType
	 *            ：订单类型，如：OrderTypeEnum.CHARGE（充值订单）、OrderTypeEnum.SHOPPING（购物订单）、OrderTypeEnum.INTEGRAL（积分订单）
	 * @param payType
	 *            ：支付方式，如：PayTypeEnum.YE、PayTypeEnum.ALI、PayTypeEnum.WX、PayTypeEnum.UN、PayTypeEnum.SBH、PayTypeEnum.YP
	 * @param channel
	 *            ：订单的支付渠道（不是订单的下单渠道），如：ChannelEnum.WX、ChannelEnum.APP、ChannelEnum.WEB、ChannelEnum.WAP
	 */
	public Result payCallBack(HttpServletRequest request, Integer code, String msg, String errDetail, String tradeNo,
			String thirdNo, String billNo, String billType, String payType, String channel);

	/**
	 * 退款回调接口
	 * 
	 */
	public Result refundCallBack(Integer code, String msg, String errDetail, String refundNo, String tradeNo,
			String thirdNo, String billNo, String billType);
}
