package com.smi.mc.service.atomic.cust;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.dao.cust.RuleInfoMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class RuleAtomicService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RuleInfoMapper ruleInfoMapper;

	public List<Map<String, Object>> qryRuleInfo(Map<String, Object> param) throws AtomicServiceException{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = ruleInfoMapper.qryRuleInfo(param);
			return list;
		} catch (Exception e) {
			this.logger.error("查询规则信息异常", e);
			throw new AtomicServiceException(e);
		}
	}

	public Map<String, String> qryRuleExp(Map<String, Object> param) throws AtomicServiceException {

		try {
			return ruleInfoMapper.qryRuleExp(param);
		} catch (Exception e) {
			this.logger.error("查询规则表达式信息异常", e);
			throw new AtomicServiceException(e);
		}
	}

	public List<Map<String, Object>> qryRuleSql(Map<String, Object> param) throws AtomicServiceException {

		try {
			return ruleInfoMapper.qryRuleSql(param);
		} catch (Exception e) {
			this.logger.error("查询规则sql信息异常", e);
			throw new AtomicServiceException(e);
		}
	}

	public List<Map<String, Object>> qrySqlParam(Map<String, Object> param) {
		try {
			return ruleInfoMapper.qrySqlParam(param);
		} catch (Exception e) {
			this.logger.error("查询规则sql参数信息异常", e);
		}
		return null;
	}

	public Map<String, Object> qryRuleHandle(Map<String, Object> param) throws AtomicServiceException {
		try {
			return ruleInfoMapper.qryRuleHandle(param);
		} catch (Exception e) {
			this.logger.error("查询规则处理信息异常", e);
			throw new AtomicServiceException(e);
		}
	}

}