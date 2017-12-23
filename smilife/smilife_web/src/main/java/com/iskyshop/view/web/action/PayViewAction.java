package com.iskyshop.view.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.iskyshop.core.exception.PayValidationException;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.AmountUtils;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.GoldRecord;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.IntegralGoods;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.events.publisher.SynchronizeOrderPublisher;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.IGoldRecordService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IIntegralGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.module.weixin.manage.coupon.activity.CouponActivityComm;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.msg.SpelTemplate;
import com.iskyshop.pay.alipay.config.AlipayConfig;
import com.iskyshop.pay.alipay.util.AlipayNotify;
import com.iskyshop.pay.bill.util.MD5Util;
import com.iskyshop.pay.chinapay.ChinaPayCommon;
import com.iskyshop.pay.tenpay.RequestHandler;
import com.iskyshop.pay.tenpay.ResponseHandler;
import com.iskyshop.pay.tenpay.util.TenpayUtil;
import com.iskyshop.pay.unionpay.UnionPayService;
import com.iskyshop.view.web.tools.BuyGiftViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.smilife.bcp.dto.common.EInterfaceState;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.FeeManageConnector;

/**
 * 
 * <p>
 * Title: PayViewAction.java
 * </p>
 * 
 * <p>
 * Description:在线支付回调控制器,处理系统支持的所有支付方式回调业务处理，包括支付宝、财付通、快钱、paypal、网银在线
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
 * @author erikzhang
 * 
 * @date 2014-5-25
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class PayViewAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGoldRecordService goldRecordService;
	@Autowired
	private IGoldLogService goldLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IIntegralGoodsService integralGoodsService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IPayoffLogService payoffservice;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private BuyGiftViewTools buyGiftViewTools;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private GoodsViewTools goodsViewTools;

	@Autowired
	private FeeManageConnector feeManageConnector;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private SynchronizeOrderPublisher synchronizeOrderPublisher;

	/**
	 * 支付宝在线支付成功回调控制
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/aplipay_return.htm")
	public synchronized ModelAndView aplipay_return(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mv = new JModelAndView("order_finish.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String trade_no = request.getParameter("trade_no"); // 支付宝交易号
		String[] order_nos = request.getParameter("out_trade_no").split("-"); // 获取订单号
		String total_fee = request.getParameter("total_fee"); // 获取总金额
		logger.debug(total_fee);
		logger.debug(request.getParameter("price"));
		String subject = request.getParameter("subject"); // new
		// String(request.getParameter("subject").getBytes("ISO-8859-1"),
		// "UTF-8"); //
		// 商品名称、订单名称
		String order_no = order_nos[2];
		String type = CommUtil.null2String(request.getParameter("body")).trim();
		String trade_status = request.getParameter("trade_status"); // 交易状态
		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(type)) {
			main_order = this.orderFormService.getObjById(CommUtil.null2Long(order_no));
		}
		if ("cash".equals(type)) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(order_no));
		}
		if ("gold".equals(type)) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(order_no));
		}
		if ("integral".equals(type)) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(order_no));
		}
		if ("group".equals(type)) {
			main_order = this.orderFormService.getObjById(CommUtil.null2Long(order_no));
		}
		// 获取支付宝GET过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 如果没有配置Tomcat的get编码为UTF-8，需要下面一行代码转码，否则会出现乱码，导致回调失败
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		AlipayConfig config = new AlipayConfig();
		if ("goods".equals(type) || "group".equals(type)) {
			config.setKey(main_order.getPayment().getSafeKey());
			config.setPartner(main_order.getPayment().getPartner());
			config.setPrivate_key(main_order.getPayment().getApp_private_key());
			config.setAli_public_key(main_order.getPayment().getApp_public_key());
			config.setSign_type(main_order.getPayment().getAlipaySignType());
			config.setSeller_email(main_order.getPayment().getSeller_email());
		}
		if ("cash".equals(type) || "gold".equals(type) || "integral".equals(type)) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if ("cash".equals(type)) {
				q_params.put("mark", obj.getPd_payment());
			}
			if ("gold".equals(type)) {
				q_params.put("mark", gold.getGold_payment());
			}
			if ("integral".equals(type)) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark", q_params, -1, -1);
			config.setKey(payments.get(0).getSafeKey());
			config.setPartner(payments.get(0).getPartner());
			config.setPrivate_key(payments.get(0).getApp_private_key());
			config.setAli_public_key(payments.get(0).getApp_public_key());
			config.setSign_type(payments.get(0).getAlipaySignType());
			config.setSeller_email(payments.get(0).getSeller_email());
		}
		config.setNotify_url(CommUtil.getURL(request) + "/alipay_notify.htm");
		config.setReturn_url(CommUtil.getURL(request) + "/aplipay_return.htm");
		boolean verify_result = AlipayNotify.verify(config, params);
		if (verify_result) { // 验证成功
			if ("goods".equals(type)) {
				if ("WAIT_SELLER_SEND_GOODS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)
						|| "TRADE_SUCCESS".equals(trade_status)) {
					User buyer = this.userService.getObjById(CommUtil.null2Long(main_order.getUser_id()));
					if (main_order.getOrder_status() < 20) { // 异步没有出来订单，则同步处理订单
						//更新主订单状态
						main_order.setOrder_status(20);
						main_order.setOut_order_id(trade_no);
						main_order.setPayTime(new Date());
						this.orderFormService.update(main_order);
                        // 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
                        this.update_goods_inventory(main_order);
						// 记录主订单日志
						OrderFormLog main_ofl = new OrderFormLog();
						main_ofl.setAddTime(new Date());
						main_ofl.setLog_info("支付宝在线支付");
						main_ofl.setLog_user(buyer);
						main_ofl.setOf(main_order);
						this.orderFormLogService.save(main_ofl);

						// 子订单操作
						if (main_order.getOrder_main() == 1
								&& !"".equals(CommUtil.null2String(main_order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools.queryGoodsInfo(main_order.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil.null2Long(child_map.get("order_id")));
								if (child_order.getOrder_status() != 20) {
									child_order.setOrder_status(20);
									child_order.setOut_order_id(main_order.getOut_order_id());
									child_order.setPayTime(new Date());
									child_order.setPayment(main_order.getPayment());
									this.orderFormService.update(child_order);
									
//									OrderFormLog child_ofl = new OrderFormLog();
//									child_ofl.setAddTime(new Date());
//									child_ofl.setLog_info("支付宝在线支付");
//									child_ofl.setLog_user(buyer);
//									child_ofl.setOf(child_order);
//									this.orderFormLogService.save(child_ofl);
									
									// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
									// 付款成功，发送邮件提示
									this.send_msg_toseller(request, child_order);
								}
							}
                        }

                        // 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
						this.update_goods_inventory(main_order);

						// 主订单付款成功，发送邮件提示
						// 向加盟商家发送付款成功邮件提示，自营商品无需发送邮件提示
						try {
							this.send_msg_tobuyer(request, main_order);
						} catch (Exception ex) {
							logger.error("In the aplipay_return function: Payment for user " + buyer.getUserName()
									+ " has been completed successfully, But sending message to the user fails." + ex);
						}
						try {
							this.send_msg_toseller(request, main_order);
						} catch (Exception ex) {
							logger.error("In the aplipay_return function: Payment for user " + buyer.getUserName()
									+ " has been completed successfully, But sending message to the corresponding seller fails."
									+ ex);
						}

                        // 支付成功发送同步订单事件
                        this.synchronizeOrderPublisher.synchronizeOrder(main_order.getId());
					}
					mv.addObject("all_price",
							this.orderFormTools.query_order_price(CommUtil.null2String(main_order.getId())));
					mv.addObject("obj", main_order);
				}
			}
			if ("group".equals(type)) {
				if ("WAIT_SELLER_SEND_GOODS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)
						|| "TRADE_SUCCESS".equals(trade_status)) {
					if (main_order.getOrder_status() != 20) { // 异步没有出来订单，则同步处理订单
						this.generate_groupInfos(request, main_order, "alipay", "支付宝在线支付", trade_no);
					}
					mv.addObject("all_price", main_order.getTotalPrice());
					mv.addObject("obj", main_order);
				}
			}
			if ("cash".equals(type)) {
				if ("WAIT_SELLER_SEND_GOODS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)
						|| "TRADE_SUCCESS".equals(trade_status)) {
					if (obj.getPd_pay_status() != 2) { // 异步没有处理该充值业务，则同步处理一下
						// TODO 支付宝回调成功后调用BCP进行生活卡充值。
						User user = this.userService.getObjById(obj.getPd_user().getId());
						ResultDTO resultDTO = feeManageConnector.recharge(user.getCustId(), total_fee, obj.getPd_sn());
						if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
							obj.setPd_status(1);
							obj.setPd_pay_status(2);
                            obj.setOut_order_id(trade_no);
							this.predepositService.update(obj);
							//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), obj.getPd_amount())));//余额都在CRM端保存和增减，故注释掉
							//this.userService.update(user);
							PredepositLog log = new PredepositLog();
							log.setAddTime(new Date());
							log.setPd_log_amount(obj.getPd_amount());
							log.setPd_log_user(obj.getPd_user());
							log.setPd_op_type("充值");
							log.setPd_type("可用预存款");
							log.setPd_log_info("支付宝在线支付_充值成功");
							this.predepositLogService.save(log);

							mv = new JModelAndView("success.html", configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1, request, response);
							mv.addObject("op_title", "恭喜您，成功充值" + obj.getPd_amount() + "元");
							mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
						} else {
							PredepositLog log = new PredepositLog();
							log.setAddTime(new Date());
							log.setPd_log_amount(obj.getPd_amount());
							log.setPd_log_user(obj.getPd_user());
							log.setPd_op_type("充值");
							log.setPd_type("可用预存款");
							log.setPd_log_info("支付宝在线支付_充值失败_" + resultDTO.getMsg());
							this.predepositLogService.save(log);

							mv = new JModelAndView("error.html", configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1, request, response);
							mv.addObject("op_title", resultDTO.getMsg() + ",充值失败:" + obj.getPd_amount() + "元");
							mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
						}
					} else {
						mv = new JModelAndView("success.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "恭喜您，成功充值" + obj.getPd_amount() + "元");
						mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
					}
				}
			}
			if ("gold".equals(type)) {
				if ("WAIT_SELLER_SEND_GOODS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)
						|| "TRADE_SUCCESS".equals(trade_status)) {
					if (gold.getGold_pay_status() != 2) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("支付宝在线支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
					}
					mv = new JModelAndView("success.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "兑换" + gold.getGold_count() + "金币成功");
					mv.addObject("url", CommUtil.getURL(request) + "/seller/gold_record_list.htm");
				}
			}
			if ("integral".equals(type)) {
				if ("WAIT_SELLER_SEND_GOODS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)
						|| "TRADE_SUCCESS".equals(trade_status)) {
					if (ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("alipay");
						this.integralGoodsOrderService.update(ig_order);
						List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(ig_order.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
							//goods.setIg_goods_count(
							//		goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
							goods.setIg_exchange_count(
									goods.getIg_exchange_count() + CommUtil.null2Int(map.get("ig_goods_count")));
							this.integralGoodsService.update(goods);
						}
					}
					mv = new JModelAndView("integral_order_finish.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("obj", ig_order);
				}
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "支付回调失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 支付宝异步通知
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/alipay_notify.htm")
	public synchronized  void  alipay_notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//
		String trade_no = request.getParameter("trade_no"); // 支付宝交易号
		String[] order_nos = request.getParameter("out_trade_no").split("-"); // 支付跳转时提供给支付宝的流水号(对应订单表中的trade_no)
		String total_fee = request.getParameter("total_fee"); // 获取总金额
		logger.debug(total_fee);
		logger.debug(request.getParameter("price"));
		String subject = request.getParameter("subject"); // new
		// String(request.getParameter("subject").getBytes("ISO-8859-1"),
		// "UTF-8"); //
		// 商品名称、订单名称
		String order_no = order_nos[2];// 对应订单表中的id列的值，并非order_id列的值
		String type = CommUtil.null2String(request.getParameter("body")).trim();
		String trade_status = request.getParameter("trade_status"); // 交易状态
		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(type) || "group".equals(type)) {
			main_order = this.orderFormService.getObjById(CommUtil.null2Long(order_no));
		}
		if ("cash".equals(type)) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(order_no));
		}
		if ("gold".equals(type)) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(order_no));
		}
		if ("integral".equals(type)) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(order_no));
		}
		// 获取支付宝GET过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 如果没有配置Tomcat的get编码为UTF-8，需要下面一行代码转码，否则会出现乱码，导致回调失败
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		logger.info("In the pc aplipay_notify function: params " + JSON.toJSONString(params));
		AlipayConfig config = new AlipayConfig();
		if ("goods".equals(type) || "group".equals(type)) {
			config.setKey(main_order.getPayment().getSafeKey());
			config.setPartner(main_order.getPayment().getPartner());
			config.setPrivate_key(main_order.getPayment().getApp_private_key());
			config.setAli_public_key(main_order.getPayment().getApp_public_key());
			config.setSign_type(main_order.getPayment().getAlipaySignType());
			config.setSeller_email(main_order.getPayment().getSeller_email());
		}
		if ("cash".equals(type) || "gold".equals(type) || "integral".equals(type)) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if ("cash".equals(type)) {
				q_params.put("mark", obj.getPd_payment());
			}
			if ("gold".equals(type)) {
				q_params.put("mark", gold.getGold_payment());
			}
			if ("integral".equals(type)) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark", q_params, -1, -1);
			config.setKey(payments.get(0).getSafeKey());
			config.setPartner(payments.get(0).getPartner());
			config.setPrivate_key(payments.get(0).getApp_private_key());
			config.setAli_public_key(payments.get(0).getApp_public_key());
			config.setSign_type(payments.get(0).getAlipaySignType());
			config.setSeller_email(payments.get(0).getSeller_email());
		}
		config.setNotify_url(CommUtil.getURL(request) + "/alipay_notify.htm");
		config.setReturn_url(CommUtil.getURL(request) + "/aplipay_return.htm");
		boolean verify_result = AlipayNotify.verify(config, params);
		if (verify_result) { // 验证成功
			if ("goods".equals(type)) {
				if ("WAIT_SELLER_SEND_GOODS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)
						|| "TRADE_SUCCESS".equals(trade_status)) {
					User buyer = this.userService.getObjById(CommUtil.null2Long(main_order.getUser_id()));
					if (main_order.getOrder_status() < 20) { // 异步没有出来订单，则同步处理订单
						//更新主订单状态
						main_order.setOrder_status(20);
						main_order.setOut_order_id(trade_no);
						main_order.setPayTime(new Date());
						this.orderFormService.update(main_order);
                        // 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
                        this.update_goods_inventory(main_order);
						// 记录主订单日志
						OrderFormLog main_ofl = new OrderFormLog();
						main_ofl.setAddTime(new Date());
						main_ofl.setLog_info("支付宝在线支付");
						main_ofl.setLog_user(buyer);
						main_ofl.setOf(main_order);
						this.orderFormLogService.save(main_ofl);
						
						// 子订单操作
						if (main_order.getOrder_main() == 1
								&& !"".equals(CommUtil.null2String(main_order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools.queryGoodsInfo(main_order.getChild_order_detail());
							for (Map child_map : maps) {
								//更新子订单状态
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil.null2Long(child_map.get("order_id")));
								if (child_order.getOrder_status() != 20) {
									child_order.setOrder_status(20);
									child_order.setOut_order_id(main_order.getOut_order_id());
									child_order.setPayTime(new Date());
									child_order.setPayment(main_order.getPayment());
									this.orderFormService.update(child_order);
                                    // 子订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
                                    this.update_goods_inventory(child_order);
									// 记录子订单日志
//									OrderFormLog child_ofl = new OrderFormLog();
//									child_ofl.setAddTime(new Date());
//									child_ofl.setLog_info("支付宝在线支付");
//									child_ofl.setLog_user(buyer);
//									child_ofl.setOf(child_order);
//									this.orderFormLogService.save(child_ofl);
									
									// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
									// 付款成功，发送邮件提示
									this.send_msg_toseller(request, child_order);

								}
							}
						}
						
						// 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
						this.update_goods_inventory(main_order);
						
						// 主订单付款成功，发送邮件提示
						// 向加盟商家发送付款成功邮件提示，自营商品无需发送邮件提示
						try {
							this.send_msg_tobuyer(request, main_order);
						} catch (Exception ex) {
							logger.error("In the aplipay_notify function: Payment for user " + buyer.getUserName()
									+ " has been completed successfully, But sending message to the user fails." + ex);
						}
						try {
							this.send_msg_toseller(request, main_order);
						} catch (Exception ex) {
							logger.error("In the aplipay_notify function: Payment for user " + buyer.getUserName()
									+ " has been completed successfully, But sending message to the corresponding seller fails."
									+ ex);
						}
                        // 支付成功发送同步订单事件
                        this.synchronizeOrderPublisher.synchronizeOrder(main_order.getId());
					}
				}
			}
			if ("group".equals(type)) {
				if ("WAIT_SELLER_SEND_GOODS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)
						|| "TRADE_SUCCESS".equals(trade_status)) {
					if (main_order.getOrder_status() != 20) { // 异步没有出来订单，则同步处理订单
						this.generate_groupInfos(request, main_order, "alipay", "支付宝在线支付", trade_no);
					}
				}
			}
			if ("cash".equals(type)) {
				if ("WAIT_SELLER_SEND_GOODS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)
						|| "TRADE_SUCCESS".equals(trade_status)) {
					if (obj.getPd_pay_status() < 2) {
						// TODO 支付宝回调成功后调用BCP进行生活卡充值。(异步)
						User user = this.userService.getObjById(obj.getPd_user().getId());
						ResultDTO resultDTO = feeManageConnector.recharge(user.getCustId(), total_fee, obj.getPd_sn());
						if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
							obj.setPd_status(1);
							obj.setPd_pay_status(2);
                            obj.setOut_order_id(trade_no);
							this.predepositService.update(obj);
							//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), obj.getPd_amount())));//余额都在CRM端保存和增减，故注释掉
							//this.userService.update(user);
							PredepositLog log = new PredepositLog();
							log.setAddTime(new Date());
							log.setPd_log_amount(obj.getPd_amount());
							log.setPd_log_user(obj.getPd_user());
							log.setPd_op_type("充值");
							log.setPd_type("可用预存款");
							log.setPd_log_info("支付宝在线支付_充值成功");
							this.predepositLogService.save(log);
						} else {
							PredepositLog log = new PredepositLog();
							log.setAddTime(new Date());
							log.setPd_log_amount(obj.getPd_amount());
							log.setPd_log_user(obj.getPd_user());
							log.setPd_op_type("充值");
							log.setPd_type("可用预存款");
							log.setPd_log_info("支付宝在线支付_充值失败_" + resultDTO.getMsg());
							this.predepositLogService.save(log);
						}
					}
				}
			}
			if ("gold".equals(type)) {
				if ("WAIT_SELLER_SEND_GOODS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)
						|| "TRADE_SUCCESS".equals(trade_status)) {
					if (gold.getGold_pay_status() < 2) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("支付宝在线支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
					}
				}
			}
			if ("integral".equals(type)) {
				if ("WAIT_SELLER_SEND_GOODS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)
						|| "TRADE_SUCCESS".equals(trade_status)) {
					if (ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("alipay");
						this.integralGoodsOrderService.update(ig_order);
						List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(ig_order.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
							//goods.setIg_goods_count(
							//		goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
							goods.setIg_exchange_count(
									goods.getIg_exchange_count() + CommUtil.null2Int(map.get("ig_goods_count")));
							this.integralGoodsService.update(goods);
						}
					}
				}
			}
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print("success");
			} catch (IOException e) {
				logger.error(e);
			}

		} else {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print("fail");
			} catch (IOException e) {
				logger.error(e);
			}

		}

	}

	/**
	 * 快钱在线支付回调处理控制
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/bill_return.htm")
	public ModelAndView bill_return(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("order_finish.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 获取扩展字段1
		String ext1 = (String) request.getParameter("ext1").trim();
		String ext2 = CommUtil.null2String(request.getParameter("ext2").trim());
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(ext2) || "group".equals(ext2)) {
			order = this.orderFormService.getObjById(CommUtil.null2Long(ext1));
		}
		if ("cash".equals(ext2)) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(ext1));
		}
		if ("gold".equals(ext2)) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(ext1));
		}
		if ("integral".equals(ext2)) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(ext1));
		}
		// 获取人民币网关账户号
		String merchantAcctId = (String) request.getParameter("merchantAcctId").trim();
		// 设置人民币网关密钥
		// /区分大小写
		String key = "";
		if ("goods".equals(ext2) || "group".equals(ext2)) {
			key = order.getPayment().getRmbKey();
		}
		if ("cash".equals(ext2) || "gold".equals(ext2) || "integral".equals(ext2)) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if ("cash".equals(ext2)) {
				q_params.put("mark", obj.getPd_payment());
			}
			if ("gold".equals(ext2)) {
				q_params.put("mark", gold.getGold_payment());
			}
			if ("integral".equals(ext2)) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark", q_params, -1, -1);
			key = payments.get(0).getRmbKey();
		}
		// /快钱会根据版本号来调用对应的接口处理程序。
		// /本代码版本号固定为v2.0
		String version = (String) request.getParameter("version").trim();
		// 获取语言种类.固定选择值。
		// /只能选择1、2、3
		// /1代表中文；2代表英文
		// /默认值为1
		String language = (String) request.getParameter("language").trim();
		// 签名类型.固定值
		// /1代表MD5签名
		// /快钱3.0后该值为4
		String signType = (String) request.getParameter("signType").trim();

		// 获取支付方式
		// /值为：10、11、12、13、14
		// /00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）.14：B2B支付（网关支付页面只显示B2B支付，但需要向快钱申请开通才能使用）
		String payType = (String) request.getParameter("payType").trim();

		// 获取银行代码
		// /参见银行代码列表
		String bankId = (String) request.getParameter("bankId").trim();
		// 获取商户订单号
		String orderId = (String) request.getParameter("orderId").trim();
		// 获取订单提交时间
		// /获取商户提交订单时的时间.14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		// /如：20080101010101
		String orderTime = (String) request.getParameter("orderTime").trim();
		// 获取原始订单金额
		// /订单提交到快钱时的金额，单位为分。
		// /比方2 ，代表0.02元
		String orderAmount = (String) request.getParameter("orderAmount").trim();
		// 获取快钱交易号
		// /获取该交易在快钱的交易号
		String dealId = (String) request.getParameter("dealId").trim();
		// 获取银行交易号
		// /如果使用银行卡支付时，在银行的交易号。如不是通过银行支付，则为空
		String bankDealId = (String) request.getParameter("bankDealId").trim();
		// 获取在快钱交易时间
		// /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		// /如；20080101010101
		String dealTime = (String) request.getParameter("dealTime").trim();
		// 获取实际支付金额
		// /单位为分
		// /比方 2 ，代表0.02元
		String payAmount = (String) request.getParameter("payAmount").trim();
		// 获取交易手续费
		// /单位为分
		// /比方 2 ，代表0.02元
		String fee = (String) request.getParameter("fee").trim();
		// 获取扩展字段2
		// /10代表 成功11代表 失败
		String payResult = (String) request.getParameter("payResult").trim();
		// 获取错误代码
		String errCode = (String) request.getParameter("errCode").trim();
		// 获取加密签名串
		String signMsg = (String) request.getParameter("signMsg").trim();
		// 生成加密串。必须保持如下顺序。
		String merchantSignMsgVal = "";
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "merchantAcctId", merchantAcctId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "version", version);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "language", language);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType", signType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType", payType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId", bankId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId", orderId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime", orderTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount", orderAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId", dealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId", bankDealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime", dealTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount", payAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult", payResult);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode", errCode);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "key", key);

		String merchantSignMsg = MD5Util.md5Hex(merchantSignMsgVal.getBytes("utf-8")).toUpperCase();
		// 商家进行数据处理，并跳转会商家显示支付结果的页面
		// /首先进行签名字符串验证
		if (signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())) {
			// /接着进行支付结果判断
			switch (Integer.parseInt(payResult)) {
			case 10:
				// 特别注意：只有signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())，且payResult=10，才表示支付成功！同时将订单金额与提交订单前的订单金额进行对比校验。
				if ("goods".equals(ext2)) {
					User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
					if (order.getOrder_status() != 20) {
						order.setOrder_status(20);
						order.setPayTime(new Date());
						this.orderFormService.update(order); // 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
						this.update_goods_inventory(order);
						OrderFormLog main_ofl = new OrderFormLog();
						main_ofl.setAddTime(new Date());
						main_ofl.setLog_info("快钱在线支付");
						main_ofl.setLog_user(buyer);
						main_ofl.setOf(order);
						this.orderFormLogService.save(main_ofl);
						this.send_msg_tobuyer(request, order);
						this.send_msg_toseller(request, order);
						if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil.null2Long(child_map.get("order_id")));
								child_order.setPayTime(new Date());
								child_order.setOrder_status(20);
								this.orderFormService.update(child_order);
								this.update_goods_inventory(order);
//								OrderFormLog child_ofl = new OrderFormLog();
//								child_ofl.setAddTime(new Date());
//								child_ofl.setLog_info("快钱在线支付");
//								child_ofl.setLog_user(buyer);
//								child_ofl.setOf(child_order);
//								this.orderFormLogService.save(child_ofl);
								// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
								// 付款成功，发送短信提示
								this.send_msg_toseller(request, child_order);
							}

						}
					}
					mv.addObject("obj", order);
				}
				if ("group".equals(ext2)) {
					this.generate_groupInfos(request, order, "bill", "快钱在线支付", "");
					mv.addObject("obj", order);
				}
				if ("cash".equals(ext2)) {
					if (obj.getPd_pay_status() < 2) { // 判断预存款支付状态
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						//User user = this.userService.getObjById(obj.getPd_user().getId());
						//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), obj.getPd_amount())));//余额都在CRM端保存和增减，故注释掉
						//this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("快钱在线支付");
						this.predepositLogService.save(log);
					}
					mv = new JModelAndView("success.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
					mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
				}
				if ("gold".equals(ext2)) {
					if (gold.getGold_pay_status() < 2) { // 判断金币充值状态
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("快钱在线支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
					}
					mv = new JModelAndView("success.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "兑换" + gold.getGold_count() + "金币成功");
					mv.addObject("url", CommUtil.getURL(request) + "/seller/gold_record_list.htm");
				}
				if ("integral".equals(ext2)) {
					if (ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.update(ig_order);
						List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(ig_order.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
							//goods.setIg_goods_count(
							//		goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
							goods.setIg_exchange_count(
									goods.getIg_exchange_count() + CommUtil.null2Int(map.get("ig_goods_count")));
							this.integralGoodsService.update(goods);
						}
					}
					mv = new JModelAndView("integral_order_finish.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("obj", ig_order);
				}
				break;
			default:
				mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
						request, response);
				mv.addObject("op_title", "快钱支付失败！");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				break;

			}

		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "快钱支付失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	public String appendParam(String returnStr, String paramId, String paramValue) {
		if (!StringUtils.isNullOrEmpty(returnStr)) {
			if (!StringUtils.isNullOrEmpty(paramValue)) {
				returnStr = returnStr + "&" + paramId + "=" + paramValue;
			}
		} else {
			if (!StringUtils.isNullOrEmpty(paramValue)) {
				returnStr = paramId + "=" + paramValue;
			}
		}
		return returnStr;
	}

	/**
	 * 快钱异步回调处理，如果同步回调出错，异步回调会弥补该功能
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/bill_notify_return.htm")
	public void bill_notify_return(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int rtnOK = 0;
		// 获取扩展字段1
		String ext1 = (String) request.getParameter("ext1").trim();
		String ext2 = CommUtil.null2String(request.getParameter("ext2").trim());
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(ext2) || "group".equals(ext2)) {
			order = this.orderFormService.getObjById(CommUtil.null2Long(ext1));
		}
		if ("cash".equals(ext2)) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(ext1));
		}
		if ("gold".equals(ext2)) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(ext1));
		}
		if ("integral".equals(ext2)) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(ext1));
		}
		// 获取人民币网关账户号
		String merchantAcctId = (String) request.getParameter("merchantAcctId").trim();
		// 设置人民币网关密钥
		// /区分大小写
		String key = "";
		if ("goods".equals(ext2) || "group".equals(ext2)) {
			key = order.getPayment().getRmbKey();
		}
		if ("cash".equals(ext2) || "gold".equals(ext2) || "integral".equals(ext2)) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if ("cash".equals(ext2)) {
				q_params.put("mark", obj.getPd_payment());
			}
			if ("gold".equals(ext2)) {
				q_params.put("mark", gold.getGold_payment());
			}
			if ("integral".equals(ext2)) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark", q_params, -1, -1);
			key = payments.get(0).getRmbKey();
		}
		// /快钱会根据版本号来调用对应的接口处理程序。
		// /本代码版本号固定为v2.0
		String version = (String) request.getParameter("version").trim();
		// 获取语言种类.固定选择值。
		// /只能选择1、2、3
		// /1代表中文；2代表英文
		// /默认值为1
		String language = (String) request.getParameter("language").trim();
		// 签名类型.固定值
		// /1代表MD5签名
		// /快钱3.0后该值为4
		String signType = (String) request.getParameter("signType").trim();

		// 获取支付方式
		// /值为：10、11、12、13、14
		// /00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）.14：B2B支付（网关支付页面只显示B2B支付，但需要向快钱申请开通才能使用）
		String payType = (String) request.getParameter("payType").trim();

		// 获取银行代码
		// /参见银行代码列表
		String bankId = (String) request.getParameter("bankId").trim();
		// 获取商户订单号
		String orderId = (String) request.getParameter("orderId").trim();
		// 获取订单提交时间
		// /获取商户提交订单时的时间.14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		// /如：20080101010101
		String orderTime = (String) request.getParameter("orderTime").trim();
		// 获取原始订单金额
		// /订单提交到快钱时的金额，单位为分。
		// /比方2 ，代表0.02元
		String orderAmount = (String) request.getParameter("orderAmount").trim();
		// 获取快钱交易号
		// /获取该交易在快钱的交易号
		String dealId = (String) request.getParameter("dealId").trim();
		// 获取银行交易号
		// /如果使用银行卡支付时，在银行的交易号。如不是通过银行支付，则为空
		String bankDealId = (String) request.getParameter("bankDealId").trim();
		// 获取在快钱交易时间
		// /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		// /如；20080101010101
		String dealTime = (String) request.getParameter("dealTime").trim();
		// 获取实际支付金额
		// /单位为分
		// /比方 2 ，代表0.02元
		String payAmount = (String) request.getParameter("payAmount").trim();
		// 获取交易手续费
		// /单位为分
		// /比方 2 ，代表0.02元
		String fee = (String) request.getParameter("fee").trim();
		// 获取扩展字段2
		// /10代表 成功11代表 失败
		String payResult = (String) request.getParameter("payResult").trim();
		// 获取错误代码
		String errCode = (String) request.getParameter("errCode").trim();
		// 获取加密签名串
		String signMsg = (String) request.getParameter("signMsg").trim();
		// 生成加密串。必须保持如下顺序。
		String merchantSignMsgVal = "";
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "merchantAcctId", merchantAcctId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "version", version);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "language", language);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType", signType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType", payType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId", bankId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId", orderId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime", orderTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount", orderAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId", dealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId", bankDealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime", dealTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount", payAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult", payResult);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode", errCode);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "key", key);

		String merchantSignMsg = MD5Util.md5Hex(merchantSignMsgVal.getBytes("utf-8")).toUpperCase();
		// 商家进行数据处理，并跳转会商家显示支付结果的页面
		// /首先进行签名字符串验证
		if (signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())) {
			// /接着进行支付结果判断
			switch (Integer.parseInt(payResult)) {
			case 10:
				// 特别注意：只有signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())，且payResult=10，才表示支付成功！同时将订单金额与提交订单前的订单金额进行对比校验。
				if ("goods".equals(ext2)) {
					User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
					if (order.getOrder_status() != 20) {
						order.setOrder_status(20);
						order.setPayTime(new Date());
						this.orderFormService.update(order);
						this.update_goods_inventory(order); // 更新商品库存
						OrderFormLog main_ofl = new OrderFormLog();
						main_ofl.setAddTime(new Date());
						main_ofl.setLog_info("快钱在线支付");
						main_ofl.setLog_user(buyer);
						main_ofl.setOf(order);
						this.orderFormLogService.save(main_ofl);
						// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
						// 付款成功，发送短信提示
						this.send_msg_tobuyer(request, order);
						this.send_msg_toseller(request, order);
						if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil.null2Long(child_map.get("order_id")));
								child_order.setOrder_status(20);
								child_order.setPayTime(new Date());
								this.orderFormService.update(child_order);
								this.update_goods_inventory(child_order); // 更新商品库存
//								OrderFormLog child_ofl = new OrderFormLog();
//								child_ofl.setAddTime(new Date());
//								child_ofl.setLog_info("快钱在线支付");
//								child_ofl.setLog_user(buyer);
//								child_ofl.setOf(child_order);
//								this.orderFormLogService.save(child_ofl);
								// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
								this.send_msg_toseller(request, child_order);
							}

						}
					}
					rtnOK = 1;
				}
				if ("group".equals(ext2)) {
					this.generate_groupInfos(request, order, "bill", "快钱在线支付", "");
					rtnOK = 1;
				}
				if ("cash".equals(ext2)) {
					if (obj.getPd_pay_status() < 2) { // 判断预存款支付状态
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						//User user = this.userService.getObjById(obj.getPd_user().getId());
						//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), obj.getPd_amount())));//余额都在CRM端保存和增减，故注释掉
						//this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("快钱在线支付");
						this.predepositLogService.save(log);
					}
					rtnOK = 1;
				}
				if ("gold".equals(ext2)) {
					if (gold.getGold_pay_status() < 2) { // 判断金币充值状态
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("快钱在线支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
					}
					rtnOK = 1;
				}
				if ("integral".equals(ext2)) {
					if (ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.update(ig_order);
						List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(ig_order.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
							//goods.setIg_goods_count(
							//		goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
							goods.setIg_exchange_count(
									goods.getIg_exchange_count() + CommUtil.null2Int(map.get("ig_goods_count")));
							this.integralGoodsService.update(goods);
						}
					}
					rtnOK = 1;
				}
				break;
			default:

				break;

			}

		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(rtnOK);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 财付通支付
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/tenpay.htm")
	public void tenpay(HttpServletRequest request, HttpServletResponse response, String id, String type, String payment_id)
			throws IOException {
		boolean submit = true; // 是否继续提交支付，防止订单重复支付，pc端打开支付页面，另外一个人用app完成了支付
		OrderForm of = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(type) || "group".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) { // 订单已经处于支付状态
				submit = false;
			}
		}
		if ("cash".equals(type)) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(id));
			if (obj.getPd_pay_status() >= 2) {
				submit = false; // 预存款已经完成充值
			}
		}
		if ("gold".equals(type)) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(id));
			if (gold.getGold_pay_status() >= 2) {
				submit = false; // 金币已经完成充值
			}
		}
		if ("integral".equals(type)) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
			if (ig_order.getIgo_status() >= 20) {
				submit = false; // 积分订单已经完成支付
			}
		}
		if (submit) {
			// 获取提交的商品价格
			String order_price = "";
			if ("goods".equals(type) || "gruop".equals(type)) {
				order_price = CommUtil.null2String(of.getTotalPrice());
			}
			if ("cash".equals(type)) {
				order_price = CommUtil.null2String(obj.getPd_amount());
			}
			if ("gold".equals(type)) {
				order_price = CommUtil.null2String(gold.getGold_money());
			}
			if ("integral".equals(type)) {
				order_price = CommUtil.null2String(ig_order.getIgo_trans_fee());
			}
			/* 商品价格（包含运费），以分为单位 */
			double total_fee = CommUtil.null2Double(order_price) * 100;
			int fee = (int) total_fee;
			// 获取提交的商品名称
			String product_name = "";
			if ("goods".equals(type) || "group".equals(type)) {
				product_name = of.getOrder_id();
			}
			if ("cash".equals(type)) {
				product_name = obj.getPd_sn();
			}
			if ("gold".equals(type)) {
				product_name = gold.getGold_sn();
			}
			if ("integral".equals(type)) {
				product_name = ig_order.getIgo_order_sn();
			}
			// 获取提交的备注信息
			String remarkexplain = "";
			if ("goods".equals(type) || "group".equals(type)) {
				remarkexplain = of.getMsg();
			}
			if ("cash".equals(type)) {
				remarkexplain = obj.getPd_remittance_info();
			}
			if ("gold".equals(type)) {
				remarkexplain = gold.getGold_exchange_info();
			}
			if ("integral".equals(type)) {
				remarkexplain = ig_order.getIgo_msg();
			}
			String attach = "";
			if ("goods".equals(type) || "group".equals(type)) {
				attach = type + "," + of.getId().toString();
			}
			if ("cash".equals(type)) {
				attach = type + "," + obj.getId().toString();
			}
			if ("gold".equals(type)) {
				attach = type + "," + gold.getId().toString();
			}
			if ("integral".equals(type)) {
				attach = type + "," + ig_order.getId().toString();
			}
			String desc = "商品：" + product_name;
			// 获取提交的订单号
			String out_trade_no = "";
			if ("goods".equals(type) || "group".equals(type)) {
				out_trade_no = of.getOrder_id();
			}
			if ("cash".equals(type)) {
				out_trade_no = obj.getPd_sn();
			}
			if (type.endsWith("gold")) {
				out_trade_no = gold.getGold_sn();
			}
			if ("integral".equals(type)) {
				out_trade_no = ig_order.getIgo_order_sn();
			}
			// 支付方式
			Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
			if (payment == null) {
				payment = new Payment();
			}
			String trade_mode = CommUtil.null2String(payment.getTrade_mode());
			String currTime = TenpayUtil.getCurrTime();
			// 创建支付请求对象
			RequestHandler reqHandler = new RequestHandler(request, response);
			reqHandler.init();
			// 设置密钥
			reqHandler.setKey(payment.getTenpay_key());
			// 设置支付网关
			reqHandler.setGateUrl("https://gw.tenpay.com/gateway/pay.htm");
			// -----------------------------
			// 设置支付参数
			// -----------------------------
			reqHandler.setParameter("partner", payment.getTenpay_partner()); // 商户号
			reqHandler.setParameter("out_trade_no", out_trade_no); // 商家订单号
			reqHandler.setParameter("total_fee", String.valueOf(fee)); // 商品金额,以分为单位
			reqHandler.setParameter("return_url", CommUtil.getURL(request) + "/tenpay_return.htm"); // 交易完成后跳转的URL
			reqHandler.setParameter("notify_url", CommUtil.getURL(request) + "/tenpay_notify.htm"); // 接收财付通通知的URL
			reqHandler.setParameter("body", desc); // 商品描述
			reqHandler.setParameter("bank_type", "DEFAULT"); // 银行类型(中介担保时此参数无效)
			reqHandler.setParameter("spbill_create_ip", request.getRemoteAddr()); // 用户的公网ip，不是商户服务器IP
			reqHandler.setParameter("fee_type", "1"); // 币种，1人民币
			reqHandler.setParameter("subject", desc); // 商品名称(中介交易时必填)
			// 系统可选参数
			reqHandler.setParameter("sign_type", "MD5"); // 签名类型,默认：MD5
			reqHandler.setParameter("service_version", "1.0"); // 版本号，默认为1.0
			reqHandler.setParameter("input_charset", "UTF-8"); // 字符编码
			reqHandler.setParameter("sign_key_index", "1"); // 密钥序号

			// 业务可选参数
			reqHandler.setParameter("attach", attach); // 附加数据，原样返回
			reqHandler.setParameter("product_fee", ""); // 商品费用，必须保证transport_fee
														// +
			reqHandler.setParameter("transport_fee", "0"); // 物流费用，必须保证transport_fee
			reqHandler.setParameter("time_start", currTime); // 订单生成时间，格式为yyyymmddhhmmss
			reqHandler.setParameter("time_expire", ""); // 订单失效时间，格式为yyyymmddhhmmss
			reqHandler.setParameter("buyer_id", ""); // 买方财付通账号
			reqHandler.setParameter("goods_tag", ""); // 商品标记
			reqHandler.setParameter("trade_mode", trade_mode); // 交易模式，1即时到账(默认)，2中介担保，3后台选择（买家进支付中心列表选择）
			reqHandler.setParameter("transport_desc", ""); // 物流说明
			reqHandler.setParameter("trans_type", "1"); // 交易类型，1实物交易，2虚拟交易
			reqHandler.setParameter("agentid", ""); // 平台ID
			reqHandler.setParameter("agent_type", ""); // 代理模式，0无代理(默认)，1表示卡易售模式，2表示网店模式
			reqHandler.setParameter("seller_id", ""); // 卖家商户号，为空则等同于partner
			// 请求的url
			String requestUrl = reqHandler.getRequestURL();
			response.sendRedirect(requestUrl);
		} else {
			response.getWriter().write("该订单已经完成支付！");
		}

	}

	/**
	 * 财付通在线支付回调控制
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/tenpay_return.htm")
	public ModelAndView tenpay_return(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("order_finish.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ResponseHandler resHandler = new ResponseHandler(request, response);
		String[] attachs = request.getParameter("attach").split(",");
		// 商户订单号
		String out_trade_no = resHandler.getParameter("out_trade_no");
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("integral".equals(attachs[0])) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(attachs[1]));
			Map q_params = new HashMap();
			q_params.put("install", true);
			q_params.put("mark", ig_order.getIgo_payment());
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark", q_params, -1, -1);
			resHandler.setKey(payments.get(0).getTenpay_key());
		}
		if ("cash".equals(attachs[0])) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(attachs[1]));
			Map q_params = new HashMap();
			q_params.put("install", true);
			q_params.put("mark", obj.getPd_payment());
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark", q_params, -1, -1);
			resHandler.setKey(payments.get(0).getTenpay_key());
		}
		if ("gold".equals(attachs[0])) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(attachs[1]));
			Map q_params = new HashMap();
			q_params.put("install", true);
			q_params.put("mark", gold.getGold_payment());
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark", q_params, -1, -1);
			resHandler.setKey(payments.get(0).getTenpay_key());
		}
		if ("goods".equals(attachs[0]) || "group".equals(attachs[0])) {
			order = this.orderFormService.getObjById(CommUtil.null2Long(attachs[1]));
			resHandler.setKey(order.getPayment().getTenpay_key());
		}
		// 判断签名
		if (resHandler.isTenpaySign()) {
			// 通知id
			String notify_id = resHandler.getParameter("notify_id");
			// 财付通订单号
			String transaction_id = resHandler.getParameter("transaction_id");
			// 金额,以分为单位
			String total_fee = resHandler.getParameter("total_fee");
			// 如果有使用折扣券，discount有值，total_fee+discount=原请求的total_fee
			String discount = resHandler.getParameter("discount");
			// 支付结果
			String trade_state = resHandler.getParameter("trade_state");
			// 交易模式，1即时到账，2中介担保
			String trade_mode = resHandler.getParameter("trade_mode");
			if ("1".equals(trade_mode)) { // 即时到账
				if ("0".equals(trade_state)) {
					// 即时到账处理业务完毕
					if ("cash".equals(attachs[0])) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						//User user = this.userService.getObjById(obj.getPd_user().getId());
						//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), obj.getPd_amount())));//余额都在CRM端保存和增减，故注释掉
						//this.userService.update(user);
						mv = new JModelAndView("success.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
						mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
					}
					if ("goods".equals(attachs[0])) {
						User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
						order.setOrder_status(20);
						order.setPayTime(new Date());
						this.orderFormService.update(order);
						this.update_goods_inventory(order); // 更新商品库存
						OrderFormLog main_ofl = new OrderFormLog();
						main_ofl.setAddTime(new Date());
						main_ofl.setLog_info("财付通在线支付");
						main_ofl.setLog_user(buyer);
						main_ofl.setOf(order);
						this.orderFormLogService.save(main_ofl);
						// 发送邮件短信
						this.send_msg_tobuyer(request, order);
						this.send_msg_toseller(request, order);
						if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil.null2Long(child_map.get("order_id")));
								child_order.setOrder_status(20);
								child_order.setPayTime(new Date());
								this.orderFormService.update(child_order);
								this.update_goods_inventory(child_order); // 更新商品库存
//								OrderFormLog child_ofl = new OrderFormLog();
//								child_ofl.setAddTime(new Date());
//								child_ofl.setLog_info("财付通在线支付");
//								child_ofl.setLog_user(buyer);
//								child_ofl.setOf(child_order);
//								this.orderFormLogService.save(child_ofl);
								// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
								// 发送邮件短信
								this.send_msg_toseller(request, child_order);
							}
						}
						mv.addObject("obj", order);
					}
					if ("group".equals(attachs[0])) {
						this.generate_groupInfos(request, order, "tenpay", "财付通即时到帐支付", "");
						mv.addObject("obj", order);
					}
					if ("gold".equals(attachs[0])) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("财付通及时到账支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
						mv = new JModelAndView("success.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "兑换" + gold.getGold_count() + "金币成功");
						mv.addObject("url", CommUtil.getURL(request) + "/seller/gold_record_list.htm");
					}
					if ("integral".equals(attachs[0])) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.update(ig_order);
						List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(ig_order.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
							//goods.setIg_goods_count(
							//		goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
							goods.setIg_exchange_count(
									goods.getIg_exchange_count() + CommUtil.null2Int(map.get("ig_goods_count")));
							this.integralGoodsService.update(goods);
						}
						mv = new JModelAndView("integral_order_finish.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("obj", ig_order);
					}
				} else {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "财付通支付失败！");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}
			} else if ("2".equals(trade_mode)) { // 中介担保
				if ("0".equals(trade_state)) {
					// 中介担保处理业务完毕
					if ("cash".equals(attachs[0])) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.update(obj);
						//User user = this.userService.getObjById(obj.getPd_user().getId());
						//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), obj.getPd_amount())));//余额都在CRM端保存和增减，故注释掉
						//this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("财付通中介担保付款");
						this.predepositLogService.save(log);
						mv = new JModelAndView("success.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
						mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
					}
					if ("goods".equals(attachs[0])) {
						order.setOrder_status(20);
						order.setPayTime(new Date());
						this.orderFormService.update(order);
						if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService
										.getObjById(CommUtil.null2Long(child_map.get("order_id")));
								child_order.setOrder_status(20);
								this.orderFormService.update(child_order);
								// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
								Store store = this.storeService.getObjById(CommUtil.null2Long(child_order.getStore_id()));
								User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
								// 付款成功，发送邮件提示
								if (child_order.getOrder_form() == 0) {
									this.msgTools.sendEmailCharge(CommUtil.getURL(request),
											"email_tobuyer_online_pay_ok_notify", buyer.getEmail(), null,
											CommUtil.null2String(child_order.getId()), child_order.getStore_id());
									this.msgTools.sendEmailCharge(CommUtil.getURL(request),
											"email_toseller_online_pay_ok_notify", store.getUser().getEmail(), null,
											CommUtil.null2String(child_order.getId()), child_order.getStore_id());
									// 付款成功，发送短信提示
									this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_tobuyer_online_pay_ok_notify",
											buyer.getMobile(), null, CommUtil.null2String(child_order.getId()),
											child_order.getStore_id());
									this.msgTools.sendSmsCharge(CommUtil.getURL(request),
											"sms_toseller_online_pay_ok_notify", store.getUser().getMobile(), null,
											CommUtil.null2String(child_order.getId()), child_order.getStore_id());
								} else {
									this.msgTools.sendSmsFree(CommUtil.getURL(request), "sms_tobuyer_online_pay_ok_notify",
											buyer.getMobile(), null, CommUtil.null2String(child_order.getId()));
									this.msgTools.sendEmailFree(CommUtil.getURL(request),
											"email_tobuyer_online_pay_ok_notify", buyer.getEmail(), null,
											CommUtil.null2String(child_order.getId()));
								}
							}
						}
						this.update_goods_inventory(order); // 更新商品库存
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("财付通中介担保付款成功");
						ofl.setLog_user(SecurityUserHolder.getCurrentUser());
						ofl.setOf(order);
						this.orderFormLogService.save(ofl);
						mv.addObject("obj", order);
					}
					if ("gold".equals(attachs[0])) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.update(gold);
						User user = this.userService.getObjById(gold.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.update(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("财付通中介担保付款成功");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.save(log);
						mv = new JModelAndView("success.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "兑换" + gold.getGold_count() + "金币成功");
						mv.addObject("url", CommUtil.getURL(request) + "/seller/gold_record_list.htm");
					}
					if ("integral".equals(attachs[0])) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.update(ig_order);
						List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(ig_order.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
							//goods.setIg_goods_count(
							//		goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
							goods.setIg_exchange_count(
									goods.getIg_exchange_count() + CommUtil.null2Int(map.get("ig_goods_count")));
							this.integralGoodsService.update(goods);
						}
						mv = new JModelAndView("integral_order_finish.html", configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("obj", ig_order);
					}
				} else {
					mv = new JModelAndView("error.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "财付通支付失败！");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "财付通认证签名失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 网银在线回调函数
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/chinabank_return.htm")
	public ModelAndView chinabank_return(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("order_finish.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String remark1 = request.getParameter("remark1"); // 备注1
		String remark2 = CommUtil.null2String(request.getParameter("remark2"));
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(remark2) || "group".equals(remark2)) {
			order = this.orderFormService.getObjById(CommUtil.null2Long(remark1.trim()));
		}
		if ("cash".equals(remark2)) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(remark1));
		}
		if ("gold".equals(remark2)) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(remark1));
		}
		if ("integral".equals(remark2)) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(remark1));
		}
		String key = "";
		if ("goods".equals(remark2) || "group".equals(remark2)) {
			key = order.getPayment().getChinabank_key();
		}
		if ("cash".equals(remark2) || "gold".equals(remark2) || "integral".equals(remark2)) {
			Map q_params = new HashMap();
			q_params.put("install", true);
			if ("cash".equals(remark2)) {
				q_params.put("mark", obj.getPd_payment());
			}
			if ("gold".equals(remark2)) {
				q_params.put("mark", gold.getGold_payment());
			}
			if ("integral".equals(remark2)) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark", q_params, -1, -1);
			key = payments.get(0).getChinabank_key();
		}
		String v_oid = request.getParameter("v_oid"); // 订单号
		String v_pmode = request.getParameter("v_pmode"); // new
		// String(request.getParameter("v_pmode").getBytes("ISO-8859-1"),
		// "UTF-8"); //
		// 支付方式中文说明，如"中行长城信用卡"
		String v_pstatus = request.getParameter("v_pstatus"); // 支付结果，20支付完成；30支付失败；
		String v_pstring = request.getParameter("v_pstring"); // new
		// String(request.getParameter("v_pstring").getBytes("ISO-8859-1"),
		// "UTF-8"); //
		// 对支付结果的说明，成功时（v_pstatus=20）为"支付成功"，支付失败时（v_pstatus=30）为"支付失败"
		String v_amount = request.getParameter("v_amount"); // 订单实际支付金额
		String v_moneytype = request.getParameter("v_moneytype"); // 币种
		String v_md5str = request.getParameter("v_md5str"); // MD5校验码
		String text = v_oid + v_pstatus + v_amount + v_moneytype + key; // 拼凑加密串
		String v_md5text = Md5Encrypt.md5(text).toUpperCase();
		if (v_md5str.equals(v_md5text)) {
			if ("20".equals(v_pstatus)) {
				// 支付成功，商户 根据自己业务做相应逻辑处理
				// 此处加入商户系统的逻辑处理（例如判断金额，判断支付状态(20成功,30失败)，更新订单状态等等）......
				if ("goods".equals(remark2)) {
					User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
					order.setOrder_status(20);
					order.setPayTime(new Date());
					this.orderFormService.update(order);
					this.update_goods_inventory(order); // 更新商品库存
					OrderFormLog main_ofl = new OrderFormLog();
					main_ofl.setAddTime(new Date());
					main_ofl.setLog_info("财付通在线支付");
					main_ofl.setLog_user(buyer);
					main_ofl.setOf(order);
					this.orderFormLogService.save(main_ofl);
					// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
					// 发送邮件短信
					this.send_msg_tobuyer(request, order);
					this.send_msg_toseller(request, order);
					if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
						List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
						for (Map child_map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(child_map.get("order_id")));
							child_order.setPayTime(new Date());
							child_order.setOrder_status(20);
							this.orderFormService.update(child_order);
							this.update_goods_inventory(child_order); // 更新商品库存
//							OrderFormLog child_ofl = new OrderFormLog();
//							child_ofl.setAddTime(new Date());
//							child_ofl.setLog_info("网银在线支付");
//							child_ofl.setLog_user(buyer);
//							child_ofl.setOf(child_order);
//							this.orderFormLogService.save(child_ofl);
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							// 发送邮件短信
							this.send_msg_toseller(request, child_order);
						}
					}
				}
				if ("group".equals(remark2)) {
					this.generate_groupInfos(request, order, "chinabank", "网银在线支付", "");
					mv.addObject("obj", order);
				}
				if (remark2.endsWith("cash")) {
					obj.setPd_status(1);
					obj.setPd_pay_status(2);
					this.predepositService.update(obj);
					//User user = this.userService.getObjById(obj.getPd_user().getId());
					//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), obj.getPd_amount())));//余额都在CRM端保存和增减，故注释掉
					//this.userService.update(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(obj.getPd_amount());
					log.setPd_log_user(obj.getPd_user());
					log.setPd_op_type("充值");
					log.setPd_type("可用预存款");
					log.setPd_log_info("网银在线支付");
					this.predepositLogService.save(log);
					mv = new JModelAndView("success.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
					mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
				}
				if ("gold".equals(remark2)) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);
					this.goldRecordService.update(gold);
					User user = this.userService.getObjById(gold.getGold_user().getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.update(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content("网银在线支付");
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.save(log);
					mv = new JModelAndView("success.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "兑换" + gold.getGold_count() + "金币成功");
					mv.addObject("url", CommUtil.getURL(request) + "/seller/gold_record_list.htm");
				}
				if ("gold".equals(remark2)) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment("bill");
					this.integralGoodsOrderService.update(ig_order);
					List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(ig_order.getGoods_info());
					for (Map map : ig_maps) {
						IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
						//goods.setIg_goods_count(goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
						goods.setIg_exchange_count(
								goods.getIg_exchange_count() + CommUtil.null2Int(map.get("ig_goods_count")));
						this.integralGoodsService.update(goods);
					}
					mv = new JModelAndView("integral_order_finish.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("obj", ig_order);
				}
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "网银在线支付失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");

		}
		return mv;
	}

	/**
	 * paypal回调方法,paypal支付成功了后，调用该方法进行后续处理
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/paypal_return.htm")
	public ModelAndView paypal_return(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("order_finish.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Enumeration en = request.getParameterNames();
		String str = "cmd=_notify-validate";
		while (en.hasMoreElements()) {
			String paramName = (String) en.nextElement();
			String paramValue = request.getParameter(paramName);
			str = str + "&" + paramName + "=" + URLEncoder.encode(paramValue);
		}
		String[] customs = request.getParameter("custom").split(",");
		String remark1 = customs[0];
		String remark2 = customs[1];
		String item_name = request.getParameter("item_name");
		String txnId = request.getParameter("txn_id");
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(remark2) || "group".equals(remark2)) {
			order = this.orderFormService.getObjById(CommUtil.null2Long(remark1.trim()));
		}
		if ("cash".equals(remark2)) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(remark1));
		}
		if ("gold".equals(remark2)) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(remark1));
		}
		if ("integral".equals(remark2)) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(remark1));
		}
		String txn_id = request.getParameter("txn_id");
		//
		// 建议在此将接受到的信息str记录到日志文件中以确认是否收到IPN信息
		// 将信息POST回给PayPal进行验证
		// 设置HTTP的头信息
		// 在Sandbox情况下，设置：
		// URL u = new URL("http://www.sanbox.paypal.com/cgi-bin/webscr");
		// URLConnection uc = u.openConnection();
		// uc.setDoOutput(true);
		// uc.setRequestProperty("Content-Type",
		// "application/x-www-form-urlencoded");
		// PrintWriter pw = new PrintWriter(uc.getOutputStream());
		// pw.println(str);
		// pw.close();
		// // 接受PayPal对IPN回发的回复信息 uc.getOutputStream()
		// BufferedReader in = new BufferedReader(new
		// InputStreamReader(uc.getInputStream()));
		// String res = in.readLine();
		// in.close();
		// logger.info("接受PayPal对IPN回发的回复信息：" + res);

		// 将POST信息分配给本地变量，可以根据您的需要添加
		// 该付款明细所有变量可参考：
		String itemName = request.getParameter("item_name");
		String paymentStatus = request.getParameter("payment_status");
		String paymentAmount = request.getParameter("mc_gross");
		String paymentCurrency = request.getParameter("mc_currency");
		String receiverEmail = request.getParameter("receiver_email");
		String payerEmail = request.getParameter("payer_email");
		// paymentStatus+ ",金额：" + paymentAmount + ",货币种类：" + paymentCurrency+
		// ",paypal支付流水号:" + txnId + ",paypal接收方账号：" + receiverEmail+
		// ",paypal支付方账号" + payerEmail);
		if ("Completed".equals(paymentStatus) || "Pending".equals(paymentStatus)) {
			if ("goods".equals(remark2)) {
				if (order.getOrder_status() < 20 && CommUtil.null2String(order.getTotalPrice()).equals(paymentAmount)) {
					User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
					order.setOrder_status(20);
					order.setPayTime(new Date());
					this.orderFormService.update(order);
					this.update_goods_inventory(order); // 更新商品库存
					OrderFormLog main_ofl = new OrderFormLog();
					main_ofl.setAddTime(new Date());
					main_ofl.setLog_info("网银在线支付");
					main_ofl.setLog_user(buyer);
					main_ofl.setOf(order);
					this.orderFormLogService.save(main_ofl);
					// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
					// 发送邮件短信
					this.send_msg_tobuyer(request, order);
					this.send_msg_toseller(request, order);
					if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
						List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
						for (Map child_map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(child_map.get("order_id")));
							child_order.setOrder_status(20);
							child_order.setPayTime(new Date());
							this.orderFormService.update(child_order);
							this.update_goods_inventory(child_order); // 更新商品库存
//							OrderFormLog child_ofl = new OrderFormLog();
//							child_ofl.setAddTime(new Date());
//							child_ofl.setLog_info("网银在线支付");
//							child_ofl.setLog_user(buyer);
//							child_ofl.setOf(child_order);
//							this.orderFormLogService.save(child_ofl);
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							// 发送邮件短信
							this.send_msg_toseller(request, child_order);
						}
					}
				}
				mv.addObject("obj", order);

			}
			if ("group".equals(remark2)) {
				this.generate_groupInfos(request, order, "paypal", "paypal在线支付", "");
				mv.addObject("obj", order);
			}
			if (remark2.endsWith("cash")) {
				if (obj.getPd_pay_status() < 2) {
					obj.setPd_status(1);
					obj.setPd_pay_status(2);
					this.predepositService.update(obj);
					//User user = this.userService.getObjById(obj.getPd_user().getId());
					//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), obj.getPd_amount())));//余额都在CRM端保存和增减，故注释掉
					//this.userService.update(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(obj.getPd_amount());
					log.setPd_log_user(obj.getPd_user());
					log.setPd_op_type("充值");
					log.setPd_type("可用预存款");
					log.setPd_log_info("Paypal在线支付");
					this.predepositLogService.save(log);
				}
				mv = new JModelAndView("success.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "成功充值：" + obj.getPd_amount());
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
			}
			if ("gold".equals(remark2)) {
				if (gold.getGold_pay_status() < 2) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);
					this.goldRecordService.update(gold);
					User user = this.userService.getObjById(gold.getGold_user().getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.update(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content("Paypal");
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.save(log);
				}
				mv = new JModelAndView("success.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
						1, request, response);
				mv.addObject("op_title", "成功充值金币:" + gold.getGold_count());
				mv.addObject("url", CommUtil.getURL(request) + "/seller/gold_record_list.htm");
			}
			if ("gold".equals(remark2)) {
				if (ig_order.getIgo_status() < 20) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment("paypal");
					this.integralGoodsOrderService.update(ig_order);
					List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(ig_order.getGoods_info());
					for (Map map : ig_maps) {
						IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
						//goods.setIg_goods_count(goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
						goods.setIg_exchange_count(
								goods.getIg_exchange_count() + CommUtil.null2Int(map.get("ig_goods_count")));
						this.integralGoodsService.update(goods);
					}
				}
				mv = new JModelAndView("integral_order_finish.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("obj", ig_order);
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "Paypal支付失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 银联在线支付成功<span style="color:red">前台</span>回调<span style="color:red">（同步）</span>
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/unionpay_return.htm")
	public ModelAndView unionPayReturn(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> backRcvParam = UnionPayService.getReturnParamAndValidate(request);
		String[] reserves = backRcvParam.get("reqReserved").split("&"); // 解析保留域
		// 获取支付来源
		String paySource = reserves[2];
		ModelAndView mv=null;
		
		//pc端支付完成跳转
		if(UnionPayService.PAY_SOURCE_WEB.equals(paySource)){
			 mv = new JModelAndView("order_finish.html",configService.getSysConfig(),
					this.userConfigService.getUserConfig(),1, request, response);
		}
		
		//wap端支付完成跳转
		if(UnionPayService.PAY_SOURCE_WAP.equals(paySource)){
			mv = new JModelAndView("wap/index.html",configService.getSysConfig(),
					this.userConfigService.getUserConfig(),1, request, response);
		}
		
		try {
			this.unionPayBusinessHandler(request, response, mv);
		} catch (PayValidationException e) {
			logger.error("银联在线支付回调前台同步处理方法验证失败！", e);
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "支付回调失败");
			if(UnionPayService.PAY_SOURCE_WEB.equals(paySource)){
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm"); 
			}else if(UnionPayService.PAY_SOURCE_WAP.equals(paySource)){
				mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
			}
		} catch (Exception e) {
			throw e;
		}
		return mv;

	}
	/**
	 * wap端银联在线支付成功<span style="color:red">前台</span>回调<span style="color:red">（同步）</span>
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	
	/**
	@RequestMapping("/wap/unionpay_return.htm")
	public ModelAndView appUnionPayReturn(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("wap/index.html",configService.getSysConfig(),
				this.userConfigService.getUserConfig(),1, request, response);
		
		try {
			this.unionPayBusinessHandler(request, response, mv);
		} catch (PayValidationException e) {
			logger.error("wap端银联在线支付回调前台同步处理方法验证失败！", e);
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "支付回调失败");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		} catch (Exception e) {
			throw e;
		}
		return mv;

	}
	*/
	/**
	 * 银联在线支付成功<span style="color:red">后台</span>回调<span style="color:red">（异步）</span>
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/unionpay_notify.htm")
	public void unionPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			this.unionPayBusinessHandler(request, response, null);
		} catch (PayValidationException e) {
			logger.error("银联在线支付回调后台异步处理方法验证失败！", e);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 银联支付业务订单处理
	 * 
	 * @param request
	 * @param mv
	 * @throws Exception
	 */
	private void unionPayBusinessHandler(HttpServletRequest request, HttpServletResponse response, ModelAndView mv)
			throws Exception {
		// 校验并获得银联回调参数
		Map<String, String> backRcvParam = com.iskyshop.pay.unionpay.UnionPayService.getReturnParamAndValidate(request);
		// 获取各项所需参数
		String trade_no = backRcvParam.get("queryId"); // 银联订单号（交易流水号）
		String[] reserves = backRcvParam.get("reqReserved").split("&"); // 解析保留域
		String[] order_nos = reserves[0].split("-"); // 通过保留域获取订单号
		String txnAmt = backRcvParam.get("txnAmt"); // 获取总金额
		txnAmt = AmountUtils.changeF2Y(txnAmt); // 分转元
		// 商品名称、订单名称
		String order_no = order_nos[2];
		String type = CommUtil.null2String(reserves[1]).trim(); // 交易类型
		String trade_status = request.getParameter("respCode"); // 交易状态
		// 获取支付来源
		String paySource = reserves[2];
		// 获取支付日志信息
		String payLogStr = "中国银联在线支付";
		if (UnionPayService.PAY_SOURCE_WAP.equals(paySource)) {
			payLogStr = "中国银联手机网页支付";
		} else if (UnionPayService.PAY_SOURCE_MOBILE.equals(paySource)) {
			payLogStr = "中国银联App支付";
		}
		// 如果是WAP支付则重新定向默认跳转页面为WAP会员中心
		if (null != mv && UnionPayService.PAY_SOURCE_WAP.equals(paySource)) {
			String retPage="/wap/buyer/center.htm";
			int typePage=1;
			if(CouponActivityComm.isCouponActivityTime()){
				typePage=2;
				retPage="redirect:/wap/jump_coupon_share.htm";
			}
			
			mv = new JModelAndView(retPage, configService.getSysConfig(),
					this.userConfigService.getUserConfig(), typePage, request, response);
		}
		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(type)) {
			main_order = this.orderFormService.getObjById(CommUtil.null2Long(order_no));
		}
		if ("cash".equals(type)) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(order_no));
		}
		if ("gold".equals(type)) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(order_no));
		}
		if ("integral".equals(type)) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(order_no));
		}
		if ("group".equals(type)) {
			main_order = this.orderFormService.getObjById(CommUtil.null2Long(order_no));
		}
		// 开始处理业务订单
		if ("goods".equals(type)) {
			if ("00".equals(trade_status)) {
				User buyer = this.userService.getObjById(CommUtil.null2Long(main_order.getUser_id()));
				if (main_order.getOrder_status() < 20) { // 异步没有出来订单，则同步处理订单
					main_order.setOrder_status(20);
					main_order.setOut_order_id(trade_no);
					main_order.setPayTime(new Date());
					this.orderFormService.update(main_order);
					// 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
					this.update_goods_inventory(main_order);
					OrderFormLog main_ofl = new OrderFormLog();
					main_ofl.setAddTime(new Date());
					main_ofl.setLog_info(payLogStr);
					main_ofl.setLog_user(buyer);
					main_ofl.setOf(main_order);
					this.orderFormLogService.save(main_ofl);
					// 主订单付款成功，发送邮件提示
					// 向加盟商家发送付款成功邮件提示，自营商品无需发送邮件提示
					try {
						this.send_msg_tobuyer(request, main_order);
					} catch (Exception ex) {
						logger.error("In the aplipay_return function: Payment for user " + buyer.getUserName()
								+ " has been completed successfully, But sending message to the user fails." + ex);
					}
					try {
						this.send_msg_toseller(request, main_order);
					} catch (Exception ex) {
						logger.error("In the aplipay_return function: Payment for user " + buyer.getUserName()
								+ " has been completed successfully, But sending message to the corresponding seller fails."
								+ ex);
					}
					// 子订单操作
					if (main_order.getOrder_main() == 1
							&& !"".equals(CommUtil.null2String(main_order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
						List<Map> maps = this.orderFormTools.queryGoodsInfo(main_order.getChild_order_detail());
						for (Map child_map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(child_map.get("order_id")));
							if (child_order.getOrder_status() != 20) {
								child_order.setOrder_status(20);
								child_order.setOut_order_id(main_order.getOut_order_id());
								child_order.setPayTime(new Date());
								child_order.setPayment(main_order.getPayment());
								this.orderFormService.update(child_order);
								// 子订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
								this.update_goods_inventory(child_order);
//								OrderFormLog child_ofl = new OrderFormLog();
//								child_ofl.setAddTime(new Date());
//								child_ofl.setLog_info(payLogStr);
//								child_ofl.setLog_user(buyer);
//								child_ofl.setOf(child_order);
//								this.orderFormLogService.save(child_ofl);
								// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
								// 付款成功，发送邮件提示
								this.send_msg_toseller(request, child_order);
							}
						}
					}
                    // 支付成功发送同步订单事件
                    this.synchronizeOrderPublisher.synchronizeOrder(main_order.getId());
				}
				if (null != mv) {
					mv.addObject("all_price",
							this.orderFormTools.query_order_price(CommUtil.null2String(main_order.getId())));
					mv.addObject("obj", main_order);
				}
			}
		}
		if ("group".equals(type)) {
			if ("00".equals(trade_status)) {
				if (main_order.getOrder_status() != 20) { // 异步没有出来订单，则同步处理订单
					this.generate_groupInfos(request, main_order, paySource, payLogStr, trade_no);
				}
				if (null != mv) {
					mv.addObject("all_price", main_order.getTotalPrice());
					mv.addObject("obj", main_order);
				}
			}
		}
		if ("cash".equals(type)) {
			if ("00".equals(trade_status)) {
				if (obj.getPd_pay_status() != 2) { // 异步没有处理该充值业务，则同步处理一下
					// 银联在线回调成功后调用BCP进行生活卡充值
					User user = this.userService.getObjById(obj.getPd_user().getId());
					ResultDTO resultDTO = feeManageConnector.recharge(user.getCustId(), txnAmt, obj.getPd_sn());
					if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
                        obj.setOut_order_id(trade_no);
						this.predepositService.update(obj);
						//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), obj.getPd_amount())));//余额都在CRM端保存和增减，故注释掉
						//this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info(payLogStr + "_充值成功");
						this.predepositLogService.save(log);

						if (null != mv) {
							// 如果是WAP支付则重新定向默认跳转页面为WAP会员中心
							mv = new JModelAndView(
									UnionPayService.PAY_SOURCE_WAP.equals(paySource) ? "/wap/buyer/center.htm"
											: "success.html",
									configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
									response);
							if (UnionPayService.PAY_SOURCE_WEB.equals(paySource)) {
								mv.addObject("op_title", "恭喜您，成功充值" + obj.getPd_amount() + "元");
								mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
							}
						}
					} else {
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info(payLogStr + "_充值失败_" + resultDTO.getMsg());
						this.predepositLogService.save(log);

						if (null != mv) {
							mv = new JModelAndView(
									UnionPayService.PAY_SOURCE_WAP.equals(paySource) ? "/wap/error.html" : "error.html",
									configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
									response);
							mv.addObject("op_title", resultDTO.getMsg() + ",充值失败," + obj.getPd_amount() + "元");
							mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
						}
					}
				} else {
					// 如果是WAP支付则重新定向默认跳转页面为WAP会员中心
					mv = new JModelAndView(
							UnionPayService.PAY_SOURCE_WAP.equals(paySource) ? "/wap/buyer/center.htm" : "success.html",
							configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
					if (UnionPayService.PAY_SOURCE_WEB.equals(paySource)) {
						mv.addObject("op_title", "恭喜您，成功充值" + obj.getPd_amount() + "元");
						mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
					}
				}
			}
		}
		if ("gold".equals(type)) {
			if ("00".equals(trade_status)) {
				if (gold.getGold_pay_status() != 2) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);
					this.goldRecordService.update(gold);
					User user = this.userService.getObjById(gold.getGold_user().getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.update(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content(payLogStr);
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.save(log);
				}
				if (null != mv) {
					mv = new JModelAndView("success.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "兑换" + gold.getGold_count() + "金币成功");
					mv.addObject("url", CommUtil.getURL(request) + "/seller/gold_record_list.htm");
				}
			}
		}
		if ("integral".equals(type)) {
			if ("00".equals(trade_status)) {
				if (ig_order.getIgo_status() < 20) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment(paySource);
					this.integralGoodsOrderService.update(ig_order);
					List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(ig_order.getGoods_info());
					for (Map map : ig_maps) {
						IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
						//goods.setIg_goods_count(goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
						goods.setIg_exchange_count(
								goods.getIg_exchange_count() + CommUtil.null2Int(map.get("ig_goods_count")));
						this.integralGoodsService.update(goods);
					}
				}
				if (null != mv) {
					mv = new JModelAndView(
							UnionPayService.PAY_SOURCE_WAP.equals(paySource) ? "/wap/integral_order_finish.html"
									: "integral_order_finish.html",
							configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("obj", ig_order);
				}
			}
		}
	}

	/**
	 * chinapay银联在线支付成功<span style="color:red">前台</span>回调<span style="color:red">（同步）</span>
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/chinapay_return.htm")
	public ModelAndView chinaPayReturn(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new JModelAndView("order_finish.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		try {
			mv = this.chinaPayBusinessHandler(request, response, mv);
		} catch (PayValidationException e) {
			logger.error("chinaPay银联在线支付回调前台同步处理方法验证失败！", e);
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
					request, response);
			mv.addObject("op_title", "支付回调失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		} catch (Exception e) {
			throw e;
		}
		return mv;

	}

	/**
	 * chinapay银联在线支付成功<span style="color:red">后台</span>回调<span style="color:red">（异步）</span>
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/chinapay_notify.htm")
	public void chinaPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			this.chinaPayBusinessHandler(request, response, null);
		} catch (PayValidationException e) {
			logger.error("chinapay银联在线支付回调后台异步处理方法验证失败！", e);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * chinapay银联支付业务订单处理
	 * 
	 * @param request
	 * @param mv
	 * @throws Exception
	 */
	private ModelAndView chinaPayBusinessHandler(HttpServletRequest request, HttpServletResponse response, ModelAndView mv)
			throws Exception {
		Map<String, String[]> retMap = request.getParameterMap();
		String priv1 = retMap.get("Priv1")[0];
		String[] privArr = priv1.split(ChinaPayCommon.PAY_SEPARATOR);

		String bank_gate_id = retMap.get("GateId")[0]; // 网关号
		String trade_no = retMap.get("orderno")[0]; // 银联订单号（交易流水号）
		String txnAmt = retMap.get("amount")[0]; // 获取总金额
		txnAmt = AmountUtils.changeF2Y(txnAmt); // 分转元
		String[] order_nos = privArr[0].split("-"); // 通过保留域获取订单号
		String order_no = order_nos[2];// 商品名称、订单名称 id
		String type = CommUtil.null2String(privArr[1]).trim(); // 交易类型
		String trade_status = retMap.get("status")[0]; // 交易状态
		String paySource = privArr[2];// 获取支付方式来源
		
		String transdate=retMap.get("transdate")[0];
		
		String chinapayInfo="银联在线支付"+paySource;
		
		String ordId="";//商户生成的订单号

		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(type)) {
			main_order = this.orderFormService.getObjById(CommUtil.null2Long(order_no));
			ordId=main_order.getTrade_order_id();
		}
		if ("cash".equals(type)) {
			obj = this.predepositService.getObjById(CommUtil.null2Long(order_no));
			ordId=obj.getTrade_order_id();
		}
		if ("gold".equals(type)) {
			gold = this.goldRecordService.getObjById(CommUtil.null2Long(order_no));
			ordId=gold.getTrade_order_id();
		}
		if ("integral".equals(type)) {
			ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(order_no));
			ordId=ig_order.getTrade_order_id();
		}
		if ("group".equals(type)) {
			main_order = this.orderFormService.getObjById(CommUtil.null2Long(order_no));
			ordId=main_order.getTrade_order_id();
		}
		
		boolean transFlag = ChinaPayCommon.getVerifyTransResponse(retMap.get("merid")[0], ordId,
				retMap.get("amount")[0], retMap.get("currencycode")[0], retMap.get("transdate")[0],
				retMap.get("transtype")[0], retMap.get("status")[0], retMap.get("checkvalue")[0]);
		if (!transFlag) {
			throw new PayValidationException("chinapay应答数据的签名验证结果[失败]。");
		}
		
		// 开始处理业务订单
		if ("goods".equals(type)) {
			if ("1001".equals(trade_status)) {
				User buyer = this.userService.getObjById(CommUtil.null2Long(main_order.getUser_id()));
				if (main_order.getOrder_status() < 20) { // 异步没有出来订单，则同步处理订单
					main_order.setOrder_status(20);
					main_order.setOut_order_id(trade_no);
					
					main_order.setBank_gate_id(bank_gate_id);
					main_order.setRettansdate(transdate);
					
					main_order.setPayTime(new Date());
					this.orderFormService.update(main_order);
					// 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
					this.update_goods_inventory(main_order);
					OrderFormLog main_ofl = new OrderFormLog();
					main_ofl.setAddTime(new Date());
					main_ofl.setLog_info(chinapayInfo);
					main_ofl.setLog_user(buyer);
					main_ofl.setOf(main_order);
					this.orderFormLogService.save(main_ofl);
					// 主订单付款成功，发送邮件提示
					// 向加盟商家发送付款成功邮件提示，自营商品无需发送邮件提示
					try {
						this.send_msg_tobuyer(request, main_order);
					} catch (Exception ex) {
						logger.error("In the chinapay_return function: Payment for user " + buyer.getUserName()
								+ " has been completed successfully, But sending message to the user fails." + ex);
					}
					try {
						this.send_msg_toseller(request, main_order);
					} catch (Exception ex) {
						logger.error("In the chinapay_return function: Payment for user " + buyer.getUserName()
								+ " has been completed successfully, But sending message to the corresponding seller fails."
								+ ex);
					}
					// 子订单操作
					if (main_order.getOrder_main() == 1
							&& !"".equals(CommUtil.null2String(main_order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
						List<Map> maps = this.orderFormTools.queryGoodsInfo(main_order.getChild_order_detail());
						for (Map child_map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(child_map.get("order_id")));
							if (child_order.getOrder_status() != 20) {
								child_order.setOrder_status(20);
								child_order.setOut_order_id(main_order.getOut_order_id());
								child_order.setPayTime(new Date());
								child_order.setPayment(main_order.getPayment());
								this.orderFormService.update(child_order);
								// 子订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
								this.update_goods_inventory(child_order);
//								OrderFormLog child_ofl = new OrderFormLog();
//								child_ofl.setAddTime(new Date());
//								child_ofl.setLog_info(chinapayInfo);
//								child_ofl.setLog_user(buyer);
//								child_ofl.setOf(child_order);
//								this.orderFormLogService.save(child_ofl);
								// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
								// 付款成功，发送邮件提示
								this.send_msg_toseller(request, child_order);
							}
						}
					}
                    // 支付成功发送同步订单事件
                    this.synchronizeOrderPublisher.synchronizeOrder(main_order.getId());
				}
				if (null != mv) {
					mv.addObject("all_price",
							this.orderFormTools.query_order_price(CommUtil.null2String(main_order.getId())));
					mv.addObject("obj", main_order);
				}
			}
		}
		if ("group".equals(type)) {
			if ("1001".equals(trade_status)) {
				if (main_order.getOrder_status() != 20) { // 异步没有出来订单，则同步处理订单
					
					main_order.setBank_gate_id(bank_gate_id);
					main_order.setRettansdate(transdate);
					
					this.generate_groupInfos(request, main_order, paySource, chinapayInfo, trade_no);
				}
				if (null != mv) {
					mv.addObject("all_price", main_order.getTotalPrice());
					mv.addObject("obj", main_order);
				}
			}
		}
		if ("cash".equals(type)) {
			if ("1001".equals(trade_status)) {
				if (obj.getPd_pay_status() != 2) { // 异步没有处理该充值业务，则同步处理一下
					// 银联在线回调成功后调用BCP进行生活卡充值
					User user = this.userService.getObjById(obj.getPd_user().getId());
					ResultDTO resultDTO = feeManageConnector.recharge(user.getCustId(), txnAmt, obj.getPd_sn());
					if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);

						obj.setOut_order_id(trade_no);
						obj.setBank_gate_id(bank_gate_id);
						obj.setRettansdate(transdate);

						this.predepositService.update(obj);
						//user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), obj.getPd_amount())));//余额都在CRM端保存和增减，故注释掉
						//this.userService.update(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info(chinapayInfo+"_充值成功");
						this.predepositLogService.save(log);

						if (null != mv) {
							
							mv = new JModelAndView("success.html",
									configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
									response);
							
							mv.addObject("op_title", "恭喜您，成功充值" + obj.getPd_amount() + "元");
							mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
							
						}
					} else {
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info(chinapayInfo+"_充值失败_" + resultDTO.getMsg());
						this.predepositLogService.save(log);

						if (null != mv) {
							mv = new JModelAndView("error.html",
									configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
									response);
							mv.addObject("op_title", resultDTO.getMsg() + ",充值失败," + obj.getPd_amount() + "元");
							mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
						}
					}
				} else {
					if (null != mv) {
						mv = new JModelAndView("success.html",
								configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
						
						mv.addObject("op_title", "恭喜您，成功充值" + obj.getPd_amount() + "元");
						mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list.htm");
					}
				}
			}
		}
		if ("gold".equals(type)) {
			if ("1001".equals(trade_status)) {
				if (gold.getGold_pay_status() != 2) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);

					gold.setOut_order_id(trade_no);
					gold.setBank_gate_id(bank_gate_id);
					gold.setRettansdate(transdate);

					this.goldRecordService.update(gold);
					User user = this.userService.getObjById(gold.getGold_user().getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.update(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content(chinapayInfo);
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.save(log);
				}
				if (null != mv) {
					mv = new JModelAndView("success.html", configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "兑换" + gold.getGold_count() + "金币成功");
					mv.addObject("url", CommUtil.getURL(request) + "/seller/gold_record_list.htm");
				}
			}
		}
		if ("integral".equals(type)) {
			if ("1001".equals(trade_status)) {
				if (ig_order.getIgo_status() < 20) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment(paySource);

					ig_order.setOut_order_id(trade_no);
					ig_order.setBank_gate_id(bank_gate_id);
					ig_order.setRettansdate(transdate);

					this.integralGoodsOrderService.update(ig_order);
					List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(ig_order.getGoods_info());
					for (Map map : ig_maps) {
						IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
						//goods.setIg_goods_count(goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
						goods.setIg_exchange_count(
								goods.getIg_exchange_count() + CommUtil.null2Int(map.get("ig_goods_count")));
						this.integralGoodsService.update(goods);
					}
				}
				if (null != mv) {
					mv = new JModelAndView("integral_order_finish.html",
							configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("obj", ig_order);
				}
			}
		}
		return mv;
	}

	private void generate_groupInfos(HttpServletRequest request, OrderForm order, String mark, String pay_info,
			String trade_no) throws Exception {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		if (order.getOrder_status() < 20) {
			order.setOrder_status(20);
			order.setOut_order_id(trade_no);
			order.setPayTime(new Date());
			// 生活团购订单付款时增加退款时效
			if (order.getOrder_cat() == 2) {
				Calendar ca = Calendar.getInstance();
				ca.add(ca.DATE, this.configService.getSysConfig().getGrouplife_order_return());
				SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String latertime = bartDateFormat.format(ca.getTime());
				order.setReturn_shipTime(CommUtil.formatDate(latertime));
			}
			this.orderFormService.update(order);
			
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info(pay_info);
			User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
			ofl.setLog_user(buyer);
			ofl.setOf(order);
			this.orderFormLogService.save(ofl);

			if (order.getOrder_cat() == 2) {
				Map map = this.orderFormTools.queryGroupInfo(order.getGroup_info());
				int count = CommUtil.null2Int(map.get("goods_count").toString());
				String goods_id = map.get("goods_id").toString();
				GroupLifeGoods goods = this.groupLifeGoodsService.getObjById(CommUtil.null2Long(goods_id));
				goods.setSelled_count(goods.getSelled_count() + CommUtil.null2Int(count));
				this.groupLifeGoodsService.update(goods);

				// 更新lucene索引
				String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
						+ File.separator + "grouplifegoods";
				File file = new File(goods_lucene_path);
				if (!file.exists()) {
					CommUtil.createFolder(goods_lucene_path);
				}
				LuceneUtil lucene = LuceneUtil.instance();
				lucene.setIndex_path(goods_lucene_path);
				lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateLifeGoodsIndex(goods));
				
				Map pay_params = new HashMap();
				pay_params.put("mark", mark);
				List<Payment> payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark",
						pay_params, -1, -1);
				List<String> code_list = new ArrayList(); // 存放团购消费码
				String codes = "";
				for (int i = 0;i < count;i++) {
					GroupInfo info = new GroupInfo();
					info.setAddTime(new Date());
					info.setLifeGoods(goods);
					info.setPayment(payments.get(0));
					info.setOrder_id(order.getId());
					info.setUser_mobile(user.getMobile()); //不同支付方式保存手机号码到团购码信息表  
					info.setUser_id(buyer.getId());
					info.setUser_name(buyer.getUserName());
					info.setGroup_sn(buyer.getId() + CommUtil.formatTime("yyyyMMddHHmmss" + i, new Date()));
					Calendar ca2 = Calendar.getInstance();
					ca2.add(ca2.DATE, this.configService.getSysConfig().getGrouplife_order_return());
					SimpleDateFormat bartDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String latertime2 = bartDateFormat2.format(ca2.getTime());
					info.setRefund_Time(CommUtil.formatDate(latertime2));
					this.groupInfoService.save(info);
					codes = codes + info.getGroup_sn() + " ";
					code_list.add(info.getGroup_sn());
				}
				
				// 如果为运营商发布的团购则进行结算日志生成
				if (order.getOrder_form() == 0) {
					Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
					store.setStore_sale_amount(
							BigDecimal.valueOf(CommUtil.add(order.getGoods_amount(), store.getStore_sale_amount()))); // 店铺本次结算总销售金额
					store.setStore_commission_amount(BigDecimal
							.valueOf(CommUtil.add(order.getCommission_amount(), store.getStore_commission_amount()))); // 店铺本次结算总佣金
					store.setStore_payoff_amount(
							BigDecimal.valueOf(CommUtil.add(order.getTotalPrice(), store.getStore_payoff_amount()))); // 店铺本次结算总佣金
					this.storeService.update(store);
					
					PayoffLog plog = new PayoffLog();
					plog.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
					plog.setPl_info("团购码生成成功");
					plog.setAddTime(new Date());
					plog.setSeller(store.getUser());
					plog.setO_id(CommUtil.null2String(order.getId()));
					plog.setOrder_id(order.getOrder_id().toString());
					plog.setCommission_amount(BigDecimal.valueOf(CommUtil.null2Double("0.00"))); // 该订单总佣金费用
					plog.setGoods_info(order.getGroup_info());
					plog.setOrder_total_price(order.getTotalPrice()); // 该订单总商品金额
					plog.setTotal_amount(this.orderFormService.getPayoffAmount(order)); // 该订单应结算金额：结算金额=订单总商品金额-总佣金费用
					this.payoffservice.save(plog);
				}
				
				// 增加系统总销售金额、总佣金
				SysConfig sc = this.configService.getSysConfig();
				sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(order.getGoods_amount(), sc.getPayoff_all_sale())));
				sc.setPayoff_all_commission(
						BigDecimal.valueOf(CommUtil.add(order.getCommission_amount(), sc.getPayoff_all_commission())));
				this.configService.update(sc);
				
				// 发送系统站内信给买家
				String msg_content = "恭喜您成功购买团购" + map.get("goods_name") + ",团购消费码分别为：" + codes
						+ "您可以到用户中心-我的生活购中查看消费码的使用情况";
				Message tobuyer_msg = new Message();
				tobuyer_msg.setAddTime(new Date());
				tobuyer_msg.setStatus(0);
				tobuyer_msg.setType(0);
				tobuyer_msg.setContent(msg_content);
				tobuyer_msg.setFromUser(this.userService.getObjByProperty(null, "userName", "admin"));
				tobuyer_msg.setToUser(buyer);
				this.messageService.save(tobuyer_msg);
				
				// 发送系统站内信给卖家
