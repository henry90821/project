package com.smi.pay.service;

import com.smi.pay.common.BaseWebIntegrationTests;
import com.smi.pay.common.CommonUtil;

import java.util.Date;

public class MainTest extends BaseWebIntegrationTests {

	public static void main(String args[]) {
		String longtime = "1463324149000"; // 20160514224424,20160515225549

		String time_expire = CommonUtil.tformat.format(new Date(Long.parseLong(longtime)));

		// Date date = new Date(timeStart);
		System.out.println(time_expire);
		System.out.println(new Date().getTime() + 3000000);

	}
}
