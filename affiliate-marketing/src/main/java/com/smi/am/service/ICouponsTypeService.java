package com.smi.am.service;

import java.util.List;

import com.smi.am.service.vo.CounponsTypeVo;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.valueobject.BaseValueObject;

import net.bull.javamelody.MonitoredWithSpring;

/**
 * 优惠券分类接口
 * @author yanghailong
 */

@MonitoredWithSpring
public interface ICouponsTypeService {
	
	/**
	 * 新增优惠券分类
	 * @param couponsTypeVo
	 * @param username
	 * @return
	 */
	public BaseValueObject saveCouponsType(CounponsTypeVo couponsTypeVo,String username);

	/**
	 * 获取所有优惠券类型信息
	 * @return
	 */
	public SmiResult<List<CounponsTypeVo>> getAllCouponsTypeList(int pageNum,int pageSize);
}
