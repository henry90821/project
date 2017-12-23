package com.smi.am.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smi.am.annotation.LoginAuth;
import com.smi.am.annotation.SystemControllerLog;
import com.smi.am.constant.SmiConstants;
import com.smi.am.dao.model.AmCounponsDetails;
import com.smi.am.dao.model.AmCoupons;
import com.smi.am.dao.model.AmGiftPackage;
import com.smi.am.service.ICouponsService;
import com.smi.am.service.IGiftPackageService;
import com.smi.am.service.vo.CouponsPackageVo;
import com.smi.am.service.vo.CouponsSomeVo;
import com.smi.am.service.vo.CouponsVo;
import com.smi.am.service.vo.CustPackVo;
import com.smi.am.service.vo.GiftPackageInfo;
import com.smi.am.service.vo.GiftPackageReqVo;
import com.smi.am.service.vo.GiftPackageResVo;
import com.smi.am.service.vo.GiftPackageVo;
import com.smi.am.service.vo.UserVo;
import com.smi.am.utils.DateUtils;
import com.smi.am.utils.SessionManager;
import com.smi.am.utils.SmiResult;
import com.smi.am.utils.ValidatorUtil;
import com.smi.tools.kits.JsonKit;
import com.smilife.core.common.controller.BaseController;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * 礼包管理控制器
 * 
 * @author smi
 *
 */
@RestController
@RequestMapping(value = SmiConstants.ADMIN_PATH + "/gift")
public class GiftPackageController extends BaseController {

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	@Autowired
	private IGiftPackageService iGiftPackageService;
	@Autowired
	private ICouponsService iCouponsService;

	/**
	 * 礼包列表查询
	 */
	@ApiOperation(value = "礼包列表查询")
	@RequestMapping(value = "/qryList", method = RequestMethod.POST)
	@SystemControllerLog(logTitle = "礼包列表查询")
	@LoginAuth
	public SmiResult<List<GiftPackageResVo>> giftQryList(
			@ApiParam(name = "gpGiftPackageId", value = "礼包ID", required = false) @RequestParam(value = "gpGiftPackageId", required = false) String gpGiftPackageId,
			@ApiParam(name = "gpGiftPackageName", value = "礼包名称", required = false) @RequestParam(value = "gpGiftPackageName", required = false) String gpGiftPackageName,
			@ApiParam(name = "putChannel", value = "投放渠道", required = false) @RequestParam(value = "putChannel", required = false) String putChannel,
			@ApiParam(name = "activityArea", value = "活动区域", required = false) @RequestParam(value = "activityArea", required = false) String activityArea,
			@ApiParam(name = "status", value = "当前状态", required = false) @RequestParam(value = "status", required = false) String status,
			@ApiParam(name = "pageNum", value = "第几页", required = true) @RequestParam(value = "pageNum", required = true) Integer pageNum,
			@ApiParam(name = "pageSize", value = "一页记录数", required = true) @RequestParam(value = "pageSize", required = true) Integer pageSize,
			HttpServletRequest request) {
		GiftPackageVo giftPackageVo = new GiftPackageVo();
		giftPackageVo.setActivityArea(activityArea);
		giftPackageVo.setGiftPackageName(gpGiftPackageName);
		if (!ValidatorUtil.isEmpty(gpGiftPackageId)) {
			giftPackageVo.setPgId(Integer.valueOf(gpGiftPackageId));
		}
		giftPackageVo.setChannel(putChannel);
		giftPackageVo.setStatus(status);
		giftPackageVo.setPageNum(pageNum);
		giftPackageVo.setPageSize(pageSize);
		this.LOGGER.info("礼包列表查询入参数据为：" + giftPackageVo.toString());
		SmiResult<List<GiftPackageResVo>> smiResult = this.iGiftPackageService.selectGiftPackageList(giftPackageVo);
		return smiResult;
	}

