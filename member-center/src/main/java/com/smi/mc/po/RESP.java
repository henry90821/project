package com.smi.mc.po;


import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.smi.mc.constants.Constants;

public class RESP implements Serializable {

	private static final long serialVersionUID = -7771555058785929438L;
	
	private String result;
	
	private String code;
	
	private String msg;
	
    public RESP(){
    	
    }
    @JSONField(name = "RESULT")
	public String getResult() {
    	return result;
    }
	
    public void setResult(String result) {
    	this.result = result;
    }
    
    
    public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@JSONField(name = "MSG")
    public String getMsg() {
    	return msg;
    }
	
    public void setMsg(String msg) {
    	this.msg = msg;
    }
    
    public static RESP createSuccessResp() {
    	RESP res = new RESP();
    	res.setResult(Constants.STATE_CODE_SUCC);
    	res.setCode(Constants.STATE_CODE_SUCC);
    	res.setMsg(Constants.STATE_MSG_SUCC);
		return res;
	}
    
    public static RESP createSuccessResp(String Code, String Msg) {
    	RESP res = new RESP();
    	res.setResult(Constants.STATE_CODE_SUCC);
    	res.setCode(Code);
    	res.setMsg(Msg);
		return res;
	}
    
    
    public static RESP createFailResp(String errCode, String errMsg) {
    	RESP res = new RESP();
    	res.setResult(Constants.STATE_CODE_FAIL);
    	res.setCode(errCode);
    	res.setMsg(errMsg);
    	return res;
    }
	@Override
	public String toString() {
		return "RESP [result=" + result + ", code=" + code + ", msg=" + msg + "]";
	}
	
}
