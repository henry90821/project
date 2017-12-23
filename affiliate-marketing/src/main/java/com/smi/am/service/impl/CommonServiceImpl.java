package com.smi.am.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.am.dao.AmActivityAreaMapper;
import com.smi.am.dao.AmActivityShopMapper;
import com.smi.am.dao.AmCounponsTypeMapper;
import com.smi.am.dao.AmDepartmentMapper;
import com.smi.am.dao.AmPreferentialtypeMapper;
import com.smi.am.dao.model.AmActivityArea;
import com.smi.am.dao.model.AmActivityAreaExample;
import com.smi.am.dao.model.AmActivityShop;
import com.smi.am.dao.model.AmActivityShopExample;
import com.smi.am.dao.model.AmCounponsType;
import com.smi.am.dao.model.AmCounponsTypeExample;
import com.smi.am.dao.model.AmDepartment;
import com.smi.am.dao.model.AmDepartmentExample;
import com.smi.am.dao.model.AmDepartmentExample.Criteria;
import com.smi.am.dao.model.AmPreferentialtype;
import com.smi.am.dao.model.AmPreferentialtypeExample;
import com.smi.am.service.ICommonService;
import com.smi.am.service.vo.ActivityAreaVo;
import com.smi.am.service.vo.ActivityShopVo;
import com.smi.am.service.vo.CounponsTypeVo;
import com.smi.am.service.vo.DepartmentVo;
import com.smi.am.service.vo.PreferentialtypeVo;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

