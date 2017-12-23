package com.smi.sms.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.sms.common.FinalValue;
import com.smi.sms.common.SmsParamsInit;
import com.smi.sms.dao.CodeDictMapper;
import com.smi.sms.dao.SmsAccountMapper;
import com.smi.sms.model.CodeDict;
import com.smi.sms.model.SmsAccount;
import com.smi.sms.service.ICodeDictService;

@Service("codeDictService")
public class CodeDictService implements ICodeDictService {

	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	CodeDictMapper codeDictMapper;

	@Autowired
	SmsAccountMapper smsAccountMapper;

	@Override
	public void setDictCache() {
		List<CodeDict> dicts = codeDictMapper.listAll();
		if (null == dicts) {
			logger.error("=============================================================");
			logger.error("================ 加载短信返回状态码字典信息异常，字典为空 =================");
			logger.error("=============================================================");

			throw new RuntimeException("加载短信返回状态码字典信息异常，字典为空");
		} else {
			logger.info("================ 加载字典信息到缓存 =================");
			for (CodeDict dict : dicts) {
				if (dict.getType() == FinalValue.DICT_TYPE_RETURN_CODE_MW) { // 梦网短信返回状态码
					SmsParamsInit.getMwReturnCodeMap().put(dict.getCode(), dict.getDescription());
				} else if (dict.getType() == FinalValue.DICT_TYPE_RETURN_CODE_WJ) { // 网景短信返回状态码
					SmsParamsInit.getWjReturnCodeMap().put(dict.getCode(), dict.getDescription());
				} else if (dict.getType() == FinalValue.DICT_TYPE_CHANNEL) { // 渠道
					SmsParamsInit.getChannelCodeMap().put(dict.getCode(), dict.getDescription());
				}else if (dict.getType() == FinalValue.DICT_TYPE_COMPANY){ //通道
					SmsParamsInit.getCompanyCodeMap().put(dict.getCode(), dict.getDescription());
				}
			}
		}

		List<SmsAccount> accounts = smsAccountMapper.listAll();
		if (null == accounts) {
			logger.error("=============================================================");
			logger.error("==================== 加载短信账号信息字典异常，账号为空 ===================");
			logger.error("=============================================================");
			throw new RuntimeException("加载短信账号信息字典异常，账号为空");
		} else {
			logger.info("================ 加载短信账号信息到缓存 =================");
			for (SmsAccount acc : accounts) {
				if (FinalValue.COMPANY_TYPE_MW.equals(String.valueOf(acc.getCompanyType()))) { // 梦网通道
					SmsParamsInit.getMwAccountInfoMap().put(acc.getAccountType(), acc);
				} else if (FinalValue.COMPANY_TYPE_WJ.equals(String.valueOf(acc.getCompanyType()))) {
					SmsParamsInit.getWjAccountInfoMap().put(acc.getAccountType(), acc);
				}
			}
		}
	}

}
