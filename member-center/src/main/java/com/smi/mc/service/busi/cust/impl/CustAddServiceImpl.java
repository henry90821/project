package com.smi.mc.service.busi.cust.impl;

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
import org.springframework.transaction.annotation.Transactional;

import com.smi.mc.constants.Constants;
import com.smi.mc.constants.SeqConstants;
import com.smi.mc.dao.cust.CodeCardMapper;
import com.smi.mc.dao.cust.CodeListMapper;
import com.smi.mc.dao.cust.CustBusiInfoMapper;
import com.smi.mc.dao.cust.CustInfoMapper;
import com.smi.mc.dao.cust.InfoLoginUserMapper;
import com.smi.mc.dao.cust.OrgOpMapper;
import com.smi.mc.exception.AtomicServiceException;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.po.RESP;
import com.smi.mc.service.atomic.billing.AcctBalanceAtomicService;
import com.smi.mc.service.atomic.cust.CommonAtomicService;
import com.smi.mc.service.atomic.cust.CustAtomicService;
import com.smi.mc.service.atomic.cust.CustCertiAtomicService;
import com.smi.mc.service.atomic.cust.LogInterfaceAtomicService;
import com.smi.mc.service.atomic.cust.PayAtomicService;
import com.smi.mc.service.busi.cust.BusiAddService;
import com.smi.mc.service.busi.cust.CustAddService;
import com.smi.mc.service.busi.cust.VCustNbrService;
import com.smi.mc.utils.CheckParamUtils;
import com.smi.mc.utils.DateUtils;
import com.smi.mc.utils.GenCustIdAndPayIdUtil;
import com.smi.mc.utils.IDCard;
import com.smi.mc.utils.MD5Utils;
import com.smi.tools.kits.RandomKit;
import com.smilife.core.exception.SmiBusinessException;

