package com.smi.am.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.smi.am.dao.model.AmCounponsDetails;
import com.smi.am.dao.model.AmCoupons;
import com.smi.am.service.vo.CouponsEditVo;
import com.smi.am.service.vo.CouponsInsertVo;
import com.smi.am.service.vo.CouponsPackageVo;
import com.smi.am.service.vo.CouponsShowVo;
import com.smi.am.service.vo.CouponsSomeVo;
import com.smi.am.service.vo.CouponsVo;
import com.smi.am.service.vo.CustCouponsVo;
import com.smi.am.service.vo.WraparoundResVo;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.valueobject.BaseValueObject;

import net.bull.javamelody.MonitoredWithSpring;

/**
 * 优惠券接口
 * @author yanghailong
 *
 */
@MonitoredWithSpring
public interface ICouponsService {

	/**
	 * 新增优惠券
	 * @param couponsVo
	 * @param username
	 * @return
	 * @author yanghailong
	 */
	public int saveCoupons(CouponsInsertVo couponsVo,String username);
	
	/**
	 * 优惠券列表查询
	 * @param conditionMap
	 * @return
	 * @author yanghailong
	 */
	public SmiResult<List<CouponsVo>> searchCouponsByConditions(Map<String, Object> conditionMap);
	
	/**
	 * 根据优惠券id查询优惠券信息
	 * @param couponsId
	 * @return
	 * @author yanghailong
	 */
	public SmiResult<CouponsShowVo> getCouponsVoById(String couponsId);
	
	/**
	 * 编辑优惠券详情
	 * @param couponsId
	 * @return
	 */
	public SmiResult<CouponsShowVo> editCouponsPageShowById(String couponsId) throws Exception;
	
	/**
	 * 根据优惠券ID编辑优惠券信息
	 * @param vo
	 * @return
	 * @author yanghailong
	 */
	public BaseValueObject editCouponsById(CouponsEditVo vo,String username);
	
	
	/**
	 * 根据优惠券ids删除优惠券
	 * @param ids
	 * @return
	 */
	public BaseValueObject deleteCouponsByIds(String ids) ;
	
	/**
	 * 根据id更新优惠券状态
	 * @param id
	 * @param auditStatus
	 * @param username
	 * @return
	 */
	public BaseValueObject commitAuditCouponsById(String id, int auditStatus,String username) ;
	
	/**
	 * 根据id复制优惠券信息
	 * @param id
	 * @param username
	 * @return
	 */
	public BaseValueObject copyCouponsById(String id,String username);
	
	/**
	 * 优惠券审核接口
	 * @param couponsId
	 * @param auditStatus
	 * @return
	 */
	public BaseValueObject auditCouponsById(String couponsId,int auditStatus,String username,String remark) throws IOException;
	
	/**
	 * 优惠券发放
	 * @param couponsId
	 * @param username
	 * @return
	 */
	public BaseValueObject deliveryCouponsById(String couponsId,String username);
	

	/**
	 * 根据会员卡号获取优惠券详情
	 * 
	 * @param amCounponsDetails
	 * @return
	 */
	public SmiResult<List<CustCouponsVo>> findCounponsDetailsJoinTable(Map<String,Object> amCounponsDetails);

	/**
	 * 根据优惠卷id查询
	 * 
	 * @param couponsId
	 * @return
	 */
	public AmCoupons findCounponsByCoupons(String couponsId);

	/**
	 * 统计发送数量
	 * 
	 * @param cdUsed
	 * @return
	 */
	public int CountCouponsUsed(int gitpag);

	/**
	 * 查出所有属于礼包的优惠券
	 * 
	 * @return
	 */
	public SmiResult<List<CouponsPackageVo>>  selectIsGiftPackage(Map<String,Object> param);
	
	
	
//	/**
//	 * 批量更新优惠券信息
//	 * @param param
//	 * @return
//	 */
//	int batchUpdateCouponsIdByPackageId(Map<String,Object> paramMap);
	
	/**
	 * 根据礼包id查询优惠券信息
	 * 
	 * @param param
	 * @return
	 */
	List<CouponsSomeVo> findCouponsByGiftPackageId(String param);
	
	/**
	 * 根据状态查询优惠券
	 * @param param
	 * @return
	 */
	SmiResult<List<WraparoundResVo>> findCouponsByStatus(Map<String,Object> param);
}