//				Message toSeller_msg = new Message();
//				toSeller_msg.setAddTime(new Date());
//				toSeller_msg.setStatus(0);
//				toSeller_msg.setType(0);
//				toSeller_msg.setContent(buyer.getUsername());
//				toSeller_msg.setFromUser(this.userService.getObjByProperty(null, "userName", "admin"));
//				toSeller_msg.setToUser(goods.getUser());
//				this.messageService.save(toSeller_msg);
				
				// 付款成功，发送短信团购消费码
				this.send_groupInfo_sms(request, order, buyer.getMobile(), "sms_tobuyer_online_ok_send_groupinfo", code_list,
						buyer.getId().toString(), goods.getUser().getId().toString());
			}
			this.send_msg_tobuyer(request, order);
		}
	}
	
	private void update_goods_inventory(OrderForm order) {
		// 付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(CommUtil.null2String(order.getId()));
		// 更新订单中组合套装商品信息
		List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
		for (Map map_combin : maps) {
			if (map_combin.get("combin_suit_info") != null) {
				Map suit_info = Json.fromJson(Map.class, CommUtil.null2String(map_combin.get("combin_suit_info")));
				int combin_count = CommUtil.null2Int(suit_info.get("suit_count"));
				List<Map> combin_goods = this.orderFormTools.query_order_suitgoods(suit_info);
				for (Map temp_goods : combin_goods) { // 更新组合套装中其他商品信息，将套装主商品排除，主商品在普通商品更新中更新信息
					for (Goods temp : goods_list) {
						if (!CommUtil.null2String(temp_goods.get("id")).equals(temp.getId().toString())) {
							Goods goods = this.goodsService.getObjById(CommUtil.null2Long(temp_goods.get("id")));
							goods.setGoods_salenum(goods.getGoods_salenum() + combin_count);
							this.goodsService.update(goods);
						}
					}
				}
			}
		}
		// 普通商品更新信息
		for (Goods goods : goods_list) {
			int goods_count = this.orderFormTools.queryOfGoodsCount(CommUtil.null2String(order.getId()),
					CommUtil.null2String(goods.getId()));
			if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						gg.setGg_selled_count(gg.getGg_selled_count() + goods_count);
						this.groupGoodsService.update(gg);
						
						// 更新lucene索引
						String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence"
								+ File.separator + "groupgoods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGroupGoodsIndex(gg));
						break;
					}
				}
			}else if(!"".equals(CommUtil.null2String(order.getSeckill_info()))){ //增加秒杀商品销量
				Map map = Json.fromJson(Map.class, order.getSeckill_info());
				SeckillGoods seckillGoods = seckillGoodsService.getObjById(Long.valueOf(String.valueOf(map.get("seckill_goods_id"))));
				seckillGoods.setGg_selled_count(seckillGoods.getGg_selled_count()+goods_count);
				seckillGoodsService.update(seckillGoods);
			}
