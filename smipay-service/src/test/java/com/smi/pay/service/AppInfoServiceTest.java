package com.smi.pay.service;

import com.smi.pay.common.BaseWebIntegrationTests;
import com.smi.pay.model.AppInfo;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.fail;

public class AppInfoServiceTest extends BaseWebIntegrationTests {
	@Resource
	private AppInfoService appInfoService;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testListAppInfo() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateAppInfoById() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoadAppInfo() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveAppInfo() {
		AppInfo appInfo = new AppInfo();
		appInfo.setAppCode("testCode22");
		appInfo.setAppDesc("testDesc22");
		appInfoService.saveAppInfo(appInfo);
	}

	@Test
	public void testDeleteAppInfo() {
		fail("Not yet implemented");
	}

}
