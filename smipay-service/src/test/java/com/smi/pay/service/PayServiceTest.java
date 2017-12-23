package com.smi.pay.service;

import com.smi.pay.common.BaseWebIntegrationTests;
import com.smi.pay.common.LogUtil;
import com.smi.pay.model.Order;
import com.smi.pay.model.OrderLog;
import com.smi.pay.model.OrderReturn;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.fail;

public class PayServiceTest extends BaseWebIntegrationTests {
	@Resource
	private PayService payService;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCheck() {
		OrderLog log = payService.getOrderLog(3066);
		OrderReturn re = payService.check(log);
		System.out.println(re.getMsg() + re.getCode());
		fail("Not yet implemented");
	}

	@Test
	public void testpayNotifych() {
		Order order = payService.getOrder(935);
		payService.payNotify(order);
		LogUtil.writeLog("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
		LogUtil.writeLog(order.billNo);

		// payService.saveOrderLog(order);
	}

}
