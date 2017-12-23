package com.iskyshop.pay.tools;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.pay.alipay.services.AlipayRet;
import com.iskyshop.pay.chinapay.ChinaPayRet;
import com.iskyshop.pay.weixi.refund.RefundResBean;
import com.iskyshop.pay.weixi.refund.WeixiRet;


@Component
public class RetPayTools {
	private static final Logger LOGGER = Logger.getLogger(RetPayTools.class);
	
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private IOrderFormService orderformService;
	
	/***
	 * 银联
	 * @param url
	 * @param orderId
	 * @param priv
	 * @return
	 */
	public Map<String,String> retPay(String url,String orderId,String priv,String pageAmount,String main_order_id){
		Map<String,String> infoMap=new HashMap<String,String>();
		OrderForm orderForm = this.orderFormService.getObjById(CommUtil.null2Long(orderId));
		OrderForm mainOrderForm = this.orderFormService.getObjById(CommUtil.null2Long(main_order_id));
		String total_fee=pageAmount;
		String out_trade_no=orderForm.getOut_order_id();
		if("".equals(out_trade_no) || out_trade_no==null){
			out_trade_no=mainOrderForm.getOut_order_id();
		}
		String subtansdate=orderForm.getSubtansdate();
		if("".equals(subtansdate) || subtansdate==null){
			subtansdate=mainOrderForm.getSubtansdate();
			if(subtansdate == null || "".equals(subtansdate)){
				Date addTime=mainOrderForm.getAddTime();
				SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
				subtansdate=simpleDateFormat.format(addTime); 
			}
		}
		Payment payment=orderForm.getPayment();
		
		infoMap=ChinaPayRet.getRetPayInfo(url, payment, out_trade_no, total_fee,priv,subtansdate);
		
		return infoMap;
	}
	/***
	 * 支付宝
	 * @param url
	 * @param paymentId
	 * @return
	 */
	public String getAlipayRetForm(String url,String orderId,String objId,String retReason,String pageAmount,String giId) throws Exception{
		String alipayForm="";
		Date date=new Date();
		OrderForm orderForm = this.orderFormService.getObjById(CommUtil.null2Long(orderId));
		String total_fee=pageAmount;
		String batch_no = CommUtil.formatTime("yyyyMMddHHmmssSSS", date);
		if("".equals(objId) || objId==null){
			batch_no=batch_no+"1";
			GroupInfo groupInfo = this.groupinfoService.getObjById(CommUtil.null2Long(giId));
			groupInfo.setAlipayBatchNo(batch_no);
			this.groupinfoService.update(groupInfo);
		}else{
			batch_no=batch_no+"0";
			ReturnGoodsLog returnGoodsLog=this.returnGoodsLogService.getObjById(CommUtil.null2Long(objId));
			returnGoodsLog.setAlipayBatchNo(batch_no);
			this.returnGoodsLogService.update(returnGoodsLog);
		}
		
		alipayForm=AlipayRet.alipayForm(orderForm,batch_no,total_fee,url,retReason);
		return alipayForm;
	}
	
	/***
	 * 支付宝(订单退款)
	 * @param url
	 * @param paymentId
	 * @return
	 */
	public String getAlipayRetForm(String url,String orderId,String retReason,String pageAmount) throws Exception{
		String alipayForm="";
		Date date=new Date();
		OrderForm orderForm = this.orderFormService.getObjById(CommUtil.null2Long(orderId));
		//2：表示订单退款，支付宝回调时通过此标示处分退款类型
		String batch_no = CommUtil.formatTime("yyyyMMddHHmmssSSS", date)+"2";
		//退款原因
		orderForm.setRefund_cause(retReason);
		//退款批准号
		orderForm.setAlipayBatchNo(batch_no);
		//保存退款信息
		this.orderFormService.update(orderForm);
		alipayForm=AlipayRet.alipayForm(orderForm,batch_no,pageAmount,url,retReason);
		return alipayForm;
	}
	/***
	 * 微信
	 * @param main_order_id  为订单的id字段的值（不是订单的order_id字段的值）
	 * @param orderId 为订单的id字段的值（不是订单的order_id字段的值）
	 * @param pageAmount
	 * @return
	 */
	public RefundResBean weixiRetPay(String main_order_id,String orderId,String pageAmount,String encode){
		RefundResBean refundResBean=new RefundResBean();
		OrderForm orderForm = this.orderFormService.getObjById(CommUtil.null2Long(orderId));
		OrderForm mainOrderForm = this.orderFormService.getObjById(CommUtil.null2Long(main_order_id));
		double order_total_price = this.orderFormTools.query_order_price(CommUtil.null2String(mainOrderForm.getId()));
		String total_fee=String.valueOf(order_total_price);
		String refund_fee=pageAmount;
		String out_trade_no=orderForm.getOut_order_id();
		if("".equals(out_trade_no) || out_trade_no==null){
			out_trade_no=mainOrderForm.getOut_order_id();
		}
		refundResBean=WeixiRet.getRetPayInfo(orderForm, out_trade_no, total_fee,refund_fee,encode);
		return refundResBean;
	}
	
}
