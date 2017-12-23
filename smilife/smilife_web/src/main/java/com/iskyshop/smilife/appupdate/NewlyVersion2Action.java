package com.iskyshop.smilife.appupdate;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.module.app.domain.AppNewlyVersionInfo;
import com.iskyshop.module.app.service.IAppNewlyVerionService;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;


@Controller
@RequestMapping(value="/api/app")
public class NewlyVersion2Action {//供星美生活APP2.0升级调用
	@Autowired
	private IAppNewlyVerionService appNewlyVerionService;
	
	
	@RequestMapping(value="/mall1901NewlyVersion.htm",produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Result getNewlyVersion(String platFormFlag, Integer verNumber) {
		Result result = new Result();
		if(!"1".equals(platFormFlag) && !"2".equals(platFormFlag) || verNumber == null) {
			result.set(ErrorEnum.REQUEST_ERROR);		
		} else {
			Map retVal = new HashMap();
			
			AppNewlyVersionInfo appVerion = this.appNewlyVerionService.queryAppNewlyVer(platFormFlag, "smi", verNumber);		
			if(appVerion != null) {
				retVal.put("verNumber", appVerion.getVerNumber());
				retVal.put("verName", CommUtil.null2String(appVerion.getVerName()));
				retVal.put("installUrl", appVerion.getInstallUrl());
				retVal.put("updateFlag", appVerion.getUpdateFlag());
				retVal.put("verDesc", CommUtil.null2String(appVerion.getVerDesc()));	
			} 
			result.setData(retVal);	
		}
		
		return result;
	}
}
