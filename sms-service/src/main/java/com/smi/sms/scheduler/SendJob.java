package com.smi.sms.scheduler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.sms.common.FinalValue;
import com.smi.sms.common.SmsParamsInit;
import com.smi.sms.common.SmsQueue;
import com.smi.sms.model.SmsHistory;
import com.smi.sms.service.ISmsHistoryService;
import com.smi.tools.exception.HttpException;
import com.smi.tools.http.HttpKit;
import com.smi.tools.kits.JsonKit;
import com.smi.tools.kits.MD5Kit;
import com.smi.tools.kits.StrKit;
import com.smi.tools.kits.XmlKit;
import com.smi.tools.lang.DateTime;

public class SendJob {

	@Autowired
	ISmsHistoryService smsHistroryService;

	private Logger logger = Logger.getLogger(this.getClass());

	public void work() {

 		logger.info("date: " + new Date().getTime());
		//SmsQueue.printQueue();
		SmsHistory sms = SmsQueue.pollElement();
		if (null != sms) {
			logger.info(JsonKit.toJsonString(sms));
			sendMsg(sms);
		}
		
	}

	
	@SuppressWarnings("unused")
	public void sendMsg(SmsHistory sms) {
		synchronized (SendJob.class) {
			if(StrKit.isBlank(sms.getCompanyType())){
				// TODO 获取高峰期相关配置，计算当前使用的通道
				String smsPeakPeriods = SmsParamsInit.smsPeakPeriods;// 高峰期时间段
				String[] smsPeakPeriodsArrays = smsPeakPeriods.split(",");
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				int hours =cal.get(Calendar.HOUR_OF_DAY);
				
				boolean b = false;
				for (int i = 0; i < smsPeakPeriodsArrays.length; i++) {
					if (smsPeakPeriodsArrays[i].equals(String.valueOf(hours))) {
						b = true;
						break;
					}
				}
				if (b) {// 处于高峰期
					logger.info("当前处于" + hours + "小时，使用高峰期通道：" + SmsParamsInit.smsPeakPeriodsCompanyType);
					sms.setCompanyType(SmsParamsInit.smsPeakPeriodsCompanyType);
				} else {// 处于非高峰期
					logger.info("当前处于" + hours + "小时，使用非高峰期通道：" + SmsParamsInit.smsOffPeakPeriodsCompanyType);
					sms.setCompanyType(SmsParamsInit.smsOffPeakPeriodsCompanyType);
				}
			}
			if (FinalValue.COMPANY_TYPE_MW.equals(sms.getCompanyType())) { // 梦网通道
				invokeMw(sms);
			} else if (FinalValue.COMPANY_TYPE_WJ.equals(sms.getCompanyType())) {
				invokeWj(sms);
			}
		}

	}

	/**
	 * 调用梦网的短信接口
	 * 
	 * @param sms
	 */
	private void invokeMw(SmsHistory sms) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", SmsParamsInit.getMwAccountInfoMap().get(sms.getAccountType()).getAccount());
		params.put("password", SmsParamsInit.getMwAccountInfoMap().get(sms.getAccountType()).getPassword());
		params.put("pszMobis", sms.getPhoneNo());
		params.put("pszMsg", sms.getContent());
		params.put("iMobiCount", sms.getMobiCount());
		params.put("pszSubPort", "*");