@Service("custAddService")
public class CustAddServiceImpl implements CustAddService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrgOpMapper orgOpMapper;

	@Autowired
	private CustBusiInfoMapper custBusiInfoMapper;

	@Autowired
	private CodeCardMapper codeCardMapper;

	@Autowired
	private CommonAtomicService commonAtomicService;

	@Autowired
	private CustAtomicService custAtomicService;

	@Autowired
	private InfoLoginUserMapper infoLoginUserMapper;

	@Autowired
	private CustCertiAtomicService custCertiAtomicService;

	@Autowired
	private PayAtomicService payAtomicService;

	@Autowired
	private BusiAddService busiAddService;

	@Autowired
	private VCustNbrService vCustNbrService;

	@Autowired
	private LogInterfaceAtomicService logInterfaceAtomicService;
	
	@Autowired
	private CodeListMapper codeListMapper;
	
	@Autowired
	private CustInfoMapper custInfoMapper;
	
	@Resource
	private AcctBalanceAtomicService acctBalanceAtomicService;
 
	@Override
	@Transactional(rollbackFor=Exception.class,value="rdsTransactionManager")
	public Map<String, Object> addCustByMTX(Map<String, Object> param) throws SmiBusinessException {
		logger.info("-----满天星会员资料新增接口参数-----" + param.toString());
		Date head = new Date();
		String ext_system = (String) param.get("EXT_SYSTEM");
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			Map<String, Object> checkmap = CheckParamUtils.checkParams(param,
					new String[] { "CHANNEL_CODE", "EXT_SYSTEM", "SYSTEM_USER_ID","CONTACT_MOBILE", "PAY_PWD", "CUST_NAME", "CARD_NAME",
							"EFF_DATE", "EXP_DATE" });
			if ("1".equals(checkmap.get("STATUS"))) {
				resMap.clear();
				resMap.put("RESP", RESP.createFailResp("1", "必填参数不能为空"));
				return resMap;
			}

			String channel_code = (String) param.get("CHANNEL_CODE");
			String card_name = (String) param.get("CARD_NAME");
			String certi_nbr = (String) param.get("CERTI_NBR");
			String certi_type = (String) param.get("CERTI_TYPE");
			String card_nbr = (String) param.get("CARD_NBR");
			String contact_mobile = (String) param.get("CONTACT_MOBILE");
			String eff_date = (String) param.get("EFF_DATE");
			String exp_date = (String) param.get("EXP_DATE");
			
			Date eff_dt = DateUtils.parseDatetime(eff_date, "yyyy-MM-dd");
			Date exp_dt = DateUtils.parseDatetime(exp_date, "yyyy-MM-dd");
			
			if(!DateUtils.isBefore(eff_dt, exp_dt)){
				resMap.clear();
				resMap.put("RESP", RESP.createFailResp("1", "失效时间不能小于生效时间!"));
				return resMap;
			}
			//会员卡为空时，调生成虚拟会员卡号服务
			if(StringUtils.isEmpty(card_nbr)){
				try {
					Map<String, Object> addVCustNbr = vCustNbrService.addVCustNbr();
					card_nbr = (String)addVCustNbr.get("V_CUST_NBR");
					param.put("CARD_NBR", card_nbr);
				} catch (BusiServiceException e) {
					logger.error("生成虚拟会员卡号序列失败", e);
					/*throw new SmiBusinessException("生成虚拟会员卡号序列失败");*/
					resMap.clear();
					resMap.put("RESP", RESP.createFailResp("999", "生成虚拟会员卡号序列失败!"));
					return resMap;
				}
			}
			
			Map<String, Object> searchParam = new HashMap<String, Object>();
			//根据影院编码查询渠道编码
			searchParam.put("CHANNEL_CODE",channel_code);
			String orgId = codeListMapper.qryCodeList(searchParam);
			if(StringUtils.isEmpty(orgId)){
				resMap.clear();
				resMap.put("RESP", RESP.createFailResp("1", "根据影院编码为"+channel_code+"查询不到渠道编码！"));
				return resMap;
			}
			param.put("ORGID", orgId);
			searchParam.clear();
			// 根据会员卡号和状态调用会员业务查询原子服务,判断会员卡号是否存在
			searchParam.put("STATUS_CD", "1000");
			searchParam.put("CARD_NBR", card_nbr);
			List<Map<String, Object>> busiList = custBusiInfoMapper.isExistCardNbr(searchParam);
			searchParam.clear();
			if (busiList != null && busiList.size() > 0) {
				resMap.clear();
				resMap.put("RESP", RESP.createFailResp("2", "会员卡号已存在"));
				return resMap;
			
			} else {
				// 未存在该会员卡号
				// 根据会员卡名称查询CARD_ID和OFFER_ID
				searchParam.put("CARD_NAME", card_name);
				Map<String, Object> resultMap = codeCardMapper.getOfferId(searchParam);
				if(resultMap == null ){
					resMap.clear();
					resMap.put("RESP", RESP.createFailResp("999", "输入参数CARD_NAME在code_card表查询不到数据！"));
					return resMap;
				}
				
				searchParam.clear();
				String offerId =  resultMap.get("OFFER_ID").toString();
				String cardId =  resultMap.get("CARD_ID").toString();
				param.put("offerId", offerId);
				param.put("cardId", cardId);
				
				//根据联系电话和状态调用会员账号查询原子服务
				searchParam.put("LOGIN_USER", contact_mobile);
				searchParam.put("STATUS_CD", "1000");
				List<Map<String, Object>> qryInfoLoginUser = infoLoginUserMapper.qryInfoLoginUser(searchParam);
				if(qryInfoLoginUser.size() > 0){
					if(Constants.SMI_BUSI_OFFER.equals(offerId)){
						//存在账号 如果是星美生活会员卡业务,则返回该手机已存在，不能办理星美生活会员卡业务
						resMap.put("RESP", RESP.createFailResp("-1", "该手机号已办理过星美生活会员卡,不能重复办理！"));
					}else  {
						//存在账号 如果是随影卡业务则直接办理业务
						String custId = (String)qryInfoLoginUser.get(0).get("CUST_ID");
						param.put("custId", custId);
						resMap = busiOperation(param);
					} 
					
				}else{
					if (StringUtils.isNotEmpty(certi_nbr) && "10".equals(certi_type)) {
						// 校验身份证
						String validatreResult = IDCard.IDCardValidate(certi_nbr);
						if (!"".equals(validatreResult)) {
							// 校验失败
							resMap.clear();
							resMap.put("RESP", RESP.createFailResp("3", "身份证号码格式不正确"));
							return resMap;
						
						} else {
							// 校验身份证成功 满天星会员资料新增核心业务逻辑
							resMap = custBusiLogincForMTX(param);
						}
					} else {
						// 满天星会员资料新增核心业务逻辑
						resMap = custBusiLogincForMTX(param);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			/*throw new SmiBusinessException("调用接口失败");*/
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("999", "调用接口失败!"));
			return resMap;
		} finally {
			Date end = new Date();
			
			Map<String, Object> logMap = new HashMap<String, Object>();
			logMap.put("REQUEST", param.toString());
			logMap.put("REQ_DATE", head);
			logMap.put("RESPONSE", resMap.toString());
			logMap.put("RES_DATE", end);
			logMap.put("REQ_SYSTEM", ext_system == null ? "" : ext_system);
			logMap.put("RES_SYSTEM", "132");
			logMap.put("INTERFACE_CODE", "CUST_ADD");
			try {
				String logId = commonAtomicService.getSequence(SeqConstants.SEQ_LOG_INTERFACE);
				logMap.put("LOG_ID", logId);
				logInterfaceAtomicService.addLogInterface(logMap);
			} catch (AtomicServiceException e) {
				this.logger.error("接口日志新增异常", e);
				/*throw new SmiBusinessException("接口日志新增异常");*/
				resMap.clear();
				resMap.put("RESP", RESP.createFailResp("999", "接口日志新增异常!"));
				return resMap;
			}
		}
		return resMap;
	}

	/**
	 * 满天星会员资料新增核心业务逻辑
	 * 
	 * @param paramMap
	 * @return
	 * @throws BusiServiceException
	 * @throws AtomicServiceException 
	 * @throws SmiBusinessException 
	 */
	@Transactional(readOnly=false)
	private Map<String, Object> custBusiLogincForMTX(Map<String, Object> paramMap) throws BusiServiceException, SmiBusinessException, AtomicServiceException {

		int loginUserRes = 0;
		Map<String, Object> resMap = new HashMap<String, Object>();
		Map<String, Object> mapInput = new HashMap<String, Object>();

		String orgid = (String) paramMap.get("ORGID");
		String ext_system = (String) paramMap.get("EXT_SYSTEM");
		String contact_mobile = (String) paramMap.get("CONTACT_MOBILE");
		String pay_pwd = (String) paramMap.get("PAY_PWD");
		String cust_name = (String) paramMap.get("CUST_NAME");
		String eff_date = (String) paramMap.get("EFF_DATE");
		String exp_date = (String) paramMap.get("EXP_DATE");
		String card_nbr = (String) paramMap.get("CARD_NBR");
		String sex = (String) paramMap.get("SEX");
		String certi_type = (String) paramMap.get("CERTI_TYPE");
		String certi_nbr = (String) paramMap.get("CERTI_NBR");
		String certi_addr = (String) paramMap.get("CERTI_ADDR");
		String offerId =  paramMap.get("offerId").toString();
		String cardId = paramMap.get("cardId").toString();
		String card_name = (String) paramMap.get("CARD_NAME");
		String system_user_id = (String) paramMap.get("SYSTEM_USER_ID");
		
		String cust_id = "";

		String smi_source = "";
		if ((null != paramMap.get("SMI_SOURCE")) && (!"".equals(paramMap.get("SMI_SOURCE"))))
			smi_source = (String) paramMap.get("SMI_SOURCE");
		else {
			smi_source = "0";
		}
		// 会员标识序列号、账户标识系列号
		try {
			cust_id = GenCustIdAndPayIdUtil.genCustId(commonAtomicService.getSequence(SeqConstants.SEQ_CUST_ID));
		} catch (AtomicServiceException e) {
			logger.error("调用cust_id序列查询失败", e);
			/*throw new SmiBusinessException("调用cust_id序列查询失败");*/
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("999", "调用cust_id序列查询失败!"));
			return resMap;
		}

		pay_pwd = MD5Utils.MD5(pay_pwd);
		
		/**
		 * 登录密码密文再随机数加密
		 */
		String rnNum = RandomKit.randomNumbers(4);
		String loginPwd = MD5Utils.MD5(pay_pwd + rnNum);

		Map<String, Object> custMap = new HashMap<String, Object>();
		custMap.put("CUST_ID", cust_id);
		custMap.put("CUST_NAME", cust_name);
		custMap.put("CUST_VIP_LEVEL", "1000");
		custMap.put("CUST_TYPE", "1000");
		custMap.put("STATUS_CD", "1000");
		custMap.put("CARD_TYPE", Constants.CARD_TYPE_ENTITY);
		custMap.put("CUST_NBR", card_nbr);
		custMap.put("CONTACT_MOBILE", contact_mobile);
		custMap.put("CONTACT_ADDR", "");
		custMap.put("SEX", sex);
		custMap.put("BIRTHDATE", "");
		custMap.put("EMAIL", "");
		custMap.put("NICK_NAME", "");
		custMap.put("ORG_ID", orgid);
		custMap.put("LOGIN_PWD", loginPwd);
		custMap.put("PAY_PWD", pay_pwd);
		custMap.put("BLACKLIST", "1");
		custMap.put("SMI_SOURCE", smi_source);
		custMap.put("RANDOM_NUMBER", rnNum);
		custMap.put("ISSYNC", 1);
		try {
			cust_id = custAtomicService.addCust(custMap);
		} catch (AtomicServiceException e) {
			logger.error("会员基本资料新增失败", e);
			/*throw new SmiBusinessException("会员基本资料新增失败");*/
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("999", "会员基本资料新增失败!"));
			return resMap;
			
		}
		// 会员账号新增
		String login_user_id = null;
		try {
			login_user_id = commonAtomicService.getSequence("SEQ_INFO_LOGIN_USER");
		} catch (Exception e) {
			this.logger.error("调用SEQ_INFO_LOGIN_USER序列查询失败", e);
			/*throw new SmiBusinessException("调用SEQ_INFO_LOGIN_USER序列查询失败");*/
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("999", "调用SEQ_INFO_LOGIN_USER序列查询失败!"));
			return resMap;
		}

		try {
			Map<String, Object> infoLoginUser = new HashMap<String, Object>();
			infoLoginUser.put("LOGIN_USER_ID", login_user_id);
			infoLoginUser.put("CUST_ID", cust_id);
			infoLoginUser.put("LOGIN_USER_TYPE", "1000");
			infoLoginUser.put("SYSTEM_ID", ext_system);
			infoLoginUser.put("LOGIN_USER", contact_mobile);
			infoLoginUser.put("STATUS_CD", "1000");

			loginUserRes = infoLoginUserMapper.addInfoLoginUser(infoLoginUser);
		} catch (Exception e) {
			this.logger.error("会员账号新增失败", e);
			/*throw new SmiBusinessException("会员账号新增失败");*/
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("999", "会员账号新增失败!"));
			return resMap;
		}

		if (loginUserRes < 1) {
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("2", "会员账号新增失败"));
			return resMap;
		}

		// 证件号码新增
		if ((StringUtils.isNotBlank(certi_nbr)) && (!"null".equals(certi_nbr))) {
			String certi_id = "";
			try {
				certi_id = this.commonAtomicService.getSequenceByTable("INFO_CERTI");
			} catch (Exception e) {
				this.logger.error("调用INFO_CERTI序列查询原子服务获取证件标识失败", e);
				/*throw new SmiBusinessException("1", "999", "调用INFO_CERTI序列查询失败");*/
				resMap.clear();
				resMap.put("RESP", RESP.createFailResp("999", "调用INFO_CERTI序列查询失败!"));
				return resMap;
			}

			try {
				mapInput.put("CERTI_ID", certi_id);
				mapInput.put("CUST_ID", cust_id);
				mapInput.put("CERTI_TYPE", certi_type);
				mapInput.put("CERTI_NBR", certi_nbr);
				mapInput.put("CERTI_ADDR", certi_addr);
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

		// 会员账户新增
		String seq_payId = "";
		try {
			seq_payId = GenCustIdAndPayIdUtil.genCustId(commonAtomicService.getSequence(SeqConstants.SEQ_PAY_ID));
		} catch (AtomicServiceException e) {
			logger.error("pay_id序列查询失败", e);
			/*throw new SmiBusinessException("pay_id序列查询失败");*/
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("999", "pay_id序列查询失败!"));
			return resMap;
		}

		try {
			Map<String, Object> infoPay = new HashMap<String, Object>();
			infoPay.put("pay_id", seq_payId);
			infoPay.put("cust_id", cust_id);
			infoPay.put("prepay_flag", "1");
			resMap.clear();
			resMap = payAtomicService.addInfoPay(infoPay);
		} catch (Exception e) {
			this.logger.error("调用会员账户新增失败", e);
			/*throw new SmiBusinessException("调用会员账户新增失败");*/
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("999", "调用会员账户新增失败!"));
			return resMap;
		}

		if (((Integer) resMap.get("SUCC_SUM")).intValue() < 1) {
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("2", "会员账户新增失败"));
			return resMap;
		}

		// 计费数据库新建余额账本
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Map<String, Object> acctBalance = new HashMap<String, Object>();

			acctBalance.put("PAY_ID", seq_payId);
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
			resMap.put("RESULT", "1");
			resMap.put("CODE", "2");
			resMap.put("MSG", "余额中心账本新增失败");
			return resMap;
		}
		
		/**
		 * 会员业务服务受理：
		 */
		Map<String, Object> busiMap = new HashMap<String, Object>();
		busiMap.put("CHANNEL_CODE", orgid);
		busiMap.put("SYSTEM_USER_ID", system_user_id);
		busiMap.put("EXT_SYSTEM", ext_system);
		busiMap.put("CUST_ID", cust_id);
		busiMap.put("SERV_CODE", "");
		busiMap.put("EXT_ORDER_ID", "");

		if (Constants.SMI_BUSI_OFFER.equals(offerId)) {
			// 星美生活业务
			Map<String, Object> busiParam = new HashMap<String, Object>();
			busiParam.put("OFFER_ID", offerId);
			busiParam.put("EFF_DATE", eff_date);
			busiParam.put("EXP_DATE", "2099-12-31");
			busiParam.put("BUSI_NBR", "");
			busiParam.put("CARD_ID", cardId);
			busiParam.put("CARD_NAME", card_name);
			busiParam.put("CARD_NBR", card_nbr);

			List<Map<String, Object>> busi = new ArrayList<Map<String, Object>>();
			busi.add(busiParam);
			busiMap.put("BUSI", busi);

		} else {
			// 非星美生活业务
			Map<String, Object> busiParam = new HashMap<String, Object>();
			busiParam.put("OFFER_ID", offerId);
			busiParam.put("EFF_DATE", eff_date);
			busiParam.put("EXP_DATE", exp_date);
			busiParam.put("BUSI_NBR", "");
			busiParam.put("CARD_ID", cardId);
			busiParam.put("CARD_NAME", card_name);
			busiParam.put("CARD_NBR", card_nbr);

			String card_nbr_smi = "";
			try {
				Map<String, Object> addVCustNbr = vCustNbrService.addVCustNbr();
				card_nbr_smi = (String)addVCustNbr.get("V_CUST_NBR");
			} catch (BusiServiceException e) {
				logger.error("生成虚拟会员卡号序列失败", e);
				/*throw new SmiBusinessException("生成虚拟会员卡号序列失败");*/
				resMap.clear();
				resMap.put("RESP", RESP.createFailResp("999", "生成虚拟会员卡号序列失败!"));
				return resMap;
			}
			
			Map<String, Object> busiSmiParam = new HashMap<String, Object>();
			busiSmiParam.put("OFFER_ID", Constants.SMI_BUSI_OFFER);
			busiSmiParam.put("EFF_DATE", eff_date);
			busiSmiParam.put("EXP_DATE", "2099-12-31");
			busiSmiParam.put("BUSI_NBR", "");
			busiSmiParam.put("CARD_ID", 1);
			busiSmiParam.put("CARD_NAME", "星美生活会员");
			busiSmiParam.put("CARD_NBR", card_nbr_smi);

			List<Map<String, Object>> busi = new ArrayList<Map<String, Object>>();
			busi.add(busiParam);
			busi.add(busiSmiParam);
			busiMap.put("BUSI", busi);
		}

		try {
			resMap = busiAddService.addBusi(busiMap);
			logger.info("======调用会员业务受理业务:" + resMap);
		} catch (BusiServiceException e) {
			this.logger.error("调用会员业务受理业务服务失败", e);
			/*throw new SmiBusinessException("1", "5", "会员业务办理失败");*/
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("999", "调用会员业务受理业务服务失败!"));
			return resMap;
			
		}
		if (!"0".equals(resMap.get("RESULT"))){
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("1", "会员业务办理失败,原因：" + resMap.get("MSG")));
			return resMap;
		}
