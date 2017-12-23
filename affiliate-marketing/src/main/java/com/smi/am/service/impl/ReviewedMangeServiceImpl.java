package com.smi.am.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smi.am.constant.SmiConstants;
import com.smi.am.dao.AmGiftPackageMapper;
import com.smi.am.service.ICouponsService;
import com.smi.am.service.IGiftPackageService;
import com.smi.am.service.IReviewedMangeService;
import com.smi.am.service.vo.UserVo;
import com.smi.am.service.vo.WraparoundResVo;
import com.smi.am.utils.SessionManager;
import com.smi.am.utils.SmiResult;
import com.smi.am.utils.ValidatorUtil;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

/**
 * 审核管理服务基类
 * 
 * @author smi
 *
 */
@Service
public class ReviewedMangeServiceImpl implements IReviewedMangeService {

	@Autowired
	private IGiftPackageService iGiftPackageService;

	@Autowired
	private ICouponsService iCouponsService;

	@Autowired
	private AmGiftPackageMapper amGiftPackageMapper;

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	/**
	 * 卷包查询
	 */
	@Override
	public SmiResult<List<WraparoundResVo>> reviewMange(Map<String, Object> param, HttpServletRequest requset) {
		SmiResult<List<WraparoundResVo>> smiResult = new SmiResult<List<WraparoundResVo>>();
		Map<String, Object> reqParamMap = new HashMap<String, Object>();
		String wrapType = (String) param.get("wraparoundType");
		String status = (String) param.get("reviewStatus");
		UserVo userVo = (UserVo) SessionManager.getUserInfo(requset);
		Integer areaId = userVo.getAreaId();
		assembleParam(reqParamMap, status);
		reqParamMap.put("areaId", areaId);
		reqParamMap.put("pageNum", param.get("pageNum"));
		reqParamMap.put("pageSize", param.get("pageSize"));
		// 设置分页
		if (!"".equals(wrapType) && wrapType != null) {
			if (SmiConstants.GIFT_PACKAGE_TYPE.equals(wrapType)) {
				smiResult = this.iGiftPackageService.selectGiftPackageByStatus(reqParamMap);
			} else if (SmiConstants.COUPONS_TYPE.equals(wrapType)) {
				smiResult = this.iCouponsService.findCouponsByStatus(reqParamMap);
			}
		} else {
			smiResult.setCode(CodeEnum.REQUEST_ERROR);
			smiResult.setMsg("请求卷包类型不能为空");
		}
		return smiResult;
	}

	/**
	 * 组装参数
	 * 
	 * @param paramMap
	 * @param str
	 */
	private static void assembleParam(Map<String, Object> paramMap, String str) {
		String[] s = str.split(",");
		if (s.length == 5) {
			paramMap.put("STATUS_ALL", "2,3,4,5,7");
		} else if (str.matches("3")) {
			paramMap.put("STATUS_NOT_ADOPT", "3");
		} else if (str.matches("2")) {
			paramMap.put("STATUS_WAIT", "2");
		} else if (str.matches("4,5,7")) {
			paramMap.put("STATUS_ADOPT", "4,5,7");
		} else {
			paramMap.put("STATUS_SEND", "5");
		}
	}

	/**
	 * 礼包审核
	 */
	@Override
	public BaseValueObject reviewed(Map<String, Object> param, HttpServletRequest requset) {
		BaseValueObject baseValueObject = new BaseValueObject();
		String packId = (String) param.get("giftPackageId");
		String reviewStatus = (String) param.get("reviewStatus");
		String remark = (String) param.get("reason");
		try {
			if (!ValidatorUtil.isEmpty(packId) && !ValidatorUtil.isEmpty(reviewStatus)) {
				Integer giftPackId = Integer.valueOf(packId);
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("packageId", giftPackId);
				paramMap.put("startStatus", String.valueOf(SmiConstants.INAUDIT));
				paramMap.put("endStatus", reviewStatus);
				paramMap.put("gpRemark", remark);
				int num = this.amGiftPackageMapper.updateStatusByPackageId(paramMap);
				if (num > 0) {
					baseValueObject.setCode(CodeEnum.SUCCESS);
					baseValueObject.setMsg("礼包审核操作成功");
				} else {
					baseValueObject.setCode(CodeEnum.PROFILE_MISSING);
					baseValueObject.setMsg("改礼包不是待审核状态");
				}
			} else {
				baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
				baseValueObject.setMsg("请求参数有误");
			}
		} catch (Exception e) {
			this.LOGGER.error("审核礼包异常", e);
			throw new SmiBusinessException("审核礼包异常");
		}
		return baseValueObject;

	}
}
