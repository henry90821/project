package com.smi.mc.service.busi.cust.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.smi.mc.common.MemberCenterConfiguration;
import com.smi.mc.constants.Constants;
import com.smi.mc.dao.cust.CodeCardMapper;
import com.smi.mc.dao.cust.InfoCustMapper;
import com.smi.mc.dao.cust.InfoLoginUserMapper;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.model.cust.InfoCust;
import com.smi.mc.model.cust.InfoLoginUser;
import com.smi.mc.po.RESP;
import com.smi.mc.service.atomic.billing.AcctBalanceAtomicService;
import com.smi.mc.service.atomic.cust.CodeOfferAtomicService;
import com.smi.mc.service.atomic.cust.CommonAtomicService;
import com.smi.mc.service.atomic.cust.CustAtomicService;
import com.smi.mc.service.atomic.cust.CustBusiAtomicService;
import com.smi.mc.service.atomic.cust.CustCertiAtomicService;
import com.smi.mc.service.atomic.cust.InfoBusiPayAtomicService;
import com.smi.mc.service.atomic.cust.InfoLoginUserAtomicService;
import com.smi.mc.service.atomic.cust.PayAtomicService;
import com.smi.mc.service.busi.cust.CustRegService;
import com.smi.mc.service.busi.cust.OrderService;
import com.smi.mc.service.busi.cust.VCustNbrService;
import com.smi.mc.utils.GenCustIdAndPayIdUtil;
import com.smi.mc.utils.MD5Utils;
import com.smi.tools.kits.DateKit;
import com.smi.tools.kits.RandomKit;
/**
 * 
 * @author smi
 * 会员注册
 */
