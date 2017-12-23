package com.iskyshop.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.module.app.domain.AppNewlyVersionInfo;
import com.iskyshop.module.app.service.IAppNewlyVerionService;


@Controller
public class NewlyVersionAction {

	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private IAppNewlyVerionService appNewlyVerionService;	
	
	
	@RequestMapping("/app/query_app_newly_ver.htm")
	public void queryAppNewlyVer(HttpServletRequest request, HttpServletResponse response, String platFormFlag, String channel, int verNumber) {
		AppNewlyVersionInfo appVerion = this.appNewlyVerionService.queryAppNewlyVer(platFormFlag, channel, verNumber);
		Map retVal = new HashMap<String,Object>();
		if(appVerion == null) {
			retVal.put("errorFlag", 1);
			retVal.put("errorMsg", "未找到指定渠道和平台的最新版本。请求的条件：platFormFlag=" + platFormFlag + ",channel=" +channel + ",verNumber=" + verNumber);
		} else {
			retVal.put("platformFlag", appVerion.getPlatformFlag());
			retVal.put("verNumber", appVerion.getVerNumber());
			retVal.put("verName", CommUtil.null2String(appVerion.getVerName()));
			retVal.put("installUrl", appVerion.getInstallUrl());
			retVal.put("updateFlag", appVerion.getUpdateFlag());
			retVal.put("verDesc", CommUtil.null2String(appVerion.getVerDesc()));
			retVal.put("channelId", appVerion.getChannelId());
			retVal.put("updateTime", appVerion.getAddTime());//发布时间
			retVal.put("errorFlag", 0);
			retVal.put("errorMsg", "");
		}			
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(retVal));
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
