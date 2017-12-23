package com.iskyshop.module.weixin.manage.coupon.activity;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.CouponRecord;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponRecordService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.pay.tenpay.util.Sha1Util;

@Controller
public class WeixinUserCouponActivity {
	private static Logger logger = Logger.getLogger(WeixinUserCouponActivity.class);
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ICouponInfoService couponinfoService;
	@Autowired
	private ICouponRecordService couponRecordService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IActivityGoodsService activityGoodsService;

	/***
	 * 随机领取
	 * 
	 * @param request
	 * @param response
	 * @param shareUId
	 * @return
	 */
	@SecurityMapping(title = "获取随机优惠券", value = "/wap/buyer/receive_coupon.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/wap/buyer/receive_coupon.htm")
	public String coupon_receive(HttpServletRequest request, HttpServletResponse response, String shareUId,
			String shareTime) {
		long shareUserId = 0;

		if (shareUId == null || "".equals(shareUId)) {
			return "redirect:/wap/coupon_share.htm?sFlag=1&flag=1&sId=";
		}

		try {
			shareUserId = Long.parseLong(shareUId);
		} catch (Exception e) {
			return "redirect:/wap/coupon_share.htm?sFlag=2&flag=1&sId=" + shareUId;
		}

		User shareUser = this.userService.getObjById(shareUserId);
		if (shareUser == null) {
			return "redirect:/wap/coupon_share.htm?sFlag=3&flag=1&sId=" + shareUserId;
		}

		if (!CouponActivityComm.isCouponActivityTime()) {
			return "redirect:/wap/coupon_share.htm?sFlag=4&flag=1&sId=" + shareUserId;
		}

		long userId = SecurityUserHolder.getCurrentUser().getId();
		Map recordParams = new HashMap();
		recordParams.put("userId", userId);
		recordParams.put("shareUserId", shareUserId);
		recordParams.put("shareTime", shareTime);
		List<CouponRecord> couponRecordList = this.couponRecordService.query(
				"select obj from CouponRecord obj where obj.userId=:userId and obj.shareUserId=:shareUserId and obj.shareTime=:shareTime",
				recordParams, -1, -1);
		if (couponRecordList != null && couponRecordList.size() > 0) {
			// 已经领取过
			return "redirect:/wap/coupon_tip.htm?tFlag=1&shareShow=1";
		}

		Map couponParams = new HashMap();
		couponParams.put("current_time", CommUtil.formatDate(CouponActivityComm.getCurDate()));
		couponParams.put("coupon_type", 0);
		List<Coupon> couponList = this.couponService.query(
				"select obj from Coupon obj where obj.coupon_type=:coupon_type and obj.coupon_begin_time<=:current_time and obj.coupon_end_time>=:current_time",
				couponParams, -1, -1);
		List<Coupon> receiveCouponList = new ArrayList<Coupon>();
		for (Coupon coupons : couponList) {
			List<CouponInfo> couponInfoList = coupons.getCouponinfos();
			int couponInfoLen = 0;
			if (couponInfoList != null && couponInfoList.size() > 0) {
				couponInfoLen = couponInfoList.size();
			}
			if (coupons.getCoupon_count() == 0 || couponInfoLen < coupons.getCoupon_count()) {
				receiveCouponList.add(coupons);
			}
		}
		if (receiveCouponList != null && receiveCouponList.size() > 0) {
			int index = CouponActivityComm.getRandomNum(receiveCouponList.size());
			Coupon coupon = receiveCouponList.get(index);
			User user = this.userService.getObjById(userId);
			int length = 0;
			if (coupon.getCouponinfos() != null && coupon.getCouponinfos().size() > 0) {
				length = coupon.getCouponinfos().size();
			}
			if (length < coupon.getCoupon_count() || coupon.getCoupon_count() == 0) {
				CouponInfo info = new CouponInfo();
				info.setAddTime(new Date());
				info.setCoupon(coupon);
				info.setCoupon_sn(UUID.randomUUID().toString());
				info.setUser(user);
				this.couponinfoService.save(info);
				CouponRecord couponRecord = new CouponRecord();
				couponRecord.setUserId(userId);
				couponRecord.setShareUserId(shareUserId);
				couponRecord.setCouponCount(1);
				couponRecord.setShareTime(shareTime);
				this.couponRecordService.save(couponRecord);
				String coupon_order_amount = coupon.getCoupon_order_amount().toString();
				String coupon_amount = coupon.getCoupon_amount().toString();
				Accessory accessory = coupon.getCoupon_acc();
				String imgpath = accessory.getPath();
				String imgname = accessory.getName();
				return "redirect:/wap/coupon_tip.htm?tFlag=0&shareShow=2&couponOrderAmount=" + coupon_order_amount
						+ "&imgpath=" + imgpath + "&imgname=" + imgname + "&couponAmount=" + coupon_amount;
			} else {
				// 抽取到的优惠券已发放完毕
				return "redirect:/wap/coupon_share.htm?sFlag=0&flag=1&sId=" + shareUserId;
			}
		} else {
			// 优惠券已发放完毕
			return "redirect:/wap/coupon_tip.htm?tFlag=2&shareShow=3";
		}

	}

