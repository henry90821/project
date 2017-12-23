package com.smi.am.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smi.am.constant.SmiConstants;
import com.smi.am.dao.AmActivityAreaMapper;
import com.smi.am.dao.AmCounponsDetailsMapper;
import com.smi.am.dao.AmCounponsTypeMapper;
import com.smi.am.dao.AmCouponsMapper;
import com.smi.am.dao.model.AmActivityArea;
import com.smi.am.dao.model.AmActivityAreaExample;
import com.smi.am.dao.model.AmCounponsDetails;
import com.smi.am.dao.model.AmCounponsDetailsExample;
import com.smi.am.dao.model.AmCounponsType;
import com.smi.am.dao.model.AmCoupons;
import com.smi.am.dao.model.AmCouponsExample;
import com.smi.am.dao.model.AmCouponsExample.Criteria;
import com.smi.am.dao.model.AmCouponsWithBLOBs;
import com.smi.am.service.ICouponsService;
import com.smi.am.service.vo.CounponsDetailsInsertVo;
import com.smi.am.service.vo.CouponRelationVo;
import com.smi.am.service.vo.CouponsEditVo;
import com.smi.am.service.vo.CouponsInsertVo;
import com.smi.am.service.vo.CouponsPackageVo;
import com.smi.am.service.vo.CouponsShowVo;
import com.smi.am.service.vo.CouponsSomeVo;
import com.smi.am.service.vo.CouponsVo;
import com.smi.am.service.vo.CustCouponsVo;
import com.smi.am.service.vo.WraparoundResVo;
import com.smi.am.utils.MemberCenterConfiguration;
import com.smi.am.utils.SmiResult;
import com.smi.am.utils.UniqueGenerate;
import com.smi.tools.http.HttpKit;
import com.smi.tools.kits.JsonKit;
import com.smi.tools.kits.StrKit;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

@Service
public class CouponsServiceImpl implements ICouponsService{
	
	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	@Autowired
	private AmCouponsMapper couponsMapper;
	
	@Autowired
	private AmCounponsDetailsMapper amCounponsDetailsMapper;
	
	@Autowired
	private AmCounponsTypeMapper amCounponsTypeMapper; 
	
	@Autowired
	private AmActivityAreaMapper amActivityAreaMapper; 
	
	@Autowired
	private MemberCenterConfiguration mcConfig;
	
	@Transactional
	@Override
	public int saveCoupons(CouponsInsertVo  couponsVo,String username) {
		AmCouponsWithBLOBs amCoupons = new AmCouponsWithBLOBs();
		Date date = new Date();
		String couponsId = UniqueGenerate.createUniqueId();//父优惠券ID
		//优惠券明细值对象
		List<CounponsDetailsInsertVo> detailList = couponsVo.getDetailList();
		String details = JsonKit.toJsonString(detailList);
		
		amCoupons.setcDetailsinfo(details);
		amCoupons.setcCreatedate(date);
		amCoupons.setcCreateuser(username);
		amCoupons.setcLastmoddate(date);
		amCoupons.setcLastmoduser(username);
		amCoupons.setcAuditstatus(SmiConstants.UNCOMMITTED);
		amCoupons.setcCouponsid(couponsId);
		amCoupons.setcIsdetele(0);
		int result = 0;
		try {
			//新增优惠券
			BeanUtils.copyProperty(amCoupons, "cCouponsname", couponsVo.getcCouponsname());
			if (couponsVo.getcAuditstatus() !=null) {
				BeanUtils.copyProperty(amCoupons, "cAuditstatus", couponsVo.getcAuditstatus());
			}
			BeanUtils.copyProperty(amCoupons, "cCouponstype", couponsVo.getcCouponstype());
			BeanUtils.copyProperty(amCoupons, "cUserchannel", couponsVo.getcUserchannel());
			BeanUtils.copyProperty(amCoupons, "cCouponsmsg", couponsVo.getcCouponsmsg());
			BeanUtils.copyProperty(amCoupons, "cAvaliabledorderamt", couponsVo.getcAvaliabledorderamt());
			BeanUtils.copyProperty(amCoupons, "cDailylimit", couponsVo.getcDailylimit());
			BeanUtils.copyProperty(amCoupons, "cPermanent", couponsVo.getcPermanent());
			if (couponsVo.getcPermanent() == SmiConstants.EXPIRES) {
				//非永久有效，保存使用期限时间
				BeanUtils.copyProperty(amCoupons, "cLimitstart",couponsVo.getcLimitstart());
				BeanUtils.copyProperty(amCoupons, "cLimitend", couponsVo.getcLimitend());
			}
			BeanUtils.copyProperty(amCoupons, "cCumulative", couponsVo.getcCumulative());
			BeanUtils.copyProperty(amCoupons, "cActivityarea", couponsVo.getcActivityarea());
			//BeanUtils.copyProperty(amCoupons, "cActivityzone", couponsVo.getcActivityzone());
			BeanUtils.copyProperty(amCoupons, "cActivitystore", StrKit.bytes(couponsVo.getcActivitystore(), "utf8"));
			BeanUtils.copyProperty(amCoupons, "cActivitystarttime",couponsVo.getcActivitystarttime());
			BeanUtils.copyProperty(amCoupons, "cActivityendtime",couponsVo.getcActivityendtime());
			BeanUtils.copyProperty(amCoupons, "cProvideuser", StrKit.bytes(couponsVo.getcProvideuser(), "utf8"));
			BeanUtils.copyProperty(amCoupons, "cPresendcount", couponsVo.getcPresendcount());
			BeanUtils.copyProperty(amCoupons, "cDeliveringway", couponsVo.getcDeliveringway());
			BeanUtils.copyProperty(amCoupons, "cRemainnum", couponsVo.getcRemainnum());
			if (couponsVo.getcDeliveringway() == 2 ) {
				BeanUtils.copyProperty(amCoupons, "cChannel", couponsVo.getcChannel());
			}else {
				BeanUtils.copyProperty(amCoupons, "cChannel", "");
			}
			if(couponsVo.getcCumulative() == 2){
				//可累加，新增关联优惠券ids
				BeanUtils.copyProperty(amCoupons, "cAccumulatecouponsids", couponsVo.getcAccumulatecouponsids());
			}
			result = couponsMapper.insert(amCoupons);
		}  catch (IllegalAccessException e) {
			LOGGER.error("service层实体类复制异常", e);
			throw new SmiBusinessException("service层实体类复制异常");
		} catch (Exception e) {
			LOGGER.error("service层实体类复制异常", e);
			throw new SmiBusinessException("service层异常");
		}
		
		return result;
	}

