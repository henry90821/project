package com.smi.pay.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smi.pay.common.CommonUtil;
import com.smi.pay.common.LogUtil;
import com.smi.pay.dao.AppInfoDao;
import com.smi.pay.dao.RefundDao;
import com.smi.pay.dao.RefundLogDao;
import com.smi.pay.model.Order;
import com.smi.pay.model.OrderReturn;
import com.smi.pay.model.Refund;
import com.smi.pay.model.RefundLog;
import com.smi.pay.sdk.wx.HttpClientConnectionManager;
import com.smi.pay.service.PayService;
import com.smi.pay.service.RefundService;

@Service("RefundService")
@Transactional(readOnly = false)
public class RefundServiceImpl implements RefundService{

	@Autowired
	private RefundLogDao refundLogDao;
	@Autowired
	private RefundDao refundDao;
	@Autowired
	private AppInfoDao appInfoDao;
	@Autowired
	private PayService payService;
	@Override
	public OrderReturn check(RefundLog refundLog) {
		//选将业务系统传过来的数据存入log表，再做合法性验效果
		refundLog.setCreateTime(new Date());
		saveRefundLog(refundLog);
		OrderReturn or=new OrderReturn();
		or.setCode(0);
		or.setMsg("OK"); 
		 if(StringUtils.isBlank(refundLog.getCustId()))
		{
			or.setCode(4);
			or.setMsg("PARAM_INVALID");
			or.setErrDetail("custId不能为空");
		}
		else if(StringUtils.isBlank(refundLog.getRefundNo()))
		{
				or.setCode(4);
				or.setMsg("PARAM_INVALID");
				or.setErrDetail("refundNo不能为空");
		}
		else if(StringUtils.isBlank(refundLog.getBillNo()))
		{
			or.setCode(4);
			or.setMsg("PARAM_INVALID");
			or.setErrDetail("billNo不能为空");
		}
		else if(refundLog.getRefundFee()<=0)
		{
			or.setCode(4);
			or.setMsg("PARAM_INVALID");
			or.setErrDetail("refundFee有误");
		}
		 Order orginOrder = payService.getOrderByBillNo(refundLog.getAppcode()+refundLog.getBillNo());
		 if(orginOrder==null)
		 {
			 or.setCode(1); 
			 or.setMsg("NO_SUCH_BILL");
			 or.setErrDetail("没有该订单");
		 }
		 else
		 {
			 if(!"0".equals(orginOrder.getCallBackStatus()))
			 {
				 or.setCode(1); 
				 or.setMsg("BILL_UNSUCCESS");
				 or.setErrDetail("该订单没有支付成功");
			 }
			 else
			 {
				 int refundCount=refundDao.refundCount(orginOrder.getBillNo());
				 //本次退款金额超过剩余可退款金额
				 if(refundCount+refundLog.getRefundFee()>orginOrder.getTotalFee())
				 {
					 or.setCode(1); 
					 or.setMsg("REFUND_AMOUNT_TOO_LARGE");
					 or.setErrDetail("提交的退款金额超出可退额度");
				 }
			 }
		 }
		 Refund historRrefund=refundDao.getByRefundNo(refundLog.refundNo+refundLog.getAppcode());
		 if(historRrefund!=null && "0".equals(historRrefund.status))
		 {
			 or.setCode(5);
			 or.setMsg("REFNUD HAS BE DONE");
			 or.setErrDetail("该退款单已经退款成功,不能重复提交");
		 }
		return or;
	}
	@Override
	public void saveRefundLog(RefundLog refundLog) {
		refundLogDao.insert(refundLog);
	}
	@Override
	public RefundLog getRefundLog(Integer id) {
		 
		return refundLogDao.load(id);
	}
	@Override
	public void updateRefundLog(RefundLog refundLog)
	{
		refundLogDao.update(refundLog);
	}
	
