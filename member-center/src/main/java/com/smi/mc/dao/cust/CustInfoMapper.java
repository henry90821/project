package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface CustInfoMapper {

	List<Map<String, Object>> qryCustInfo(Map<String, Object> param);

	int addCust(Map<String, Object> param);

	int updCust(Map<String, Object> param);
	
	List<Map<String, Object>> qryCustInfoByPage(Map<String, Object> param);

	int selectCountPages(Map<String, Object> param);
	
	/**
	 * 根据手机号获取custId
	 * @param loginMobile
	 * @return
	 */
	List<Map<String, Object>> getCustIdByloginMobile(String[] loginMobile);
}
