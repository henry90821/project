package com.smi.mc.service.atomic.cust;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bull.javamelody.MonitoredWithSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.dao.cust.CommonServiceMapper;
import com.smi.mc.exception.AtomicServiceException;

@MonitoredWithSpring
@Service
public class CommonAtomicService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	CommonServiceMapper commonServiceMapper;

	/**
	 * 获取序列号
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public String getSequence(String param) throws AtomicServiceException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String resSequence = null;
		try {
			paramMap.put("SEQUENCE_NAME", param);
			resSequence = commonServiceMapper.getSequenceByTable(paramMap);
		} catch (Exception e) {
			this.logger.error("获取" + param + "序列异常", e);
			throw new AtomicServiceException(e);
		}
		this.logger.debug("得到" + param + "序列：" + resSequence);
		return resSequence;
	}

	/**
	 * 获取序列号
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public String getSequenceByTable(String param) throws AtomicServiceException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String resSequence = null;
		try {
			paramMap.put("SEQUENCE_NAME", "seq_" + param);
			resSequence = commonServiceMapper.getSequenceByTable(paramMap);
		} catch (Exception e) {
			this.logger.error("获取" + param + "表异常", e);
			throw new AtomicServiceException(e);
		}
		this.logger.debug("得到" + param + "表序列：" + resSequence);
		return resSequence;
	}

	/**
	 * 执行语句
	 * 
	 * @param param
	 * @return
	 * @throws AtomicServiceException
	 */
	public List<Map<String, Object>> excuteSQL(Map<String, Object> param) throws AtomicServiceException {
		try {
			List<Map<String, Object>> map = commonServiceMapper.excuteSQL(param);
			return map;
		} catch (Exception e) {
			this.logger.error("获取" + param + "表异常", e);
			throw new AtomicServiceException(e);
		}
	}
}