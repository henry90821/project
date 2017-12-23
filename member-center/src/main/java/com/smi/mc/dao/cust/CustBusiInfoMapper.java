package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface CustBusiInfoMapper {

	List<Map<String, Object>> qryCustBusiInfo(Map<String, Object> param);
	
	List<Map<String, Object>> isExistCardNbr(Map<String, Object> param);

	int addCustBusi(Map<String, Object> param);

	int updateCustBusi(Map<String, Object> param);

	int updateCardNbrByCustId(Map<String, Object> param);
}
