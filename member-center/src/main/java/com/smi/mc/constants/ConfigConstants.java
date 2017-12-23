package com.smi.mc.constants;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ConfigConstants {
	
	public static String rechargeVipURL;
	
	public static String BalanceQryURL;
	
	public static String privatePath;
	
	public static String SvcContElement="\"SvcCont\":";
	
	public static String LEFTSIGN="{";
	
	public static String RIGHTSIGN="}";
	
	public static String COMMA=",";
	
	static{
		 try {
			Properties pp = PropertiesLoaderUtils.loadProperties(new ClassPathResource("config.properties"));
			privatePath=pp.getProperty("private_path");
			rechargeVipURL=pp.getProperty("recharge_vip_url");
			BalanceQryURL=pp.getProperty("balanceQry_url");
		 } catch (IOException e) {
			e.printStackTrace();
		}
	}

}
