package com.smilife.bcp.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smilife.bcp.dto.request.OrderCancleReq;
import com.smilife.bcp.dto.request.PaymentReq;
import com.smilife.bcp.dto.response.MemberBalanceResp;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.FeeManageConnector;
import com.smilife.bcp.service.common.BusinessHandler;
import com.tydic.eshop.dto.shop.ei.ReqRechargeInfo;
import com.tydic.framework.base.exception.ServiceException;
import com.tydic.framework.util.MathUtil;
import com.tydic.shop.constant.InterfaceConstant;
import com.tydic.shop.dto.BalanceDetail;
import com.tydic.shop.dto.MemberBalanceDTO;
import com.tydic.shop.dto.req.ReqMemberBalanceQuery;
import com.tydic.shop.util.FeeInvokeUtil;

@Service
public class FeeManageConnectorImpl implements FeeManageConnector {

	@Autowired
	private FeeInvokeUtil feeInvokeUtil;
	
	private Logger logger = Logger.getLogger(this.getClass());

	public MemberBalanceResp queryEnergy(String custId) {
		MemberBalanceResp memberBalanceResp = null;
		// 组装请求对象及参数
		ReqMemberBalanceQuery reqMemberBalanceQuery = new ReqMemberBalanceQuery();
		reqMemberBalanceQuery.setQryType("2");// 查询明细
		reqMemberBalanceQuery.setBalType("6");// 所有非通信余额（包括现金、星币和资源）
		reqMemberBalanceQuery.setMemberId(custId);// 会员ID
		// 封装报文
		List<Map<String, Object>> soo = BusinessHandler.handParam(reqMemberBalanceQuery, ReqMemberBalanceQuery.TYPE,
				ReqMemberBalanceQuery.ATT_KEY_NAME);
		try {
			MemberBalanceDTO memberBalanceDTO = feeInvokeUtil.invokeObj(InterfaceConstant.BIC_QUERY_MEMBER_BALANCE, soo,
					"RESP", MemberBalanceDTO.class);
			memberBalanceResp = new MemberBalanceResp();

			// 获取余额明细（现金账本、星币账本、通信账本等）
			List<BalanceDetail> balanceDetails = memberBalanceDTO.getBalanceDetails();
			String cash = "0.00";
			String flCash = "0.00";
			String xingCoin = "0";
			for (BalanceDetail balance : balanceDetails) {
				if ("1".equals(balance.getBalanceTypeId())) {// 现金账本
					cash = balance.getBalance();
				} else if ("8".equals(balance.getBalanceTypeId())) {// 星币
					xingCoin = balance.getBalance();
				} else if ("9".equals(balance.getBalanceTypeId())) {// 返利余额,多个账本相加
					flCash = MathUtil.add2(flCash, balance.getBalance());
				}
			}
			// 封装返回对象
			String totalCash = MathUtil.add2(cash, flCash);
			BigDecimal decimalCash = new BigDecimal(totalCash);
			memberBalanceResp.setCash(decimalCash);
			memberBalanceResp.setXingCoin(Integer.parseInt(xingCoin));
			memberBalanceResp.setXingCoinRate(memberBalanceDTO.getXingCoinRate());
		} catch (ServiceException e) {
			logger.warn(e.getMessage() + "。 custId:" + custId);
		}

		return memberBalanceResp;

	}

	public ResultDTO recharge(String custId, String rcgAmt, String tradeNo) {
		ResultDTO resultDTO = null;
		// 组装请求对象及参数
		ReqRechargeInfo rechargeInfo = new ReqRechargeInfo();
		rechargeInfo.setPayAmount(rcgAmt);
		rechargeInfo.setRequestId(tradeNo);
		rechargeInfo.setBankSerialNbr(tradeNo);
		rechargeInfo.setMemberId(custId);

		// 封装报文
		List<Map<String, Object>> params = BusinessHandler.handParam(rechargeInfo, null, "RECHARGE_INFO");

		try {
			resultDTO = feeInvokeUtil.invokeObj("BIC_MEMBER_RECHARGE", params, "RESP", ResultDTO.class);
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
			resultDTO = new ResultDTO();
			resultDTO.setCode(e.getErrCode());
			resultDTO.setMsg(e.getMessage());
			resultDTO.setResult("1");
		}

		return resultDTO;
	}

	public ResultDTO payment(PaymentReq paymentReq) {
		ResultDTO resultDTO = null;

		// 封装报文
		List<Map<String, Object>> params = BusinessHandler.handParam(paymentReq, PaymentReq.TYPE, PaymentReq.ATT_KEY_NAME);

		try {
			resultDTO = feeInvokeUtil.invokeObj("BIC_CUST_PAYMENT", params, "RESP", ResultDTO.class);
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
			resultDTO = new ResultDTO();
			resultDTO.setCode(e.getErrCode());
			resultDTO.setMsg(e.getMessage());
			resultDTO.setResult("1");
		}

		return resultDTO;
	}

	@Override
	public ResultDTO orderCancle(OrderCancleReq cancleReq) {
		ResultDTO resultDTO = null;

		// 封装报文
		List<Map<String, Object>> params = BusinessHandler.handParam(cancleReq, OrderCancleReq.TYPE, OrderCancleReq.ATT_KEY_NAME);

		try {
			resultDTO = feeInvokeUtil.invokeObj("BIC_CUST_ORDERCANCLE", params, "RESP", ResultDTO.class);
		} catch (ServiceException e) {
			logger.warn(e.getMessage());
			resultDTO = new ResultDTO();
			resultDTO.setCode(e.getErrCode());
			resultDTO.setMsg(e.getMessage());
			resultDTO.setResult("1");
		}

		return resultDTO;
	}
}