@Service("custRegService")
public class CustRegServiceImpl implements CustRegService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private CommonAtomicService commonAtomicService;

	@Resource
	private InfoLoginUserAtomicService infoLoginUserAtomicService;

	@Autowired
	private VCustNbrService vCustNbrService;

	@Resource
	private CustAtomicService custAtomicService;

	@Resource
	private PayAtomicService payAtomicService;

	@Resource
	private CustBusiAtomicService custBusiAtomicService;

	@Resource
	private CodeOfferAtomicService codeOfferAtomicService;

	@Resource
	private InfoBusiPayAtomicService infoBusiPayAtomicService;

	@Resource
	private AcctBalanceAtomicService acctBalanceAtomicService;
	
	@Autowired
	private CodeCardMapper codeCardMapper;

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CustCertiAtomicService custCertiAtomicService;
	
	@Autowired
	private InfoCustMapper infoCustMapper;

	@Autowired
	private InfoLoginUserMapper infoLoginUserMapper;
	
	@Autowired
	private MemberCenterConfiguration memberCenterConfiguration;
	
	@Transactional(rollbackFor=Exception.class,value="rdsTransactionManager")
	public Map<String, Object> custReg(Map<String, Object> param) throws BusiServiceException {

		Map<String, Object> map = new HashMap<String, Object>();
		Date date = new Date();

		String CHANNEL_CODE = (String) param.get("CHANNEL_CODE");
		String SYSTEM_USER_ID = (String) param.get("SYSTEM_USER_ID");
		String EXT_SYSTEM = (String) param.get("EXT_SYSTEM");
		String LOGIN_USER_TYPE = (String) param.get("LOGIN_USER_TYPE");
		String LOGIN_USER = (String) param.get("LOGIN_USER");
		String LOGIN_PWD = (String) param.get("LOGIN_PWD");
		String PAY_PWD = (String) param.get("PAY_PWD");
		String CUST_NAME = (String) param.get("CUST_NAME");
		String SEX = (String) param.get("SEX");
		String EMAIL = (String) param.get("EMAIL");
		String BIRTHDAY = (String) param.get("BIRTHDAY");
		String certiType = (String) param.get("certiType");
		String certiNbr = (String) param.get("certiNbr");
		String certiAddr = (String) param.get("certiAddr");
		String rndNum = RandomKit.randomNumbers(4);

		String SMI_SOURCE = "";
		if ((null != param.get("SMI_SOURCE")) && (!"".equals(param.get("SMI_SOURCE"))))
			SMI_SOURCE = (String) param.get("SMI_SOURCE");
		else {
			SMI_SOURCE = "0";
		}
		if (StringUtils.isEmpty(CHANNEL_CODE)) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "1");
			map.put("MSG", "必填参数channel_code不能为空");
			return map;
		}
		
		if (StringUtils.isEmpty(LOGIN_USER_TYPE)) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "1");
			map.put("MSG", "必填参数为空");
			return map;
		}
		if (StringUtils.isEmpty(LOGIN_USER)) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "1");
			map.put("MSG", "必填参数为空");
			return map;
		}
		if (StringUtils.isEmpty(CUST_NAME)) {
			CUST_NAME = LOGIN_USER;
		}

		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			param.put("STATUS_CD_REQ", "1100");
			resMap = this.infoLoginUserAtomicService.qryInfoLoginUser(param);
		} catch (Exception e) {
			this.logger.error("查询登录帐号失败", e);
			throw new BusiServiceException(e);
		}

		List<Map<String, Object>> CUST = (List) resMap.get("CUST");
		if (CUST.size() > 0) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "1195");
			map.put("MSG", "登陆账号已存在");
			return map;
		}

		String V_CUST_NBR = null;
		try {
			resMap.clear();
			resMap = this.vCustNbrService.addVCustNbr();
		} catch (Exception e) {
			this.logger.error("虚拟卡号生成失败", e);
			throw new BusiServiceException(e);
		}
		V_CUST_NBR = (String) resMap.get("V_CUST_NBR");
		String CUST_ID = null;
		try {
			String seq = this.commonAtomicService.getSequence("SEQ_CUST_ID");
			CUST_ID=GenCustIdAndPayIdUtil.genCustId(seq); // 生成cust_id
		} catch (Exception e) {
			this.logger.error("调用序列查询失败", e);
			throw new BusiServiceException(e);
		}
		try {
			InfoCust infoCust = new InfoCust();
			infoCust.setCustId(CUST_ID);
			infoCust.setCustName(CUST_NAME);
			infoCust.setCustVipLevel("1000");
			infoCust.setCustType("1000");
			infoCust.setStatusCd("1000");
			infoCust.setCardType(Constants.CARD_TYPE_VIRTUAL);
			infoCust.setCustNbr(V_CUST_NBR);
			infoCust.setContactMobile(LOGIN_USER);
			infoCust.setContactAddr("");
			infoCust.setSex(SEX);
			if (StringUtils.isNoneEmpty(BIRTHDAY)) {
				infoCust.setBirthdate(DateKit.parseDate(BIRTHDAY));
			}
			infoCust.setEmail(EMAIL);
			infoCust.setNickName("");
			infoCust.setOrgId(CHANNEL_CODE);
			/**
			 * 密码规则：密码密文+4位随机数，再MD5加密
			 */
			if (memberCenterConfiguration.getOpenRndnumEncryption().equals("true")) {
				//开启登录密码逻辑:MD5(密文+随机数)
				LOGIN_PWD = MD5Utils.MD5(LOGIN_PWD + rndNum) ;
			}
			infoCust.setLoginPwd(LOGIN_PWD);
			infoCust.setPayPwd(PAY_PWD);
			infoCust.setBlacklist("1");
			infoCust.setSmiSource(SMI_SOURCE);
			infoCust.setCrtDate(date);
			infoCust.setModDate(date);
			infoCust.setRandomNumber(rndNum);
			infoCust.setIssync((short)1);
			infoCustMapper.insertSelective(infoCust);
			CUST_ID = infoCust.getCustId();
		} catch (Exception e) {
			this.logger.error("会员基本资料新增失败", e);
			throw new BusiServiceException(e);
		}

		String LOGIN_USER_ID = null;
		try {
			LOGIN_USER_ID = this.commonAtomicService.getSequence("SEQ_INFO_LOGIN_USER");
		} catch (Exception e) {
			this.logger.error("调用序列查询失败", e);
			throw new BusiServiceException(e);
		}
		try {
			
			InfoLoginUser infoLoginUserDb = new InfoLoginUser();
			infoLoginUserDb.setLoginUserId(LOGIN_USER_ID);
			infoLoginUserDb.setCustId(CUST_ID);
			infoLoginUserDb.setLoginUserType(LOGIN_USER_TYPE);
			infoLoginUserDb.setSystemId(CHANNEL_CODE);
			infoLoginUserDb.setLoginUser(LOGIN_USER);
			infoLoginUserDb.setStatusCd("1000");
			infoLoginUserDb.setCrtDate(date);
			infoLoginUserDb.setModDate(date);
			int inserResult = infoLoginUserMapper.insertSelective(infoLoginUserDb);
			resMap.put("SUCC_SUM", inserResult);
		} catch (Exception e) {
			this.logger.error("会员账号新增失败", e);
			throw new BusiServiceException(e);
		}

		if (((Integer) resMap.get("SUCC_SUM")).intValue() < 1) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "2");
			map.put("MSG", "会员账号新增失败");
			return map;
		}
		
		/**
		 * 证件号码新增
		 */
		if ((StringUtils.isNotBlank(certiNbr)) && (!"null".equals(certiNbr))) {
			Map<String, Object> mapInput = new HashMap<String, Object>();
			String certi_id = "";
			try {
				certi_id = this.commonAtomicService.getSequenceByTable("INFO_CERTI");
			} catch (Exception e) {
				this.logger.error("调用INFO_CERTI序列查询原子服务获取证件标识失败", e);
				resMap.clear();
				resMap.put("RESP", RESP.createFailResp("999", "调用INFO_CERTI序列查询失败!"));
				return resMap;
			}
			try {
				
				mapInput.put("CERTI_ID", certi_id);
				mapInput.put("CUST_ID", CUST_ID);
				mapInput.put("CERTI_TYPE", certiType);
				mapInput.put("CERTI_NBR", certiNbr);
				mapInput.put("CERTI_ADDR", certiAddr);
				mapInput.put("STATUS_CD", "1000");
				mapInput.put("AUTH_FLAG", "1");
				resMap = custCertiAtomicService.addCerti(mapInput);
			} catch (Exception e) {
				this.logger.error("调用会员证件新增原子服务失败", e);
				/*throw new SmiBusinessException("1", "999", "会员证件新增失败");*/
				resMap.clear();
				resMap.put("RESP", RESP.createFailResp("999", "调用会员证件新增原子服务失败!"));
				return resMap;
			}
			mapInput.clear();
			resMap.clear();
		}

		String PAY_ID = null;
		try {
			String seqId = this.commonAtomicService.getSequence("SEQ_PAY_ID");
			PAY_ID=GenCustIdAndPayIdUtil.genPayId(seqId); // pay_id的生成
		} catch (Exception e) {
			this.logger.error("调用序列查询失败", e);
			throw new BusiServiceException(e);
		}

		try {
			Map<String, Object> infoPay = new HashMap<String, Object>();
			infoPay.put("pay_id", PAY_ID);
			infoPay.put("cust_id", CUST_ID);
			infoPay.put("prepay_flag", "1");
			resMap.clear();
			resMap = this.payAtomicService.addInfoPay(infoPay);
		} catch (Exception e) {
			this.logger.error("调用会员账户新增失败", e);
			throw new BusiServiceException(e);
		}

		if (((Integer) resMap.get("SUCC_SUM")).intValue() < 1) {
			map.put("RESULT", "1");
			map.put("CODE", "2");
			map.put("MSG", "会员账户新增失败");
			return map;
		}
        String V_CARD_NAME = this.codeCardMapper.getCardName();//根据cardid=1查询cardname(星美会员卡名称)
        
		String BUSI_ID = null;
		try {
			BUSI_ID = this.commonAtomicService.getSequence("SEQ_INFO_CUST_BUSI");
		} catch (Exception e) {
			this.logger.error("调用序列查询失败", e);
			throw new BusiServiceException(e);
		}

		BigDecimal OFFER_ID = null;
		String BUSI_NAME = null;
		try {
			Map<String, Object> offerName = new HashMap<String, Object>();
			offerName.put("OFFER_NAME", "星美生活业务");
			resMap.clear();
			resMap = this.codeOfferAtomicService.qryCodeOfferByOffername(offerName);
		} catch (Exception e) {
			this.logger.error("调用序列查询失败", e);
			throw new BusiServiceException(e);
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> CODE_OFFER = (List) resMap.get("CODE_OFFER");
		if (CODE_OFFER.size() >= 1) {
			if (((Map) CODE_OFFER.get(0)).containsKey("OFFER_ID")) {
				OFFER_ID = (BigDecimal) ((Map) CODE_OFFER.get(0)).get("OFFER_ID");
			}
			if (((Map) CODE_OFFER.get(0)).containsKey("OFFER_NAME"))
				BUSI_NAME = (String) ((Map) CODE_OFFER.get(0)).get("OFFER_NAME");
		} else {
			map.put("RESULT", "1");
			map.put("CODE", "2");
			map.put("MSG", "未查询到需要办理的基础业务");
			return map;
		}

		try {
			Map<String, Object> custBusi = new HashMap<String, Object>();
			custBusi.put("BUSI_ID", BUSI_ID);
			custBusi.put("CUST_ID", CUST_ID);
			custBusi.put("OFFER_ID", OFFER_ID);
			custBusi.put("BUSI_NAME", BUSI_NAME);
			custBusi.put("BUSI_NBR", "");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			custBusi.put("EFF_DATE", df.format(new Date()));
			custBusi.put("EXP_DATE", "2099-12-31");
			custBusi.put("STATUS_CD", Constants.CUST_BUSI_STATUS_CD_VALID);
			//增加会员信息的三个字段
			custBusi.put("CARD_ID", 1);
			custBusi.put("CARD_NAME", V_CARD_NAME);
			custBusi.put("CARD_NBR", V_CUST_NBR);
			resMap.clear();
			resMap = this.custBusiAtomicService.addCustBusi(custBusi);
		} catch (Exception e) {
			this.logger.error("调用会员业务新增原子服务", e);
			throw new BusiServiceException(e);
		}

		if (((Integer) resMap.get("SUCC_SUM")).intValue() < 1) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "2");
			map.put("MSG", "会员业务新增失败");
			return map;
		}

		String REL_ID = null;
		try {
			REL_ID = this.commonAtomicService.getSequence("SEQ_INFO_BUSI_PAY_REL");
		} catch (Exception e) {
			this.logger.error("调用序列查询失败", e);
			throw new BusiServiceException(e);
		}
		try {
			Map<String, Object> infoBusiPay = new HashMap<String, Object>();
			infoBusiPay.put("REL_ID", REL_ID);
			infoBusiPay.put("PAY_ID", PAY_ID);
			infoBusiPay.put("BUSI_ID", BUSI_ID);
			infoBusiPay.put("STATUS_CD", "1000");
			resMap.clear();
			resMap = this.infoBusiPayAtomicService.addInfoBusiPay(infoBusiPay);
		} catch (Exception e) {
			this.logger.error("业务账户关系新增失败", e);
			throw new BusiServiceException(e);
		}

		if (((Integer) resMap.get("SUCC_SUM")).intValue() < 1) {
			map.put("RESULT", "1");
			map.put("CODE", "2");
			map.put("MSG", "会员业务新增失败");
			return map;
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Map<String, Object> acctBalance = new HashMap<String, Object>();

			acctBalance.put("PAY_ID", PAY_ID);
			acctBalance.put("BALANCE_TYPE_ID", "1");
			acctBalance.put("REAL_BALANCE", "0");
			acctBalance.put("BALANCE", "0");
			acctBalance.put("EFF_DATE", df.format(new Date()));
			acctBalance.put("EXP_DATE", "20991231235959");
			resMap.clear();
			resMap = this.acctBalanceAtomicService.acctBalanceInsert(acctBalance);
		} catch (Exception e) {
			this.logger.error("余额中心账本新增失败", e);
			throw new BusiServiceException(e);
		}

		if (((Integer) resMap.get("SUCC_NUM")).intValue() < 1) {
			map.put("RESULT", "1");
			map.put("CODE", "2");
			map.put("MSG", "会员业务新增失败");
			return map;
		}

		/**
		 * 删除订单日志新增逻辑 
		 */
		
		/*try {
			Map<String, Object> order = new HashMap<String, Object>();
			Map<String, Object> ADD_ORDER_REQ = new HashMap<String, Object>();
			List<Map<String, Object>> ADD_ORDER_CHILD_REQ = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> ADD_ORDER_PAY_REQ = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> ADD_ORDER_DETAIL_REQ = new ArrayList<Map<String, Object>>();
			Map<String, Object> addOrderChildReq = new HashMap<String, Object>();
			List<Map<String, Object>> ADD_ORDER_FEE_REQ = new ArrayList<Map<String, Object>>();

			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String CREATE_DATE = df2.format(new Date());

			addOrderChildReq.put("OBJ_TYPE", "100");
			addOrderChildReq.put("OBJ_ID", CUST_ID);
			addOrderChildReq.put("CREATE_DATE", CREATE_DATE);
			addOrderChildReq.put("ADD_ORDER_DETAIL_REQ", ADD_ORDER_DETAIL_REQ);
			addOrderChildReq.put("ADD_ORDER_FEE_REQ", ADD_ORDER_FEE_REQ);
			ADD_ORDER_CHILD_REQ.add(addOrderChildReq);

			ADD_ORDER_REQ.put("ORDER_ID", "");
			ADD_ORDER_REQ.put("CUST_ID", CUST_ID);
			ADD_ORDER_REQ.put("CREATE_DATE", CREATE_DATE);
			ADD_ORDER_REQ.put("ORDER_TYPE", "1000");
			ADD_ORDER_REQ.put("CREATE_OPERATOR_ID", SYSTEM_USER_ID == null ? CHANNEL_CODE : SYSTEM_USER_ID);
			ADD_ORDER_REQ.put("CREATE_CHANNEL_ID", CHANNEL_CODE);
			ADD_ORDER_REQ.put("EXT_SYSTEM", EXT_SYSTEM);
			ADD_ORDER_REQ.put("EXT_ORDER_ID", "");
			ADD_ORDER_REQ.put("BUSINESS_TYPE", "13");
			ADD_ORDER_REQ.put("SERV_CODE", "100");
			ADD_ORDER_REQ.put("DEVICE_NUMBER", "");
			ADD_ORDER_REQ.put("STATUS_CD", "1098");
			ADD_ORDER_REQ.put("ADD_ORDER_CHILD_REQ", ADD_ORDER_CHILD_REQ);
			ADD_ORDER_REQ.put("ADD_ORDER_PAY_REQ", ADD_ORDER_PAY_REQ);

			order.put("ADD_ORDER_REQ", ADD_ORDER_REQ);
			resMap.clear();
			resMap = this.orderService.addOrders(order);
		} catch (Exception e) {
			this.logger.error("调用订单新增业务服务失败", e);
			throw new BusiServiceException(e);
		}

		if (0 != ((Integer) resMap.get("RESULT")).intValue()) {
			map.put("RESULT", "1");
			map.put("CODE", resMap.get("CODE"));
			map.put("MSG", resMap.get("MSG"));
			return map;
		}*/

		map.put("RESULT", Constants.STATE_CODE_SUCC);
		map.put("CODE", "");
		map.put("MSG", "");
		map.put("CUST_ID", CUST_ID);
		return map;
	}
}