	@Override
	public SmiResult<List<CouponsVo>> searchCouponsByConditions(Map<String, Object> conditionMap) {
		SmiResult<List<CouponsVo>> smiResult = new SmiResult<List<CouponsVo>>();
		//2、设置分页
		PageHelper.startPage((Integer)conditionMap.get("PAGE_NUM"), (Integer)conditionMap.get("PAGE_SIZE"));
		//3、执行查询
		List<CouponsVo> couponsList = couponsMapper.searchCouponsByConditions(conditionMap);
		//4、取分页后结果
		PageInfo<CouponsVo> pageInfo = new PageInfo<>(couponsList);
		
		for (CouponsVo couponsVo : couponsList) {
			String getcActivityarea = couponsVo.getcActivityarea();
			StringBuilder areaNames = null;
			List<Integer> arealist = null; 
			AmActivityAreaExample amActivityAreaExample = null;
			
			if(StringUtils.isNoneEmpty(getcActivityarea)){
				String[] areas = getcActivityarea.split(",");
				arealist = new ArrayList<Integer>();
				areaNames = new StringBuilder();
				for (String area : areas) {
					arealist.add(Integer.parseInt(area));
				}
				amActivityAreaExample = new AmActivityAreaExample();
				com.smi.am.dao.model.AmActivityAreaExample.Criteria createCriteria = amActivityAreaExample
						.createCriteria();
				createCriteria.andAIdIn(arealist);
				List<AmActivityArea> areaList = amActivityAreaMapper.selectByExample(amActivityAreaExample);
				for (AmActivityArea amActivityArea : areaList) {
					areaNames.append(amActivityArea.getaActivityarea()).append(",");
				}
				couponsVo.setcActivityareaName(areaNames.substring(0, areaNames.length() - 1));
			}
			
			//获取单发优惠券、礼包详情，
			String cdetailInfo = couponsVo.getCdetailInfo();
			int packageNum = 0;
			int couponsNum = 0;
			List<CounponsDetailsInsertVo> detailInfoList = JsonKit.parserToList(cdetailInfo, CounponsDetailsInsertVo.class);
			for (CounponsDetailsInsertVo counponsDetailsInsertVo : detailInfoList) {
				if(counponsDetailsInsertVo.getCdIsgitpackagecounpons() == Integer.parseInt(SmiConstants.GIFT_PACKAGE_TYPE)){
					packageNum = counponsDetailsInsertVo.getCouponsCount();
				}else{
					couponsNum = counponsDetailsInsertVo.getCouponsCount();
				}
			}
			if(packageNum == 0 ){
				couponsVo.setPretest((couponsNum+packageNum)+"");
			}else {
				couponsVo.setPretest((couponsNum+packageNum) + "（含礼包" + packageNum + "）");
			}
			
		}
		smiResult.setTotal(pageInfo.getTotal());
		smiResult.setPages(pageInfo.getPages());
		smiResult.setPageSize(pageInfo.getPageSize());
		smiResult.setData(couponsList);
		smiResult.setCode(CodeEnum.SUCCESS);
		return smiResult;
	}

