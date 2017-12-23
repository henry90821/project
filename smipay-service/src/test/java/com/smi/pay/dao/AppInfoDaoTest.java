package com.smi.pay.dao;

import com.smi.pay.common.BaseWebIntegrationTests;
import com.smi.pay.model.AppInfo;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.fail;

public class AppInfoDaoTest extends BaseWebIntegrationTests {

	@Resource
	private AppInfoDao appInfoDao;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDeleteByPrimaryKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsert() {
		AppInfo appInfo = new AppInfo();
		appInfo.setAppCode("daotestCode111");
		appInfo.setAppDesc("daotestDesc111");
		appInfoDao.insert(appInfo);

	}

}
