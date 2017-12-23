package com.smi.sms.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.pagehelper.PageInfo;
import com.smi.sms.dao.SmsHistoryMapper;
import com.smi.sms.model.query.PageQuery;
import com.smi.sms.model.query.QueryResult;
import com.smi.tools.kits.JsonKit;

public class SmsHistoryServiceTest extends BaseServiceTest {
	
	@Autowired
	ISmsHistoryService smsHistoryService;
	
	@Autowired
	SmsHistoryMapper smsHistoryMapper;
	
	
	@Test
	public void testPageQuery() {
		PageInfo<QueryResult> page = smsHistoryService.listHistory(1, 20, null, null, null, null, null, "101");
		
		List<QueryResult> list = page.getList();
		
		System.out.println(list.size());
		System.out.println(page.getPageNum());
		System.out.println(page.getPages());
		System.out.println(page.getTotal());
		System.out.println(JsonKit.toJsonString(page));
	}
	
	@Test
	public void testPageInfo() {
		PageQuery query = new PageQuery();
		
		List<QueryResult> list = smsHistoryMapper.listAll(query);
		
		PageInfo<QueryResult> page = new PageInfo<QueryResult>(list);
		
		
		System.out.println(list.size());
		System.out.println(page.getPageNum());
		System.out.println(page.getPages());
		System.out.println(page.getTotal());
		System.out.println(JsonKit.toJsonString(page));
	}
}