	@Override
	public SmiResult<CouponsShowVo> getCouponsVoById(String couponsId) {
		SmiResult<CouponsShowVo> smiResult = new SmiResult<CouponsShowVo>();
		AmCouponsWithBLOBs coupons = findCounponsByCoupons(couponsId);
		if(coupons == null) {
			smiResult.setCode(CodeEnum.REQUEST_ERROR);
			smiResult.setMsg("找不到该优惠券信息！");
			return smiResult;
		}
		List<CounponsDetailsInsertVo> detailsList = JsonKit.parserToList(coupons.getcDetailsinfo(), CounponsDetailsInsertVo.class);
		CouponsShowVo couponsVo = new CouponsShowVo();
		try {
			BeanUtils.copyProperties(couponsVo, coupons);
			couponsVo.setDetailList(detailsList);
			BeanUtils.copyProperty(couponsVo, "cActivitystarttime", coupons.getcActivitystarttime());
			BeanUtils.copyProperty(couponsVo, "cActivityendtime",coupons.getcActivityendtime());
			BeanUtils.copyProperty(couponsVo, "cLimitstart", coupons.getcLimitstart() );
			BeanUtils.copyProperty(couponsVo, "cLimitend", coupons.getcLimitend() );
			BeanUtils.copyProperty(couponsVo, "cAccumulatecouponsids",coupons.getcAccumulatecouponsids() == null ? "" :coupons.getcAccumulatecouponsids());
			String prouser = StrKit.str(coupons.getcProvideuser(), "utf8");
			BeanUtils.copyProperty(couponsVo, "cProvideuser", prouser);
			String store = StrKit.str(coupons.getcActivitystore(), "utf8");
			BeanUtils.copyProperty(couponsVo, "cActivitystoreArray", JSONArray.parseArray(store));
		} catch (Exception e) {
			LOGGER.error("service层实体类复制异常", e);
			throw new SmiBusinessException("service层异常");
		}
		Integer status = coupons.getcAuditstatus();
		int realNum = 0;
		int usedNum = 0;
		if(status == SmiConstants.INAUDIT || status == SmiConstants.STAYOUT || status == SmiConstants.HANDEDOUT){
			//统计实发数量和已用数量
			AmCounponsDetailsExample detailsExample = new AmCounponsDetailsExample();
			com.smi.am.dao.model.AmCounponsDetailsExample.Criteria createCriteria = detailsExample.createCriteria();
			createCriteria.andCdCounponsidEqualTo(couponsId);
			createCriteria.andCdUsedEqualTo(0);
			realNum = amCounponsDetailsMapper.countByExample(detailsExample);
			
			AmCounponsDetailsExample detailsExample2 = new AmCounponsDetailsExample();
			com.smi.am.dao.model.AmCounponsDetailsExample.Criteria createCriteria2 = detailsExample2.createCriteria();
			createCriteria2.andCdCounponsidEqualTo(couponsId);
			createCriteria2.andCdUsedEqualTo(2);
			usedNum = amCounponsDetailsMapper.countByExample(detailsExample2);
			
		}
		couponsVo.setcRealNum(realNum);
		couponsVo.setcUsedNum(usedNum);
		
		//获取优惠券类型名称，活动区名称
		AmCounponsType counponsType = amCounponsTypeMapper.selectByPrimaryKey(coupons.getcCouponstype());
		couponsVo.setcCouponsTypeName(counponsType.getCtCounponsname());
		StringBuilder areanames = new StringBuilder();
		if(StringUtils.isNoneEmpty(coupons.getcActivityarea())){
			String[] areaIds = coupons.getcActivityarea().split(",");
			List<Integer> areaList = new ArrayList<Integer>();
			for (String area : areaIds) {
				areaList.add(Integer.parseInt(area));
			}
			AmActivityAreaExample amActivityAreaExample = new AmActivityAreaExample();
			com.smi.am.dao.model.AmActivityAreaExample.Criteria createCriteria = amActivityAreaExample.createCriteria();
			createCriteria.andAIdIn(areaList);
			List<AmActivityArea> areas = amActivityAreaMapper.selectByExample(amActivityAreaExample);
			for (AmActivityArea amActivityArea : areas) {
				areanames.append(amActivityArea.getaActivityarea()).append(",");
			}
			couponsVo.setcActivityarea(areanames.substring(0, areanames.length()-1));
		}

		smiResult.setData(couponsVo);
		smiResult.setCode(CodeEnum.SUCCESS);
		return smiResult;
	}
	
