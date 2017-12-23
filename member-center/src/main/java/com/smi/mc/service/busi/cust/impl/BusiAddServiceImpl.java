package com.smi.mc.service.busi.cust.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.mc.constants.Constants;
import com.smi.mc.exception.AtomicServiceException;
import com.smi.mc.exception.BusiServiceException;
import com.smi.mc.po.RESP;
import com.smi.mc.service.atomic.cust.CodeOfferAtomicService;
import com.smi.mc.service.atomic.cust.CommonAtomicService;
import com.smi.mc.service.atomic.cust.CustAtomicService;
import com.smi.mc.service.atomic.cust.CustBusiAtomicService;
import com.smi.mc.service.busi.cust.BusiAddService;
import com.smi.mc.service.busi.cust.BusiQryCustService;
import com.smi.mc.service.busi.cust.OrderService;

/**
 * 会员业务受理服务
 * 
 * @author smi
 *
 */
@Service("busiAddService")
public class BusiAddServiceImpl implements BusiAddService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CodeOfferAtomicService codeOfferAtomicService;

	@Autowired
	private CommonAtomicService commonAtomicService;

	@Autowired
	private CustBusiAtomicService custBusiAtomicService;

	@Autowired
	private CustAtomicService custAtomicService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private BusiQryCustService busiQryCustService;

	@SuppressWarnings("unchecked")
	public Map<String, Object> addBusi(Map<String, Object> param) throws BusiServiceException {

		Map<String, Object> map = new HashMap<String, Object>();
		if (param.isEmpty()) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "1100");
			map.put("MSG", "传入的参数不能为空");
			return map;
		}
		String CHANNEL_CODE = (String) param.get("CHANNEL_CODE");
		String SYSTEM_USER_ID = (String) param.get("SYSTEM_USER_ID");
		String EXT_SYSTEM = (String) param.get("EXT_SYSTEM");
		String CUST_ID = (String) param.get("CUST_ID");
		// 外部系统订单号
		String EXT_ORDER_ID = (String) param.get("EXT_ORDER_ID");
		if (StringUtils.isEmpty(CHANNEL_CODE)) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "1100");
			map.put("MSG", "CHANNEL_CODE必填参数为空");
			return map;
		}
		if (StringUtils.isEmpty(SYSTEM_USER_ID)) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "1100");
			map.put("MSG", "SYSTEM_USER_ID必填参数为空");
			return map;
		}
		if (StringUtils.isEmpty(EXT_SYSTEM)) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "1100");
			map.put("MSG", "EXT_SYSTEM必填参数为空");
			return map;
		}
		if (StringUtils.isEmpty(CUST_ID)) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "1100");
			map.put("MSG", "CUST_ID必填参数为空");
			return map;
		}

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> BUSI = (List<Map<String, Object>>) param.get("BUSI");
		if ((BUSI == null) || (BUSI.isEmpty())) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", "1100");
			map.put("MSG", "必填参数为空");
			return map;
		}
		for (int i = 0; i < BUSI.size(); i++) {
			String OFFER_ID = String.valueOf(((Map<String, Object>) BUSI.get(i)).get("OFFER_ID"));
			String EFF_DATE = (String) ((Map<String, Object>) BUSI.get(i)).get("EFF_DATE");
			String EXP_DATE = (String) ((Map<String, Object>) BUSI.get(i)).get("EXP_DATE");
			if (StringUtils.isEmpty(OFFER_ID)) {
				map.put("RESULT", Constants.STATE_CODE_FAIL);
				map.put("CODE", "1100");
				map.put("MSG", "必填参数为空");
				return map;
			}
			if (StringUtils.isEmpty(EFF_DATE)) {
				map.put("RESULT", Constants.STATE_CODE_FAIL);
				map.put("CODE", "1100");
				map.put("MSG", "必填参数为空");
				return map;
			}
			if (StringUtils.isEmpty(EXP_DATE)) {
				map.put("RESULT", Constants.STATE_CODE_FAIL);
				map.put("CODE", "1100");
				map.put("MSG", "必填参数为空");
				return map;
			}

		}

		Map<String, Object> resMap = new HashMap<String, Object>();
		String[] offerNames = new String[BUSI.size()];
		String[] purchaseMonthStrs = new String[BUSI.size()];
		String OFFER_ID = "";
		for (int i = 0; i < BUSI.size(); i++) {
			OFFER_ID = String.valueOf(((Map<String, Object>) BUSI.get(i)).get("OFFER_ID"));
			try {
				Map<String, Object> codeOffer = new HashMap<String, Object>();
				codeOffer.put("OFFER_ID", OFFER_ID);
				resMap = this.codeOfferAtomicService.qryCodeOffer(codeOffer);
			} catch (Exception e) {
				this.logger.error("调用业务定义查询失败", e);
				throw new BusiServiceException(e);
			}
			List<Map<String, Object>> Code_Offer = (List<Map<String, Object>>) resMap.get("Code_Offer");
			if ((Code_Offer == null) || (Code_Offer.isEmpty())) {
				map.put("RESULT", Constants.STATE_CODE_FAIL);
				map.put("CODE", "");
				map.put("MSG", "OFFER_ID不正确");
				return map;
			}
			if (!((Map<String, Object>) Code_Offer.get(0)).containsKey("OFFER_NAME")) {
				map.put("RESULT", Constants.STATE_CODE_FAIL);
				map.put("CODE", "");
				map.put("MSG", "OFFER_ID不正确");
				return map;
			}
			String OFFER_NAME = (String) ((Map<String, Object>) Code_Offer.get(0)).get("OFFER_NAME");

			if (StringUtils.isEmpty(OFFER_NAME)) {
				map.put("RESULT", Constants.STATE_CODE_FAIL);
				map.put("CODE", "");
				map.put("MSG", "OFFER_ID不正确");
				return map;
			}
			offerNames[i] = OFFER_NAME;
			purchaseMonthStrs[i] = ((String) ((Map<String, Object>) Code_Offer.get(0)).get("EFF_MONTH_COUNT"));
		}

		List<Map<String, Object>> orderchildmaps = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < BUSI.size(); i++) {
			String BUSI_ID = null;
			String EFF_DATE = (String) ((Map<String, Object>) BUSI.get(i)).get("EFF_DATE");
			String EXP_DATE = (String) ((Map<String, Object>) BUSI.get(i)).get("EXP_DATE");
			String BUSI_NBR = (String) ((Map<String, Object>) BUSI.get(i)).get("BUSI_NBR");
			String OFFERID = (String) ((Map<String, Object>) BUSI.get(i)).get("OFFER_ID");
			String CARD_ID = String.valueOf(((Map<String, Object>) BUSI.get(i)).get("CARD_ID"));
			String CARD_NAME = (String) ((Map<String, Object>) BUSI.get(i)).get("CARD_NAME");
			String CARD_NBR = (String) ((Map<String, Object>) BUSI.get(i)).get("CARD_NBR");
			try {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("CUST_ID", CUST_ID);
				params.put("OFFER_ID", OFFERID);
				params.put("STATUS_CD", "1000");
				
				Map<String, Object> mapcust = new HashMap<>();
				
				if(Constants.SMI_BUSI_OFFER.equals(OFFERID)){
					mapcust = (Map<String, Object>) this.custBusiAtomicService.isExistCardNbr(params);
				}else{
					mapcust = (Map<String, Object>) this.custBusiAtomicService.qryCustBusiInfo(params);
				}
				
				List<Object> listBusi = (List<Object>) mapcust.get("QRY_RESULTS");
				if (listBusi.size() > 0 && Constants.SMI_BUSI_OFFER.equals(OFFERID)) {
					Map<String, Object> mapBusi = (Map<String, Object>) listBusi.get(0);
					BUSI_ID = (String) mapBusi.get("BUSI_ID");
					if (BUSI_ID == null || BUSI_ID.equals("")) {
						try {
							BUSI_ID = this.commonAtomicService.getSequence("SEQ_INFO_CUST_BUSI");
						} catch (AtomicServiceException e3) {
							this.logger.error("调用SEQ_INFO_CUST_BUSI序列号异常", e3);
							throw new BusiServiceException(e3);
						}
					}
					if(BUSI.size() != 2){
						//单独办理星美会员业务，则更新会员卡号即可
						try {
							Map<String, Object> reqParam = new HashMap<String, Object>();
							reqParam.put("CUST_ID", CUST_ID);
							reqParam.put("CUST_NBR", CARD_NBR);
							int succ = this.custAtomicService.updCust(reqParam);// 调用会员基本资料修改原子服务
							if (succ > 0) {
								this.logger.info("会员基本资料修改原子服务成功,条数：" + succ);
							} else {
								this.logger.error("会员基本资料修改原子服务失败");
							}
							Map<String, Object> requestMap = new HashMap<String, Object>();
							requestMap.put("BUSI_ID", BUSI_ID);
							requestMap.put("EFF_DATE", EFF_DATE);
							requestMap.put("EXP_DATE", EXP_DATE);
							requestMap.put("BUSI_NBR", BUSI_NBR);
							requestMap.put("CARD_ID", CARD_ID);
							requestMap.put("CARD_NAME", CARD_NAME);
							requestMap.put("CARD_NBR", CARD_NBR);
							try {
								this.custBusiAtomicService.updateCustBusi(requestMap); // 修改会员业务原子服务
							} catch (AtomicServiceException e2) {
								this.logger.error("调用会员业务修改服务异常", e2);
								throw new BusiServiceException(e2);
							}
						} catch (AtomicServiceException e1) {
							this.logger.error("调用会员资料修改服务异常", e1);
							throw new BusiServiceException(e1);
						}
					}
				} else {
					String purchaseMonthStr = purchaseMonthStrs[i];

					try {
						BUSI_ID = this.commonAtomicService.getSequence("SEQ_INFO_CUST_BUSI");
					} catch (AtomicServiceException e3) {
						this.logger.error("调用SEQ_INFO_CUST_BUSI序列号异常", e3);
						throw new BusiServiceException(e3);
					}
					
					String expDate = "";
					if ((null != purchaseMonthStr) && (!"".equals(purchaseMonthStr))) {
						int purchaseMonth = Integer.parseInt(purchaseMonthStr);
						expDate = createBusiExpDate(purchaseMonth);
					} else {
						expDate = "2099-12-31";
					}
					try {
						Map<String, Object> custBusi = new HashMap<String, Object>();
						custBusi.put("BUSI_ID", BUSI_ID);
						custBusi.put("CUST_ID", CUST_ID);
						custBusi.put("EXT_ORDER_ID", EXT_ORDER_ID);
						custBusi.put("OFFER_ID", ((Map<String, Object>) BUSI.get(i)).get("OFFER_ID"));
						custBusi.put("BUSI_NAME", offerNames[i]);
						custBusi.put("EFF_DATE", ((Map<String, Object>) BUSI.get(i)).get("EFF_DATE"));
						if(StringUtils.isEmpty(EXP_DATE)){
							custBusi.put("EXP_DATE", expDate);
						}else{
							custBusi.put("EXP_DATE", EXP_DATE);
						}
						custBusi.put("STATUS_CD", "1000");
						custBusi.put("BUSI_NBR", ((Map<String, Object>) BUSI.get(i)).get("BUSI_NBR"));
						custBusi.put("CARD_ID", ((Map<String, Object>) BUSI.get(i)).get("CARD_ID"));
						custBusi.put("CARD_NAME", ((Map<String, Object>) BUSI.get(i)).get("CARD_NAME"));
						custBusi.put("CARD_NBR", ((Map<String, Object>) BUSI.get(i)).get("CARD_NBR"));
						// 新增3个字段
						custBusi.put("CARD_ID", ((Map<String, Object>) BUSI.get(i)).get("CARD_ID"));
						custBusi.put("CARD_NAME", ((Map<String, Object>) BUSI.get(i)).get("CARD_NAME"));
						custBusi.put("CARD_NBR", ((Map<String, Object>) BUSI.get(i)).get("CARD_NBR"));
						resMap.clear();
						resMap = this.custBusiAtomicService.addCustBusi(custBusi);
						if (((Integer) resMap.get("SUCC_SUM")).intValue() < 1) {
							map.put("RESULT", Constants.STATE_CODE_FAIL);
							map.put("CODE", "");
							map.put("MSG", "会员业务新增失败");
							return map;
						}
					} catch (Exception e) {
						this.logger.error("调用会员业务新增失败", e);
						throw new BusiServiceException(e);
					}
				}
			} catch (Exception e) {
				this.logger.error("调用会员业务查询失败", e);
				throw new BusiServiceException(e);
			}

			Map<String, Object> orderchildmap = new HashMap<String, Object>();
			orderchildmap.put("OBJ_TYPE", ((Map<String, Object>) BUSI.get(i)).get("OFFER_ID"));
			orderchildmap.put("OBJ_ID", BUSI_ID);

			List<Map<String, Object>> orderchilddetailList = new ArrayList<Map<String, Object>>();
			Map<String, Object> orderchilddetail = new HashMap<String, Object>();
			orderchilddetail.put("OBJ_TYPE", "T");
			orderchilddetail.put("OBJ_ID", ((Map<String, Object>) BUSI.get(i)).get("OFFER_ID"));
			orderchilddetail.put("FIELD_PARAMETER", "");
			orderchilddetail.put("FIELD_NAME", "INFO_CUST_BUSI");
			orderchilddetail.put("BEFORE_PARAMETER", "");
			orderchilddetail.put("AFTER_PARAMETER", "");
			orderchilddetailList.add(orderchilddetail);

			orderchildmap.put("ADD_ORDER_DETAIL_REQ", orderchilddetailList);
			orderchildmaps.add(orderchildmap);
		}

		try {
			List<String> list = new ArrayList<String>();

			for (int i = 0; i < BUSI.size(); i++) {
				list.add(String.valueOf(((Map<String, Object>) BUSI.get(i)).get("OFFER_ID")));
			}
			Map<String, Object> busiCust = new HashMap<String, Object>();
			busiCust.put("OFFER_ID", list);
			if ((param.get("SERV_CODE") != null) && (!"".equals(param.get("SERV_CODE"))))
				busiCust.put("SERV_CODE", param.get("SERV_CODE"));
			else {
				busiCust.put("SERV_CODE", "107");
			}

			resMap.clear();
			resMap = this.busiQryCustService.busiCustQry(busiCust);
		} catch (Exception e) {
			this.logger.error("调用业务费用查询失败", e);
			throw new BusiServiceException(e);
		}
		String ORDER_SUM = "";
		List<Map<String, Object>> CODE_OFFER = new ArrayList<Map<String, Object>>();
		RESP QRY_RESULTS = null;
		if (resMap.containsKey("QRY_RESULTS")) {
			QRY_RESULTS = (RESP) resMap.get("QRY_RESULTS");
		}
		if ("1".equals(QRY_RESULTS.getResult())) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", QRY_RESULTS.getCode());
			map.put("MSG", QRY_RESULTS.getMsg());
			return map;
		}
		if (resMap.containsKey("ORDER_SUM")) {
			ORDER_SUM = Long.toString(((Long) resMap.get("ORDER_SUM")).longValue());
		}
		if (resMap.containsKey("CODE_OFFER")) {
			CODE_OFFER = (List<Map<String, Object>>) resMap.get("CODE_OFFER");
		}

		try {
			Map<String, Object> order = new HashMap<String, Object>();
			Map<String, Object> ADD_ORDER_REQ = new HashMap<String, Object>();
			List<Map<String, Object>> ADD_ORDER_CHILD_REQ = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> ADD_ORDER_PAY_REQ = new ArrayList<Map<String, Object>>();

			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String CREATE_DATE = df2.format(new Date());

			ADD_ORDER_CHILD_REQ = createChildOrder(orderchildmaps, CODE_OFFER);

			Map<String, Object> orderchildmap1 = new HashMap<String, Object>();
			orderchildmap1.put("OBJ_TYPE", "107");
			orderchildmap1.put("OBJ_ID", CUST_ID);
			orderchildmap1.put("CREATE_DATE", new Date());
			ADD_ORDER_CHILD_REQ.add(orderchildmap1);

			ADD_ORDER_REQ.put("ORDER_ID", "");
			ADD_ORDER_REQ.put("CUST_ID", CUST_ID);
			ADD_ORDER_REQ.put("EXT_ORDER_ID", EXT_ORDER_ID);
			ADD_ORDER_REQ.put("CREATE_DATE", CREATE_DATE);
			ADD_ORDER_REQ.put("ORDER_TYPE", "1000");
			ADD_ORDER_REQ.put("CREATE_OPERATOR_ID", SYSTEM_USER_ID);
			ADD_ORDER_REQ.put("CREATE_CHANNEL_ID", CHANNEL_CODE);
			ADD_ORDER_REQ.put("EXT_SYSTEM", EXT_SYSTEM);
			// ADD_ORDER_REQ.put("EXT_ORDER_ID", "");
			ADD_ORDER_REQ.put("BUSINESS_TYPE", "13");
			if ((param.get("SERV_CODE") != null) && (!"".equals(param.get("SERV_CODE"))))
				ADD_ORDER_REQ.put("SERV_CODE", param.get("SERV_CODE"));
			else {
				ADD_ORDER_REQ.put("SERV_CODE", "107");
			}

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
		String ORDER_ID = (String) resMap.get("ORDER_ID");
		String PAY_STATUS = (String) resMap.get("PAY_STATUS");
		int RESULT = Integer.parseInt(String.valueOf(resMap.get("RESULT")));
		if (RESULT != 0) {
			map.put("RESULT", Constants.STATE_CODE_FAIL);
			map.put("CODE", resMap.get("CODE"));
			map.put("MSG", resMap.get("MSG"));
			return map;
		}

		map.put("PAY_STATUS", PAY_STATUS);
		map.put("ORDER_ID", ORDER_ID);
		map.put("ORDER_SUM", ORDER_SUM);
		map.put("CODE_OFFER", CODE_OFFER);

		map.put("RESULT", Constants.STATE_CODE_SUCC);
		map.put("CODE", "");
		map.put("MSG", "");
		return map;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> createChildOrder(List<Map<String, Object>> childList,
			List<Map<String, Object>> ofrList) {
		String OBJ_TYPE = "";
		String ofrID = "";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if ((childList != null) && (childList.size() > 0)) {
			@SuppressWarnings("rawtypes")
			Iterator localIterator1;
			Map<String, Object> child;
			if ((ofrList != null) && (ofrList.size() > 0)) {
				for (localIterator1 = childList.iterator(); localIterator1.hasNext();) {
					child = (Map<String, Object>) localIterator1.next();
					OBJ_TYPE = String.valueOf(child.get("OBJ_TYPE"));
					for (Map<String, Object> ofr : ofrList) {
						ofrID = ((BigDecimal) ofr.get("OBJ_TYPE")).toString();
						if (OBJ_TYPE.equals(ofrID)) {
							List<Map<String, Object>> orderFeemaps = new ArrayList<Map<String, Object>>();
							Map<String, Object> orderFeemap = new HashMap<String, Object>();

							orderFeemap.put("ORDER_FEE", ofr.get("PAY_NUMBER"));
							orderFeemap.put("PAY_TYPE", ofr.get("PAY_TYPE"));
							orderFeemaps.add(orderFeemap);
							child.put("ADD_ORDER_FEE_REQ", orderFeemaps);
							list.add(child);
						}
					}
				}
			} else
				return null;
		} else {
			return null;
		}
		return list;
	}

	public String createBusiExpDate(int purchaseMonth) {
		String expDate = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(2, purchaseMonth);
		expDate = sdf.format(calendar1.getTime());
		return expDate;
	}
}