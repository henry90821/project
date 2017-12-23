package com.iskyshop.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IUserService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-configuration.xml",
		"classpath:applicationContext-security.xml"})
public class SecurityManagerSupportTest {
	
	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private IUserService userService;
	
	private String userName = "17000183156";
	
	@Test
	public void testGetUser() {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("userName", userName);
		params.put("email", userName);
		params.put("mobile", userName);
		
		
		List<User> users = this.userService.query(
				"select obj from User obj where obj.userName =:userName or obj.email=:email or obj.mobile=:mobile", params,
				-1, -1);
		
		printInfo(users);
		
		
	}
	
	/**
	 * 打印信息
	 * 
	 * @param obj
	 *            待打印的对象
	 */
	public void printInfo(Object obj) {
		logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
		if (StringUtils.isNullOrEmpty(obj)) {
			logger.info("没有查询到数据，返回对象为NULL");
		} else {
			logger.info("result=> " + obj);
		}

		logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
}