		sms.setSendTime(new DateTime());
		try {
			String rtnStrXml = HttpKit.post(SmsParamsInit.getMwAccountInfoMap().get(sms.getAccountType()).getBaseUrl(),
					params);
			
			logger.info(sms.getPhoneNo() + "：短信发送结果1： " + rtnStrXml);

			String code = XmlKit.getElementValue(XmlKit.parserXml(rtnStrXml).getRootElement()).trim();
			logger.info(sms.getPhoneNo() + "：Code： " + code);
			logger.info(sms.getPhoneNo() + "：短信发送结果词典：" + SmsParamsInit.getMwReturnCodeMap().get(code));
			if (code.length() <= 7) {
				sms.setStatusCode(code);
				sms.setNote(SmsParamsInit.getMwReturnCodeMap().get(code));
			} else {
				sms.setStatusCode(FinalValue.SEND_STATUS_SUECCESS);
				sms.setNote("发送成功");
			}
		}catch(HttpException e){
			e.printStackTrace();
			logger.error(sms.getPhoneNo() + "发送短信失败： " + e);
			sms.setStatusCode(FinalValue.SEND_STATUS_FAIL);
			sms.setNote(e.toString());

			// 梦网接口调用异常时切换到网景通道
			logger.info(sms.getPhoneNo() + "发送短信失败, 自动切换到网景通道...");
			sms.setCompanyType(FinalValue.COMPANY_TYPE_WJ);
			invokeWj(sms);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(sms.getPhoneNo() + "发送短信失败： " + e);
			sms.setStatusCode(FinalValue.SEND_STATUS_FAIL);
			sms.setNote(e.toString());

			// 梦网接口调用异常时切换到网景通道
			logger.info(sms.getPhoneNo() + "发送短信失败, 自动切换到网景通道...");
			sms.setCompanyType(FinalValue.COMPANY_TYPE_WJ);
			invokeWj(sms);
		} finally {
			sms.setSendTime(new Date());
			smsHistroryService.updateSendStatus(sms);
		}
	}

	/**
	 * 调用网景的短信接口
	 * 
	 * @param sms
	 * @throws UnsupportedEncodingException
	 */
	private void invokeWj(SmsHistory sms) {
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			params.put("cust_code", SmsParamsInit.getWjAccountInfoMap().get(sms.getAccountType()).getAccount());// 用户账户
			// params.put("sp_code", "1069065177364");//长号码,选填
			params.put("destMobiles", sms.getPhoneNo());
			params.put("content", URLEncoder.encode(sms.getContent(), "utf-8"));// 短信内容
			params.put(
					"sign",
					MD5Kit.getMD5String(URLEncoder.encode(sms.getContent(), "utf-8")
							+ SmsParamsInit.getWjAccountInfoMap().get(sms.getAccountType()).getPassword()));// 签名

			String rtn = HttpKit.post(SmsParamsInit.getWjAccountInfoMap().get(sms.getAccountType()).getBaseUrl(),
					HttpKit.toParams(params));
			rtn = URLDecoder.decode(rtn);
			logger.info(sms.getPhoneNo() + "：短信发送结果： " + rtn);
			Map<String, Object> returnMap = decodeMsg(rtn);
			if ((boolean) returnMap.get("result")) {
				sms.setStatusCode(FinalValue.SEND_STATUS_SUECCESS);
			} else {
				sms.setStatusCode(FinalValue.SEND_STATUS_FAIL);
			}
			sms.setNote(JsonKit.toJsonString(returnMap));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}catch(HttpException e){
			e.printStackTrace();
			logger.error(sms.getPhoneNo() + "发送短信失败： " + e);
			sms.setStatusCode(FinalValue.SEND_STATUS_FAIL);
			sms.setNote(e.toString());

			// 网景接口调用异常时切换到梦网通道
			logger.info(sms.getPhoneNo() + "发送短信失败, 自动切换到梦网通道...");
			sms.setCompanyType(FinalValue.COMPANY_TYPE_MW);
			invokeMw(sms);
		}catch (IOException e) {
			e.printStackTrace();
			logger.error(sms.getPhoneNo() + "发送短信失败： " + e);
			sms.setStatusCode(FinalValue.SEND_STATUS_FAIL);
			sms.setNote(e.toString());

			// 网景接口调用异常时切换到梦网通道
			logger.info(sms.getPhoneNo() + "发送短信失败, 自动切换到梦网通道...");
			sms.setCompanyType(FinalValue.COMPANY_TYPE_MW);
			invokeMw(sms);
		} finally {
			sms.setSendTime(new Date());
			smsHistroryService.updateSendStatus(sms);
		}
	}

	/**
	 * 网景返回状态码解码
	 * @param msg
	 * @return
	 */
	public Map<String, Object> decodeMsg(String msg) {
		String[] strs = msg.split("\n");
		Map<String, Object> returnMap = new HashMap<String, Object>();
		String codeMsg = strs[0];
		String result = codeMsg.substring(0, codeMsg.indexOf(":"));
		String message = codeMsg.substring(codeMsg.lastIndexOf(":") + 1, codeMsg.length());
		if ("SUCCESS".equals(result)) {
			returnMap.put("result", true);
		} else {
			returnMap.put("result", false);
		}
		returnMap.put("message", message);
		Map<String, Object> detailMap = new HashMap<String, Object>();
		for (int i = 1; i < strs.length; i++) {
			String tmp = strs[i];
			String[] tmps = tmp.split(":");
			String phone = tmps[0];
			String msgId = tmps[1];
			String code = tmps[2];
			detailMap.put(phone, msgId + ":" + SmsParamsInit.getWjReturnCodeMap().get(code));
		}
		returnMap.put("details", detailMap);
		return returnMap;
	}
}
