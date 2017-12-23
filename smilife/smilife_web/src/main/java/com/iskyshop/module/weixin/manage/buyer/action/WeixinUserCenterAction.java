package com.iskyshop.module.weixin.manage.buyer.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.pay.tools.PayTools;
import com.iskyshop.smilife.charge.IChargeService;
import com.iskyshop.smilife.enums.ChannelEnum;
import com.iskyshop.smilife.enums.OrderTypeEnum;
import com.iskyshop.smilife.payCenter.IpayCenterService;
import com.iskyshop.view.web.tools.IntegralViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;
import com.smilife.bcp.dto.common.EInterfaceState;
import com.smilife.bcp.dto.common.EPwdResetType;
import com.smilife.bcp.dto.common.EPwdType;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.UserManageConnector;
import com.tydic.framework.util.PropertyUtil;

/**
 * 
 * 
 * <p>
 * Title:MobileUserCenterAction.java
 * </p>
 * 
 * <p>
 * Description: 移动端用户中心
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author jy
 * 
 * @version iskyshop_b2b2c_2015
 */
@Controller
public class WeixinUserCenterAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderformService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IMessageService messageService;

	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private StoreViewTools storeViewTools;

	@Autowired
	private UserManageConnector manageConnector;
	@Autowired
	private PaymentTools paymentTools;

	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private PayTools payTools;
	@Autowired
	private IpayCenterService payCenterService; 
	@Autowired
	private IChargeService chargeService; 
	@Autowired
	private UserTools userTools;

	
	@RequestMapping("/wap/authority.htm")
	public ModelAndView authorityError(HttpServletRequest request, HttpServletResponse response) {
		return new JModelAndView("/wap/authority.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
	}
	
	/**
	 * 手机客户端商城首页
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@SecurityMapping(title = "用户中心", value = "/wap/buyer/center.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/center.htm")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/user_index.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		Map params = new HashMap();
		params.put("user_id", user.getId());
		params.put("status", 0);
		// 优惠券
		mv.addObject(
				"couponInfos",
				this.couponInfoService.query(
						"select obj.id from CouponInfo obj where obj.user.id=:user_id and obj.status = :status", params, -1,
						-1).size());
		params.clear();
		params.put("status", 10);
		params.put("user_id", user.getId().toString());
		mv.addObject(
				"orders_10",
				this.orderformService
						.query("select obj.id from OrderForm obj where obj.order_cat!=2 and obj.user_id= :user_id and obj.order_status = :status",
								params, -1, -1).size());
		mv.addObject("integralViewTools", integralViewTools);
		params.clear();
		params.put("type", 0);
		params.put("user_id", user.getId());
		// 收藏商品
		List<Favorite> favorite_goods = this.favoriteService.query(
				"select obj from Favorite obj where obj.type=:type and obj.user_id=:user_id", params, 0, 6);
		mv.addObject("favorite_goods", favorite_goods);
		params.clear();
		params.put("type", 1);
		params.put("user_id", user.getId());
		// 收藏店铺
		mv.addObject("favorite_store", this.favoriteService.query(
				"select obj from Favorite obj where obj.type=:type and obj.user_id=:user_id", params, 0, 6));
		params.clear();
		params.put("status_1", 10);
		params.put("user_id", user.getId().toString());
		params.put("order_main", 1);
		// 订单未支付
		List<OrderForm> order_nopays = this.orderformService.query(
				"select count(obj.id) from OrderForm obj where obj.order_status=:status_1 and obj.user_id=:user_id"
						+ " and obj.order_main=:order_main", params, -1, -1);
		String order_nopay = "";
		if (CommUtil.null2Int(order_nopays.get(0)) > 9) {
			order_nopay = "9+";
		} else {
			order_nopay = order_nopays.get(0) + "";
		}
		mv.addObject("order_nopay", order_nopay);
		params.clear();
		params.put("status_1", 20);
		params.put("user_id", user.getId().toString());
		params.put("order_main", 1);
		// 订单未发货
		List<OrderForm> order_noships = this.orderformService.query(
				"select count(obj.id) from OrderForm obj where obj.order_status=:status_1 and obj.user_id=:user_id"
						+ " and obj.order_main=:order_main", params, -1, -1);
		String order_noship = "";
		if (CommUtil.null2Int(order_noships.get(0)) > 9) {
			order_noship = "9+";
		} else {
			order_noship = order_noships.get(0) + "";
		}
		mv.addObject("order_noship", order_noship);
		params.clear();
		params.put("status_1", 30);
		params.put("status_2", 35);
		params.put("user_id", user.getId().toString());
		params.put("order_main", 1);
		// 订单未收货
		List<OrderForm> order_notakes = this.orderformService
				.query("select count(obj.id) from OrderForm obj where (obj.order_status=:status_1 or obj.order_status=:status_2) and obj.user_id=:user_id"
						+ " and obj.order_main=:order_main", params, -1, -1);
		String order_notake = "";
		if (CommUtil.null2Int(order_notakes.get(0)) > 9) {
			order_notake = "9+";
		} else {
			order_notake = order_notakes.get(0) + "";
		}
		mv.addObject("order_notake", order_notake);
		List<Goods> your_like_goods = new ArrayList<Goods>();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("goodscookie".equalsIgnoreCase(cookie.getName())) {
					String[] like_gcid = cookie.getValue().split(",");
					Set<Long> gc_ids = new HashSet<Long>();
					for (String gcid : like_gcid) {
						Goods goods = this.goodsService.getObjById(CommUtil.null2Long(gcid));
						if (goods != null) {
							gc_ids.add(goods.getGc().getParent().getId());
						}
					}
					if (gc_ids.size() > 0) {
						Map map = new HashMap();
						map.put("ids", gc_ids);
						your_like_goods = this.goodsService
								.query("select obj from Goods obj where obj.goods_status=0 and obj.gc.parent.id in (:ids) order by obj.goods_salenum desc",
										map, 0, 9);
					} else {
						your_like_goods = this.goodsService.query(
								"select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc", null,
								0, 9);
					}
				} else {
					your_like_goods = this.goodsService
							.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
									null, 0, 9);
				}
			}
		} else {
			your_like_goods = this.goodsService.query(
					"select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc", null, 0, 9);
		}
		// 猜你喜欢
		mv.addObject("your_like_goods", your_like_goods);
		// 查询未读站内信数量
		List<Message> msgs = new ArrayList<Message>();
		params.clear();
		params.put("status", 0);
		params.put("user_id", user.getId());
		msgs = this.messageService
				.query("select count(obj.id) from Message obj where obj.status=:status and obj.toUser.id=:user_id and obj.parent.id is null order by obj.addTime desc",
						params, -1, -1);
		mv.addObject("msg_size", msgs.get(0));
		mv.addObject("user", user);
		mv.addObject("integralViewTools", integralViewTools);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param password
	 * @param new_password
	 * @param code
	 * @return
	 */

	@SecurityMapping(title = "用户中心修改密码", value = "/wap/buyer/edit_password.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/edit_password.htm")
	public ModelAndView edit_password(HttpServletRequest request, HttpServletResponse response, String password,
			String new_password, String code) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/edit_password.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	@SecurityMapping(title = "用户中心完善资料", value = "/wap/buyer/datum.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/datum.htm")
	public ModelAndView datum(HttpServletRequest request, HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/datum.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ("bind".equals(type)) {
			mv = new JModelAndView("user/wap/usercenter/datum_bind.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
		}
		mv.addObject("type", type);
		return mv;
	}

	

	/**
	 * 完善资料2
	 * 
	 * @param request
	 * @param userName
	 * @param password
	 * @param email
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	@RequestMapping("/wap/buyer/datum2.htm")
	public String datum2(HttpServletRequest request, HttpServletResponse response, String userName, String password)
			throws HttpException, IOException {
		boolean reg = true;
		// 进一步控制用户名不能重复
		Map params = new HashMap();
		params.put("userName", userName);
		List<User> users = this.userService.query("select obj from User obj where obj.userName=:userName", params, -1, -1);
		if (users != null && users.size() > 0) {
			reg = false;
		}
		if (reg) {
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			user.setUserName(userName);
			user.setMobile(userName);
			user.setPassword(password);
			user.setUserMark(null);
			this.userService.save(user);
		}
		return "redirect:/wap/buyer/center.htm";
	}

	/**
	 * 用户中心修改密码保存
	 * 
	 * @param request
	 * @param response
	 * @param password
	 * @param new_password
	 * @param code
	 * @return
	 */
	@SecurityMapping(title = "用户中心修改密码保存", value = "/wap/buyer/edit_password_save.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/edit_password_save.htm")
	public ModelAndView edit_password_save(HttpServletRequest request, HttpServletResponse response, String password,
			String new_password) {
		ModelAndView mv = new JModelAndView("wap/error.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		if (user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) {
			// TODO 会员密码修改
			ResultDTO resultDTO = manageConnector.updatePwd(user.getCustId(), Md5Encrypt.md5(password).toLowerCase(),
					Md5Encrypt.md5(new_password).toLowerCase(), EPwdResetType.MODIFY.getState(),
					EPwdType.LOGIN_PWD.getState());
			if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
				// 商城数据库密码定死为123qwe了，所以需要去掉修改数据库的处理
				/*user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
				this.userService.update(user);*/
				mv = new JModelAndView("wap/success.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "修改密码成功！");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/center.htm");
			} else {
				mv.addObject("op_title", resultDTO.getMsg());
				mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/edit_password.htm");
			}
		} else {
			mv.addObject("op_title", "原始密码错误！");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/edit_password.htm");
		}
		return mv;
	}

	/**
	 * 手机端用户优惠券
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "用户优惠券", value = "/wap/buyer/coupon.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/coupon.htm")
	public ModelAndView buyer_coupon(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/coupon.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		if (user != null) {
			Map params = new HashMap();
			params.put("user_id", user.getId());
			List<CouponInfo> couponinfos = this.couponInfoService.query(
					"select obj from CouponInfo obj where obj.user.id=:user_id ORDER BY obj.addTime DESC ", params, -1, -1);
			mv.addObject("couponinfos", couponinfos);
		}
		return mv;
	}

	/**
	 * 手机端用户收藏的商品
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "移动端商品收藏", value = "/wap/buyer/favorite.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/favorite.htm")
	public ModelAndView favorite(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/favorite.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("type", 0);
		List<Favorite> favorites = this.favoriteService.query(
				"select obj from Favorite obj where obj.user_id=:user_id and obj.type=:type order by obj.addTime desc",
				params, -1, -1);
		mv.addObject("objs", favorites);
		return mv;
	}

	/**
	 * 手机端用户收藏的商品
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "移动端店铺收藏", value = "/wap/buyer/favorite_store.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/favorite_store.htm")
	public ModelAndView favorite_store(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/favorite_store.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("type", 1);
		List<Favorite> favorites = this.favoriteService.query(
				"select obj from Favorite obj where obj.user_id=:user_id and obj.type=:type order by obj.addTime desc",
				params, -1, -1);
		mv.addObject("objs", favorites);
		mv.addObject("storeViewTools", storeViewTools);
		return mv;
	}

	/**
	 * 手机端用户收藏的商品删除
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "移动端商品取消收藏", value = "/wap/buyer/favorite_del.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/favorite_del.htm")
	public String favorite_del(HttpServletRequest request, HttpServletResponse response, String id) {
		Favorite favorite = this.favoriteService.getObjById(CommUtil.null2Long(id));
		if (favorite != null && favorite.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId())) {
			this.favoriteService.delete(CommUtil.null2Long(id));
		}
		return "redirect:/wap/buyer/favorite.htm";
	}

	/**
	 * 手机端用户收藏的商品删除
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "移动端商品取消收藏", value = "/wap/buyer/favorite_store_del.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/favorite_store_del.htm")
	public String favorite_store_del(HttpServletRequest request, HttpServletResponse response, String id) {
		Favorite favorite = this.favoriteService.getObjById(CommUtil.null2Long(id));
		if (favorite != null && favorite.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId())) {
			this.favoriteService.delete(CommUtil.null2Long(id));
		}
		return "redirect:/wap/buyer/favorite_store.htm";
	}

	/**
	 * 手机端用户的消息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "用户消息", value = "/wap/buyer/message_list.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/message_list.htm")
	public ModelAndView message_list(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/message_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		long user_id = SecurityUserHolder.getCurrentUser().getId();
		Map map = new HashMap();
		map.put("uid", user_id);
		List<Message> messages = this.messageService.query(
				"select obj from Message obj where obj.toUser.id=:uid order by obj.addTime desc", map, -1, -1);
		mv.addObject("objs", messages);
		return mv;
	}

	/**
	 * 手机端用户的消息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "用户服务中心", value = "/wap/buyer/service_center.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/service_center.htm")
	public ModelAndView service_center(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/service_center.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param password
	 * @param new_password
	 * @param code
	 * @return
	 */

	@SecurityMapping(title = "账户充值", value = "/wap/buyer/predeposit.htm*", rtype = "buyer", rname = "移动端账户充值", rcode = "wap_user_center", rgroup = "移动端账户充值")
	@RequestMapping("/wap/buyer/predeposit.htm")
	public ModelAndView predeposit(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/buyer_predeposit.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("paymentTools", this.paymentTools);
		return mv;
	}
	
	@SecurityMapping(title = "账户充值", value = "/wap/buyer/predeposit2.htm*", rtype = "buyer", rname = "移动端账户充值", rcode = "wap_user_center", rgroup = "移动端账户充值")
	@RequestMapping("/wap/buyer/predeposit2.htm")
	public ModelAndView predeposit2(HttpServletRequest request, HttpServletResponse response, String mobile,
			String pd_amount){
		ModelAndView mv = new JModelAndView("user/wap/usercenter/buyer_predeposit2.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if(StringUtils.isNullOrEmpty(mobile) || StringUtils.isNullOrEmpty(pd_amount)){
			mv = new JModelAndView("/wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}else{
			String mark = PropertyUtil.getProperty("wap_pay_center_mark");
			mv.addObject("mark", mark);
			mv.addObject("mobile", mobile);
			mv.addObject("pd_amount", pd_amount);
			mv.addObject("paymentTools", this.paymentTools);
		}
		return mv;
	}

	@SecurityMapping(title = "会员充值保存", value = "/wap/buyer/predeposit_save.htm*", rtype = "buyer", rname = "预存款管理", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping("/wap/buyer/predeposit_save.htm")
	public ModelAndView predeposit_save(HttpServletRequest request, HttpServletResponse response, String id,
			String pd_payment,String mobile) {
		ModelAndView mv = new JModelAndView("wap/line_pay.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			WebForm wf = new WebForm();
			Predeposit obj = null;
			if (StringUtils.isNullOrEmpty(id)) {
				obj = wf.toPo(request, Predeposit.class);
				obj.setAddTime(new Date());
				if ("outline".equals(pd_payment)) {
					obj.setPd_pay_status(1);
				} else {
					obj.setPd_pay_status(0);
				}
				if(StringUtils.isNullOrEmpty(mobile)){//未传账号绑定的手机号时则取当前用户
					obj.setPd_user(SecurityUserHolder.getCurrentUser());
					obj.setPd_sn("pd" + CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ SecurityUserHolder.getCurrentUser().getId());
				}else{
					User userByName = userService.getBuyerOrMainSellerByMobile(mobile);
					if(userByName == null) {
						mv = new JModelAndView("/wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "被充值账号不存在：其绑定的手机号为：" + mobile);
						mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/predeposit.htm");
					}
					obj.setPd_user(userByName);
					obj.setPd_sn("pd" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + userByName.getId());
				}
				obj.setPayUser(SecurityUserHolder.getCurrentUser());// 充值用户为当前登录者
				this.predepositService.save(obj);
			} else {
				Predeposit pre = this.predepositService.getObjById(CommUtil.null2Long(id));
				obj = (Predeposit) wf.toPo(request, pre);
				this.predepositService.update(obj);
			}
			if("wx_pay".equals(pd_payment)){
				List<Payment> payments = new ArrayList<Payment>();
				Map params = new HashMap();
				params.put("mark", "wx_pay");
				payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params, -1, -1);
				String appid = null;
				Payment payment = null;
				if (payments.size() > 0) {
					payment = payments.get(0);
					appid = payment.getWx_appid();
				}
				try {
					response.sendRedirect("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+CommUtil.getURL(request)+"/catchopenid.htm&response_type=code&scope=snsapi_userinfo&state=order_"+obj.getId()+"_cash#wechat_redirect");
					//snsapi_base静默授权并自动跳转到回调页的
					//response.sendRedirect("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+CommUtil.getURL(request)+"/catchopenid.htm&response_type=code&scope=snsapi_base&state=order_"+obj.getId()+"_cash#wechat_redirect");
					//response.sendRedirect(CommUtil.getURL(request) + "/weixin/pay/wx_pay.htm?openid=o0yums2HOLtsmmZ4Aw__te67v3DY&id="+ obj.getId() + "&type=cash");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if ("outline".equals(pd_payment)) {
				mv = new JModelAndView("/wap/success.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "线下支付提交成功，等待审核");
				mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/center.htm");
			} else {
				mv.addObject("payType", pd_payment);
				mv.addObject("type", "cash");
				mv.addObject("url", CommUtil.getURL(request));
				mv.addObject("payTools", payTools);
				mv.addObject("cash_id", obj.getId());
				Map params = new HashMap();
				params.put("install", true);
				params.put("mark", obj.getPd_payment());
				List<Payment> payments = this.paymentService.query(
						"select obj from Payment obj where obj.install=:install and obj.mark=:mark", params, -1, -1);
				mv.addObject("payment_id", payments.size() > 0 ? payments.get(0).getId() : new Payment());
			}
			
			String mark = PropertyUtil.getProperty("wap_pay_center_mark");
			if("1".equals(mark)){
				String returnUrl = CommUtil.getURL(request) + PropertyUtil.getProperty("wap_return_url_predeposit"); //回调业务系统地址
				return (ModelAndView)this.predepositService.predepositPay(request, response, mobile, CommUtil.null2String(obj.getPd_amount()), ChannelEnum.WAP.name(), OrderTypeEnum.CHARGE.getIndex(), returnUrl);
			}
		} else {
			mv = new JModelAndView("/wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/buyer/center.htm");
		}
		return mv;
	}
}
