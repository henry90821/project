package com.smi.mc.api.service;


import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import com.smi.mc.api.common.BaseWebIntegrationTests;
import com.smi.mc.api.utils.RedisUtil;
import com.smi.mc.api.valueobject.MemberInfoVo;
import com.smi.tools.kits.StrKit;

public class RedisTest extends BaseWebIntegrationTests{
	
	@Autowired
	private RedisUtil redisUtil;
	
	
	@Test
	public void getActivityShop(){
		String infoCustByToken = redisUtil.getInfoCustIdByToken("ZTY0ZDdkMGMtNmZjZS00MWUzLWE4ZWUtOGE1ODA1MjA0OTJi");
		System.out.println(infoCustByToken);
	}
	
	@Test
	public void testBase64(){
		String uuid = UUID.randomUUID().toString();
		System.out.println("uuid: " + uuid);
		String encodeToString = Base64Utils.encodeToString(uuid.getBytes());
		System.out.println(encodeToString);
		
		byte[] decodeFromString = Base64Utils.decodeFromString(encodeToString);
		String string = StrKit.str(decodeFromString, "UTF8");
		System.out.println(string);
		
		System.out.println(uuid.equals(string));
		
	}
	
}