//			if("".equals(CommUtil.null2String(order.getSeckill_info()))){
				goods.setGoods_salenum(goods.getGoods_salenum() + goods_count);
				this.goodsService.update(goods);
				
				// 更新lucene索引
				String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
						+ "goods";
				File file = new File(goods_lucene_path);
				if (!file.exists()) {
					CommUtil.createFolder(goods_lucene_path);
				}
				LuceneUtil lucene = LuceneUtil.instance();
				lucene.setIndex_path(goods_lucene_path);
				lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
				
				// 商品日志
				GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(goods.getId());
				todayGoodsLog.setGoods_salenum(todayGoodsLog.getGoods_salenum() + goods_count);
				Map<String, Integer> logordermap = (Map<String, Integer>) Json.fromJson(todayGoodsLog.getGoods_order_type());
				String ordertype = order.getOrder_type();
				if (logordermap.containsKey(ordertype)) {
					logordermap.put(ordertype, logordermap.get(ordertype) + goods_count);
				} else {
					logordermap.put(ordertype, goods_count);
				}
				todayGoodsLog.setGoods_order_type(Json.toJson(logordermap, JsonFormat.compact()));
				Map<String, Integer> logspecmap = (Map<String, Integer>) Json.fromJson(todayGoodsLog.getGoods_sale_info());
				List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools.queryOfGoodsGsps(CommUtil.null2String(order.getId()),
						CommUtil.null2String(goods.getId()));
				String spectype = "";
				for (GoodsSpecProperty gsp : temp_gsp_list) {
					spectype += gsp.getSpec().getName() + ":" + gsp.getValue() + " ";
				}
				if (logspecmap.containsKey(spectype)) {
					logspecmap.put(spectype, logspecmap.get(spectype) + goods_count);
				} else {
					logspecmap.put(spectype, goods_count);
				}
				todayGoodsLog.setGoods_sale_info(Json.toJson(logspecmap, JsonFormat.compact()));
				this.goodsLogService.update(todayGoodsLog);
