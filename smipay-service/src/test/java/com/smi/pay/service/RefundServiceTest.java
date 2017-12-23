package com.smi.pay.service;

import com.smi.pay.common.BaseWebIntegrationTests;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

public class RefundServiceTest extends BaseWebIntegrationTests {
	@Resource
	private RefundService refundService;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testUpdateRefundLog() {
		// Refund refund=refundService.getRefundByRefundBill("2016427224055974110001");
		// System.out.print(" 原退款单号："+refund.getRefundNo());
		// refundService.refundNotify(refund);
		// System.out.print(" 退款单号："+refund.getRefundNo().substring(0,refund.getRefundNo().length()-5));
		long cut = new Date().getTime();
		// refundService.refundNotify(refund);
		System.out.print(" 时间测试：" + new Date().getTime());

	}

	public static void main(String args[]) {
		long cut = new Date().getTime();
		// refundService.refundNotify(refund);
		System.out.println(" 时间测试：" + new Date().getTime());
		Long et = Long.parseLong("1461930869034");

		System.out.println((et - cut));

	}

}