	/***
	 * 跳转信息页面
	 * 
	 * @param request
	 * @param response
	 * @param tFlag
	 * @param shareShow
	 * @param imgpath
	 * @param imgname
	 * @param couponOrderAmount
	 * @return
	 */
	@RequestMapping("/wap/coupon_tip.htm")
	public ModelAndView coupon_tip(HttpServletRequest request, HttpServletResponse response, String tFlag, String shareShow,
			String imgpath, String imgname, String couponOrderAmount, String couponAmount) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/coupon_share.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("tFlag", tFlag);
		mv.addObject("shareShow", shareShow);
		mv.addObject("imgpath", imgpath);
		mv.addObject("imgname", imgname);
		mv.addObject("couponOrderAmount", couponOrderAmount);
		mv.addObject("couponAmount", couponAmount);
		return mv;
	}

	/***
	 * 跳转
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/jump_coupon_share.htm")
	public String jump_coupon_share(HttpServletRequest request, HttpServletResponse response) {
		String curUrl = "/wap/coupon_share_" + SecurityUserHolder.getCurrentUser().getId() + "_"
				+ CouponActivityComm.getCurTime("yyyyMMddHHmmssSSS") + ".htm";
		request.getSession().setAttribute("jumpflag", "0");
		request.getSession().setAttribute("curUrl", CommUtil.getURL(request) + curUrl);
		return "redirect:" + curUrl;
	}

	/***
	 * 分享，领取
	 * 
	 * @param request
	 * @param response
	 * @param sId
	 * @param sFlag
	 * @param flag
	 * @return
	 */
	@RequestMapping("/wap/coupon_share.htm")
	public ModelAndView coupon_share(HttpServletRequest request, HttpServletResponse response, String sId, String sFlag,
			String flag, String shareTime) {
		HttpSession session = request.getSession();
		String jumpflag = (String) session.getAttribute("jumpflag");
		String curUrl = (String) session.getAttribute("curUrl");
		session.removeAttribute("jumpflag");
		if ("0".equals(jumpflag)) {
			ModelAndView mv = new JModelAndView("user/wap/usercenter/coupon_activity_success.html",
					configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
			Map<String, String> jsonMap = this.getWeixiConfig(request, response, curUrl);
			mv.addObject("sId", sId);
			mv.addObject("jsonMap", jsonMap);
			return mv;
		} else {
			ModelAndView mv = new JModelAndView("user/wap/usercenter/coupon_share.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			String shareFlag = "0";

			if ("1".equals(flag)) {
				shareFlag = sFlag;
			} else {
				if (sId == null || "".equals(sId)) {
					shareFlag = "1";
				}

				if ("0".equals(shareFlag)) {
					try {
						Long.parseLong(sId);
					} catch (Exception e) {
						shareFlag = "2";
					}
				}

				if ("0".equals(shareFlag)) {
					User shareUser = this.userService.getObjById(Long.parseLong(sId));
					if (shareUser == null) {
						shareFlag = "3";
					}
				}

				if ("0".equals(shareFlag)) {
					if (!CouponActivityComm.isCouponActivityTime()) {
						shareFlag = "4";
					}
				}

			}

			mv.addObject("sId", sId);
			mv.addObject("shareFlag", shareFlag);
			mv.addObject("shareShow", 0);
			mv.addObject("shareTime", shareTime);

			return mv;
		}
	}

	/***
	 * 微信分享配置参数
	 * 
	 * @param request
	 * @param response
	 */
	public Map<String, String> getWeixiConfig(HttpServletRequest request, HttpServletResponse response, String curUrl) {
		List<Payment> payments = new ArrayList<Payment>();
		Map params = new HashMap();
		params.put("mark", "wx_pay");
		payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params, -1, -1);
		Payment payment = payments.get(0);
		String appid = payment.getWx_appid();
		String wxSecret = payment.getWx_appSecret();

		String tokenUrl = CouponActivityComm.getPropertiesByKey("weixi.access_token.url");

		String tokenDataStr = "appid=" + appid + "&secret=" + wxSecret;
		String tokenJsonRet = CouponActivityComm.sendHttp(tokenDataStr, tokenUrl);
		JSONObject tokenJsonObject = JSONObject.parseObject(tokenJsonRet);
		String access_token = tokenJsonObject.getString("access_token");
		// String expires_in=tokenJsonObject.getString("expires_in");

		String ticketUrl = CouponActivityComm.getPropertiesByKey("weixi.jsapi_ticket.url");
		String ticketDataStr = "access_token=" + access_token;
		String ticketJsonRet = CouponActivityComm.sendHttp(ticketDataStr, ticketUrl);
		JSONObject ticketJsonObject = JSONObject.parseObject(ticketJsonRet);
		// ticketJsonObject.getString("errcode");
		// ticketJsonObject.getString("errmsg");
		String jsapi_ticket = ticketJsonObject.getString("ticket");
		// ticketJsonObject.getString("expires_in");

		String nonce_str = Sha1Util.getNonceStr();
		Date date = new Date();
		String timestamp = CommUtil.formatTime("yyyyMMddHHmmssSSS", date);
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("noncestr", nonce_str);
		treeMap.put("jsapi_ticket", jsapi_ticket);
		treeMap.put("timestamp", timestamp);
		treeMap.put("url", curUrl);

		StringBuffer stringBuffer = new StringBuffer();
		Set<Entry<String, String>> entrySet = treeMap.entrySet();
		Iterator<Entry<String, String>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = (Entry<String, String>) iterator.next();
			String mapKey = entry.getKey();
			String mapValue = entry.getValue();
			if (null != mapValue && !"".equals(mapValue)) {
				stringBuffer.append(mapKey + "=" + mapValue + "&");
			}
		}
		String itrStr = stringBuffer.toString();
		itrStr = itrStr.substring(0, itrStr.length() - 1);
		String signature = Sha1Util.getSha1(itrStr);

		Map<String, String> jsonMap = new HashMap<String, String>();
		jsonMap.put("appId", appid);
		jsonMap.put("timestamp", timestamp);
		jsonMap.put("nonceStr", nonce_str);
		jsonMap.put("signature", signature);

		return jsonMap;
	}

	/***
	 * 跳转值
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/wap/jump_value.htm")
	public void jumpValue(HttpServletRequest request, HttpServletResponse response, String curUrl) {
		request.getSession().setAttribute("jumpflag", "0");
		request.getSession().setAttribute("curUrl", curUrl);
		String shareTime = CouponActivityComm.getCurTime("yyyyMMddHHmmssSSS");
		request.getSession().setAttribute("curUrl", curUrl + shareTime);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.print(shareTime);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}

	/***
	 * 
	 * @param request
	 * @param response
	 * @param custId
	 * @param amount
	 *            有值 就 是 红包 请求方有 概率
	 * @return
	 */
	@RequestMapping("/wap/other_receive_coupon.htm")
	public void other_receive_coupon(HttpServletRequest request, HttpServletResponse response, String custId, String amount,
			String activityId) {
		String retJson = "";
		Map jsonMap = new HashMap();

		if (amount == null || "".equals(amount)) {
			jsonMap = this.other_receive_coupon_yy(request, response, custId, amount, activityId);
		} else {// 红包
			jsonMap = this.other_receive_coupon_hb(request, response, custId, amount, activityId);
		}

		retJson = JSON.toJSONString(jsonMap);

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.print(retJson);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}

	/***
	 * 红包
	 * 
	 * @param request
	 * @param response
	 * @param custId
	 * @param amount
	 * @param activityId
	 * @return
	 */
	public Map other_receive_coupon_hb(HttpServletRequest request, HttpServletResponse response, String custId,
			String amount, String activityId) {

		int retCode = 0;
		String msg = "";
		int retAmount = 0;

		String coupon_amount = "";

		Map allCouponParams = new HashMap();
		allCouponParams.put("current_time", CommUtil.formatDate(CouponActivityComm.getCurDate()));
		allCouponParams.put("actid", Long.parseLong(activityId));
		allCouponParams.put("caType", 1);
		allCouponParams.put("ac_status", 1);
		allCouponParams.put("ag_status", 1);

		List<Coupon> allCouponList = new ArrayList<Coupon>();

		List<ActivityGoods> allActivityGoodsList = this.activityGoodsService.query(
				"select obj from ActivityGoods obj where obj.act.id=:actid and obj.act.ac_status=:ac_status and obj.acType=:caType and obj.ag_status=:ag_status and obj.act.ac_begin_time<=:current_time and obj.act.ac_end_time>=:current_time and obj.coupon.coupon_begin_time<=:current_time and obj.coupon.coupon_end_time>=:current_time",
				allCouponParams, -1, -1);
		for (ActivityGoods allActivityGoods : allActivityGoodsList) {
			allCouponList.add(allActivityGoods.getCoupon());
		}

		List<Coupon> allReceiveCouponList = new ArrayList<Coupon>();
		for (Coupon allCoupons : allCouponList) {
			List<CouponInfo> allCouponInfoList = allCoupons.getCouponinfos();
			int allCouponInfoLen = 0;
			if (allCouponInfoList != null && allCouponInfoList.size() > 0) {
				allCouponInfoLen = allCouponInfoList.size();
			}
			if (allCoupons.getCoupon_count() == 0 || allCouponInfoLen < allCoupons.getCoupon_count()) {
				allReceiveCouponList.add(allCoupons);
			}
		}

		if (allReceiveCouponList != null && allReceiveCouponList.size() > 0) {
			Map couponParams = new HashMap();
			couponParams.put("current_time", CommUtil.formatDate(CouponActivityComm.getCurDate()));
			couponParams.put("actid", Long.parseLong(activityId));
			couponParams.put("coupon_amount", BigDecimal.valueOf(Long.parseLong(amount)));
			couponParams.put("caType", 1);
			couponParams.put("ac_status", 1);
			couponParams.put("ag_status", 1);
			List<Coupon> couponList = new ArrayList<Coupon>();

			List<ActivityGoods> activityGoodsList = this.activityGoodsService.query(
					"select obj from ActivityGoods obj where obj.act.id=:actid and obj.act.ac_status=:ac_status and obj.acType=:caType and obj.ag_status=:ag_status and obj.act.ac_begin_time<=:current_time and obj.act.ac_end_time>=:current_time and obj.coupon.coupon_amount=:coupon_amount and obj.coupon.coupon_begin_time<=:current_time and obj.coupon.coupon_end_time>=:current_time",
					couponParams, -1, -1);
			for (ActivityGoods activityGoods : activityGoodsList) {
				couponList.add(activityGoods.getCoupon());
			}

			List<Coupon> receiveCouponList = new ArrayList<Coupon>();
			for (Coupon coupons : couponList) {
				List<CouponInfo> couponInfoList = coupons.getCouponinfos();
				int couponInfoLen = 0;
				if (couponInfoList != null && couponInfoList.size() > 0) {
					couponInfoLen = couponInfoList.size();
				}
				if (coupons.getCoupon_count() == 0 || couponInfoLen < coupons.getCoupon_count()) {
					receiveCouponList.add(coupons);
				}
			}
			if (receiveCouponList != null && receiveCouponList.size() > 0) {
				int index = CouponActivityComm.getRandomNum(receiveCouponList.size());
				Coupon coupon = receiveCouponList.get(index);

				Map userParams = new HashMap();
				userParams.put("custId", custId);
				List<User> userList = this.userService.query("select obj from User obj where obj.custId=:custId", userParams,
						-1, -1);
				if (userList != null && userList.size() > 0) {
					User user = userList.get(0);

					int length = 0;
					if (coupon.getCouponinfos() != null && coupon.getCouponinfos().size() > 0) {
						length = coupon.getCouponinfos().size();
					}
					if (length < coupon.getCoupon_count() || coupon.getCoupon_count() == 0) {
						CouponInfo info = new CouponInfo();
						info.setAddTime(new Date());
						info.setCoupon(coupon);
						info.setCoupon_sn(UUID.randomUUID().toString());
						info.setUser(user);
						this.couponinfoService.save(info);
						coupon_amount = coupon.getCoupon_amount().toString();
						retCode = 0;
						msg = "ok";
						retAmount = Integer.parseInt(coupon_amount);
					} else {
						// 抽取到的优惠券已发放完毕
						retCode = 3;
						msg = "领取的人 过多，请重新领取";
					}
				} else {
					retCode = 4;
					msg = "没有同步用户信息";
				}

			} else {
				// 优惠券已发放完毕
				retCode = 1;
				msg = "此金额发放完了";
			}
		} else {
			retCode = 2;
			msg = "全部发放完了";
		}

		Map jsonMap = new HashMap();
		jsonMap.put("retCode", retCode);
		jsonMap.put("msg", msg);
		jsonMap.put("amount", retAmount);

		return jsonMap;
	}

	/***
	 * 大转盘
	 * 
	 * @param request
	 * @param response
	 * @param custId
	 * @param amount
	 * @param activityId
	 * @return
	 */
	public Map other_receive_coupon_yy(HttpServletRequest request, HttpServletResponse response, String custId,
			String amount, String activityId) {

		int retCode = 0;
		String msg = "";
		int retAmount = 0;

		String coupon_amount = "";

		Map couponParams = new HashMap();
		couponParams.put("current_time", CommUtil.formatDate(CouponActivityComm.getCurDate()));
		couponParams.put("actid", Long.parseLong(activityId));
		couponParams.put("caType", 1);
		couponParams.put("ac_status", 1);
		couponParams.put("ag_status", 1);
		List<Coupon> couponList = new ArrayList<Coupon>();

		List<ActivityGoods> activityGoodsList = this.activityGoodsService.query(
				"select obj from ActivityGoods obj where obj.act.id=:actid and obj.act.ac_status=:ac_status and obj.acType=:caType and obj.ag_status=:ag_status and obj.act.ac_begin_time<=:current_time and obj.act.ac_end_time>=:current_time and obj.coupon.coupon_begin_time<=:current_time and obj.coupon.coupon_end_time>=:current_time",
				couponParams, -1, -1);
		for (ActivityGoods activityGoods : activityGoodsList) {
			couponList.add(activityGoods.getCoupon());
		}

		List<Coupon> receiveCouponList = new ArrayList<Coupon>();
		for (Coupon coupons : couponList) {
			List<CouponInfo> couponInfoList = coupons.getCouponinfos();
			int couponInfoLen = 0;
			if (couponInfoList != null && couponInfoList.size() > 0) {
				couponInfoLen = couponInfoList.size();
			}
			if (coupons.getCoupon_count() == 0 || couponInfoLen < coupons.getCoupon_count()) {
				receiveCouponList.add(coupons);
			}
		}
		if (receiveCouponList != null && receiveCouponList.size() > 0) {
			int index = CouponActivityComm.getRandomNum(receiveCouponList.size());
			Coupon coupon = receiveCouponList.get(index);

			Map userParams = new HashMap();
			userParams.put("custId", custId);
			List<User> userList = this.userService.query("select obj from User obj where obj.custId=:custId", userParams, -1,
					-1);
			if (userList != null && userList.size() > 0) {
				User user = userList.get(0);

				int length = 0;
				if (coupon.getCouponinfos() != null && coupon.getCouponinfos().size() > 0) {
					length = coupon.getCouponinfos().size();
				}
				if (length < coupon.getCoupon_count() || coupon.getCoupon_count() == 0) {
					CouponInfo info = new CouponInfo();
					info.setAddTime(new Date());
					info.setCoupon(coupon);
					info.setCoupon_sn(UUID.randomUUID().toString());
					info.setUser(user);
					this.couponinfoService.save(info);
					coupon_amount = coupon.getCoupon_amount().toString();
					retCode = 0;
					msg = "ok";
					retAmount = Integer.parseInt(coupon_amount);
				} else {
					// 抽取到的优惠券已发放完毕
					retCode = 3;
					msg = "领取的人 过多，请重新领取";
				}
			} else {
				retCode = 4;
				msg = "没有同步用户信息";
			}

		} else {
			// 优惠券已发放完毕
			retCode = 2;
			msg = "全部发放完了";
		}

		Map jsonMap = new HashMap();
		jsonMap.put("retCode", retCode);
		jsonMap.put("msg", msg);
		jsonMap.put("amount", retAmount);

		return jsonMap;
	}

}
