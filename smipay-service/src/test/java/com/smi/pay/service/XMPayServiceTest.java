package com.smi.pay.service;

import com.smi.pay.common.BaseWebIntegrationTests;
import com.smi.pay.common.CommonUtil;
import com.smi.pay.sdk.wx.MD5Util;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

public class XMPayServiceTest extends BaseWebIntegrationTests {
	@Resource
	private XMPayService xmPayService;
	@Resource
	private PayService payService;

	@Before
	public void setUp() throws Exception {
	}

	// 3131000000394858 3131000000394859 密码：123qwe
	@Test
	public void testGetBal() {
		// xmPayService.getBal("3131000000394858");
	}

	@Test
	public void checkPassword() {
		String custId = "37278";
		String pd = "123qwe";

		String passWord = MD5Util.MD5(pd);
		System.out.println("---passWord：" + passWord);
		boolean result = xmPayService.checkPassword(custId, passWord);
		System.out.println("---验证密码结果：" + result);
	}

	@Test
	public void pay() {
		// Order order=payService.getOrder(665);
		// xmPayService.pay(order);
		// SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Long timeStart=Long.parseLong("1463122213000");
		String time_expire = CommonUtil.tformat.format(new Date(1463122213000l + 300000));

		// Date date = new Date(timeStart);
		System.out.println(time_expire);
		System.out.println(new Date().getTime() + 60000);

	}

}