//			}
		}
	}

	/**
	 * 发送短信团购消费码
	 * 
	 * @param request
	 * @param order
	 * @param mobile
	 * @param mark
	 * @param codes
	 * @param buyer_id
	 * @param seller_id
	 * @throws Exception
	 */
	private void send_groupInfo_sms(HttpServletRequest request, OrderForm order, String mobile, String mark,
			List<String> codes, String buyer_id, String seller_id) throws Exception {
		Template template = this.templateService.getObjByProperty(null, "mark", mark);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < codes.size(); i++) {
			sb.append(codes.get(i) + ",");
		}
		String code = sb.toString();
		if (template != null && template.isOpen()) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			context.setVariable("buyer", this.userService.getObjById(CommUtil.null2Long(buyer_id)));
			context.setVariable("seller", this.userService.getObjById(CommUtil.null2Long(seller_id)));
			context.setVariable("config", this.configService.getSysConfig());
			context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", CommUtil.getURL(request));
			context.setVariable("order", order);
			context.setVariable("code", code);
			Expression ex = exp.parseExpression(template.getContent(), new SpelTemplate());
			String content = ex.getValue(context, String.class);
			try {
				this.msgTools.sendSMS(mobile, content);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 在线支付回调后，向买家、商家发送短信、邮件提醒订单在线付款成功！
	 * 
	 * @param request
	 * @param order
	 * @throws Exception
	 */
	private void send_msg_tobuyer(HttpServletRequest request, OrderForm order) throws Exception {
		User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
		if (order.getOrder_form() == 0) {
			Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
			User seller = store.getUser();
			this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_tobuyer_online_pay_ok_notify", buyer.getEmail(),
					null, CommUtil.null2String(order.getId()), order.getStore_id());
			this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_tobuyer_online_pay_ok_notify", buyer.getMobile(),
					null, CommUtil.null2String(order.getId()), order.getStore_id());
		} else {
			this.msgTools.sendEmailFree(CommUtil.getURL(request), "email_tobuyer_online_pay_ok_notify", buyer.getEmail(),null,
					CommUtil.null2String(order.getId()));
			this.msgTools.sendSmsFree(CommUtil.getURL(request), "sms_tobuyer_online_pay_ok_notify", buyer.getMobile(), null,
					CommUtil.null2String(order.getId()));
		}
	}

	/**
	 * 在线支付回调后，向买家、商家发送短信、邮件提醒订单在线付款成功！
	 * 
	 * @param request
	 * @param order
	 * @throws Exception
	 */
	private void send_msg_toseller(HttpServletRequest request, OrderForm order) throws Exception {
		User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
		if (order.getOrder_form() == 0) {
			Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
			User seller = store.getUser();
			this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_toseller_online_pay_ok_notify", seller.getEmail(),
					null, CommUtil.null2String(order.getId()), order.getStore_id());
			this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_online_pay_ok_notify", seller.getMobile(),
					null, CommUtil.null2String(order.getId()), order.getStore_id());
		}
	}

}
