package com.smi.am.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.smi.am.common.BaseWebIntegrationTests;
import com.smi.am.service.vo.CouponsVo;
import com.smi.am.utils.SmiResult;

public class CouponsServiceTest extends BaseWebIntegrationTests{

	@Autowired
	private ICouponsService couponsService;
	
	@Test
	public void searchCoupons(){
		Map<String, Object> conditMap = new HashMap<String, Object>();
		conditMap.put("PAGE_NUM", 1);
		conditMap.put("PAGE_SIZE", 10);
		
		SmiResult<List<CouponsVo>> searchCouponsByConditions = couponsService.searchCouponsByConditions(conditMap);
		for (CouponsVo couponsVo : searchCouponsByConditions.getData()) {
			System.out.println(couponsVo.getcCouponsname());
		}
	}
	
}