	@Override
	public SmiResult<CouponsShowVo> editCouponsPageShowById(String couponsId) throws Exception {
		SmiResult<CouponsShowVo> smiResult = new SmiResult<CouponsShowVo>();
		AmCouponsWithBLOBs coupons = findCounponsByCoupons(couponsId);
		if(coupons == null) {
			smiResult.setCode(CodeEnum.REQUEST_ERROR);
			smiResult.setMsg("找不到该优惠券信息！");
			return smiResult;
		}
		
		List<CounponsDetailsInsertVo> detailsList = JsonKit.parserToList(coupons.getcDetailsinfo(), CounponsDetailsInsertVo.class);
		CouponsShowVo couponsVo = new CouponsShowVo();
		try {
			BeanUtils.copyProperties(couponsVo, coupons);
			couponsVo.setDetailList(detailsList);
			BeanUtils.copyProperty(couponsVo, "cActivitystarttime", coupons.getcActivitystarttime());
			BeanUtils.copyProperty(couponsVo, "cActivityendtime",coupons.getcActivityendtime());
			BeanUtils.copyProperty(couponsVo, "cLimitstart", coupons.getcLimitstart());
			BeanUtils.copyProperty(couponsVo, "cLimitend", coupons.getcLimitend());
			BeanUtils.copyProperty(couponsVo, "cAccumulatecouponsids",coupons.getcAccumulatecouponsids() == null ? "" :coupons.getcAccumulatecouponsids());
			String prouser = StrKit.str(coupons.getcProvideuser(), "utf8");
			BeanUtils.copyProperty(couponsVo, "cProvideuser", prouser);
			String store = StrKit.str(coupons.getcActivitystore(), "utf8");
			BeanUtils.copyProperty(couponsVo, "cActivitystoreArray", JSONArray.parseArray(store));
		} catch (IllegalAccessException e) {
			LOGGER.error("service层实体类复制异常", e);
			throw new SmiBusinessException("service层实体类复制异常");
		} catch (InvocationTargetException e) {
			LOGGER.error("service层实体类复制异常", e);
			throw new SmiBusinessException("service层异常");
		}
		Integer status = coupons.getcAuditstatus();
		int realNum = 0;
		int usedNum = 0;
		if(status == SmiConstants.INAUDIT || status == SmiConstants.STAYOUT || status == SmiConstants.HANDEDOUT){
			//统计实发数量和已用数量
			AmCounponsDetailsExample detailsExample = new AmCounponsDetailsExample();
			com.smi.am.dao.model.AmCounponsDetailsExample.Criteria createCriteria = detailsExample.createCriteria();
			createCriteria.andCdCounponsidEqualTo(couponsId);
			createCriteria.andCdUsedEqualTo(0);
			realNum = amCounponsDetailsMapper.countByExample(detailsExample);
			
			AmCounponsDetailsExample detailsExample2 = new AmCounponsDetailsExample();
			com.smi.am.dao.model.AmCounponsDetailsExample.Criteria createCriteria2 = detailsExample2.createCriteria();
			createCriteria2.andCdCounponsidEqualTo(couponsId);
			createCriteria2.andCdUsedEqualTo(2);
			usedNum = amCounponsDetailsMapper.countByExample(detailsExample2);
			
		}
		couponsVo.setcRealNum(realNum);
		couponsVo.setcUsedNum(usedNum);
		//获取关联的优惠券信息
		String couponsIds = coupons.getcAccumulatecouponsids();
		if(StringUtils.isNoneEmpty(couponsIds)){
			List<CouponRelationVo> couponslist = couponsMapper.getCouponsShowVos(Arrays.asList(couponsIds.split(",")));
			couponsVo.setCouponRelationVoList(couponslist);
		}
		smiResult.setData(couponsVo);
		smiResult.setCode(CodeEnum.SUCCESS);
		return smiResult;
	}
	
	@Override
	@Transactional
	public BaseValueObject editCouponsById(CouponsEditVo vo,String username) {
		Date date = new Date();
		BaseValueObject baseValueObject = new BaseValueObject();
		AmCouponsWithBLOBs amCoupons = findCounponsByCoupons(vo.getcCouponsid());
		if(amCoupons == null) {
			baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
			baseValueObject.setMsg("找不到该优惠券信息！");
			return baseValueObject;
		}
		
		List<CounponsDetailsInsertVo> detailList = vo.getDetailList();
		String details = JsonKit.toJsonString(detailList);
		amCoupons.setcDetailsinfo(details);
		amCoupons.setcLastmoddate(date);
		amCoupons.setcLastmoduser(username);
		try {
			BeanUtils.copyProperty(amCoupons, "cCouponsid", vo.getcCouponsid());
			BeanUtils.copyProperty(amCoupons, "cCouponsname", vo.getcCouponsname());
			BeanUtils.copyProperty(amCoupons, "cCouponstype", vo.getcCouponstype());
			BeanUtils.copyProperty(amCoupons, "cChannel", vo.getcChannel());
			BeanUtils.copyProperty(amCoupons, "cUserchannel", vo.getcUserchannel());
			BeanUtils.copyProperty(amCoupons, "cCouponsmsg", vo.getcCouponsmsg());
			BeanUtils.copyProperty(amCoupons, "cAvaliabledorderamt", vo.getcAvaliabledorderamt());
			BeanUtils.copyProperty(amCoupons, "cDailylimit", vo.getcDailylimit());
			BeanUtils.copyProperty(amCoupons, "cPermanent", vo.getcPermanent());
			BeanUtils.copyProperty(amCoupons, "cCumulative", vo.getcCumulative());
			BeanUtils.copyProperty(amCoupons, "cActivityarea", vo.getcActivityarea());
			BeanUtils.copyProperty(amCoupons, "cActivitystore", StrKit.bytes(vo.getcActivitystore(), "utf8"));
			BeanUtils.copyProperty(amCoupons, "cProvideuser", StrKit.bytes(vo.getcProvideuser(), "utf8"));
			BeanUtils.copyProperty(amCoupons, "cPresendcount", vo.getcPresendcount());
			BeanUtils.copyProperty(amCoupons, "cLimitstart", vo.getcLimitstart());
			BeanUtils.copyProperty(amCoupons, "cLimitend",vo.getcLimitend());
			BeanUtils.copyProperty(amCoupons, "cActivitystarttime", vo.getcActivitystarttime());
			BeanUtils.copyProperty(amCoupons, "cActivityendtime",vo.getcActivityendtime());
			BeanUtils.copyProperty(amCoupons, "cDeliveringway", vo.getcDeliveringway());
			BeanUtils.copyProperty(amCoupons, "cAccumulatecouponsids", vo.getcAccumulatecouponsids());
			BeanUtils.copyProperty(amCoupons, "cAuditstatus", vo.getcAuditstatus());
		} catch (IllegalAccessException e) {
			LOGGER.error("service层实体类复制异常", e);
			throw new SmiBusinessException("service层实体类复制异常");
		} catch (Exception e) {
			LOGGER.error("service层实体类复制异常", e);
			throw new SmiBusinessException("service层异常");
		}
		couponsMapper.updateByPrimaryKeyWithBLOBs(amCoupons);
		baseValueObject.setCode(CodeEnum.SUCCESS);
		return baseValueObject;
	}
	
