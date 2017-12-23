package com.smi.am.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smi.am.dao.AmCounponsTypeMapper;
import com.smi.am.dao.model.AmCounponsType;
import com.smi.am.service.ICouponsTypeService;
import com.smi.am.service.vo.CounponsTypeVo;
import com.smi.am.service.vo.CouponsVo;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

@Service
public class CouponsTypeServiceImpl implements ICouponsTypeService{
	
	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());
	
	@Autowired
	private AmCounponsTypeMapper amCounponsTypeMapper;

	@Transactional
	@Override
	public BaseValueObject saveCouponsType(CounponsTypeVo couponsTypeVo, String username) {
		BaseValueObject baseValueObject = new BaseValueObject();
		AmCounponsType amCounponsType = new AmCounponsType();
		Date date = new Date();
		amCounponsType.setCtCreatedate(date);
		amCounponsType.setCtCreateuser(username);
		amCounponsType.setCtLastmoddate(date);
		amCounponsType.setCtLastmoduser(username);
		try {
			BeanUtils.copyProperty(amCounponsType, "ctCounponsname", couponsTypeVo.getCtCounponsname());
			BeanUtils.copyProperty(amCounponsType, "ctCounponstype", couponsTypeVo.getCtCounponstype());
			BeanUtils.copyProperty(amCounponsType, "ctRemark", couponsTypeVo.getCtRemark());
			amCounponsTypeMapper.insert(amCounponsType);
		} catch (IllegalAccessException e) {
			LOGGER.error("service层实体类复制异常", e);
			throw new SmiBusinessException("service层实体类复制异常");
		} catch (Exception e) {
			LOGGER.error("service层实体类复制异常", e);
			throw new SmiBusinessException("service层异常");
		}
		baseValueObject.setCode(CodeEnum.SUCCESS);
		return baseValueObject;
	}

	@Override
	public SmiResult<List<CounponsTypeVo>> getAllCouponsTypeList(int pageNum,int pageSize) {
		SmiResult<List<CounponsTypeVo>> smiResult = new SmiResult<List<CounponsTypeVo>>();
		PageHelper.startPage(pageNum, pageSize, "ct_createDate desc ");
		List<CounponsTypeVo> couponTypeList = amCounponsTypeMapper.getAllConponsType();
		//4、取分页后结果
		PageInfo<CounponsTypeVo> pageInfo = new PageInfo<>(couponTypeList);
		smiResult.setTotal(pageInfo.getTotal());
		smiResult.setPages(pageInfo.getPages());
		smiResult.setPageSize(pageInfo.getPageSize());
		smiResult.setData(couponTypeList);
		smiResult.setCode(CodeEnum.SUCCESS);
		return smiResult;
	}

}