@Service
public class CommonServiceImpl implements ICommonService{

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());
	
	@Autowired
	private AmDepartmentMapper amDepartmentMapper; 
	
	@Autowired
	private AmCounponsTypeMapper amCounponsTypeMapper;
	
	@Autowired
	private AmActivityAreaMapper amActivityAreaMapper;
	
	@Autowired
	private AmActivityShopMapper amActivityShopMapper; 
	
	@Autowired
	private AmPreferentialtypeMapper amPreferentialtypeMapper; 
	
	@Override
	public int getAreaIdByDeptId(int deptId) {
		AmDepartmentExample amDepartmentExample = new AmDepartmentExample();
		Criteria createCriteria = amDepartmentExample.createCriteria();
		createCriteria.andDDepartmentidEqualTo(deptId);
		List<AmDepartment> selectByExample = amDepartmentMapper.selectByExample(amDepartmentExample);
		AmDepartment amDepartment = selectByExample.get(0);
		return amDepartment.getdAreaid();
	}

	@Override
	public List<CounponsTypeVo> getAllCouponsType() throws Exception {
		AmCounponsTypeExample amCounponsType = new AmCounponsTypeExample();
		amCounponsType.setOrderByClause(" ct_createDate desc ");
		List<AmCounponsType> counponslist = amCounponsTypeMapper.selectByExample(amCounponsType);
		List<CounponsTypeVo> couponVoList = new ArrayList<CounponsTypeVo>();
		CounponsTypeVo counponsTypeVo = null;
		for (AmCounponsType couponType : counponslist) {
			counponsTypeVo = new CounponsTypeVo();
			BeanUtils.copyProperties(counponsTypeVo, couponType);
			couponVoList.add(counponsTypeVo);
		}
		return couponVoList ;
	}
	
	@Override
	public List<PreferentialtypeVo> getAllPreferentialtype() throws Exception {
		List<PreferentialtypeVo> typevos = new ArrayList<PreferentialtypeVo>();
		PreferentialtypeVo preferentialtypeVo = null;
		List<AmPreferentialtype> typelist = amPreferentialtypeMapper.selectByExample(null);
		for (AmPreferentialtype amPreferentialtype : typelist) {
			preferentialtypeVo = new PreferentialtypeVo();
			BeanUtils.copyProperties(preferentialtypeVo, amPreferentialtype);
			typevos.add(preferentialtypeVo);
		}
		return typevos;
	}


	@Override
	public List<ActivityAreaVo> getAllActivityArea(Integer areaId) throws Exception {
		AmActivityAreaExample amActivityAreaExample = new AmActivityAreaExample();
		List<ActivityAreaVo> areaVos = new ArrayList<ActivityAreaVo>();
		ActivityAreaVo activityAreaVo = null;
		amActivityAreaExample.setOrderByClause(" a_id desc ");
		com.smi.am.dao.model.AmActivityAreaExample.Criteria createCriteria = amActivityAreaExample.createCriteria();
		createCriteria.andAAreapidEqualTo(areaId);
		List<AmActivityArea> areaList = amActivityAreaMapper.selectByExample(amActivityAreaExample);
		for (AmActivityArea amActivityArea : areaList) {
			activityAreaVo = new ActivityAreaVo();
			BeanUtils.copyProperties(activityAreaVo, amActivityArea);
			areaVos.add(activityAreaVo);
		}
		return areaVos;
	}

	@Override
	public List<ActivityShopVo> getAllActivityStoreByZoneId(Integer zoneId) throws Exception {
		AmActivityShopExample amActivityShopExample = new AmActivityShopExample();
		ActivityShopVo activityShopVo = null;
		List<ActivityShopVo> shopVos = new ArrayList<ActivityShopVo>();
		amActivityShopExample.setOrderByClause(" as_id desc ");
		com.smi.am.dao.model.AmActivityShopExample.Criteria createCriteria = amActivityShopExample.createCriteria();
		createCriteria.andAsActivityareaidEqualTo(zoneId);
		List<AmActivityShop> shopList = amActivityShopMapper.selectByExample(amActivityShopExample);
		for (AmActivityShop amActivityShop : shopList) {
			activityShopVo = new ActivityShopVo();
			BeanUtils.copyProperties(activityShopVo, amActivityShop);
			shopVos.add(activityShopVo);
		}
		return shopVos;
	}
	
	
	
	@Override
	public List<ActivityAreaVo> getShopByAreaId(int areaId) throws Exception {
		List<ActivityAreaVo> areaVos = new ArrayList<ActivityAreaVo>();
		ActivityAreaVo areaVo = null; 
		AmActivityAreaExample amActivityAreaExample = new AmActivityAreaExample();
		amActivityAreaExample.setOrderByClause(" a_id desc ");
		com.smi.am.dao.model.AmActivityAreaExample.Criteria createCriteria = amActivityAreaExample.createCriteria();
		createCriteria.andAAreapidEqualTo(areaId);
		List<AmActivityArea> areas = amActivityAreaMapper.selectByExample(amActivityAreaExample);
		for (AmActivityArea area : areas) {
			areaVo = new ActivityAreaVo();
			BeanUtils.copyProperties(areaVo, area);
			//获取活动门店
			int areaid = area.getaId();
			AmActivityShopExample example = new AmActivityShopExample();
			List<ActivityShopVo>  activityShopVos = new ArrayList<ActivityShopVo>();
			ActivityShopVo shopVo = null;
			example.setOrderByClause(" as_id desc ");
			if(areaId > 0){
				com.smi.am.dao.model.AmActivityShopExample.Criteria createCriteriashop = example.createCriteria();
				createCriteriashop.andAsActivityareaidEqualTo(areaid);
			}
			List<AmActivityShop> selectByExample = amActivityShopMapper.selectByExample(example);
			for (AmActivityShop amActivityShop : selectByExample) {
				shopVo = new ActivityShopVo();
				BeanUtils.copyProperties(shopVo, amActivityShop);
				activityShopVos.add(shopVo);
			}
			areaVo.setActivityShopVos(activityShopVos);
			
			areaVos.add(areaVo);
		}
		return areaVos;
	}

	@Override
	public List<DepartmentVo> getAllDeptInfo() throws Exception {
		AmDepartmentExample amDepartmentExample = new AmDepartmentExample();
		List<DepartmentVo> departmentVos = new ArrayList<DepartmentVo>();
		DepartmentVo departmentVo = null;
		amDepartmentExample.setOrderByClause(" d_departmentId ");
		List<AmDepartment> departments = amDepartmentMapper.selectByExample(amDepartmentExample);
		for (AmDepartment amDepartment : departments) {
			departmentVo = new DepartmentVo();
			BeanUtils.copyProperties(departmentVo, amDepartment);
			departmentVos.add(departmentVo);
		}
		return departmentVos;
	}


	

}
