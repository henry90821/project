package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface CustRelMapper {

	int addCustRel(Map<String, Object> param);

	int updateCustRel(Map<String, Object> param);

	List<Map<String, Object>> qryCustRel(Map<String, Object> param);

}