/*			throw new SmiBusinessException("1", "5", "会员业务办理失败,原因：" + resMap.get("MSG"));
*/
		if("0".equals(resMap.get("RESULT"))){
			resMap.clear();
			Map<String, Object> custParam = new HashMap<>();
			custParam.put("PAY_PWD", pay_pwd);
			custParam.put("CUST_NAME", cust_name);
			custParam.put("CUST_ID", cust_id);
			this.updateCust(custParam);
			if(Constants.SMI_BUSI_OFFER.equals(offerId)){
				resMap.put("RESP", RESP.createSuccessResp("4", "新增会员成功，并办理" + card_name + "成功！"));
			}else{
				resMap.put("RESP", RESP.createSuccessResp("5", "新增会员成功，并办理" + card_name + "成功！"));
			}
		}
		
		return resMap;
	}

	/**
	 * 会员业务受理方法
	 * 1.如果传参是办理星美会员业务，根据会员标识判断该用户是否办理过星美会员业务，有则更新info_cust表的cust_nbr字段和info_cust_busi表的card_nbr字段，无则继续新增
	 * 2.随影卡业务只管新增
	 * @param paramMap
	 * @return
	 * @throws BusiServiceException
	 * @throws AtomicServiceException 
	 * @throws SmiBusinessException 
	 */
	@Transactional(readOnly=false)
	private Map<String,Object> busiOperation(Map<String,Object> paramMap) throws BusiServiceException, SmiBusinessException, AtomicServiceException {
		Map<String,Object> resMap = new HashMap<>();
		
		String custId = (String)paramMap.get("custId");
		String offerId = (String)paramMap.get("offerId");
		String card_nbr = (String)paramMap.get("CARD_NBR");
		String orgid = (String) paramMap.get("ORGID");
		String ext_system = (String) paramMap.get("EXT_SYSTEM");
		String system_user_id = (String) paramMap.get("SYSTEM_USER_ID");
		//更新姓名和密码字段
		String passWord = (String)paramMap.get("PAY_PWD");
		String cust_name = (String)paramMap.get("CUST_NAME");
		
		String eff_date = (String) paramMap.get("EFF_DATE");
		String exp_date = (String) paramMap.get("EXP_DATE");
		String cardId = paramMap.get("cardId").toString();
		String card_name = (String) paramMap.get("CARD_NAME");
		
		Map<String, Object> busiMap = new HashMap<String, Object>();
		busiMap.put("CHANNEL_CODE", orgid);
		busiMap.put("SYSTEM_USER_ID", system_user_id);
		busiMap.put("EXT_SYSTEM", ext_system);
		busiMap.put("CUST_ID", custId);
		busiMap.put("SERV_CODE", "");
		busiMap.put("EXT_ORDER_ID", "");
		
		if (Constants.SMI_BUSI_OFFER.equals(offerId)) {
			// 星美生活业务
			Map<String, Object> busiParam = new HashMap<String, Object>();
			busiParam.put("OFFER_ID", offerId);
			busiParam.put("EFF_DATE", eff_date);
			busiParam.put("EXP_DATE", "2099-12-31");
			busiParam.put("BUSI_NBR", "");
			busiParam.put("CARD_ID", cardId);
			busiParam.put("CARD_NAME", card_name);
			busiParam.put("CARD_NBR", card_nbr);

			List<Map<String, Object>> busi = new ArrayList<Map<String, Object>>();
			busi.add(busiParam);
			busiMap.put("BUSI", busi);

		} else {
			// 非星美生活业务
			Map<String, Object> busiParam = new HashMap<String, Object>();
			busiParam.put("OFFER_ID", Constants.SMI_BUSI_OFFER);
			busiParam.put("EFF_DATE", eff_date);
			busiParam.put("EXP_DATE", "2099-12-31");
			busiParam.put("BUSI_NBR", "");
			busiParam.put("CARD_ID", 1);
			busiParam.put("CARD_NAME", "星美生活会员");
			busiParam.put("CARD_NBR", card_nbr);

			Map<String, Object> busiSmiParam = new HashMap<String, Object>();
			busiSmiParam.put("OFFER_ID", offerId);
			busiSmiParam.put("EFF_DATE", eff_date);
			busiSmiParam.put("EXP_DATE", exp_date);
			busiSmiParam.put("BUSI_NBR", "");
			busiSmiParam.put("CARD_ID", cardId);
			busiSmiParam.put("CARD_NAME", card_name);
			busiSmiParam.put("CARD_NBR", card_nbr);

			List<Map<String, Object>> busi = new ArrayList<Map<String, Object>>();
			busi.add(busiParam);
			busi.add(busiSmiParam);
			busiMap.put("BUSI", busi);
		}
		
		try {
			resMap = busiAddService.addBusi(busiMap);
			logger.info("======调用会员业务受理业务:" + resMap);
		} catch (BusiServiceException e) {
			this.logger.error("调用会员业务受理业务服务失败", e);
			/*throw new SmiBusinessException("1", "5", "会员业务办理失败");*/
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("999", "调用会员业务受理业务服务失败!"));
			return resMap;
			
		}
		if (!"0".equals(resMap.get("RESULT"))){
			resMap.clear();
			resMap.put("RESP", RESP.createFailResp("1", "会员业务办理失败,原因：" + resMap.get("MSG")));
			return resMap;
		}
/*			throw new SmiBusinessException("1", "5", "会员业务办理失败,原因：" + resMap.get("MSG"));
*/
		if("0".equals(resMap.get("RESULT"))){
			resMap.clear();
			Map<String, Object> custParam = new HashMap<>();
			custParam.put("PAY_PWD", MD5Utils.MD5(passWord));
			custParam.put("CUST_NAME", cust_name);
			custParam.put("CUST_ID", custId);
			this.updateCust(custParam);
			if(Constants.SMI_BUSI_OFFER.equals(offerId)){
				resMap.put("RESP", RESP.createSuccessResp("6", "已存在该会员信息，办理星美生活会员换卡成功！"));
			}else{
				resMap.put("RESP", RESP.createSuccessResp("7", "已存在该会员信息，办理" + card_name + "成功！"));
			}
			
		}
		
		return resMap;
	}	
	
	private int updateCust(Map<String, Object> custParam) throws SmiBusinessException, AtomicServiceException{
		return custAtomicService.updCust(custParam);
	}
	
}
