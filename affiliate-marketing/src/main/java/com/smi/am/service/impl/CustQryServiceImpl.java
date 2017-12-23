package com.smi.am.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.smi.am.constant.SmiConstants;
import com.smi.am.service.CustQryService;
import com.smi.am.service.vo.CustVo;
import com.smi.am.utils.CommUtils;
import com.smi.am.utils.MemberCenterConfiguration;
import com.smi.am.utils.RedisUtil;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.exception.SmiBusinessCodeException;
import com.smilife.core.exception.SmiBusinessException;

/**
 * 会员查询服务实现类
 * 
 * @author smi
 *
 */
@Service
public class CustQryServiceImpl implements CustQryService {
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private MemberCenterConfiguration mcConfig;

	/**
	 * 会员列表查询，http请求post
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SmiResult<List<CustVo>> qryCustList(Map<String, Object> paramMap) {
		SmiResult<List<CustVo>> smiReslut = new SmiResult<List<CustVo>>();
		List<CustVo> custList = new ArrayList<CustVo>();
		try {
			String returnStr = CommUtils.sendPost(mcConfig.getCustQryUrl(), paramMap, "utf-8");
			if (!"".equals(returnStr) || returnStr != null) {
				JSONObject JSON = (JSONObject) JSONObject.parse(returnStr);
				if (JSON.containsKey("CUST_INFO_LIST")) {
					List<Map<String, Object>> listCust = (List<Map<String, Object>>) JSON.get("CUST_INFO_LIST");
					if (listCust.size() > 0) {
						for (Map<String, Object> custMap : listCust) {
							CustVo vo = new CustVo();
							vo.setCustId((String) custMap.get("CUST_ID"));
							vo.setBirthdate((String) custMap.get("BIRTHDATE"));
							vo.setContactMobile((String) custMap.get("CONTACT_MOBILE"));
							vo.setCustName((String) custMap.get("CUST_NAME")); 
							vo.setCrtDate((String) custMap.get("CRT_DATE"));
							vo.setCustVipLevel((String) custMap.get("CUST_VIP_LEVEL"));
							vo.setCustNbr((String) custMap.get("CUST_NBR"));
							vo.setSex((String) custMap.get("SEX"));
							String orgkey=(String) custMap.get("ORG_ID");
							if(SmiConstants.DIANQUCODE.equals(orgkey)){
								vo.setOrgId(SmiConstants.DIANQUCODE_NAME);
							}else{
							 String orgName=(String) redisUtil.get(SmiConstants.CHANNEL_REDIS_KEY+orgkey);
							 vo.setOrgId(orgName);
							}
							custList.add(vo);
						}
						smiReslut.setData(custList);
						smiReslut.setPageSize((Integer) JSON.get("PAGE_SIZE"));
						smiReslut.setPages((Integer) JSON.get("TOTAL_PAGE"));
						smiReslut.setTotal((Integer) JSON.get("TOTAL"));
						smiReslut.setCode(CodeEnum.SUCCESS);
						smiReslut.setMsg("成功");
					} else {
						smiReslut.setCode(CodeEnum.PROFILE_MISSING);
						smiReslut.setMsg("查询无数据");
					}
				}
			} else {
				smiReslut.setCode(CodeEnum.REQUEST_ERROR);
				smiReslut.setMsg("发往会员中心无响应");
			}
		} catch (Exception e) {
			throw new SmiBusinessCodeException("发往会员中心异常", e);
		}
		return smiReslut;
	}

	/**
	 * 会员
	 */
	@Override
	public String qryCustCardPack(Map<String, Object> paramMap) throws SmiBusinessException {
		return null;
	}

}
