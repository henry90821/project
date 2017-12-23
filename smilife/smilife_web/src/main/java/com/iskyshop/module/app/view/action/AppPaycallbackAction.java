package com.iskyshop.module.app.view.action;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
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

import com.iskyshop.core.tools.CommUtil;
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
import com.iskyshop.msg.MsgTools;
import com.iskyshop.msg.SpelTemplate;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.smilife.bcp.dto.common.EInterfaceState;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.FeeManageConnector;

/**
 * 
 * <p>
 * Title: MobilePayViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端订单支付回调接口控制器
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
 * @author hezeng
 * 
 * @date 2014-8-18
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppPaycallbackAction {

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
	private IGoodsLogService goodsLogService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private FeeManageConnector feeManageConnector;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;

	/**
	 * 手机端网页支付宝同步回调地址，当手机端支付成功后调用该接口修改订单信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/app/alipay_return.htm")
	public void app_alipay_return(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map json_map = new HashMap();
		int code = 100;// 100回调成功，-100回调失败
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		// 商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
		// 商户快捷支付秘钥，
		String private_key = new String(request.getParameter("private").getBytes("ISO-8859-1"), "UTF-8");
		String order_nos[] = out_trade_no.split("-");
		String order_no = order_nos[2];
		OrderForm main_order = this.orderFormService.getObjById(CommUtil.null2Long(order_no));
		Payment payment = this.getPaymentbyMark("alipay_app");
		User buyer = this.userService.getObjById(CommUtil.null2Long(main_order.getUser_id()));
		if (private_key.equals(payment.getApp_private_key())) { // 验证成功
			if (main_order.getOrder_status() < 20) { // 异步没有出来订单，则同步处理订单
				//更新主订单状态
				main_order.setOrder_status(20);
				main_order.setPayTime(new Date());
				main_order.setPayment(payment);
				main_order.setOut_order_id(trade_no);
				this.orderFormService.update(main_order);
				
				// 记录主订单日志
				OrderFormLog main_ofl = new OrderFormLog();
				main_ofl.setAddTime(new Date());
				main_ofl.setLog_info("支付宝App在线支付");
				main_ofl.setLog_user(buyer);
				main_ofl.setOf(main_order);
				this.orderFormLogService.save(main_ofl);
				
				// 子订单操作
				if (main_order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(main_order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
					List<Map> maps = this.orderFormTools.queryGoodsInfo(main_order.getChild_order_detail());
					for (Map child_map : maps) {
						OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map
								.get("order_id")));
						child_order.setOrder_status(20);
						child_order.setPayTime(new Date());
						child_order.setPayment(payment);
						child_order.setOut_order_id(main_order.getOut_order_id());
						this.orderFormService.update(child_order);
						
//						OrderFormLog child_ofl = new OrderFormLog();
//						child_ofl.setAddTime(new Date());
//						child_ofl.setLog_info("支付宝在线支付");
//						child_ofl.setLog_user(buyer);
//						child_ofl.setOf(child_order);
//						this.orderFormLogService.save(child_ofl);
						
						// 付款成功，发送邮件提示
						// 向加盟商家发送付款成功短信提示，当订单为平台订单时，使用平台免费短信邮件接口
						this.send_msg_toseller(request, child_order);
					}
				}
				
				// 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存,发送验证码
				if (main_order.getOrder_cat() == 2) { // 生活购订单,发送团购码
					this.generate_groupInfos(request, main_order, "alipay_app", "支付宝App在线支付", out_trade_no);
				} else { // 普通商品订单
					this.update_goods_inventory(main_order);
				}
				
				// 主订单付款成功，发送邮件提示
				// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
				this.send_msg_tobuyer(request, main_order);
				this.send_msg_toseller(request, main_order);
				
				code = 100;
			} else { // 已经支付
				code = -100;// 订单已经支付
			}
		} else {
			code = -100;// 订单已经支付
		}
		json_map.put("code", code);
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 手机端网页支付宝异步回调地址
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/app/alipay_notify.htm")
	public void app_alipay_notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		// 商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
		// 交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
		String order_nos[] = out_trade_no.split("-");
		String order_no = order_nos[2];
		String type = order_nos[3];
		String total_fee = request.getParameter("total_fee");
		Predeposit obj = null;
		OrderForm main_order = this.orderFormService.getObjById(CommUtil.null2Long(order_no));
		
//		User buyer = this.userService.getObjById(CommUtil.null2Long(main_order.getUser_id()));
		if ("TRADE_FINISHED".equals(trade_status) || "TRADE_SUCCESS".equals(trade_status)) { // 验证成功
			if ("cash".equals(type)) {
				obj = this.predepositService.getObjById(CommUtil.null2Long(order_no));
				if (obj.getPd_pay_status() < 2) {
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

			} else if (main_order.getOrder_status() < 20) { // 异步没有出来订单，则同步处理订单
				//更新主订单状态
				main_order.setOrder_status(20);
				main_order.setPayTime(new Date());
				Payment payment = this.getPaymentbyMark("alipay_app");
				main_order.setPayment(payment);
				main_order.setOut_order_id(trade_no);
				this.orderFormService.update(main_order);
				
				// 记录主订单日志
				OrderFormLog main_ofl = new OrderFormLog();
				main_ofl.setAddTime(new Date());
				main_ofl.setLog_info("支付宝App在线支付");
				User main_buyer = this.userService.getObjById(CommUtil.null2Long(main_order.getUser_id()));
				main_ofl.setLog_user(main_buyer);
				main_ofl.setOf(main_order);
				this.orderFormLogService.save(main_ofl);
				
				// 子订单操作
				if (main_order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(main_order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
					List<Map> maps = this.orderFormTools.queryGoodsInfo(main_order.getChild_order_detail());
					for (Map child_map : maps) {
						//更新子订单状态
						OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map
								.get("order_id")));
						child_order.setOrder_status(20);
						child_order.setPayTime(new Date());
						child_order.setPayment(payment);
						child_order.setOut_order_id(main_order.getOut_order_id());
						this.orderFormService.update(child_order);
						
						// 记录子订单日志
//						OrderFormLog ofl = new OrderFormLog();
//						ofl.setAddTime(new Date());
//						ofl.setLog_info("支付宝在线支付");
//						ofl.setLog_user(buyer);
//						ofl.setOf(child_order);
//						this.orderFormLogService.save(ofl);
						
						// 付款成功，发送邮件提示
						// 向加盟商家发送付款成功短信提示，当订单为平台订单时，使用平台免费短信邮件接口
						this.send_msg_toseller(request, child_order);
					}
				}
				
				// 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存,发送验证码
				if (main_order.getOrder_cat() == 2) { // 生活购订单
					this.generate_groupInfos(request, main_order, "alipay_app", "支付宝App在线支付", out_trade_no);
				} else { // 普通商品订单
					this.update_goods_inventory(main_order);
				}
				
				
				// 主订单付款成功，发送邮件提示
				// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
				this.send_msg_tobuyer(request, main_order);
				this.send_msg_toseller(request, main_order);
				
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
			} else { // 已经支付
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
		} else { // 验证失败
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

	@RequestMapping("/app/integral_alipay_return.htm")
	public void integral_alipay_return(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map json_map = new HashMap();
		int code = 100;// 100回调成功，-100回调失败
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		// 商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
		// 商户快捷支付秘钥，
		String private_key = new String(request.getParameter("private").getBytes("ISO-8859-1"), "UTF-8");
		String order_nos[] = out_trade_no.split("-");
		String order_no = order_nos[2];
		Payment payment = this.getPaymentbyMark("alipay_app");
		if (private_key.equals(payment.getApp_private_key())) { // 验证成功
			IntegralGoodsOrder order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(order_no));
			code = 100;
			if (order.getIgo_status() == 0) {
				order.setIgo_status(20);
				order.setIgo_payment("alipay_app");
				order.setIgo_pay_time(new Date());
				order.setOut_order_id(trade_no);
				boolean ret = this.integralGoodsOrderService.update(order);
				if (ret) {
					List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(order.getGoods_info());
					for (Map map : ig_maps) {
						IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
						// goods.setIg_goods_count(goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
						goods.setIg_exchange_count(goods.getIg_exchange_count()
								+ CommUtil.null2Int(map.get("ig_goods_count")));
						this.integralGoodsService.update(goods);
					}
				}
				code = 100;// 成功
			} else {
				code = -100;//
			}
		} else {
			code = -100;//
		}
		json_map.put("code", code);
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * app支付宝异步回调地址
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/app/integral_alipay_notify.htm")
	public void integral_app_alipay_notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		// 商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
		// 交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
		String order_nos[] = out_trade_no.split("-");
		String order_no = order_nos[2];
		IntegralGoodsOrder order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(order_no));
		Payment payment = this.getPaymentbyMark("alipay_app");

		if ("TRADE_FINISHED".equals(trade_status) || "TRADE_SUCCESS".equals(trade_status)) { // 验证成功
			if (order.getIgo_status() == 0) { // 异步没有出来订单，则同步处理订单
				order.setOut_order_id(trade_no);
				
				order.setIgo_status(20);
				order.setIgo_payment("alipay_app");
				order.setIgo_pay_time(new Date());
				boolean ret = this.integralGoodsOrderService.update(order);
				if (ret) {
					List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(order.getGoods_info());
					for (Map map : ig_maps) {
						IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
						// goods.setIg_goods_count(goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
						goods.setIg_exchange_count(goods.getIg_exchange_count()
								+ CommUtil.null2Int(map.get("ig_goods_count")));
						this.integralGoodsService.update(goods);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else { // 已经支付
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
		} else { // 验证失败
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

	// 发起通知请求
	@RequestMapping("/app/wx_pay_return.htm")
	public void wx_pay(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
		String line = "";
		StringBuffer strBuf = new StringBuffer();
		while ((line = in.readLine()) != null) {
			strBuf.append(line).append("\n");
		}
		in.close();
		logger.debug(strBuf.toString().trim());
		Map<String, String> xml = this.doXMLParse(strBuf.toString().trim());
		Payment payment = this.getPaymentbyMark("wx_app");
		if ("SUCCESS".equals(xml.get("return_code").toString())) {
			if ("SUCCESS".equals(xml.get("result_code").toString())) {
				// 金额,以分为单位
				String total_fee = xml.get("total_fee");
				String[] attachs = xml.get("attach").split("_");
				String trade_no = xml.get("transaction_id");
				String type = attachs[3];
				String order_no = attachs[1];
				OrderForm main_order = null;
				Predeposit obj = null;
				GoldRecord gold = null;
				IntegralGoodsOrder ig_order = null;
				if ("goods".equals(type) || "group".equals(type)) {
					main_order = this.orderFormService.getObjById(CommUtil.null2Long(attachs[0]));
					User buyer = this.userService.getObjById(CommUtil.null2Long(main_order.getUser_id()));
					if (main_order != null && main_order.getOrder_status() < 20) { // 异步没有出来订单，则同步处理订单
						main_order.setOrder_status(20);
						main_order.setOut_order_id(trade_no);
						main_order.setPayTime(new Date());
						main_order.setPayment(payment);
						this.orderFormService.update(main_order);
						// 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
						this.update_goods_inventory(main_order);
						OrderFormLog main_ofl = new OrderFormLog();
						main_ofl.setAddTime(new Date());
						main_ofl.setLog_info("微信支付");
						main_ofl.setLog_user(buyer);
						main_ofl.setOf(main_order);
						this.orderFormLogService.save(main_ofl);
						// 主订单付款成功，发送邮件提示
						// 向加盟商家发送付款成功邮件提示，自营商品无需发送邮件提示
						this.send_msg_tobuyer(request, main_order);
						this.send_msg_toseller(request, main_order);
						// 子订单操作
						if (main_order.getOrder_main() == 1
								&& !"".equals(CommUtil.null2String(main_order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
							List<Map> maps = this.orderFormTools.queryGoodsInfo(main_order.getChild_order_detail());
							for (Map child_map : maps) {
								OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map
										.get("order_id")));
								if (child_order.getOrder_status() != 20) {
									child_order.setOrder_status(20);
									child_order.setOut_order_id(main_order.getOut_order_id());
									child_order.setPayTime(new Date());
									child_order.setPayment(payment);
									this.orderFormService.update(child_order);
									// 子订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
									this.update_goods_inventory(child_order);
//									OrderFormLog child_ofl = new OrderFormLog();
//									child_ofl.setAddTime(new Date());
//									child_ofl.setLog_info("微信支付");
//									child_ofl.setLog_user(buyer);
//									child_ofl.setOf(child_order);
//									this.orderFormLogService.save(child_ofl);
									// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
									// 付款成功，发送邮件提示
									this.send_msg_toseller(request, child_order);

								}
							}
						}
					}
				}

				if ("integral".equals(type)) {
					ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(attachs[0]));
					if (ig_order != null && ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("wx_pay");
						this.integralGoodsOrderService.update(ig_order);
						List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(ig_order.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
							// goods.setIg_goods_count(
							// goods.getIg_goods_count() - CommUtil.null2Int(map.get("ig_goods_count")));
							goods.setIg_exchange_count(goods.getIg_exchange_count()
									+ CommUtil.null2Int(map.get("ig_goods_count")));
							this.integralGoodsService.update(goods);
						}
					}
				}

				if ("group".equals(type)) {
					if (main_order != null && main_order.getOrder_status() != 20) {
						this.generate_groupInfos(request, main_order, "wx_pay", "微信支付", main_order.getTrade_no());
					}
				}
				if ("cash".equals(type)) {
					Double fee=CommUtil.null2Double(total_fee)/100;
					obj = this.predepositService.getObjById(CommUtil.null2Long(attachs[0]));
					if (obj.getPd_pay_status() < 2) {
						User user = this.userService.getObjById(obj.getPd_user().getId());
						ResultDTO resultDTO = feeManageConnector.recharge(user.getCustId(),fee.toString(), obj.getPd_sn());
						if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
							obj.setPd_status(1);
							obj.setPd_pay_status(2);
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
				PrintWriter write = response.getWriter();
				write.print("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
			} else {
				PrintWriter write = response.getWriter();
				write.print("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>");
			}
		} else {

			PrintWriter write = response.getWriter();
			write.print("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>");
		}

	}

	private void generate_groupInfos(HttpServletRequest request, OrderForm order, String mark, String pay_info,
			String trade_no) throws Exception {
		order.setOrder_status(20);
		order.setOut_order_id(trade_no);
		order.setPayTime(new Date());
		// 生活团购订单付款时增加退款时效
		if (order.getOrder_cat() == 2) {
			Calendar ca = Calendar.getInstance();
			ca.add(ca.DATE, this.configService.getSysConfig().getGrouplife_order_return());
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String latertime = bartDateFormat.format(ca.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(latertime);
			order.setReturn_shipTime(date);
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
			String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
					+ "grouplifegoods";
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
			List<String> code_list = new ArrayList();// 存放团购消费码
			String codes = "";
			for (int i = 0;i < count;i++) {
				GroupInfo info = new GroupInfo();
				info.setAddTime(new Date());
				info.setLifeGoods(goods);
				info.setPayment(payments.get(0));
				info.setOrder_id(order.getId());
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
				store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(order.getGoods_amount(),
						store.getStore_sale_amount())));// 店铺本次结算总销售金额
				store.setStore_commission_amount(BigDecimal.valueOf(CommUtil.add(order.getCommission_amount(),
						store.getStore_commission_amount())));// 店铺本次结算总佣金
				store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(order.getTotalPrice(),
						store.getStore_payoff_amount())));// 店铺本次结算总佣金
				this.storeService.update(store);
				
				PayoffLog plog = new PayoffLog();
				plog.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
				plog.setPl_info("团购码生成成功");
				plog.setAddTime(new Date());
				plog.setSeller(store.getUser());
				plog.setO_id(CommUtil.null2String(order.getId()));
				plog.setOrder_id(order.getOrder_id().toString());
				plog.setCommission_amount(BigDecimal.valueOf(CommUtil.null2Double("0.00")));// 该订单总佣金费用
				plog.setGoods_info(order.getGroup_info());
				plog.setOrder_total_price(order.getTotalPrice());// 该订单总商品金额
				plog.setTotal_amount(this.orderFormService.getPayoffAmount(order));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
				this.payoffservice.save(plog);
			}
			
			// 增加系统总销售金额、总佣金
			SysConfig sc = this.configService.getSysConfig();
			sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(order.getGoods_amount(), sc.getPayoff_all_sale())));
			sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil.add(order.getCommission_amount(),
					sc.getPayoff_all_commission())));
			this.configService.update(sc);
			
			// 发送系统站内信给买家
			String msg_content = "恭喜您成功购买团购" + map.get("goods_name") + ",团购消费码分别为：" + codes + "您可以到用户中心-我的生活购中查看消费码的使用情况";
			Message tobuyer_msg = new Message();
			tobuyer_msg.setAddTime(new Date());
			tobuyer_msg.setStatus(0);
			tobuyer_msg.setType(0);
			tobuyer_msg.setContent(msg_content);
			tobuyer_msg.setFromUser(this.userService.getObjByProperty(null, "userName", "admin"));
			tobuyer_msg.setToUser(buyer);
			this.messageService.save(tobuyer_msg);
			
			// 发送系统站内信给卖家
//			Message toSeller_msg = new Message();
//			toSeller_msg.setAddTime(new Date());
//			toSeller_msg.setStatus(0);
//			toSeller_msg.setType(0);
//			toSeller_msg.setContent(buyer.getUsername());
//			toSeller_msg.setFromUser(this.userService.getObjByProperty(null, "userName", "admin"));
//			toSeller_msg.setToUser(goods.getUser());
//			this.messageService.save(toSeller_msg);
			
			// 付款成功，发送短信团购消费码
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.send_groupInfo_sms(request, order, buyer.getMobile(), "sms_tobuyer_online_ok_send_groupinfo",
						code_list, buyer.getId().toString(), goods.getUser().getId().toString());
			}
		}
	}

	private void update_goods_inventory(OrderForm order) {
		// 付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(CommUtil.null2String(order.getId()));
		// 更新订单中组合套装商品信息
		List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
		for (Map map_combin : maps) {
			if (map_combin.get("combin_suit_info") != null) {
				Map suit_info = Json.fromJson(Map.class,
						CommUtil.null2String(map_combin.get("combin_suit_info")));
				int combin_count = CommUtil.null2Int(suit_info.get("suit_count"));
				List<Map> combin_goods = this.orderFormTools.query_order_suitgoods(suit_info);
				for (Map temp_goods : combin_goods) { // 更新组合套装中其他商品信息，将套装主商品排除，主商品在普通商品更新中更新信息
					for (Goods temp : goods_list) {
						if (!CommUtil.null2String(temp_goods.get("id")).equals(temp.getId().toString())) {
							Goods goods = this.goodsService.getObjById(CommUtil.null2Long(temp_goods
									.get("id")));
							goods.setGoods_salenum(goods.getGoods_salenum() + combin_count);
							this.goodsService.update(goods);
						}
					}
				}
			}
		}
		for (Goods goods : goods_list) {
			int goods_count = this.orderFormTools.queryOfGoodsCount(CommUtil.null2String(order.getId()),
					CommUtil.null2String(goods.getId()));
			if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						gg.setGg_selled_count(gg.getGg_selled_count() + goods_count);
						this.groupGoodsService.update(gg);
						
						// 更新lucene索引
						String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
								+ "groupgoods";
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
				String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator + "goods";
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
			this.msgTools.sendEmailFree(CommUtil.getURL(request), "email_tobuyer_online_pay_ok_notify", buyer.getEmail(),
					null, order.getId().toString());
			this.msgTools.sendSmsFree(CommUtil.getURL(request), "sms_tobuyer_online_pay_ok_notify", buyer.getMobile(), null,
					order.getId().toString());
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
			this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_toseller_online_pay_ok_notify",
					seller.getEmail(), null, CommUtil.null2String(order.getId()), order.getStore_id());
			this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_online_pay_ok_notify", seller.getMobile(),
					null, CommUtil.null2String(order.getId()), order.getStore_id());
		}
	}

	private Payment getPaymentbyMark(String mark) {
		Map params = new HashMap();
		Set marks = new TreeSet();
		Payment payment = null;
		marks.add(mark);
		params.put("marks", marks);
		params.put("install", true);
		List<Payment> payments = this.paymentService.query(
				"select obj from Payment obj where obj.mark in(:marks) and obj.install=:install", params, -1, -1);
		if (payments.size() > 0) {
			payment = payments.get(0);
		}
		return payment;
	}

	/**
	 * 
	 * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
	 * 
	 * @param strxml
	 * 
	 * @return
	 * 
	 * @throws JDOMException
	 * 
	 * @throws IOException
	 */

	public Map doXMLParse(String strxml) throws JDOMException, IOException {

		strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
		if (null == strxml || "".equals(strxml)) {
			return null;
		}
		Map m = new HashMap();
		InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
		SAXBuilder builder = new SAXBuilder();
		org.jdom.Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			String k = e.getName();
			String v = "";
			List children = e.getChildren();
			if (children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = this.getChildrenText(children);
			}
			m.put(k, v);
		}
		// 关闭流
		in.close();
		return m;
	}

	/**
	 * 
	 * 获取子结点的xml
	 * 
	 * @param children
	 * 
	 * @return String
	 */

	public String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if (!children.isEmpty()) {
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if (!list.isEmpty()) {
					sb.append(this.getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}
		return sb.toString();

	}
}
