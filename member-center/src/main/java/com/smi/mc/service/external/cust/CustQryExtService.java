package com.smi.mc.service.external.cust;

import java.util.Map;

import net.sf.json.JSONObject;

public interface CustQryExtService {

	public Map<String,Object> custQry(JSONObject JSON);
}
