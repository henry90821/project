package com.smi.am.dao;

import java.util.List;
import java.util.Map;

import com.smi.am.dao.model.AmGiftPackage;
import com.smi.am.service.vo.CustPackVo;
import com.smi.am.service.vo.GiftPackageVo;
import com.smi.am.service.vo.HomeCouponsInfo;
import com.smi.am.service.vo.HomeGitPackageInfo;

public interface AmGiftPackageMapper {

	int deleteByPrimaryKey(Integer gpId);

	int insert(AmGiftPackage record);

	int insertSelective(AmGiftPackage record);

	/**
	 * 根据礼包id查询礼包详情
	 * 
	 * @param gpId
	 * @return
	 */
	AmGiftPackage selectByPrimaryKey(Integer gpId);

	/**
	 * 礼包的逻辑删除
	 * 
	 * @param record
	 * @return
	 */
	int updateByPrimaryKeySelective(List<Integer> record);

	int updateByPrimaryKeyWithBLOBs(AmGiftPackage record);

	/**
	 * 根据礼包id更新礼包信息
	 * @param record
	 * @return
	 */
	int updateByPrimaryKey(AmGiftPackage record);

	List<AmGiftPackage> selectGiftPackageByCustNbr(String custNbr);

	List<AmGiftPackage> selectGiftPackageByParam(GiftPackageVo custPackVo);

	/**
	 * 新增礼包
	 * 
	 * @param record
	 * @return
	 */
	int insertAmGiftPackage(AmGiftPackage record);

	/**
	 * 根据状态查询礼包信息
	 * 
	 * @param param
	 * @return
	 */
	List<AmGiftPackage> selectGiftPackageByStatus(Map<String, Object> param);

	/**
     * 获取星美总部活动中的礼包数量
     * @param paramMap
     * @return
     */
    List<HomeGitPackageInfo> getGitpackageNumBySmiHeader(Map<String, Object> paramMap);
    
    /**
     * 获取各个运营中心的礼包数量
     * @param paramMap
     * @return
     */
    List<HomeGitPackageInfo> getGitpackageNumByOperCenter(Map<String, Object> paramMap);
    
    
    /**
     * 根据礼包id修改发送礼包的状态
     * @param packageId
     * @return
     */
    int updateStatusByPackageId(Map<String,Object> param);
    
    /**
     * 更新所有已到活动开始时间并确认上线的礼包状态为已发放
     * @return
     */
    int deliveryConfirmOnlineGitPackage();
    
    /**
     * 更新所有已到活动开始时间还未审核的礼包状态为未通过
     * @return
     */
    int updateAllUnAuditGitPackage();
    
    /**
     * 更新所有已到活动结束时间还未发放的礼包状态为已过期
     * @return
     */
    int updateAllUnDeliveryGitPackage();
}