package com.smi.am.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.smi.am.dao.model.AmCoupons;
import com.smi.am.dao.model.AmCouponsExample;
import com.smi.am.dao.model.AmCouponsWithBLOBs;
import com.smi.am.service.vo.CouponRelationVo;
import com.smi.am.service.vo.CouponsSomeVo;
import com.smi.am.service.vo.CouponsVo;
import com.smi.am.service.vo.HomeCouponsInfo;

public interface AmCouponsMapper {
    int countByExample(AmCouponsExample example);

    int deleteByExample(AmCouponsExample example);

    int deleteByPrimaryKey(String cCouponsid);

    int insert(AmCouponsWithBLOBs record);

    int insertSelective(AmCouponsWithBLOBs record);

    List<AmCouponsWithBLOBs> selectByExampleWithBLOBs(AmCouponsExample example);

    List<AmCoupons> selectByExample(AmCouponsExample example);

    AmCouponsWithBLOBs selectByPrimaryKey(String cCouponsid);

    int updateByExampleSelective(@Param("record") AmCouponsWithBLOBs record, @Param("example") AmCouponsExample example);

    int updateByExampleWithBLOBs(@Param("record") AmCouponsWithBLOBs record, @Param("example") AmCouponsExample example);

    int updateByExample(@Param("record") AmCoupons record, @Param("example") AmCouponsExample example);

    int updateByPrimaryKeySelective(AmCouponsWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(AmCouponsWithBLOBs record);

    int updateByPrimaryKey(AmCoupons record);
    
    /**
     * 根据查询条件查询优惠券信息
     * @param conditionMap
     * @return 
     */
    List<CouponsVo> searchCouponsByConditions(Map<String, Object> conditionMap);
   
    /**
     * 批量删除优惠券(逻辑删除)
     * @param couponsIds
     * @return
     */
    int batchDeteleCouponsByIds(String[] couponsIds);
    
    /**
     * 获取星美总部活动中的优惠券数量
     * @param paramMap
     * @return
     */
    List<HomeCouponsInfo> getCouponsNumBySmiHeader(Map<String, Object> paramMap);
    
    /**
     * 获取各个运营中心的优惠券数量
     * @param paramMap
     * @return
     */
    List<HomeCouponsInfo> getCouponsNumByOperCenter(Map<String, Object> paramMap);
    
    /**
     * 根据状态查询优惠券信息
     * @param paramMap
     * @return
     */
    List<AmCoupons> selectStatusByCoupons(Map<String,Object> paramMap);
    
    /**
     * 根据优惠券ids获得优惠券信息
     * @param couponsIds
     * @return
     */
    List<CouponRelationVo> getCouponsShowVos(List<String> couponsIds);
    
    /**
     * 获取所有已到活动开始时间未审核的优惠券IDS
     * @return
     */
    List<String> getAllUnAduitCoupons();
    
    /**
     * 获取所有已到活动结束时间未发放的优惠券IDS
     * @return
     */
    List<String> getAllUnDeliveryCoupons();
    
   /**
    * 根据优惠券id批量更新优惠券状态
    * @param couponsIds
    * @param auditStatus
    * @return
    */
    int updateAuditStatusByCouponsId(Map<String, Object> paramMap);
    
    /**
     * 根据礼包id查出优惠券
     * @param param
     * @return
     */
    List<CouponsSomeVo> selectCouBygiftPackageId(Map<String,Object> param);
    
    /**
     * 更新所有已到活动开始时间并确认上线的优惠券状态为已发放
     * @return
     */
    int deliveryConfirmOnlineCoupons();
    
    /**
     * 更新所有已到活动开始时间还未审核的优惠券状态为未通过
     * @return
     */
    int updateAllUnAuditCoupons();
    
    /**
     * 更新所有已到活动结束时间还未发放的优惠券状态为已过期
     * @return
     */
    int updateAllUnDeliveryCoupons();
    
}