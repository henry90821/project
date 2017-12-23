package com.smi.am.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.smi.am.dao.model.AmCounponsDetails;
import com.smi.am.dao.model.AmCounponsDetailsExample;
import com.smi.am.dao.model.AmCoupons;
import com.smi.am.service.vo.CustCouponsVo;

public interface AmCounponsDetailsMapper {
    int countByExample(AmCounponsDetailsExample example);

    int deleteByExample(AmCounponsDetailsExample example);

    int deleteByPrimaryKey(Integer cdDetailid);

    int insert(AmCounponsDetails record);

    int insertSelective(AmCounponsDetails record);

    List<AmCounponsDetails> selectByExample(AmCounponsDetailsExample example);

    AmCounponsDetails selectByPrimaryKey(Integer cdDetailid);
    
    List<CustCouponsVo> selectByCdCustId(String custNbr);
    
    int updateByExampleSelective(@Param("record") AmCounponsDetails record, @Param("example") AmCounponsDetailsExample example);

    int updateByExample(@Param("record") AmCounponsDetails record, @Param("example") AmCounponsDetailsExample example);

    int updateByPrimaryKeySelective(AmCounponsDetails record);

    int updateByPrimaryKey(AmCounponsDetails record);
	int selectCountByUsed(int gpId);

	/**
	 * 查出是属于礼包的优惠券
	 * 
	 * @return
	 */
	List<AmCoupons> selectIsGiftPackage(Map<String, Object> paramMap);

	/**
	 * 根据礼包id批量更新优惠券
	 * 
	 * @param param
	 * @return
	 */

	int batchUpdateCouponsIdByPackageId(List<AmCounponsDetails> paramList);
	
	/**
	 * !-- 根据优惠券id批量更新礼包id以及会员卡号 -->
	 * @param param
	 * @return
	 */
	int batchUpdatePackageIdByCouponsId(Map<String,Object> param);
	
	/**
	 * 批量更新优惠券明细信息
	 * @param param
	 * @return
	 */
	int batchUpdatePackageIdByCouponsIds(List<AmCounponsDetails> param);
	
	/**
	 * 批量新增优惠券明细数据
	 * @param details
	 * @return
	 */
	int batchInsertCouponsDetails(List<AmCounponsDetails> details);
	
}