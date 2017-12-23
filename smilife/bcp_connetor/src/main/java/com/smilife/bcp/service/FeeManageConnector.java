package com.smilife.bcp.service;

import com.smilife.bcp.dto.request.OrderCancleReq;
import com.smilife.bcp.dto.request.PaymentReq;
import com.smilife.bcp.dto.response.MemberBalanceResp;
import com.smilife.bcp.dto.response.ResultDTO;

/**
 * bcp计费相关接口
 * 
 * @author liz 2015-9-7
 */
public interface FeeManageConnector {

	/**
	 * 通过BCP调用BILL进行会员余额查询
	 * 
	 * @param custId
	 *            会员Id
	 * @return MemberBalanceResp
	 */
	public MemberBalanceResp queryEnergy(String custId);

	/**
	 * 通过BCP调用BILL进行会员余额充值
	 * 
	 * @param custId
	 *            会员Id
	 * @param rcgAmt
	 *            充值金额
	 * @param tradeNo
	 *            订单Id
	 * @return ResultDTO
	 */
	public ResultDTO recharge(String custId, String rcgAmt, String tradeNo);

	/**
	 * 通过BCP调用BILL进行会员消费支付接口
	 * 
	 * @param paymentReq
	 *            支付请求对象
	 * @return ResultDTO
	 */
	public ResultDTO payment(PaymentReq paymentReq);

	/**
	 * 通过BCP调用BILL进行取消支付接口
	 * 
	 * @param cancleReq
	 *            取消支付请求对象
	 * @return ResultDTO
	 */
	public ResultDTO orderCancle(OrderCancleReq cancleReq);

}
