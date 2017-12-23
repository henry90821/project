package com.smi.am.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.am.common.BaseWebIntegrationTests;
import com.smi.am.service.IUserservice;
import com.smi.am.service.vo.UserVo;

public class UserServiceTest extends BaseWebIntegrationTests{

	@Autowired
	private IUserservice userService;
	
	@Test
	public void testLogin() throws Exception{
		UserVo findUserByUsernameAndPwd = userService.findUserByUsernameAndPwd("cd", "123456");
		System.out.println(findUserByUsernameAndPwd.getuUsername());
	}
	
	
}
