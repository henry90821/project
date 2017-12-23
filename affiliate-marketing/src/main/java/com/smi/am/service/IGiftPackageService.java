package com.smi.am.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.smi.am.dao.model.AmGiftPackage;
import com.smi.am.service.vo.CustPackVo;
import com.smi.am.service.vo.GiftPackageInfo;
import com.smi.am.service.vo.GiftPackageReqVo;
import com.smi.am.service.vo.GiftPackageResVo;
import com.smi.am.service.vo.GiftPackageVo;
import com.smi.am.service.vo.WraparoundResVo;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.valueobject.BaseValueObject;

import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface IGiftPackageService {

	/**
	 * 根据会员卡号查询礼包信息
	 * 
	 * @param custNbr
	 * @return
	 */
	SmiResult<List<GiftPackageResVo>> selectGiftPackageByCustNbr(Map<String, Object> custNbrMap);

	/**
	 * 查询礼包列表
	 * 
	 * @param custNbr
	 * @return
	 */
	SmiResult<List<GiftPackageResVo>> selectGiftPackageList(GiftPackageVo custPackVo);

	/**
	 * 编辑礼包
	 * 
	 * @param custPackVo
	 * @return
	 */
	BaseValueObject updateGiftPackage(GiftPackageReqVo custPackVo,HttpServletRequest requset);
	
	/**
	 * 礼包新增
	 * @param giftPackageVo
	 * @return
	 */
	int addGiftPackage(GiftPackageVo amGiftPackage,HttpServletRequest requset);
	
	/**
	 * 逻辑删除
	 * @param param
	 * @return
	 */
	BaseValueObject deleteLogicGiftPackage(Map<String,Object> paramMap,HttpServletRequest requset);
	
	
	
	/**
	 * 根据状态查询礼包信息
	 * @param param
	 * @return
	 */
	SmiResult<List<WraparoundResVo>> selectGiftPackageByStatus(Map<String,Object> param);
	
	/**
	 * 复制礼包
	 * @param param
	 * @return
	 */
	int copyGiftPackage(String param,HttpServletRequest requset);
	
	/**
	 * 查看
	 * @param param
	 * @return
	 */
	SmiResult<GiftPackageInfo> findGiftByPackageId(String param);
	
	/**
	 * 发放礼包
	 */
	BaseValueObject sendGiftPackage(Map<String,Object> param,HttpServletRequest request);
	
	/**
	 * 礼包提交审核
	 * @param param
	 * @param request
	 * @return
	 */
	BaseValueObject reviewGiftPackage(Map<String,Object> param,HttpServletRequest request);
}
