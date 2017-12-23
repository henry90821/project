package com.smi.mc.service.external.cust;

import java.util.Map;

import com.smi.mc.exception.BusiServiceException;

import net.sf.json.JSONObject;

public interface BusiAddExtService {

	public Map<String, Object> addBusi(JSONObject JSON) throws BusiServiceException; 
}
