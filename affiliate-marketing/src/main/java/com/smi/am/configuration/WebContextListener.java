package com.smi.am.configuration;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.beans.factory.annotation.Autowired;

import com.smi.am.constant.SmiConstants;
import com.smi.am.dao.AmActivityShopMapper;
import com.smi.am.dao.model.AmActivityShop;
import com.smi.am.utils.RedisUtil;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

@WebListener
public class WebContextListener implements ServletContextListener{

	private static final Logger LOGGER = LoggerUtils.getLogger(WebContextListener.class);
	
	@Autowired
	private AmActivityShopMapper amActivityShopMapper;
	
	@Autowired
	private RedisUtil redisUtil;

	public void contextInitialized(ServletContextEvent sce) {
		LOGGER.info("活动门店数据同步到redis缓存 start...");
		// 获取数据库门店信息存进redis缓存
		List<AmActivityShop> sActivityShops = amActivityShopMapper.selectByExample(null);
		for (AmActivityShop amActivityShop : sActivityShops) {
			if (!redisUtil.exists(SmiConstants.CHANNEL_REDIS_KEY + amActivityShop.getAsOrgid())) {
				redisUtil.set(SmiConstants.CHANNEL_REDIS_KEY + amActivityShop.getAsOrgid(), amActivityShop.getAsActivityshop());
			}
		}
		LOGGER.info("活动门店数据同步到redis缓存 end...");
	}

	public void contextDestroyed(ServletContextEvent sce) {
		LOGGER.info("WebContextListener已经被销毁。");
	}
	
}
