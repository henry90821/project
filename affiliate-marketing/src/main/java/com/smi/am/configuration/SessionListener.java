package com.smi.am.configuration;


import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import com.smi.am.constant.SmiConstants;
import com.smi.am.service.vo.UserVo;
import com.smi.am.utils.RedisUtil;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

/**
 * session监听器
 * @author yanghailong
 *
 */
@WebListener
public class SessionListener implements HttpSessionListener{
	
	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());
	
	@Autowired
	private RedisUtil redisUtil;

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		LOGGER.info("session初始化...");
	}

	/**
	 * 在session销毁的时候 把loginUserMap中保存的键值对清除
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		LOGGER.info("session被销毁...");
		UserVo user = (UserVo)event.getSession().getAttribute(SmiConstants.USERNAME);
        if(user!=null){
        	LOGGER.info("session监听器，session销毁，username是："+user.getuUsername());
        	
        	String sessionId = (String)redisUtil.get(SmiConstants.USERNAME_REDIS_KEY + user.getuUsername());
        	if(StringUtils.isNotEmpty(sessionId)){
        		boolean isExists = redisUtil.exists(SmiConstants.SESSION_REDIS_KEY + sessionId);
        		if(isExists)
        			redisUtil.remove(SmiConstants.SESSION_REDIS_KEY +sessionId);
        		
        		redisUtil.remove(SmiConstants.USERNAME_REDIS_KEY + user.getuUsername());
        	}
        }
	}

	
	
}