	@Override
	public Refund getRefund(Integer id) {
		 
		return refundDao.load(id);
	}
	/**
	 * 临时单生成正式单
	 */
	@Override
	public synchronized Refund  saveRefund(RefundLog refundLog) {
		 Refund historRrefund=refundDao.getByRefundNo(refundLog.refundNo+refundLog.getAppcode());
		  if(historRrefund!=null)
		  {
			  refundDao.deleteByPrimaryKey(historRrefund.getId());
		  }
		  Refund refund=new Refund();
		  refund.setAppcode(refundLog.getAppcode());
		  refund.setReqno(refundLog.getReqno());
		  refund.setSign(refundLog.getSign());
		  refund.setCustId(refundLog.getCustId());
		  refund.setRefundFee(refundLog.getRefundFee());
		  //支付宝格式要求以年月日编号打头，组装退款单号时，code放在后面
		  refund.setRefundNo(refundLog.getRefundNo()+refundLog.getAppcode());
		  refund.setBillNo(refundLog.getAppcode()+refundLog.getBillNo());
		  refund.setMemo(refundLog.getMemo());
		  refund.setPackageValue(refundLog.getPackageValue());
		  refund.setCreateTime(new Date());
		  refundDao.insert(refund);
		return refund;
	}
	@Override
	public Refund getRefundByRefundBill(String refundBill) {
		return refundDao.getByRefundNo(refundBill);
	}
	@Override
	public void updateRefund(Refund refund) {
		refundDao.update(refund);
	}
	@Override
	@Async
	public void refundNotify(final Refund refund) {
		if("0".equals(refund.noticeStatus))
		{
			return;
		}
		final String url=appInfoDao.getByCodeRefund(refund.getAppcode()).getCallBackUrl();
		final Order orginOrder = payService.getOrderByBillNo(refund.getBillNo());
	 
		
		Thread t=new Thread()
		{
			boolean flag=false;
			int num=0;
			public void run()
			{
				 DefaultHttpClient client = new DefaultHttpClient();
				 client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,20000);//连接时间
				 client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,20000);//数据传输时间
				 client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
				  while(!flag && num<5)
				  {
				    HttpPost httpost= HttpClientConnectionManager.getPostMethod(url); 
				    String  returnStr="";
			     try {
			    	 if(num>0)
			    	 {
			    		 Thread.sleep(10000*num);
			    	 }
			    	 num++;
			    	 List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
			    	 LogUtil.writeLog("------Notify url:"+url+"-------Notify--billNo是:"+refund.getBillNo().substring(5)+" 退款单号："+refund.getRefundNo().substring(0,refund.getRefundNo().length()-5));
				        nvps.add(new BasicNameValuePair("code",refund.getCallBackStatus()));  
				        nvps.add(new BasicNameValuePair("msg", refund.getCallBackMemo()));  
				        nvps.add(new BasicNameValuePair("errorDetail", refund.getCallBackMemo()));  
				        nvps.add(new BasicNameValuePair("tradeNo", refund.getBillNo()));  
				        nvps.add(new BasicNameValuePair("thirdNo", refund.getPaySn()));  
				        nvps.add(new BasicNameValuePair("billNo",refund.getBillNo().substring(5)));  
				        nvps.add(new BasicNameValuePair("refundNo", refund.getRefundNo().substring(0,refund.getRefundNo().length()-5)));
				        nvps.add(new BasicNameValuePair("channel", orginOrder.getChannel())); 
				        nvps.add(new BasicNameValuePair("payType", orginOrder.getPayType()));
				       final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(nvps);  
					 httpost.setEntity(entity);
					 HttpResponse response = client.execute(httpost);
					 returnStr = EntityUtils.toString(response.getEntity(), "UTF-8");
					 LogUtil.writeLog("-------------refundNotify--returnStr是:"+returnStr+"--BILLNO:"+refund.getBillNo().substring(5));
					 if("success".equals(returnStr))
				     {
				    	 refund.setNoticeStatus("0");
				    	 refund.setNoticeReturn(returnStr);
				    	 refund.setLastNoticeTime(new Date());
				    	 refund.setNoticeTimes((refund.getNoticeTimes()==null?0:refund.getNoticeTimes())+1);
				    	 updateRefund(refund);
				    	 flag=true;
				     }
				     else
				     {
				    	 if(returnStr.length()>500)
				    	 {
				    		 returnStr=returnStr.substring(0,500);
				    	 }
				    	 refund.setNoticeStatus("1");
				    	 refund.setNoticeReturn(returnStr);
				    	 refund.setLastNoticeTime(new Date());
				    	 refund.setNoticeTimes((refund.getNoticeTimes()==null?0:refund.getNoticeTimes())+1);
				    	 updateRefund(refund);
				     }
			     }
			     catch (Exception e) {
						e.printStackTrace();
						refund.setNoticeStatus("1");
						refund.setLastNoticeTime(new Date());
						refund.setNoticeReturn("通知业务系统出现错误:"+CommonUtil.getExceptionStr(e));
						refund.setNoticeTimes((refund.getNoticeTimes()==null?0:refund.getNoticeTimes())+1);
						updateRefund(refund);
					}
			     
				  }
				 
			}
		};
		
		t.start();
	  
    
	}
	
	
	
	/**
	 * 查询下单日志
	 */
	public List queryRefundLog(Map params)
	{
	  return refundLogDao.getAll(params);
	}
	
	/**
	 * 查询退款日志
	 */
	public List queryRefund(Map params){
		return refundDao.getAll(params);
	}
	 
 

}
