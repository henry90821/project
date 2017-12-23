package com.smi.mc.service.busi.cust.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.constants.Constants;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.service.atomic.cust.InfoRandomCodeAtomicService;
import com.smi.mc.service.busi.cust.ChkPinService;

@Service("chkPinService")
public class ChkPinServiceImpl implements ChkPinService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private InfoRandomCodeAtomicService infoRandomCodeAtomicService;

	public Map<String, Object> checkPin(Map<String, Object> param) throws BusiServiceException {
		Map<String, Object> map = new HashMap<String, Object>();

		String MOBILE = (String) param.get("MOBILE");
		String RANDOM_CODE = (String) param.get("RANDOM_CODE");
		if (StringUtils.isEmpty(MOBILE)) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "");
			map.put("MSG", "必填参数为空");
			return map;
		}
		if (StringUtils.isEmpty(RANDOM_CODE)) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "");
			map.put("MSG", "必填参数为空");
			return map;
		}

		try {
			Map<String, Object> randomCode = new HashMap<String, Object>();
			randomCode.put("CODE_TYPE", "20");
			randomCode.put("RANDOM_CODE", RANDOM_CODE);
			randomCode.put("APPLICANT_TYPE", "2000");
			randomCode.put("APPLICANT_ID", MOBILE);
			randomCode.put("STATUS_CD", "1000");

			Map<String, Object> reMap = new HashMap<String, Object>();
			reMap = this.infoRandomCodeAtomicService.qryRandomCode(randomCode);
			if (reMap.containsKey("INFO_RANDOM_CODE")) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) reMap.get("INFO_RANDOM_CODE");
				if (list.size() > 0) {
					if ((((Map<String, Object>) list.get(0)).containsKey("EFF_DATE"))
							&& (((Map<String, Object>) list.get(0)).containsKey("EXP_DATE"))) {
						Date EFF_DATE = (Date) ((Map<String, Object>) list.get(0)).get("EFF_DATE");
						Date EXP_DATE = (Date) ((Map<String, Object>) list.get(0)).get("EXP_DATE");
						if ((EFF_DATE.before(new Date())) && (new Date().before(EXP_DATE))) {
							try {
								Map<String, Object> params = new HashMap<String, Object>();
								Map<String, Object> reMap2 = new HashMap<String, Object>();
								params.put("APPLICANT_ID", MOBILE);
								params.put("STATUS_CD", "1100");
								reMap2 = this.infoRandomCodeAtomicService.updRandomCode1(params);
								map.put("RESULT", "0");
								map.put("CODE", "");
								map.put("MSG", "");
							} catch (Exception e) {
								this.logger.error("调用随机码修改原子服务失败", e);
								throw new BusiServiceException(e);
							}
						} else {
							map.put("RESULT", Constants.STATE_CODE_FAIL);
							map.put("CODE", "1189");
							map.put("MSG", "短信验证码已过期");
							return map;
						}
					}
				} else {
					map.put("RESULT", Constants.STATE_CODE_FAIL);
					map.put("CODE", "1190");
					map.put("MSG", "短信验证码不正确");
					return map;
				}
			} else {
				this.logger.error("出参设置错误!");
			}
		} catch (Exception e) {
			this.logger.error("短信验证码校验失败", e);
			throw new BusiServiceException(e);
		}

		return map;
	}
}