	@Transactional
	@Override
	public BaseValueObject auditCouponsById(String couponsId, int auditStatus, String username,String remark) throws IOException {
		BaseValueObject baseValueObject = new BaseValueObject();
		Date date = new Date();
		//判断是否具备审核条件
		AmCouponsWithBLOBs coupons = findCounponsByCoupons(couponsId);
		if(coupons == null) {
			baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
			baseValueObject.setMsg("找不到该优惠券信息！");
			return baseValueObject;
		}
		
		if (coupons.getcAuditstatus() == SmiConstants.INAUDIT){
			if (auditStatus == SmiConstants.FAILED) {
				//审核不通过，更新状态
				coupons.setcAuditstatus(auditStatus);
				coupons.setcLastmoddate(date);
				coupons.setcLastmoduser(username);
				coupons.setcRemark(remark);
				couponsMapper.updateByPrimaryKeySelective(coupons);
			}else if(auditStatus == SmiConstants.STAYOUT) {
				//审核通过后，把优惠券明细批量insert到明细表
				//创建单发优惠券以优惠券数量为准，礼包优惠券以礼包优惠券数据为准
				coupons.setcAuditstatus(auditStatus);
				coupons.setcLastmoddate(date);
				coupons.setcLastmoduser(username);
				coupons.setcRemark(remark);
				couponsMapper.updateByPrimaryKeySelective(coupons);
				
				List<AmCounponsDetails> counponsDetails = new ArrayList<AmCounponsDetails>();
				String channel = "";
				if(coupons.getcDeliveringway() == SmiConstants.ACCOUNT_ISSUE){
					channel = mcConfig.getAccountDelivery();
				}else {
					if(Integer.parseInt(coupons.getcChannel()) == SmiConstants.SMILIFE_CHANNEL)
						channel = mcConfig.getSmilifeDelivery();
					else if(Integer.parseInt(coupons.getcChannel()) == SmiConstants.MTX_CHANNLE)
						channel = mcConfig.getMtxDelivery();
				}
				
				//如果存在礼包优惠券，直接批量insert
				int packageNum = 0;
				int couponsNum = 0;
				List<CounponsDetailsInsertVo> detailsList = JsonKit.parserToList(coupons.getcDetailsinfo(), CounponsDetailsInsertVo.class);
				for (CounponsDetailsInsertVo details : detailsList) {
					if(details.getCdIsgitpackagecounpons() == Integer.parseInt(SmiConstants.GIFT_PACKAGE_TYPE)){
						packageNum = details.getCouponsCount();
					}else if(details.getCdIsgitpackagecounpons() == Integer.parseInt(SmiConstants.COUPONS_TYPE)) {
						couponsNum = details.getCouponsCount();
					}
				}
				
				/**
				 * 批处理insert数据 ，创建单发优惠券以发放对象数量为准
				 */
				if (couponsNum > 0 ) {
					//账户发放时才需要批量insert明细表
					String providerUser = "";
					try {
						providerUser = new String(coupons.getcProvideuser(),"utf8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (StringUtils.isNotEmpty(providerUser)) {
						LOGGER.info("调会员中心接口,根据前端的手机账号获取对应的custId,参数："+providerUser);
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("loginMobiles", providerUser);
						//调会员中心接口,根据前端的手机账号获取对应的custId
						String jsonstr = HttpKit.post(mcConfig.getCustUrl() + "/cust/getCustIdByLoginMobiles", paramMap);
						SmiResult<List<Map<String, Object>>> smiResult = (SmiResult<List<Map<String, Object>>>)JsonKit.parseObject(jsonstr, SmiResult.class);
						if (smiResult.getCode() != 1) {
							throw new SmiBusinessException("调会员中心接口查询会员标识异常");
						}
						
						List<Map<String, Object>> memberInfo = smiResult.getData();
						AmCounponsDetails amCounponsDetails = null;
						/**
						 * 绑定单发优惠券用户
						 */
						for (Map<String, Object> map : memberInfo) {
							amCounponsDetails = new AmCounponsDetails();
							amCounponsDetails.setCdCounponsid(coupons.getcCouponsid());
							amCounponsDetails.setCdCustid((String)map.get("CUST_ID"));
							amCounponsDetails.setCdLoginmobile((String)map.get("LOGIN_USER"));
							amCounponsDetails.setCdDetailid(channel + UniqueGenerate.createUniqueId());
							amCounponsDetails.setCdIsgitpackagecounpons(Integer.parseInt(SmiConstants.COUPONS_TYPE));
							amCounponsDetails.setCdUsed(SmiConstants.UNUSED);
							
							//用户领取时,设置投放渠道
							if (coupons.getcDeliveringway() == SmiConstants.USER_RECEIVE) 
								amCounponsDetails.setCdCounponschanneltype(Integer.parseInt(coupons.getcChannel()));
							
							counponsDetails.add(amCounponsDetails);
						}
					
						//未有用户绑定的单发优惠券
						int userNum = memberInfo.size();
						for (int i = 0; i < couponsNum - userNum; i++) {
							amCounponsDetails = new AmCounponsDetails();
							amCounponsDetails.setCdCounponsid(coupons.getcCouponsid());
							amCounponsDetails.setCdDetailid(channel + UniqueGenerate.createUniqueId());
							amCounponsDetails.setCdIsgitpackagecounpons(Integer.parseInt(SmiConstants.COUPONS_TYPE));
							amCounponsDetails.setCdUsed(SmiConstants.UNCLAIMED);
							//用户领取时,设置投放渠道
							if (coupons.getcDeliveringway() == SmiConstants.USER_RECEIVE) 
								amCounponsDetails.setCdCounponschanneltype(Integer.parseInt(coupons.getcChannel()));
							
							counponsDetails.add(amCounponsDetails);
						}
						
					}
				}
				
				
				if(packageNum>0){
					AmCounponsDetails amCounponsDetailsForPackage = null;
					for (int i = 0; i < packageNum; i++) {
						amCounponsDetailsForPackage = new AmCounponsDetails();
						String couponsDetailId = channel + UniqueGenerate.createUniqueId();
						
						amCounponsDetailsForPackage.setCdCounponsid(coupons.getcCouponsid());
						amCounponsDetailsForPackage.setCdDetailid(couponsDetailId);
						amCounponsDetailsForPackage.setCdIsgitpackagecounpons(Integer.parseInt(SmiConstants.GIFT_PACKAGE_TYPE));
						amCounponsDetailsForPackage.setCdUsed(SmiConstants.UNCLAIMED);
						if (StringUtils.isNotEmpty(coupons.getcChannel())) {
							amCounponsDetailsForPackage.setCdCounponschanneltype(Integer.parseInt(coupons.getcChannel()));
						}
						counponsDetails.add(amCounponsDetailsForPackage);
					}
				}

				
				// 批量处理counponsDetails里面的数据
				if (counponsDetails.size()>0) {
					amCounponsDetailsMapper.batchInsertCouponsDetails(counponsDetails);
				}
			}
			baseValueObject.setCode(CodeEnum.SUCCESS);
		}else {
			//审核失败
			baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
			baseValueObject.setMsg("审核失败，此优惠券状态不是待审核状态");
		}
		
		return baseValueObject;
	}
	
	@Transactional
	@Override
	public BaseValueObject deliveryCouponsById(String couponsId, String username) {
		BaseValueObject baseValueObject = new BaseValueObject();
		
		AmCouponsWithBLOBs coupons = findCounponsByCoupons(couponsId);
		if(coupons == null) {
			baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
			baseValueObject.setMsg("找不到该优惠券信息！");
			return baseValueObject;
		}
		
		Date date = new Date();
		if(coupons.getcAuditstatus() != SmiConstants.STAYOUT){
			baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
			baseValueObject.setMsg("优惠券发放失败，此优惠券状态不是待发放状态");
		}else {
			coupons.setcCouponsid(couponsId);
			coupons.setcLastmoddate(date);
			coupons.setcLastmoduser(username);
			coupons.setcAuditstatus(SmiConstants.CONFIRMONLINE);
			couponsMapper.updateByPrimaryKeySelective(coupons);
			baseValueObject.setCode(CodeEnum.SUCCESS);
		}
		return baseValueObject;
	}

	
	@Transactional
	@Override
	public BaseValueObject deleteCouponsByIds(String ids) {
		BaseValueObject baseValueObject = new BaseValueObject();
		String[] idsArr = ids.split(",");
		//只有当前状态为“待发放”“未通过”“未提交”“已过期”的优惠券可执行删除
		List<Integer> auditStatuslist = new ArrayList<Integer>(); 
		auditStatuslist.add(SmiConstants.UNCOMMITTED);
		auditStatuslist.add(SmiConstants.FAILED);
		auditStatuslist.add(SmiConstants.STAYOUT);
		auditStatuslist.add(SmiConstants.EXPIRED);
		//判断优惠券ID是否存在
		AmCouponsExample amCouponsExample = new AmCouponsExample();
		Criteria createCriteria = amCouponsExample.createCriteria();
		createCriteria.andCCouponsidIn(Arrays.asList(idsArr));
		createCriteria.andCIsdeteleEqualTo(0);
		createCriteria.andCAuditstatusIn(auditStatuslist);
		int count = couponsMapper.countByExample(amCouponsExample);
		if(idsArr.length != count){
			baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
			baseValueObject.setMsg("删除失败，请检查优惠券ID是否存在或者优惠券状态是否达到可删除条件。");
		}else {
			couponsMapper.batchDeteleCouponsByIds(idsArr);
			baseValueObject.setCode(CodeEnum.SUCCESS);
		}
		return baseValueObject;
	}

	@Transactional
	@Override
	public BaseValueObject commitAuditCouponsById(String id, int auditStatus, String username) {
		BaseValueObject baseValueObject = new BaseValueObject();
		AmCouponsWithBLOBs amCoupons = findCounponsByCoupons(id);
		if(amCoupons == null) {
			baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
			baseValueObject.setMsg("找不到该优惠券信息！");
			return baseValueObject;
		}
		//判断是否符合提交审核的条件
		if(amCoupons.getcAuditstatus() == SmiConstants.UNCOMMITTED){
			amCoupons.setcCouponsid(id);
			amCoupons.setcLastmoddate(new Date());
			amCoupons.setcLastmoduser(username);
			amCoupons.setcAuditstatus(auditStatus);
			couponsMapper.updateByPrimaryKeySelective(amCoupons);
			baseValueObject.setCode(CodeEnum.SUCCESS);
		}else {
			baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
			baseValueObject.setMsg("提交失败，请检查该优惠券是否未提交状态。");
		}
		return baseValueObject;
	}

	@Transactional
	@Override
	public BaseValueObject copyCouponsById(String id, String username) {
		BaseValueObject baseValueObject = new BaseValueObject();
		
		AmCouponsWithBLOBs coupons = findCounponsByCoupons(id);
		if(coupons == null) {
			baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
			baseValueObject.setMsg("找不到该优惠券信息！");
			return baseValueObject;
		}
		if(coupons.getcAuditstatus() == SmiConstants.HANDEDOUT || coupons.getcAuditstatus() == SmiConstants.EXPIRED){
			//已发放和已过期才可以复制优惠券
			Date date = new Date();
			coupons.setcCreatedate(date);
			coupons.setcLastmoddate(date);
			coupons.setcCreateuser(username);
			coupons.setcLastmoduser(username);
			coupons.setcAuditstatus(SmiConstants.UNCOMMITTED);
			coupons.setcCouponsid(UniqueGenerate.createUniqueId());
			couponsMapper.insert(coupons);
			baseValueObject.setCode(CodeEnum.SUCCESS);
		}else {
			baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
			baseValueObject.setMsg("只有已发放和已过期的优惠券才可以复制，请检查此优惠券状态。");
		}
		return baseValueObject;
	}

	/**
	 * 根据会员卡号获取优惠券信息
	 */
	@Override
	public SmiResult<List<CustCouponsVo>> findCounponsDetailsJoinTable(Map<String,Object> custNbrMap) {
		SmiResult<List<CustCouponsVo>> smiResult=new SmiResult<List<CustCouponsVo>>();
		try {
			// 开始分页
			PageHelper.startPage((Integer)custNbrMap.get("pageNum"), (Integer)custNbrMap.get("pageSize"));
			String custNbr=(String) custNbrMap.get("custNbr");
			List<CustCouponsVo> amList = this.amCounponsDetailsMapper.selectByCdCustId(custNbr);
			PageInfo<CustCouponsVo> page = new PageInfo<CustCouponsVo>(amList);
			if (amList.size() > 0) {
				smiResult.setData(amList);
			}
			smiResult.setPages(page.getPages());
			smiResult.setTotal(page.getTotal());
			smiResult.setPageSize(page.getPageSize());
		} catch (Exception e) {
			this.LOGGER.error("根据会员卡号获取优惠券信息异常" + e);
			throw new SmiBusinessException("根据会员卡号获取优惠券信息异常");
		}
		return smiResult;
	}

	/**
	 * 根据优惠券id查出优惠卷信息
	 */
	@Override
	public AmCouponsWithBLOBs findCounponsByCoupons(String couponsId) {
		AmCouponsExample amCouponsExample = new AmCouponsExample();
		Criteria amCriteria = amCouponsExample.createCriteria();
		amCriteria.andCCouponsidEqualTo(couponsId);
		amCriteria.andCIsdeteleEqualTo(SmiConstants.NOREMOVE);
		List<AmCouponsWithBLOBs> couponsList = couponsMapper.selectByExampleWithBLOBs(amCouponsExample);
		if(couponsList.size() == 0) 
			return null;
		else 
			return couponsList.get(0);
	}

	/**
	 * 统计发送数量
	 */
	@Override
	public int CountCouponsUsed(int gitpag) {
		int sum = this.amCounponsDetailsMapper.selectCountByUsed(gitpag);
		return sum;
	}

	/**
	 * 查出所有属于礼包类型的优惠券
	 */
	@Override
	public SmiResult<List<CouponsPackageVo>> selectIsGiftPackage(Map<String, Object> paramMap) {
		List<CouponsPackageVo> couponsList = new ArrayList<CouponsPackageVo>();
		SmiResult<List<CouponsPackageVo>> smiResult = new SmiResult<List<CouponsPackageVo>>();
		int pageNum = (int) paramMap.get("pageNum");
		int pageSize = (int) paramMap.get("pageSize");
		try {
			// 开始分页
			PageHelper.startPage(pageNum, pageSize);
			List<AmCoupons> CounponsDetails = this.amCounponsDetailsMapper.selectIsGiftPackage(paramMap);
			PageInfo<AmCoupons> pageinfo = new PageInfo<>(CounponsDetails);
			if (CounponsDetails.size() > 0) {
				for (AmCoupons am : CounponsDetails) {
					CouponsPackageVo cv = new CouponsPackageVo();
					BeanUtils.copyProperties(cv, am);
					couponsList.add(cv);
				}
			} else {
				smiResult.setMsg("查无数据");
			}
			smiResult.setCode(CodeEnum.SUCCESS);
			smiResult.setData(couponsList);
			smiResult.setTotal(pageinfo.getTotal());
			smiResult.setPages(pageinfo.getPages());
			smiResult.setPageSize(pageinfo.getPageSize());
		} catch (Exception e) {
			this.LOGGER.error("获所有属于礼包类型的优惠券异常", e);
			throw new SmiBusinessException("获所有属于礼包类型的优惠券异常");
		}
		return smiResult;
	}

//	/**
//	 * 批量更新优惠券信息
//	 * 
//	 * @param param
//	 * @return
//	 */
//	@Override
//	public int batchUpdateCouponsIdByPackageId(Map<String, Object> paramMap) {
//		int num = this.amCounponsDetailsMapper.batchUpdateCouponsIdByPackageId(paramMap);
//		this.LOGGER.info("批量更新优惠券" + num + "数据");
//		return num;
//	}

	/**
	 * 根据礼包id获取优惠券信息
	 */
	@Override
	public List<CouponsSomeVo> findCouponsByGiftPackageId(String param) {
		List<CouponsSomeVo> couponsList = null;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("cdGiftPackageId", Integer.parseInt(param));
			couponsList = this.couponsMapper.selectCouBygiftPackageId(paramMap);
		} catch (Exception e) {
			this.LOGGER.error("根据礼包id获取优惠券信息异常", e);
			throw new SmiBusinessException("根据礼包id获取优惠券信息异常");
		}
		return couponsList;
	}

