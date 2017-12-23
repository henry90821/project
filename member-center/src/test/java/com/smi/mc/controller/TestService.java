package com.smi.mc.controller;

import com.smi.mc.common.BaseWebIntegrationTests;
import com.smi.mc.service.atomic.cust.BusiQryFeeAtomicService;
import com.smi.mc.service.atomic.cust.CardInfoModAtomicService;
import com.smi.mc.service.atomic.cust.CinemaCardInfoAtomicService;
import com.smi.mc.service.atomic.cust.CodeOfferAtomicService;
import com.smi.mc.service.atomic.cust.CodeOfferRegionAtomicService;
import com.smi.mc.service.atomic.cust.CommonAtomicService;
import com.smi.mc.service.atomic.cust.CustAtomicService;
import com.smi.mc.service.atomic.cust.CustBusiAtomicService;
import com.smi.mc.service.atomic.cust.CustCertiAtomicService;
import com.smi.mc.service.atomic.cust.CustHeadInfoAtomicService;
import com.smi.mc.service.atomic.cust.CustRelAtomicService;
import com.smi.mc.service.atomic.cust.InfoBusiPayAtomicService;
import com.smi.mc.service.atomic.cust.InfoLoginUserAtomicService;
import com.smi.mc.service.atomic.cust.InfoOrderAtomicService;
import com.smi.mc.service.atomic.cust.InfoOrderChildAtomicService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Andriy on 16/5/20.
 */
public class TestService extends BaseWebIntegrationTests {

	@Autowired
	CustAtomicService custAtomicService;
	@Autowired
	BusiQryFeeAtomicService busiQryFeeAtomicService;
	@Autowired
	CardInfoModAtomicService cardInfoModAtomicService;
	@Autowired
	CinemaCardInfoAtomicService cinemaCardInfoAtomicService;
	@Autowired
	CodeOfferAtomicService codeOfferAtomicService;
	@Autowired
	CodeOfferRegionAtomicService codeOfferRegionAtomicService;
	@Autowired
	CommonAtomicService commonAtomicService;
	@Autowired
	CustBusiAtomicService custBusiAtomicService;
	@Autowired
	CustCertiAtomicService custCertiAtomicService;
	@Autowired
	CustHeadInfoAtomicService custHeadInfoAtomicService;
	@Autowired
	CustRelAtomicService custRelAtomicService;
	@Autowired
	InfoBusiPayAtomicService infoBusiPayAtomicService;
	@Autowired
	InfoLoginUserAtomicService infoLoginUserAtomicService;
	@Autowired
	InfoOrderAtomicService infoOrderAtomicService;
	@Autowired
	InfoOrderChildAtomicService infoOrderChildAtomicService;

