package com.smi.am.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.smi.am.constant.SmiConstants;
import com.smi.am.dao.AmActivityAreaMapper;
import com.smi.am.dao.AmCouponsMapper;
import com.smi.am.dao.AmDepartmentMapper;
import com.smi.am.dao.AmGiftPackageMapper;
import com.smi.am.dao.AmSyslogMapper;
import com.smi.am.dao.SystemLogMapper;
import com.smi.am.dao.model.AmActivityArea;
import com.smi.am.dao.model.AmActivityAreaExample;
import com.smi.am.dao.model.AmDepartment;
import com.smi.am.dao.model.AmGiftPackage;
import com.smi.am.dao.model.AmSyslog;
import com.smi.am.dao.model.AmSyslogExample;
import com.smi.am.service.IHomePageService;
import com.smi.am.service.vo.HomeCouponsInfo;
import com.smi.am.service.vo.HomeGitPackageInfo;
import com.smi.am.service.vo.HomePageVo;
import com.smi.am.service.vo.SystemLogVo;
import com.smi.am.service.vo.UserVo;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

@Service
public class HomePageServiceImpl implements IHomePageService{

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());
	
	@Autowired
	private AmCouponsMapper amCouponsMapper; 
	
	@Autowired
	private AmDepartmentMapper amDepartmentMapper;
	
	@Autowired
	private AmSyslogMapper amSyslogMapper;
	
	@Autowired
	private AmGiftPackageMapper amGiftPackageMapper;
	
	@Autowired
	private AmActivityAreaMapper amActivityAreaMapper; 
	
	@Override
	public SmiResult<HomePageVo> showHomePageInfo(UserVo userVo) throws Exception {
		Integer deptId = userVo.getuDepartid();
		AmDepartment dept = amDepartmentMapper.selectByPrimaryKey(deptId);
		Integer areaId = dept.getdAreaid();
		List<HomeCouponsInfo> couponsList = new ArrayList<HomeCouponsInfo>(); 
		List<HomeGitPackageInfo> gitpackageNumList = new ArrayList<HomeGitPackageInfo>();
		HomePageVo homePageVo = new HomePageVo();
		SmiResult<HomePageVo> smiResult = new SmiResult<HomePageVo>();
		if(areaId == SmiConstants.SMI_HEADER){
			//星美总部员工首页列表展示
			//优惠券列表
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("ISGITPACKAGE", 0);
			paramMap.put("AUDIT_STATUS", SmiConstants.STAYOUT);
			couponsList = amCouponsMapper.getCouponsNumBySmiHeader(paramMap);
			
			//礼包列表
			paramMap.clear();
			paramMap.put("AUDIT_STATUS", SmiConstants.STAYOUT);
			gitpackageNumList = amGiftPackageMapper.getGitpackageNumBySmiHeader(paramMap);
			
		}else{
			//各大运营中心首页列表展示
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("ACTIVITY_AREA", areaId);
			paramMap.put("ISGITPACKAGE", 0);
			paramMap.put("AUDIT_STATUS", SmiConstants.STAYOUT);
			couponsList = amCouponsMapper.getCouponsNumByOperCenter(paramMap);
			
			//礼包列表
			paramMap.clear();
			paramMap.put("AUDIT_STATUS", SmiConstants.STAYOUT);
			paramMap.put("ACTIVITY_AREA", areaId);
			gitpackageNumList = amGiftPackageMapper.getGitpackageNumByOperCenter(paramMap);
		}
		
		AmSyslogExample amSyslogExample = new AmSyslogExample();
		com.smi.am.dao.model.AmSyslogExample.Criteria createCriteria = amSyslogExample.createCriteria();
		createCriteria.andSlAreaidEqualTo(areaId);
		PageHelper.startPage(1, 5, " sl_operateDate desc ");
		List<AmSyslog> sysLogList = amSyslogMapper.selectByExample(amSyslogExample);
		List<SystemLogVo> syslogvos = new ArrayList<SystemLogVo>();
		SystemLogVo systemLogVo = null;
		
		for (AmSyslog log : sysLogList) {
			 systemLogVo = new SystemLogVo();
			 BeanUtils.copyProperty(systemLogVo, "slUsername", log.getSlUsername());
			 BeanUtils.copyProperty(systemLogVo, "slLogTitle", log.getSlLogtitle());
			 BeanUtils.copyProperty(systemLogVo, "slLogDetail", log.getSlLogdetail());
			 
			 syslogvos.add(systemLogVo);
		}
		
		homePageVo.setSysLogList(syslogvos);
		homePageVo.setCouponsNum(couponsList.size());
		homePageVo.setCouponsList(couponsList);
		homePageVo.setGitPackageNum(gitpackageNumList.size());
		homePageVo.setGitpackageList(gitpackageNumList);
		
		//区域ids转换成区域名称
		List<Integer> arealist = null; 
		StringBuilder areaNames = null;
		AmActivityAreaExample amActivityAreaExample = null;
		for (HomeCouponsInfo couponsInfo : couponsList) {
			String areaIds = couponsInfo.getAreaIds();
			if(StringUtils.isNoneEmpty(areaIds)){
				String[] areas = areaIds.split(",");
				arealist = new ArrayList<Integer>();
				areaNames = new StringBuilder();
				for (String area : areas) {
					arealist.add(Integer.parseInt(area));
				}
				amActivityAreaExample = new AmActivityAreaExample();
				com.smi.am.dao.model.AmActivityAreaExample.Criteria createCriteriaArea = amActivityAreaExample.createCriteria();
				createCriteriaArea.andAIdIn(arealist);
				List<AmActivityArea> areaList = amActivityAreaMapper.selectByExample(amActivityAreaExample);
				for (AmActivityArea amActivityArea : areaList) {
					areaNames.append(amActivityArea.getaActivityarea()).append(",");
				}
				couponsInfo.setAreaName(areaNames.substring(0, areaNames.length()-1));
			}
			
		}
		
		for (HomeGitPackageInfo gitpackageInfo : gitpackageNumList) {
			String areaIds = gitpackageInfo.getAreaIds();
			if(StringUtils.isNoneEmpty(areaIds)){
				String[] areas = areaIds.split(",");
				arealist = new ArrayList<Integer>();
				areaNames = new StringBuilder();
				for (String area : areas) {
					arealist.add(Integer.parseInt(area));
				}
				amActivityAreaExample = new AmActivityAreaExample();
				com.smi.am.dao.model.AmActivityAreaExample.Criteria createCriteriaArea = amActivityAreaExample.createCriteria();
				createCriteriaArea.andAIdIn(arealist);
				List<AmActivityArea> areaList = amActivityAreaMapper.selectByExample(amActivityAreaExample);
				for (AmActivityArea amActivityArea : areaList) {
					areaNames.append(amActivityArea.getaActivityarea()).append(",");
				}
				gitpackageInfo.setAreaName(areaNames.substring(0, areaNames.length()-1));
			}
			
		}
		
		smiResult.setData(homePageVo);
		smiResult.setCode(CodeEnum.SUCCESS);
		return smiResult;
	}

}
