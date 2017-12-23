package com.smi.am.service;

import java.util.List;

import com.smi.am.service.vo.ActivityAreaVo;
import com.smi.am.service.vo.ActivityShopVo;
import com.smi.am.service.vo.CounponsTypeVo;
import com.smi.am.service.vo.DepartmentVo;
import com.smi.am.service.vo.PreferentialtypeVo;

import net.bull.javamelody.MonitoredWithSpring;

/**
 * 基础数据服务层
 * @author yanghailong
 *
 */
@MonitoredWithSpring
public interface ICommonService {

	/**
	 * 根据部门id获取所属区域id
	 * @param deptId
	 * @return
	 */
	public int getAreaIdByDeptId(int deptId) ;
	
	/**
	 * 获取所有优惠券类型基础数据
	 * @return
	 */
	List<CounponsTypeVo> getAllCouponsType() throws Exception;
	
	/**
	 * 获取所有优惠券分类类别基础数据
	 * @return
	 * @throws Exception
	 */
	List<PreferentialtypeVo> getAllPreferentialtype() throws Exception;
	
	/**
	 * 根据areaId获取该运营中心的所有大区
	 * areaId=0时，获取所有运营中心
	 * @return
	 * @throws Exception
	 */
	List<ActivityAreaVo> getAllActivityArea(Integer areaId) throws Exception;
	
	
	/**
	 * 根据大区ID获取该大区下所有的门店信息
	 * @param zoneId
	 * @return
	 * @throws Exception
	 */
	List<ActivityShopVo> getAllActivityStoreByZoneId(Integer zoneId) throws Exception;
	
	
	/**
	 * 根据活动区Id得到所属活动门店
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	List<ActivityAreaVo> getShopByAreaId(int areaId) throws Exception;
	
	/**
	 * 获取部门基础数据
	 * @return
	 * @throws Exception
	 */
	List<DepartmentVo> getAllDeptInfo() throws Exception;
}