	/**
	 * 会员基本信息查询测试入口
	 */
	@Test
	public void tests() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("CUST_ID", "31010000000001");
			Map<String, Object> maps = custAtomicService.qryCustInfo(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 业务定义查询测试入口
	 */
	@Test
	public void tests1() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("OFFER_ID", null);
			param.put("SERV_CODE", "112");
			Map<String, Object> maps = busiQryFeeAtomicService.qryBusiFeeInfo(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 业务定义查询测试入口
	 */
	@Test
	public void tests2() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("CINEMA_CARD_NUM", "114914310000053");
			// param.put("CINEMA_CARD_INFO_ID", 120);
			param.put("NEW_FLAG", "0");
			param.put("OPER_DATE", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			Map<String, Object> maps = cardInfoModAtomicService.cinemaCardInfoMod(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 老会员卡号查询测试入口
	 */
	@Test
	public void tests3() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("CINEMA_CARD_NUM", "114914310000053");
			// param.put("CINEMA_CARD_INFO_ID", 120);
			param.put("NEW_FLAG", "0");
			// param.put("OPER_DATE", new SimpleDateFormat("yyyy-MM-dd
			// HH:mm:ss").format(new Date()));
			Map<String, Object> maps = cinemaCardInfoAtomicService.qryCinemaCardInfo(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 业务定义根据offer_name字段查询测试入口
	 */
	@Test
	public void tests4() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			// param.put("CINEMA_CARD_NUM", "114914310000053");
			// param.put("CINEMA_CARD_INFO_ID", 120);
			param.put("OFFER_NAME", "百度联名业务");
			// param.put("OPER_DATE", new SimpleDateFormat("yyyy-MM-dd
			// HH:mm:ss").format(new Date()));
			Map<String, Object> maps = codeOfferAtomicService.qryCodeOfferByOffername(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 业务区域关系查询测试入口
	 */
	@Test
	public void tests5() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			// param.put("CINEMA_CARD_NUM", "114914310000053");
			param.put("STATUS_CD", "1000");
			param.put("REGION_ID", "20442");
			param.put("OFFER_ID", "1001");
			Map<String, Object> maps = codeOfferRegionAtomicService.qryOfferIdByRegion(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 执行语句测试入口
	 */
	@Test
	public void tests6() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			// param.put("REGION_ID", "20442");
			// param.put("OFFER_ID", "1001");
			// String param="pay_id";
			// String maps = commonAtomicService.getSequenceByTable(param);
			param.put("SQL", "SELECT * FROM INFO_ORDER");
			List<Map<String, Object>> maps = commonAtomicService.excuteSQL(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 会员业务查询测试入口
	 */
	@Test
	public void tests7() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("STATUS_CD", "1000");
			// param.put("OFFER_ID", "1001");
			// String param="pay_id";
			// String maps = commonAtomicService.getSequenceByTable(param);
			Map<String, Object> maps = custBusiAtomicService.qryCustBusiInfo(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 会员证件信息查询(联表code_list查)测试入口
	 */
	@Test
	public void tests8() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			// param.put("STATUS_CD", "1000");
			// param.put("OFFER_ID", "1001");
			// String param="pay_id";
			// String maps = commonAtomicService.getSequenceByTable(param);
			Map<String, Object> maps = custCertiAtomicService.qryCertiInfo(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 会员头像信息查询测试入口
	 */
	@Test
	public void tests9() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			// param.put("STATUS_CD", "1000");
			param.put("CUST_ID", "31010000000001");
			// String param="pay_id";
			// String maps = commonAtomicService.getSequenceByTable(param);
			Map<String, Object> maps = custHeadInfoAtomicService.custHeadInfoQuery(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 会员关系的查询测试入口
	 */
	@Test
	public void tests10() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			// param.put("STATUS_CD", "1000");
			param.put("CUST_ID_M", "4354334");
			// String param="pay_id";
			// String maps = commonAtomicService.getSequenceByTable(param);
			Map<String, Object> maps = custRelAtomicService.qryCustRel(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 查询服务账号关系测试入口
	 */
	@Test
	public void tests11() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			// param.put("STATUS_CD", "1000");
			param.put("PAY_ID", "2188553888620593");
			// String param="pay_id";
			// String maps = commonAtomicService.getSequenceByTable(param);
			Map<String, Object> maps = infoBusiPayAtomicService.qryInfoBusiPay(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 查询有效的会员账号测试入口
	 */
	@Test
	public void tests12() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("LOGIN_USER", "13788887777");
			param.put("CUST_ID", "31010000000092");
			// String param="pay_id";
			// String maps = commonAtomicService.getSequenceByTable(param);
			Map<String, Object> maps = infoLoginUserAtomicService.qryInfoLoginUserForCheck(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 订单表查询测试入口
	 */
	@Test
	public void tests13() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("ORDER_ID", "736");
			param.put("CUST_ID", "31000000334128");
			// String param="pay_id";
			// String maps = commonAtomicService.getSequenceByTable(param);
			Map<String, Object> maps = infoOrderAtomicService.qryInfoOrder(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 子订单查询测试入口
	 */
	@Test
	public void tests14() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			// param.put("LOGIN_USER", "13788887777");
			param.put("ORDER_ID", "337");
			// String param="pay_id";
			// String maps = commonAtomicService.getSequenceByTable(param);
			Map<String, Object> maps = infoOrderChildAtomicService.qryInfo(param);
			System.out.println(maps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