	/**
	 * 根据状态查询优惠券
	 */
	@Override
	public SmiResult<List<WraparoundResVo>> findCouponsByStatus(Map<String, Object> param) {
		List<WraparoundResVo> WraparoundResVoList = new ArrayList<WraparoundResVo>();
		SmiResult<List<WraparoundResVo>> smiResult = new SmiResult<List<WraparoundResVo>>();
		// 设置分页
		PageHelper.startPage((Integer) param.get("pageNum"), (Integer) param.get("pageSize"));
		List<AmCoupons> couponsList = this.couponsMapper.selectStatusByCoupons(param);
		PageInfo<AmCoupons> pageCoup = new PageInfo<AmCoupons>(couponsList);
		if (couponsList.size() > 0) {
			for (AmCoupons coupons : couponsList) {
				WraparoundResVo resVo = new WraparoundResVo();
				resVo.setWraparoundName(coupons.getcCouponsname());
				resVo.setId(coupons.getcCouponsid());
				resVo.setPutChannel(coupons.getcChannel());
				resVo.setUsableChannel(String.valueOf(coupons.getcUserchannel()));
				resVo.setActivityStartTime(coupons.getcActivitystarttime());
				resVo.setActivityEndTime(coupons.getcActivityendtime());
				resVo.setActivityArea(String.valueOf(coupons.getcActivityarea()));
				resVo.setStatus(String.valueOf(coupons.getcAuditstatus()));
				resVo.setcRemark(coupons.getcRemark());
				WraparoundResVoList.add(resVo);
			}
			smiResult.setCode(CodeEnum.SUCCESS);
			smiResult.setData(WraparoundResVoList);
			smiResult.setMsg("成功");
		} else {
			smiResult.setCode(CodeEnum.SUCCESS);
			smiResult.setData(WraparoundResVoList);
			smiResult.setMsg("查无数据");
		}
		smiResult.setPages(pageCoup.getPages());
		smiResult.setTotal(pageCoup.getTotal());
		smiResult.setPageSize(pageCoup.getPageSize());
		return smiResult;
	}

}
