package com.smi.am.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.smi.am.common.BaseWebIntegrationTests;
import com.smi.am.constant.SmiConstants;
import com.smi.am.dao.AmCounponsDetailsMapper;
import com.smi.am.dao.model.AmCounponsDetails;
import com.smi.am.utils.RedisUtil;
import com.smi.tools.http.HttpKit;

public class RedisTest extends BaseWebIntegrationTests{
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private AmCounponsDetailsMapper amCounponsDetailsMapper;
	
	@Test
	public void getActivityShop(){
		System.out.println(redisUtil.get(SmiConstants.CHANNEL_REDIS_KEY+"245565"));
	}
	
	@Test
	public void testMember(){
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("loginMobiles", "10000024089,17100000836,19087865631");
			String jsonstr = HttpKit.post("http://localhost:8080/member/inside/cust/getCustIdByLoginMobiles",map );
			System.out.println(jsonstr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	@Transactional
	public void testBatchUpdate(){
		List<AmCounponsDetails> maps = new ArrayList<AmCounponsDetails>();
		AmCounponsDetails amCounponsDetails = new AmCounponsDetails();
		amCounponsDetails.setCdCustid("xxx");
		amCounponsDetails.setCdLoginmobile("xxx");
		amCounponsDetails.setCdGitpackageid(123);
		amCounponsDetails.setCdCounponsid("11953326727531738");
		
		AmCounponsDetails amCounponsDetails2 = new AmCounponsDetails();
		amCounponsDetails2.setCdCustid("yyy");
		amCounponsDetails2.setCdLoginmobile("yyyy");
		amCounponsDetails2.setCdGitpackageid(123);
		amCounponsDetails2.setCdCounponsid("11953326727531738");
		maps.add(amCounponsDetails);
		maps.add(amCounponsDetails2);
		
		int batchUpdatePackageIdByCouponsIds = amCounponsDetailsMapper.batchUpdatePackageIdByCouponsIds(maps);
		System.out.println(batchUpdatePackageIdByCouponsIds);
	
	}
}
