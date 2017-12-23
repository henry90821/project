package com.iskyshop.foundation.service.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.iskyshop.core.base.BaseTester;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IUserService;

public class OrderFormServiceImplTest extends BaseTester{
	
	@Autowired
	private IOrderFormService orderFormService;

	@Test
	public void test() {
		//System.out.println(orderFormService.getOrderRefundAmount("20160225171730372320853"));;
	}

}