	/**
	 * 礼包编辑
	 * 
	 * @param giftPackageVo
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "礼包编辑")
	@RequestMapping(value = "/editList", method = RequestMethod.POST)
	@SystemControllerLog(logTitle = "礼包编辑")
	@LoginAuth
	public BaseValueObject giftEdit(@RequestBody GiftPackageReqVo reqVo, HttpServletRequest requset) {
		this.LOGGER.info("礼包编辑入参数据为：" + reqVo.toString());
		BaseValueObject smiResult = this.iGiftPackageService.updateGiftPackage(reqVo, requset);
		return smiResult;

	}

	/**
	 * 查询属于礼包类型的优惠券并未绑定其他礼包的优惠券
	 * 
	 * @return
	 * 
	 */
	@ApiOperation(value = "查询属于礼包类型的优惠券并未绑定其他礼包的优惠券")
	@RequestMapping(value = "/qryGiftInCou", method = RequestMethod.GET)
	@SystemControllerLog(logTitle = "查询属于礼包类型的优惠券并未绑定其他礼包的优惠券")
	@LoginAuth
	public SmiResult<List<CouponsPackageVo>> qryGiftInCoupons(
			@ApiParam(name = "pageNum", value = "第几页", required = true) @RequestParam(value = "pageNum", required = true) Integer pageNum,
			@ApiParam(name = "pageSize", value = "一页记录数", required = true) @RequestParam(value = "pageSize", required = true) Integer pageSize,
			HttpServletRequest request) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pageNum", pageNum);
		paramMap.put("pageSize", pageSize);
		UserVo userVo = (UserVo) SessionManager.getUserInfo(request);
		paramMap.put("areaId", userVo.getAreaId());
		SmiResult<List<CouponsPackageVo>> smiResult = this.iCouponsService.selectIsGiftPackage(paramMap);
		return smiResult;

	}

	/**
	 * 查询属于礼包类型的优惠券
	 * 
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "根据礼包id查询优惠券")
	@RequestMapping(value = "/qryCouponsByGiftId", method = RequestMethod.GET)
	@SystemControllerLog(logTitle = "查询属于礼包类型的优惠券")
	@LoginAuth
	public SmiResult<List<CouponsSomeVo>> qryCouponsByGiftPackageId(
			@ApiParam(value = "礼包id", name = "giftPackageId") @RequestParam(value = "giftPackageId", required = true) String giftPackageId,
			HttpServletRequest request) {
		this.LOGGER.info("查询属于礼包类型的优惠券入参数据为：" + giftPackageId);
		SmiResult<List<CouponsSomeVo>> smiResult = new SmiResult<List<CouponsSomeVo>>();
		List<CouponsSomeVo> amCouponList = this.iCouponsService.findCouponsByGiftPackageId(giftPackageId);
		smiResult.setCode(CodeEnum.SUCCESS);
		smiResult.setData(amCouponList);
		smiResult.setMsg("查询成功");
		return smiResult;

	}

	/**
	 * 查看礼包
	 * 
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "查看礼包")
	@RequestMapping(value = "/checkGift", method = RequestMethod.GET)
	@SystemControllerLog(logTitle = "查看礼包")
	@LoginAuth
	public SmiResult<GiftPackageInfo> qryGiftByGiftPackageId(
			@ApiParam(value = "礼包id", name = "giftPackageId") @RequestParam(value = "giftPackageId", required = true) String giftPackageId,
			HttpServletRequest request) {
		this.LOGGER.info("查询属于礼包类型的优惠券入参数据为：" + giftPackageId);
		SmiResult<GiftPackageInfo> smiResult = this.iGiftPackageService.findGiftByPackageId(giftPackageId);
		return smiResult;

	}

	/**
	 * 新增礼包
	 * 
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "新增礼包")
	@RequestMapping(value = "/addGiftPackage", method = RequestMethod.POST)
	@SystemControllerLog(logTitle = "新增礼包")
	@LoginAuth
	public BaseValueObject addGiftPackage(
			@ApiParam(value = "礼包名称", name = "giftPackageName", required = true) @RequestParam(value = "giftPackageName", required = true) String giftPackageName,
			@ApiParam(value = "优惠券列表(例如[123123,123123])", name = "couponsList", required = true) @RequestParam(value = "couponsList", required = true) String couponsList,
			@ApiParam(value = "发放方式(账户发放1,用户领取2)", name = "gpDeliveringWay", required = true) @RequestParam(value = "gpDeliveringWay", required = true) @Valid @Pattern(regexp = "^[0-9]*$", message = "输入参数不正确") String gpDeliveringWay,
			@ApiParam(value = "投放渠道(星美生活1,满天星2)", name = "gpChannel", required = false) @RequestParam(value = "gpChannel", required = false) String gpChannel,
			@ApiParam(value = "预发数量", name = "preSendNum", required = true) @RequestParam(value = "preSendNum", required = true) String preSendNum,
			@ApiParam(value = "审核状态", name = "reviewStatus", required = true) @RequestParam(value = "reviewStatus", required = true) String reviewStatus,
			@ApiParam(value = "活动门店", name = "activityShop", required = true) @RequestParam(value = "activityShop", required = true) String activityShop,
			@ApiParam(value = "活动运营区id", name = "activityArea", required = true) @RequestParam(value = "activityArea", required = true) String activityArea,
			@ApiParam(value = "活动开始时间", name = "activityStartTime", required = true) @RequestParam(value = "activityStartTime", required = true) String activityStartTime,
			@ApiParam(value = "活动结束时间", name = "activityEndTime", required = true) @RequestParam(value = "activityEndTime", required = true) String activityEndTime,
			@ApiParam(value = "发放对象(例如[123123,123123])", name = "custNbrList") @RequestParam(value = "custNbrList", required = false) String custNbrList,
			HttpServletRequest requset) {
		BaseValueObject smiResult = new BaseValueObject();
		GiftPackageVo go = new GiftPackageVo();
		boolean flag = go.validatorParam(giftPackageName, gpDeliveringWay, activityShop, activityArea,
				couponsList, activityStartTime, activityEndTime, preSendNum, reviewStatus);
		if (flag == false) {
			smiResult.setCode(CodeEnum.REQUEST_ERROR);
			smiResult.setMsg("必填参数不能为空");
		} else {
			go.setGiftPackageName(giftPackageName);
			go.setGpDeliveringWay(Integer.valueOf(gpDeliveringWay));
			go.setActivityShop(activityShop);
			go.setActivityArea(activityArea);
			go.setChannel(gpChannel);
			go.setCustNbrList(custNbrList);
			go.setCouponsList(couponsList);
			go.setStartTime(activityStartTime);
			go.setEndTime(activityEndTime);
			go.setPreSendNum(preSendNum);
			go.setStatus(reviewStatus);
			this.LOGGER.info("新增礼包入参数据为：" + go.toString());
			this.iGiftPackageService.addGiftPackage(go, requset);
			smiResult.setCode(CodeEnum.SUCCESS);
			smiResult.setMsg("新增成功");
		}
		return smiResult;

	}

	/**
	 * 删除礼包
	 * 
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "删除礼包")
	@RequestMapping(value = "/removeGiftPackage", method = RequestMethod.POST)
	@SystemControllerLog(logTitle = "删除礼包")
	@LoginAuth
	public BaseValueObject removeGiftPackage(
			@ApiParam(name = "gpGiftPackageId", value = "礼包id", required = true) @RequestParam(value = "gpGiftPackageId", required = true) String gpGiftPackageId,
			HttpServletRequest requset) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("gpGiftPackageId", gpGiftPackageId);
		this.LOGGER.info("删除礼包入参数据为：" + paramMap.toString());
		BaseValueObject baseValueObject = this.iGiftPackageService.deleteLogicGiftPackage(paramMap, requset);
		return baseValueObject;

	}

	/**
	 * 礼包提交审核
	 * 
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "礼包提交审核")
	@RequestMapping(value = "/reviewGiftPackage", method = RequestMethod.POST)
	@SystemControllerLog(logTitle = "礼包提交审核")
	@LoginAuth
	public BaseValueObject giftReviewed(
			@ApiParam(name = "gpGiftPackageId", value = "礼包id", required = true) @RequestParam(value = "gpGiftPackageId", required = true) String gpGiftPackageId,
			HttpServletRequest requset) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("gpGiftPackageId", gpGiftPackageId);
		this.LOGGER.info("审核礼包入参数据为：" + paramMap.toString());
		BaseValueObject baseValueObject = this.iGiftPackageService.reviewGiftPackage(paramMap, requset);
		return baseValueObject;

	}

	/**
	 * 复制礼包
	 * 
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "复制礼包")
	@RequestMapping(value = "/copyGiftPackage", method = RequestMethod.POST)
	@SystemControllerLog(logTitle = "复制礼包")
	@LoginAuth
	public BaseValueObject copyGiftPackage(
			@ApiParam(name = "gpGiftPackageId", value = "礼包id", required = true) @RequestParam(value = "gpGiftPackageId", required = true) String gpGiftPackageId,
			HttpServletRequest requset) {
		this.LOGGER.info("复制礼包入参数据为：" + gpGiftPackageId);
		BaseValueObject baseValueObject = new BaseValueObject();
		this.iGiftPackageService.copyGiftPackage(gpGiftPackageId, requset);
		baseValueObject.setCode(CodeEnum.SUCCESS);
		baseValueObject.setMsg("复制礼包成功");
		return baseValueObject;

	}

	/**
	 * 发放礼包
	 * 
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "发放礼包")
	@RequestMapping(value = "/sendGiftPackage", method = RequestMethod.POST)
	@SystemControllerLog(logTitle = "发放礼包")
	@LoginAuth
	public BaseValueObject sendGiftPackage(
			@ApiParam(name = "gpGiftPackageId", value = "礼包id", required = true) @RequestParam(value = "gpGiftPackageId", required = true) String gpGiftPackageId,
			HttpServletRequest requset) {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("gpGiftPackageId", gpGiftPackageId);
		this.LOGGER.info("发放礼包入参数据为：" + gpGiftPackageId);
		BaseValueObject baseValueObject = this.iGiftPackageService.sendGiftPackage(paramMap, requset);
		return baseValueObject;

	}

}
