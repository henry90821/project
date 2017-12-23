package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface InfoVitMapper {

	Map<String, Object> qryInfoVit(Map<String, Object> param);

	List<Long> qrySumInfoVit(Map<String, Object> param);

	int addInfoVit(Map<String, Object> param);

	int updateInfoVit(Map<String, Object> param);

	Map<String, Object> infoVitQry(Map<String, Object> param);

}
