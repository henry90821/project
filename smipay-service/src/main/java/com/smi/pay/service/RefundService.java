package com.smi.pay.service;

import java.util.List;
import java.util.Map;

import com.smi.pay.model.OrderReturn;
import com.smi.pay.model.Refund;
import com.smi.pay.model.RefundLog;


public interface RefundService {
	 /**
	  *  检查退款提交信息是否合法 
	  * @return
	  */
	public OrderReturn check(RefundLog refundLog);
	
	/**
	 * 保存退款请求日志
	 * @param refundLog
	 */
	public void saveRefundLog(RefundLog refundLog);
	
	/**
	 * 获取退款记录
	 * @param id
	 * @return
	 */
	public RefundLog getRefundLog(Integer id);
	

	/**
	 * 更新退款记录
	 * @param id
	 * @return
	 */
	public void updateRefundLog(RefundLog refundLog);
	
	/**
	 * 生成正式退款记录
	 * @param refundLog
	 * @return
	 */
	public Refund saveRefund(RefundLog refundLog);
	
	/**
	 * 根据ID查询退款记录
	 * @param refundBill
	 * @return
	 */
	public Refund getRefund(Integer id);
	
	/**
	 * 根据退款单号查询退款记录
	 * @param refundBill
	 * @return
	 */
	public Refund getRefundByRefundBill(String refundBill);
	
	/**
	 * 退款成功后更新退款记录
	 * @param refund
	 */
	public void updateRefund(Refund refund);
	
	/**
	 * 通知业务系统退款成功
	 * @param refund
	 * @return
	 */
	public void refundNotify(Refund refund);
	
	/**
	 * 查询下单日志
	 */
	public List queryRefundLog(Map params);
	
	/**
	 * 查询退款日志
	 */
	public List queryRefund(Map params);
	
}
