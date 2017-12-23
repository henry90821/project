package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface CertiInfoMapper {

	  List<Map<String,Object>> qryCertiInfo(Map<String,Object> param);
	
	  int addCerti(Map<String,Object> param);
	  
	  int updCerti(Map<String,Object> param);
	  
	  List<Map<String,Object>> queryCerti(Map<String,Object> param);
}
