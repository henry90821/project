package com.iskyshop.pay.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.GoldRecord;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.service.IGoldRecordService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.pay.alipay.config.AlipayConfig;
import com.iskyshop.pay.alipay.services.AlipayService;
import com.iskyshop.pay.alipay.util.AlipaySubmit;
import com.iskyshop.pay.alipay.util.UtilDate;
import com.iskyshop.pay.bill.config.BillConfig;
import com.iskyshop.pay.bill.services.BillService;
import com.iskyshop.pay.bill.util.BillCore;
import com.iskyshop.pay.bill.util.MD5Util;
import com.iskyshop.pay.chinabank.util.ChinaBankSubmit;
import com.iskyshop.pay.chinapay.ChinaPayNoCardService;
import com.iskyshop.pay.chinapay.ChinaPayService;
import com.iskyshop.pay.paypal.PaypalTools;
import com.iskyshop.pay.unionpay.UnionPayService;

/**
 * <p>
 * Title: PayTools.java
 * </p>
 * <p/>
 * <p>
 * Description:在线支付工具类，用来生成主流常见支付平台的在线支付信息，并提交到支付平台 及支付宝手机网页在线支付
 * </p>
 * <p/>
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * <p/>
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 *
 * @author erikzhang
 * @version iskyshop_b2b2c v2.0 2015版
 * @date 2014-4-28
 */
@Component
@SuppressWarnings("all")
public class PayTools {
	private final Logger LOGGER = Logger.getLogger(this.getClass());

	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormtools;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IGoldRecordService goldRecordService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private ISysConfigService configService;

	/**
	 * 根据支付类型生成支付宝在线表单 0为及时支付、1为担保支付、2为标准双接口,支持分润处理,由于支付宝限制，分润操作只支持即时到帐
	 *
	 * @param url
	 *            系统url
	 * @param payment_id
	 *            支付方式id
	 * @param type
	 *            支付类型，分为goods支付商品，cash在线充值
	 * @param id
	 *            订单编号，根据type区分类型
	 * @return String
	 */
	public String genericAlipay(String url, String payment_id, String type, String id) {
		boolean submit = true; // 是否继续提交支付，防止订单重复支付，pc端打开支付页面，另外一个人用app完成了支付
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 订单已经处于支付状态
				submit = false;
			}
		}
		if ("cash".equals(type)) {
			pd = this.predepositService.getObjById(CommUtil.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
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
		if ("group".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 团购订单已经处于支付状态
				submit = false;
			}
		}
		if (submit) {
			Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
			if (payment == null) {
				payment = new Payment();
			}
			int interfaceType = payment.getInterfaceType();
			AlipayConfig config = new AlipayConfig();
			Map params = new HashMap();
			params.put("mark", "alipay");
			List<Payment> payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params,
					-1, -1);
			Payment shop_payment = new Payment();
			if (payments.size() > 0) {
				shop_payment = payments.get(0);
			}
			if (!"".equals(CommUtil.null2String(payment.getSafeKey()))
					&& !"".equals(CommUtil.null2String(payment.getPartner()))) {
				config.setKey(payment.getSafeKey());
				config.setPartner(payment.getPartner());
				config.setPrivate_key(payment.getApp_private_key());
				config.setAli_public_key(payment.getApp_public_key());
				config.setSign_type(payment.getAlipaySignType());
			} else {
				config.setKey(shop_payment.getSafeKey());
				config.setPartner(shop_payment.getPartner());
			}
			config.setSeller_email(payment.getSeller_email());
			config.setNotify_url(url + "/alipay_notify.htm");
			config.setReturn_url(url + "/aplipay_return.htm");
//			SysConfig sysConfig = this.configService.getSysConfig();
			if (interfaceType == 0) {// 及时到账支付
				String out_trade_no = "";
				String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
				if ("goods".equals(type) || "group".equals(type)) {
					of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
					boolean flag = this.orderFormService.update(of); // 更新订单流水号
					if (flag) {
						out_trade_no = "order-" + trade_no + "-" + of.getId().toString();
					}
				}

				if ("cash".equals(type)) {
					pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
					boolean flag = this.predepositService.update(pd);
					if (flag) {
						out_trade_no = "pd-" + trade_no + "-" + pd.getId().toString();
					}
				}
				if ("gold".equals(type)) {
					gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
					boolean flag = this.goldRecordService.update(gold);
					if (flag) {
						out_trade_no = "gold-" + trade_no + "-" + gold.getId().toString();
					}
				}
				if ("integral".equals(type)) {
					ig_order.setIgo_order_sn("igo-" + trade_no + "-" + ig_order.getId().toString());
					boolean flag = this.integralGoodsOrderService.update(ig_order);
					if (flag) {
						out_trade_no = "igo-" + trade_no + "-" + ig_order.getId().toString();
					}
				}
				// 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
				String subject = ""; //
				if ("goods".equals(type)) {
					subject = of.getOrder_id();
				}
				if ("cash".equals(type)) {
					subject = pd.getPd_sn();
				}
				if ("gold".equals(type)) {
					subject = gold.getGold_sn();
				}
				if ("integral".equals(type)) {
					subject = ig_order.getIgo_order_sn();
				}
				if ("store_deposit".equals(type)) {
					subject = "store_deposit";
				}
				if ("group".equals(type)) {
					subject = of.getOrder_id();
				}
				// 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
				String body = type;
				// 订单总金额，显示在支付宝收银台里的“应付总额”里
				String total_fee = ""; //
				if ("goods".equals(type)) {
					double total_price = this.orderFormtools.query_order_price(CommUtil.null2String(of.getId()));
					total_fee = CommUtil.null2String(total_price);
				}
				if ("cash".equals(type)) {
					total_fee = CommUtil.null2String(pd.getPd_amount());
				}
				if ("gold".equals(type)) {
					total_fee = CommUtil.null2String(gold.getGold_money());
				}
				if ("integral".equals(type)) {
					total_fee = CommUtil.null2String(ig_order.getIgo_trans_fee());
				}
				if ("group".equals(type)) {
					total_fee = CommUtil.null2String(of.getTotalPrice());
				}
				// 扩展功能参数——默认支付方式//
				// 默认支付方式，取值见“即时到帐接口”技术文档中的请求参数列表
				String paymethod = "";
				// 默认网银代号，代号列表见“即时到帐接口”技术文档“附录”→“银行列表”
				String defaultbank = "";
				// 扩展功能参数——防钓鱼//
				// 防钓鱼时间戳
				String anti_phishing_key = "";
				// 获取客户端的IP地址，建议：编写获取客户端IP地址的程序
				String exter_invoke_ip = "";
				// 注意：
				// 1.请慎重选择是否开启防钓鱼功能
				// 2.exter_invoke_ip、anti_phishing_key一旦被设置过，那么它们就会成为必填参数
				// 3.开启防钓鱼功能后，服务器、本机电脑必须支持远程XML解析，请配置好该环境。
				// 4.建议使用POST方式请求数据
				// 示例：
				// anti_phishing_key = AlipayService.query_timestamp();
				// //获取防钓鱼时间戳函数
				// exter_invoke_ip = "202.1.1.1";

				// 扩展功能参数——其他///

				// 自定义参数，可存放任何内容（除=、&等特殊字符外），不会显示在页面上
				String extra_common_param = type;
				// 默认买家支付宝账号
				String buyer_email = "";
				// 商品展示地址，要用http:// 格式的完整路径，不允许加?id=123这类自定义参数
				String show_url = "";
				// 扩展功能参数——分润(若要使用，请按照注释要求的格式赋值)//s
				// 提成类型，该值为固定值：10，不需要修改
				String royalty_type = "10";
				// 减去支付宝手续费
				// 提成信息集
				String royalty_parameters = "";
				// 注意：
				// 与需要结合商户网站自身情况动态获取每笔交易的各分润收款账号、各分润金额、各分润说明。最多只能设置10条
				// 各分润金额的总和须小于等于total_fee
				// 提成信息集格式为：收款方Email_1^金额1^备注1|收款方Email_2^金额2^备注2
				// 把请求参数打包成数组
				Map<String, String> sParaTemp = new HashMap<String, String>();
				sParaTemp.put("payment_type", "1");
				sParaTemp.put("out_trade_no", out_trade_no);
				sParaTemp.put("subject", subject);
				sParaTemp.put("body", body);
				sParaTemp.put("total_fee", total_fee);
				sParaTemp.put("show_url", show_url);
				sParaTemp.put("paymethod", paymethod);
				sParaTemp.put("defaultbank", defaultbank);
				sParaTemp.put("anti_phishing_key", anti_phishing_key);
				sParaTemp.put("exter_invoke_ip", exter_invoke_ip);
				sParaTemp.put("extra_common_param", extra_common_param);
				sParaTemp.put("buyer_email", buyer_email);
				// 构造函数，生成请求URL
				try {
					result = AlipayService.create_direct_pay_by_user(config, sParaTemp);
				} catch (UnsupportedEncodingException e) {
					LOGGER.error("生成支付宝请求URL失败！", e);
				}
			}
			if (interfaceType == 1) {// 担保支付接口
				// 请与贵网站订单系统中的唯一订单号匹配
				String out_trade_no = "";
				String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
				if ("goods".equals(type)) {
					of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
					boolean flag = this.orderFormService.update(of); // 更新订单流水号
					if (flag) {
						out_trade_no = "order-" + trade_no + "-" + of.getId().toString();
					}
				}

				if ("cash".equals(type)) {
					pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
					boolean flag = this.predepositService.update(pd);
					if (flag) {
						out_trade_no = "pd-" + trade_no + "-" + pd.getId().toString();
					}
				}
				if ("gold".equals(type)) {
					gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
					boolean flag = this.goldRecordService.update(gold);
					if (flag) {
						out_trade_no = "gold-" + trade_no + "-" + gold.getId().toString();
					}
				}
				if ("integral".equals(type)) {
					ig_order.setIgo_order_sn("igo-" + trade_no + "-" + ig_order.getId().toString());
					boolean flag = this.integralGoodsOrderService.update(ig_order);
					if (flag) {
						out_trade_no = "igo-" + trade_no + "-" + ig_order.getId().toString();
					}
				}

				if ("group".equals(type)) {
					of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
					boolean flag = this.orderFormService.update(of); // 更新订单流水号
					if (flag) {
						out_trade_no = "order-" + trade_no + "-" + of.getId().toString();
					}
				}
				// 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
				String subject = ""; //
				if ("goods".equals(type)) {
					subject = of.getOrder_id();
				}
				if ("cash".equals(type)) {
					subject = pd.getPd_sn();
				}
				if ("gold".equals(type)) {
					subject = gold.getGold_sn();
				}
				if ("integral".equals(type)) {
					subject = ig_order.getIgo_order_sn();
				}
				if ("store_deposit".equals(type)) {
					subject = "store_deposit";
				}
				if ("group".equals(type)) {
					subject = of.getOrder_id();
				}
				// 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
				String body = type;
				// 订单总金额，显示在支付宝收银台里的“应付总额”里
				String total_fee = ""; //
				if ("goods".equals(type)) {
					total_fee = CommUtil.null2String(of.getTotalPrice());
				}
				if ("cash".equals(type)) {
					total_fee = CommUtil.null2String(pd.getPd_amount());
				}
				if ("gold".equals(type)) {
					total_fee = CommUtil.null2String(gold.getGold_money());
				}
				if ("integral".equals(type)) {
					total_fee = CommUtil.null2String(ig_order.getIgo_trans_fee());
				}
				if ("group".equals(type)) {
					total_fee = CommUtil.null2String(of.getTotalPrice());
				}
				// 订单总金额，显示在支付宝收银台里的“应付总额”里
				String price = String.valueOf(total_fee);
				// 物流费用，即运费。
				String logistics_fee = "0.00";
				// 物流类型，三个值可选：EXPRESS（快递）、POST（平邮）、EMS（EMS）
				String logistics_type = "EXPRESS";
				// 物流支付方式，两个值可选：SELLER_PAY（卖家承担运费）、BUYER_PAY（买家承担运费）
				String logistics_payment = "SELLER_PAY";
				// 商品数量，建议默认为1，不改变值，把一次交易看成是一次下订单而非购买一件商品。
				String quantity = "1";
				// 扩展参数//
				// 自定义参数，可存放任何内容（除=、&等特殊字符外），不会显示在页面上
				String extra_common_param = "";
				// 买家收货信息（推荐作为必填）
				// 该功能作用在于买家已经在商户网站的下单流程中填过一次收货信息，而不需要买家在支付宝的付款流程中再次填写收货信息。
				// 若要使用该功能，请至少保证receive_name、receive_address有值
				String receive_name = "";
				String receive_address = "";
				String receive_zip = "";
				String receive_phone = ""; // 收货人电话号码
				String receive_mobile = "";
				// 网站商品的展示地址，不允许加?id=123这类自定义参数
				String show_url = "";
				// 把请求参数打包成数组
				Map<String, String> sParaTemp = new HashMap<String, String>();
				sParaTemp.put("payment_type", "1");
				sParaTemp.put("show_url", show_url);
				sParaTemp.put("out_trade_no", out_trade_no);
				sParaTemp.put("subject", subject);
				sParaTemp.put("body", body);
				sParaTemp.put("price", price);
				sParaTemp.put("logistics_fee", logistics_fee);
				sParaTemp.put("logistics_type", logistics_type);
				sParaTemp.put("logistics_payment", logistics_payment);
				sParaTemp.put("quantity", quantity);
				sParaTemp.put("extra_common_param", extra_common_param);
				sParaTemp.put("receive_name", receive_name);
				sParaTemp.put("receive_address", receive_address);
				sParaTemp.put("receive_zip", receive_zip);
				sParaTemp.put("receive_phone", receive_phone);
				sParaTemp.put("receive_mobile", receive_mobile);

				// 构造函数，生成请求URL
				try {
					result = AlipayService.create_partner_trade_by_buyer(config, sParaTemp);
				} catch (UnsupportedEncodingException e) {
					LOGGER.error("生成支付宝请求URL失败！", e);
				}
			}
			if (interfaceType == 2) {// 标准双接口
				// 请与贵网站订单系统中的唯一订单号匹配
				String out_trade_no = "";
				String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
				if ("goods".equals(type)) {
					of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
					boolean flag = this.orderFormService.update(of); // 更新订单流水号
					if (flag) {
						out_trade_no = "order-" + trade_no + "-" + of.getId().toString();
					}
				}

				if ("cash".equals(type)) {
					pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
					boolean flag = this.predepositService.update(pd);
					if (flag) {
						out_trade_no = "pd-" + trade_no + "-" + pd.getId().toString();
					}
				}
				if ("gold".equals(type)) {
					gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
					boolean flag = this.goldRecordService.update(gold);
					if (flag) {
						out_trade_no = "gold-" + trade_no + "-" + gold.getId().toString();
					}
				}
				if ("integral".equals(type)) {
					ig_order.setIgo_order_sn("igo-" + trade_no + "-" + ig_order.getId().toString());
					boolean flag = this.integralGoodsOrderService.update(ig_order);
					if (flag) {
						out_trade_no = "igo-" + trade_no + "-" + ig_order.getId().toString();
					}
				}
				if ("group".equals(type)) {
					of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
					boolean flag = this.orderFormService.update(of); // 更新订单流水号
					if (flag) {
						out_trade_no = "order-" + trade_no + "-" + of.getId().toString();
					}
				}
				// 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
				String subject = ""; //
				if ("goods".equals(type)) {
					subject = of.getOrder_id();
				}
				if ("cash".equals(type)) {
					subject = pd.getPd_sn();
				}
				if ("gold".equals(type)) {
					subject = gold.getGold_sn();
				}
				if ("integral".equals(type)) {
					subject = ig_order.getIgo_order_sn();
				}
				if ("store_deposit".equals(type)) {
					subject = "store_deposit";
				}
				if ("group".equals(type)) {
					subject = of.getOrder_id();
				}
				// 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
				String body = type;
				// 订单总金额，显示在支付宝收银台里的“应付总额”里
				String total_fee = ""; //
				if ("goods".equals(type)) {
					total_fee = CommUtil.null2String(of.getTotalPrice());
				}
				if ("cash".equals(type)) {
					total_fee = CommUtil.null2String(pd.getPd_amount());
				}
				if ("gold".equals(type)) {
					total_fee = CommUtil.null2String(gold.getGold_money());
				}
				if ("integral".equals(type)) {
					total_fee = CommUtil.null2String(ig_order.getIgo_trans_fee());
				}
				if ("group".equals(type)) {
					total_fee = CommUtil.null2String(of.getTotalPrice());
				}
				// 订单总金额，显示在支付宝收银台里的“应付总额”里
				String price = String.valueOf(total_fee);

				// 物流费用，即运费。
				String logistics_fee = "0.00";
				// 物流类型，三个值可选：EXPRESS（快递）、POST（平邮）、EMS（EMS）
				String logistics_type = "EXPRESS";
				// 物流支付方式，两个值可选：SELLER_PAY（卖家承担运费）、BUYER_PAY（买家承担运费）
				String logistics_payment = "SELLER_PAY";

				// 商品数量，建议默认为1，不改变值，把一次交易看成是一次下订单而非购买一件商品。
				String quantity = "1";
				// 买家收货信息（推荐作为必填）
				String extra_common_param = "";
				// 该功能作用在于买家已经在商户网站的下单流程中填过一次收货信息，而不需要买家在支付宝的付款流程中再次填写收货信息。
				// 若要使用该功能，请至少保证receive_name、receive_address有值
				String receive_name = "";
				String receive_address = "";
				String receive_zip = "";
				String receive_phone = ""; // 收货人电话号码，如：0571-81234567
				String receive_mobile = "";
				// 网站商品的展示地址，不允许加?id=123这类自定义参数
				String show_url = "";
				// 把请求参数打包成数组
				Map<String, String> sParaTemp = new HashMap<String, String>();
				sParaTemp.put("payment_type", "1");
				sParaTemp.put("show_url", show_url);
				sParaTemp.put("out_trade_no", out_trade_no);
				sParaTemp.put("subject", subject);
				sParaTemp.put("body", body);
				sParaTemp.put("price", price);
				sParaTemp.put("logistics_fee", logistics_fee);
				sParaTemp.put("logistics_type", logistics_type);
				sParaTemp.put("logistics_payment", logistics_payment);
				sParaTemp.put("quantity", quantity);
				sParaTemp.put("extra_common_param", extra_common_param);
				sParaTemp.put("receive_name", receive_name);
				sParaTemp.put("receive_address", receive_address);
				sParaTemp.put("receive_zip", receive_zip);
				sParaTemp.put("receive_phone", receive_phone);
				sParaTemp.put("receive_mobile", receive_mobile);
				// 构造函数，生成请求URL
				try {
					result = AlipayService.trade_create_by_buyer(config, sParaTemp);
				} catch (UnsupportedEncodingException e) {
					LOGGER.error("生成支付宝请求URL失败！", e);
				}
			}
		} else {
			result = "该订单已经完成支付！";
		}

		return result;
	}

	/**
	 * 生成快钱在线表单
	 *
	 * @param url
	 *            系统url
	 * @param payment_id
	 *            支付方式id
	 * @param type
	 *            支付类型，分为goods支付商品，cash在线充值
	 * @param id
	 *            订单编号，根据type区分类型
	 */
	public String generic99Bill(String url, String payment_id, String type, String id) throws UnsupportedEncodingException {
		boolean submit = true; // 是否继续提交支付，防止订单重复支付，pc端打开支付页面，另外一个人用app完成了支付
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 订单已经处于支付状态
				submit = false;
			}
		}
		if ("cash".equals(type)) {
			pd = this.predepositService.getObjById(CommUtil.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
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
		if ("group".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 团购订单已经处于支付状态
				submit = false;
			}
		}
		if (submit) {
			Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
			if (payment == null)
				payment = new Payment();
			BillConfig config = new BillConfig(payment.getMerchantAcctId(), payment.getRmbKey(), payment.getPid());
			// 人民币网关账户号
			// /请登录快钱系统获取用户编号，用户编号后加01即为人民币网关账户号。
			String merchantAcctId = config.getMerchantAcctId();
			String key = config.getKey();
			String inputCharset = "1"; // 字符编码 1为UTF-8 2为GBK 3为GB2312
			String bgUrl = url + "/bill_notify_return.htm"; // 服务器接受支付结果的异步后台地址
			String pageUrl = url + "/bill_return.htm"; // 服务器接受支付结果的同步后台地址
			String version = "v2.0"; // 网关版本
			String language = "1"; // 网关页面显示语言种类,1为中文
			String signType = "1"; // 签名类型,1代表MD5加密签名方式,快钱3.0后该值推荐为4，但是为1 可以使用
			// 支付人姓名
			// /可为中文或英文字符
			String payerName = SecurityUserHolder.getCurrentUser().getUserName();
			// 支付人联系方式类型.固定选择值
			// /只能选择1
			// /1代表Email
			String payerContactType = "1";
			// 支付人联系方式
			// /只能选择Email或手机号
			String payerContact = "";
			// 商户订单号
			// /由字母、数字、或[-][_]组成
			String orderId = "";
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
			if ("goods".equals(type)) {
				of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
				boolean flag = this.orderFormService.update(of); // 更新订单流水号
				if (flag) {
					orderId = "order-" + trade_no + "-" + of.getId().toString();
				}
			}

			if ("cash".equals(type)) {
				pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
				boolean flag = this.predepositService.update(pd);
				if (flag) {
					orderId = "pd-" + trade_no + "-" + pd.getId().toString();
				}
			}
			if ("gold".equals(type)) {
				gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
				boolean flag = this.goldRecordService.update(gold);
				if (flag) {
					orderId = "gold-" + trade_no + "-" + gold.getId().toString();
				}
			}
			if ("integral".equals(type)) {
				ig_order.setIgo_order_sn("igo-" + trade_no + "-" + ig_order.getId().toString());
				boolean flag = this.integralGoodsOrderService.update(ig_order);
				if (flag) {
					orderId = "igo-" + trade_no + "-" + ig_order.getId().toString();
				}
			}
			if ("group".equals(type)) {
				of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
				boolean flag = this.orderFormService.update(of); // 更新订单流水号
				if (flag) {
					orderId = "order-" + trade_no + "-" + of.getId().toString();
				}
			}
			// 订单金额
			// /以分为单位，必须是整型数字
			// /比方2，代表0.02元
			String orderAmount = "";
			if ("goods".equals(type)) {
				double total_price = this.orderFormtools.query_order_price(CommUtil.null2String(of.getId()));
				orderAmount = String.valueOf((int) Math.floor(CommUtil.null2Double(total_price) * 100));
			}
			if ("cash".equals(type)) {
				orderAmount = String.valueOf((int) Math.floor(CommUtil.null2Double(pd.getPd_amount()) * 100));
			}
			if ("gold".equals(type)) {
				orderAmount = String.valueOf((int) Math.floor(CommUtil.null2Double(gold.getGold_money()) * 100));
			}
			if ("integral".equals(type)) {
				orderAmount = String.valueOf((int) Math.floor(CommUtil.null2Double(ig_order.getIgo_trans_fee()) * 100));
			}
			if ("group".equals(type)) {
				orderAmount = String.valueOf((int) Math.floor(CommUtil.null2Double(of.getTotalPrice()) * 100));
			}
			// 订单提交时间
			// /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
			// /如；20080101010101
			String orderTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
			// 商品名称
			// /可为中文或英文字符
			String productName = "";
			if ("goods".equals(type)) {
				productName = of.getOrder_id();
			}
			if ("cash".equals(type)) {
				productName = pd.getPd_sn();
			}
			if ("gold".equals(type)) {
				productName = gold.getGold_sn();
			}
			if ("integral".equals(type)) {
				productName = ig_order.getIgo_order_sn();
			}
			if ("store_deposit".equals(type)) {
				productName = "store_deposit";
			}
			if ("group".equals(type)) {
				productName = of.getOrder_id();
			}
			// 商品数量
			// /可为空，非空时必须为数字
			String productNum = "1";

			// 商品代码
			// /可为字符或者数字
			String productId = "";

			// 商品描述
			String productDesc = "";

			// 扩展字段1
			// /在支付结束后原样返回给商户
			String ext1 = "";
			if ("goods".equals(type)) {
				ext1 = of.getId().toString();
			}
			if ("cash".equals(type)) {
				ext1 = pd.getId().toString();
			}
			if ("gold".equals(type)) {
				ext1 = gold.getId().toString();
			}
			if ("integral".equals(type)) {
				ext1 = ig_order.getId().toString();
			}
			if ("group".equals(type)) {
				ext1 = of.getId().toString();
			}
			// 扩展字段2
			// /在支付结束后原样返回给商户
			String ext2 = type;

			// 支付方式.固定选择值
			// /只能选择00、10、11、12、13、14
			// /00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）
			String payType = "00";

			// 同一订单禁止重复提交标志
			// /固定选择值： 1、0
			// /1代表同一订单号只允许提交1次；0表示同一订单号在没有支付成功的前提下可重复提交多次。默认为0建议实物购物车结算类商户采用0；虚拟产品类商户采用1
			String redoFlag = "0";

			// 快钱的合作伙伴的账户号
			// /如未和快钱签订代理合作协议，不需要填写本参数
			String pid = "";
			if (config.getPid() != null)
				pid = config.getPid();
			// 生成加密签名串
			// /请务必按照如下顺序和规则组成加密串！
			String signMsgVal = "";
			signMsgVal = BillCore.appendParam(signMsgVal, "inputCharset", inputCharset);
			signMsgVal = BillCore.appendParam(signMsgVal, "pageUrl", pageUrl);
			signMsgVal = BillCore.appendParam(signMsgVal, "bgUrl", bgUrl);
			signMsgVal = BillCore.appendParam(signMsgVal, "version", version);
			signMsgVal = BillCore.appendParam(signMsgVal, "language", language);
			signMsgVal = BillCore.appendParam(signMsgVal, "signType", signType);
			signMsgVal = BillCore.appendParam(signMsgVal, "merchantAcctId", merchantAcctId);
			signMsgVal = BillCore.appendParam(signMsgVal, "payerName", payerName);
			signMsgVal = BillCore.appendParam(signMsgVal, "payerContactType", payerContactType);
			signMsgVal = BillCore.appendParam(signMsgVal, "payerContact", payerContact);
			signMsgVal = BillCore.appendParam(signMsgVal, "orderId", orderId);
			signMsgVal = BillCore.appendParam(signMsgVal, "orderAmount", orderAmount);
			signMsgVal = BillCore.appendParam(signMsgVal, "orderTime", orderTime);
			signMsgVal = BillCore.appendParam(signMsgVal, "productName", productName);
			signMsgVal = BillCore.appendParam(signMsgVal, "productNum", productNum);
			signMsgVal = BillCore.appendParam(signMsgVal, "productId", productId);
			signMsgVal = BillCore.appendParam(signMsgVal, "productDesc", productDesc);
			signMsgVal = BillCore.appendParam(signMsgVal, "ext1", ext1);
			signMsgVal = BillCore.appendParam(signMsgVal, "ext2", ext2);
			signMsgVal = BillCore.appendParam(signMsgVal, "payType", payType);
			signMsgVal = BillCore.appendParam(signMsgVal, "redoFlag", redoFlag);
			signMsgVal = BillCore.appendParam(signMsgVal, "pid", pid);
			signMsgVal = BillCore.appendParam(signMsgVal, "key", key);
			// 生成加密签名串
			String signMsg = MD5Util.md5Hex(signMsgVal.getBytes("UTF-8")).toUpperCase();

			// 把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("inputCharset", inputCharset);
			sParaTemp.put("pageUrl", pageUrl);
			sParaTemp.put("bgUrl", bgUrl);
			sParaTemp.put("version", version);
			sParaTemp.put("language", language);
			sParaTemp.put("signType", signType);
			sParaTemp.put("signMsg", signMsg);
			sParaTemp.put("merchantAcctId", merchantAcctId);
			sParaTemp.put("payerName", payerName);
			sParaTemp.put("payerContactType", payerContactType);
			sParaTemp.put("payerContact", payerContact);
			sParaTemp.put("orderId", orderId);
			sParaTemp.put("orderAmount", orderAmount);
			sParaTemp.put("orderTime", orderTime);
			sParaTemp.put("productName", productName);
			sParaTemp.put("productNum", productNum);
			sParaTemp.put("productId", productId);
			sParaTemp.put("productDesc", productDesc);
			sParaTemp.put("ext1", ext1);
			sParaTemp.put("ext2", ext2);
			sParaTemp.put("payType", payType);
			sParaTemp.put("redoFlag", redoFlag);
			sParaTemp.put("pid", pid);
			result = BillService.buildForm(config, sParaTemp, "post", "确定");
		} else {
			result = "该订单已经完成支付！";
		}

		return result;
	}

	/**
	 * 生成网银在线表单
	 *
	 * @param url
	 *            系统url
	 * @param payment_id
	 *            支付方式id
	 * @param type
	 *            支付类型，分为goods支付商品，cash在线充值
	 * @param id
	 *            订单编号，根据type区分类型
	 */
	public String genericChinaBank(String url, String payment_id, String type, String id) {
		boolean submit = true; // 是否继续提交支付，防止订单重复支付，pc端打开支付页面，另外一个人用app完成了支付
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 订单已经处于支付状态
				submit = false;
			}
		}
		if ("cash".equals(type)) {
			pd = this.predepositService.getObjById(CommUtil.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
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
		if ("group".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 团购订单已经处于支付状态
				submit = false;
			}
		}

		if (submit) {
			Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
			if (payment == null)
				payment = new Payment();
			List<SysMap> list = new ArrayList<SysMap>();
			String v_mid = payment.getChinabank_account(); // 网银商户号
			list.add(new SysMap("v_mid", v_mid));
			String key = payment.getChinabank_key(); // 网银私钥
			list.add(new SysMap("key", key));
			String v_url = url + "/chinabank_return.htm"; // 网银付款回调地址
			list.add(new SysMap("v_url", v_url));
			String v_oid = "";
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
			if ("goods".equals(type)) {
				of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
				boolean flag = this.orderFormService.update(of); // 更新订单流水号
				if (flag) {
					v_oid = "order-" + trade_no + "-" + of.getId().toString();
				}
			}

			if ("cash".equals(type)) {
				pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
				boolean flag = this.predepositService.update(pd);
				if (flag) {
					v_oid = "pd-" + trade_no + "-" + pd.getId().toString();
				}
			}
			if ("gold".equals(type)) {
				gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
				boolean flag = this.goldRecordService.update(gold);
				if (flag) {
					v_oid = "gold-" + trade_no + "-" + gold.getId().toString();
				}
			}
			if ("integral".equals(type)) {
				ig_order.setIgo_order_sn("igo-" + trade_no + "-" + ig_order.getId().toString());
				boolean flag = this.integralGoodsOrderService.update(ig_order);
				if (flag) {
					v_oid = "igo-" + trade_no + "-" + ig_order.getId().toString();
				}
			}
			if ("group".equals(type)) {
				of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
				boolean flag = this.orderFormService.update(of); // 更新订单流水号
				if (flag) {
					v_oid = "order-" + trade_no + "-" + of.getId().toString();
				}
			}
			list.add(new SysMap("v_oid", v_oid));
			String v_amount = "";
			if ("goods".equals(type)) {
				double total_price = this.orderFormtools.query_order_price(CommUtil.null2String(of.getId()));
				v_amount = CommUtil.null2String(total_price); // 订单总价格
			}
			if ("cash".equals(type)) {
				v_amount = CommUtil.null2String(pd.getPd_amount()); // 订单总价格
			}
			if ("gold".equals(type)) {
				v_amount = CommUtil.null2String(gold.getGold_money()); // 订单总价格
			}
			if ("integral".equals(type)) {
				v_amount = CommUtil.null2String(ig_order.getIgo_trans_fee()); // 订单总价格
			}
			if ("group".equals(type)) {
				v_amount = CommUtil.null2String(of.getTotalPrice()); // 订单总价格
			}
			list.add(new SysMap("v_amount", v_amount));
			String v_moneytype = "CNY"; // 支付币种，CNY表示人民币
			list.add(new SysMap("v_moneytype", v_moneytype));
			String temp = v_amount + v_moneytype + v_oid + v_mid + v_url + key; // 拼凑加密串
			String v_md5info = Md5Encrypt.md5(temp).toUpperCase(); // 使用MD5加密字符串
			list.add(new SysMap("v_md5info", v_md5info));
			// 以下为可选项
			String v_rcvname = ""; // 收货人
			String v_rcvaddr = ""; // 收货地址
			String v_rcvtel = ""; // 收货人电话
			String v_rcvpost = ""; // 收货人邮编
			String v_rcvemail = ""; // 收货人邮件
			String v_rcvmobile = ""; // 收货人手机号码
			String remark1 = ""; // 备注1
			if ("goods".equals(type)) {
				remark1 = of.getId().toString();
			}
			if ("cash".equals(type)) {
				remark1 = pd.getId().toString();
			}
			if ("gold".equals(type)) {
				remark1 = gold.getId().toString();
			}
			if ("integral".equals(type)) {
				remark1 = ig_order.getId().toString();
			}
			if ("group".equals(type)) {
				remark1 = of.getId().toString();
			}
			list.add(new SysMap("remark1", remark1));
			String remark2 = type; // 备注2
			list.add(new SysMap("remark2", remark2));
			result = ChinaBankSubmit.buildForm(list);
		} else {
			result = "该订单已经完成支付！";
		}

		return result;
	}

	/**
	 * 生成Paypal支付表单并自动提交
	 *
	 * @param url
	 * @param payment_id
	 * @param type
	 * @param id
	 * @return
	 */
	public String genericPaypal(String url, String payment_id, String type, String id) {
		boolean submit = true; // 是否继续提交支付，防止订单重复支付，pc端打开支付页面，另外一个人用app完成了支付
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 订单已经处于支付状态
				submit = false;
			}
		}
		if ("cash".equals(type)) {
			pd = this.predepositService.getObjById(CommUtil.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
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
		if ("group".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 团购订单已经处于支付状态
				submit = false;
			}
		}
		if (submit) {
			Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
			if (payment == null)
				payment = new Payment();
			List<SysMap> sms = new ArrayList<SysMap>();
			String business = payment.getPaypal_userId(); // Paypal商户号
			sms.add(new SysMap("business", business));
			String return_url = url + "/paypal_return.htm"; // paypal付款回调地址
			String notify_url = url + "/paypal_return.htm"; // paypal notify地址
			sms.add(new SysMap("return", return_url));
			String item_name = "";
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
			if ("goods".equals(type)) {
				of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
				boolean flag = this.orderFormService.update(of); // 更新订单流水号
				if (flag) {
					item_name = "order-" + trade_no + "-" + of.getId().toString();
				}
			}

			if ("cash".equals(type)) {
				pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
				boolean flag = this.predepositService.update(pd);
				if (flag) {
					item_name = "pd-" + trade_no + "-" + pd.getId().toString();
				}
			}
			if ("gold".equals(type)) {
				gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
				boolean flag = this.goldRecordService.update(gold);
				if (flag) {
					item_name = "gold-" + trade_no + "-" + gold.getId().toString();
				}
			}
			if ("integral".equals(type)) {
				ig_order.setIgo_order_sn("igo-" + trade_no + "-" + ig_order.getId().toString());
				boolean flag = this.integralGoodsOrderService.update(ig_order);
				if (flag) {
					item_name = "igo-" + trade_no + "-" + ig_order.getId().toString();
				}
			}
			if ("group".equals(type)) {
				of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
				boolean flag = this.orderFormService.update(of); // 更新订单流水号
				if (flag) {
					item_name = "order-" + trade_no + "-" + of.getId().toString();
				}
			}
			sms.add(new SysMap("item_name", item_name));
			String amount = "";
			String item_number = "";
			if ("goods".equals(type)) {
				double total_price = this.orderFormtools.query_order_price(CommUtil.null2String(of.getId()));
				amount = CommUtil.null2String(total_price); // 订单总价格
				item_number = of.getOrder_id();
			}
			if ("cash".equals(type)) {
				amount = CommUtil.null2String(pd.getPd_amount()); // 订单总价格
				item_number = pd.getPd_sn();
			}
			if ("gold".equals(type)) {
				amount = CommUtil.null2String(gold.getGold_money()); // 订单总价格
				item_number = gold.getGold_sn();
			}
			if ("integral".equals(type)) {
				amount = CommUtil.null2String(ig_order.getIgo_trans_fee()); // 订单总价格
				item_number = ig_order.getIgo_order_sn();
			}
			if ("group".equals(type)) {
				amount = CommUtil.null2String(of.getTotalPrice()); // 订单总价格
				item_number = of.getOrder_id();
			}
			sms.add(new SysMap("amount", amount));
			sms.add(new SysMap("notify_url", notify_url));
			sms.add(new SysMap("cmd", "_xclick"));
			sms.add(new SysMap("currency_code", payment.getCurrency_code()));
			sms.add(new SysMap("item_number", item_number));
			// sms.add(new SysMap("no_shipping", "1"));
			// sms.add(new SysMap("no_note", "1"));
			// 以下为可选项
			String custom = ""; // 备注1
			if ("goods".equals(type)) {
				custom = of.getId().toString();
			}
			if ("cash".equals(type)) {
				custom = pd.getId().toString();
			}
			if ("gold".equals(type)) {
				custom = gold.getId().toString();
			}
			if ("integral".equals(type)) {
				custom = ig_order.getId().toString();
			}
			if ("group".equals(type)) {
				custom = of.getId().toString();
			}
			custom = custom + "," + type;
			sms.add(new SysMap("custom", custom));
			result = PaypalTools.buildForm(sms);
		} else {
			result = "该订单已经完成支付！";
		}
		return result;
	}

	/**
	 * 生成手机端网页支付信息,V1.3版手机网页客户端仅仅支持用户支付,mobile_mark手机端支付标识，该标识存在时说明为手机端支付， 支付完成后调用手机端回调地址
	 *
	 * @param url
	 * @param payment_id
	 * @param type
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String genericAlipayWap(String url, String payment_id, String type, String id) throws Exception {
		boolean submit = true; // 是否继续提交支付，防止订单重复支付，pc端打开支付页面，另外一个人用app完成了支付
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 订单已经处于支付状态
				submit = false;
			}
		}
		if ("cash".equals(type)) {
			pd = this.predepositService.getObjById(CommUtil.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
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
		if ("group".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 团购订单已经处于支付状态
				submit = false;
			}
		}
		if (submit) {
			Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
			if (payment == null) {
				payment = new Payment();
			}
			int interfaceType = payment.getInterfaceType();
			AlipayConfig config = new AlipayConfig();
			Map params = new HashMap();
			params.put("mark", "alipay_wap");
			List<Payment> payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params,
					-1, -1);
			Payment shop_payment = new Payment();
			if (payments.size() > 0) {
				shop_payment = payments.get(0);
			}
			if (!"".equals(CommUtil.null2String(payment.getSafeKey()))
					&& !"".equals(CommUtil.null2String(payment.getPartner()))) {
				config.setKey(payment.getSafeKey());
				config.setPartner(payment.getPartner());
				config.setPrivate_key(payment.getApp_private_key());
				config.setAli_public_key(payment.getApp_public_key());
				config.setSign_type(payment.getAlipaySignType());
			} else {
				config.setKey(shop_payment.getSafeKey());
				config.setPartner(shop_payment.getPartner());
			}
			config.setSeller_email(payment.getSeller_email());
			// //////////////////////////////////调用授权接口alipay.wap.trade.create.direct获取授权码token//////////////////////////////////////
			// 返回格式
			String format = "xml";
			// 必填，不需要修改
			// 返回格式
			String v = "2.0";
			// 必填，不需要修改
			// 请求号
			String req_id = UtilDate.getOrderNum();
			// 必填，须保证每次请求都是唯一
			// req_data详细信息
			// 服务器异步通知页面路径
			String notify_url = url + "/wap/alipay_notify.htm";
			// 需http://格式的完整路径，不能加?id=123这类自定义参数
			// 页面跳转同步通知页面路径
			String call_back_url = url + "/wap/alipay_return.htm";
			// 需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/
			// 操作中断返回地址
			String merchant_url = url + "/wap/buyer/order_list.htm";
			// 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
			// String pay_body = type;
			// 用户付款中途退出返回商户的地址。需http://格式的完整路径，不允许加?id=123这类自定义参数
			// 卖家支付宝帐户
			String seller_email = payment.getSeller_email();
			// 必填
			// 商户订单号
			String out_trade_no = "";
			// begin
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
			if ("goods".equals(type)) {
				of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
				boolean flag = this.orderFormService.update(of); // 更新订单流水号
				if (flag) {
					out_trade_no = "order-" + trade_no + "-" + of.getId().toString() + "-" + type;
				}
			}

			if ("cash".equals(type)) {
				pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
				boolean flag = this.predepositService.update(pd);
				if (flag) {
					out_trade_no = "pd-" + trade_no + "-" + pd.getId().toString() + "-" + type;
				}
			}
			if ("gold".equals(type)) {
				gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
				boolean flag = this.goldRecordService.update(gold);
				if (flag) {
					out_trade_no = "gold-" + trade_no + "-" + gold.getId().toString() + "-" + type;
				}
			}
			if ("integral".equals(type)) {
				ig_order.setIgo_order_sn("igo-" + trade_no + "-" + ig_order.getId().toString());
				boolean flag = this.integralGoodsOrderService.update(ig_order);
				if (flag) {
					out_trade_no = "igo-" + trade_no + "-" + ig_order.getId().toString() + "-" + type;
				}
			}
			if ("group".equals(type)) {
				of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
				boolean flag = this.orderFormService.update(of); // 更新订单流水号
				if (flag) {
					out_trade_no = "order-" + trade_no + "-" + of.getId().toString() + "-" + type;
				}
			}
			// 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
			// 商户网站订单系统中唯一订单号，必填
			// 订单名称
			String subject = "goods";
			if ("goods".equals(type)) {
				subject = of.getOrder_id();
			}
			if ("cash".equals(type)) {
				subject = pd.getPd_sn();
			}
			if ("gold".equals(type)) {
				subject = gold.getGold_sn();
			}
			if ("integral".equals(type)) {
				subject = ig_order.getIgo_order_sn();
			}
			if ("store_deposit".equals(type)) {
				subject = "store_deposit";
			}
			// 必填
			// 付款金额
			// 订单总金额，显示在支付宝收银台里的“应付总额”里
			String total_fee = ""; //
			if ("goods".equals(type)) {
				// 获取订单总价格
				double total_price = this.orderFormtools.query_order_price(CommUtil.null2String(of.getId()));
				total_fee = CommUtil.null2String(total_price);
			}
			if ("cash".equals(type)) {
				total_fee = CommUtil.null2String(pd.getPd_amount());
			}
			if ("gold".equals(type)) {
				total_fee = CommUtil.null2String(gold.getGold_money());
			}
			if ("integral".equals(type)) {
				total_fee = CommUtil.null2String(ig_order.getIgo_trans_fee());
			}
			// 必填
			// 请求业务参数详细
			String req_dataToken = "<direct_trade_create_req><notify_url>" + notify_url + "</notify_url><call_back_url>"
					+ call_back_url + "</call_back_url><seller_account_name>" + seller_email
					+ "</seller_account_name><out_trade_no>" + out_trade_no + "</out_trade_no><subject>" + subject
					+ "</subject><total_fee>" + total_fee + "</total_fee><merchant_url>" + merchant_url
					+ "</merchant_url><pay_body>" + type + "</pay_body></direct_trade_create_req>";
			// 必填
			// ////////////////////////////////////////////////////////////////////////////////
			// 把请求参数打包成数组
			Map<String, String> sParaTempToken = new HashMap<String, String>();
			sParaTempToken.put("service", "alipay.wap.trade.create.direct");
			sParaTempToken.put("partner", config.getPartner());
			sParaTempToken.put("_input_charset", config.getInput_charset());
			sParaTempToken.put("sec_id", config.getSign_type());
			sParaTempToken.put("format", format);
			sParaTempToken.put("v", v);
			sParaTempToken.put("req_id", req_id);
			sParaTempToken.put("req_data", req_dataToken);
			// 建立请求
			String sHtmlTextToken = AlipaySubmit.buildRequest(config, "wap", sParaTempToken, "", "");
			// URLDECODE返回的信息
			sHtmlTextToken = URLDecoder.decode(sHtmlTextToken, config.getInput_charset());
			// 获取token
			String request_token = AlipaySubmit.getRequestToken(config, sHtmlTextToken);
			// //////////////////////////////////根据授权码token调用交易接口alipay.wap.auth.authAndExecute//////////////////////////////////////
			// 业务详细
			String req_data = "<auth_and_execute_req><request_token>" + request_token
					+ "</request_token></auth_and_execute_req>";
			// 必填
			// 把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("service", "alipay.wap.auth.authAndExecute");
			sParaTemp.put("partner", config.getPartner());
			sParaTemp.put("_input_charset", config.getInput_charset());
			sParaTemp.put("sec_id", config.getSign_type());
			sParaTemp.put("format", format);
			sParaTemp.put("v", v);
			sParaTemp.put("req_data", req_data);
			// 建立请求
			String WAP_ALIPAY_GATEWAY_NEW = "http://wappaygw.alipay.com/service/rest.htm?";
			result = AlipaySubmit.buildForm(config, sParaTemp, WAP_ALIPAY_GATEWAY_NEW, "get", "确认");
		} else {
			result = "该订单已经完成支付！";
		}
		return result;
	}

	/**
	 * 中国银联WEB和WAP端支付
	 * 
	 * @param url
	 * @param payment_id
	 * @param type
	 * @param id
	 * @param paySource
	 *            支付来源【web、wap】
	 * @return
	 */
	public String genericUnionPay(String url, String payment_id, String type, String id, String paySource)
			throws IOException {
		boolean submit = true; // 是否继续提交支付，防止订单重复支付，pc端打开支付页面，另外一个人用app完成了支付
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 订单已经处于支付状态
				submit = false;
			}
		}
		if ("cash".equals(type)) {
			pd = this.predepositService.getObjById(CommUtil.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
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
		if ("group".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 团购订单已经处于支付状态
				submit = false;
			}
		}
		// 判断订单是否已经支付了
		if (submit) {
			Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
			if (payment == null) {
				payment = new Payment();
			}
			String out_trade_no = "";
			Date payDate = new Date();
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", payDate);
			// 根据商品类型生成流水号
			if ("goods".equals(type) || "group".equals(type)) {
				of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());
				boolean flag = this.orderFormService.update(of); // 更新订单流水号
				if (flag) {
					out_trade_no = "order-" + trade_no + "-" + of.getId().toString();
				}
			}

			if ("cash".equals(type)) {
				pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
				boolean flag = this.predepositService.update(pd);
				if (flag) {
					out_trade_no = "pd-" + trade_no + "-" + pd.getId().toString();
				}
			}
			if ("gold".equals(type)) {
				gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());
				boolean flag = this.goldRecordService.update(gold);
				if (flag) {
					out_trade_no = "gold-" + trade_no + "-" + gold.getId().toString();
				}
			}
			if ("integral".equals(type)) {
				ig_order.setIgo_order_sn("igo-" + trade_no + "-" + ig_order.getId().toString());
				boolean flag = this.integralGoodsOrderService.update(ig_order);
				if (flag) {
					out_trade_no = "igo-" + trade_no + "-" + ig_order.getId().toString();
				}
			}
			// 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
			String body = type;
			// 订单总金额，显示在支付宝收银台里的“应付总额”里
			String total_fee = "";
			if ("goods".equals(type)) {
				double total_price = this.orderFormtools.query_order_price(CommUtil.null2String(of.getId()));
				total_fee = CommUtil.null2String(total_price);
			}
			if ("cash".equals(type)) {
				total_fee = CommUtil.null2String(pd.getPd_amount());
			}
			if ("gold".equals(type)) {
				total_fee = CommUtil.null2String(gold.getGold_money());
			}
			if ("integral".equals(type)) {
				total_fee = CommUtil.null2String(ig_order.getIgo_trans_fee());
			}
			if ("group".equals(type)) {
				total_fee = CommUtil.null2String(of.getTotalPrice());
			}
			// 获得支付宝单字符串
			result = UnionPayService.getPayFormStr(url, payment, out_trade_no, body, total_fee, paySource);
		} else {
			result = "该订单已经完成支付！";
		}
		return result;
	}

	/***
	 * chinapay银联在线WEB和WAP端支付
	 * 
	 * @param url
	 * @param payment_id
	 * @param type
	 * @param id
	 * @param paySource
	 * @return
	 */
	public String genericChinaPay(String url, String payment_id, String type, String id, String paySource, String cardType,String gate_id) {
		boolean submit = true; // 是否继续提交支付，防止订单重复支付，pc端打开支付页面，另外一个人用app完成了支付
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if ("goods".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 订单已经处于支付状态
				submit = false;
			}
		}
		if ("cash".equals(type)) {
			pd = this.predepositService.getObjById(CommUtil.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
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
		if ("group".equals(type)) {
			of = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {// 团购订单已经处于支付状态
				submit = false;
			}
		}
		// 判断订单是否已经支付了
		if (submit) {
			Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
			if (payment == null) {
				payment = new Payment();
			}
			String out_trade_no = "";
			String trade_order_id = "";
			Date payDate = new Date();
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", payDate);
			trade_order_id = CommUtil.formatTime("yyyyMMddHHmmssSSS", payDate);
			trade_order_id = trade_order_id.substring(1);
			String subtransdate=CommUtil.formatTime("yyyyMMdd", new Date());
			// 根据商品类型生成流水号
			if ("goods".equals(type) || "group".equals(type)) {
				of.setTrade_no("order-" + trade_no + "-" + of.getId().toString());

				of.setTrade_order_id(trade_order_id);
				of.setSubtansdate(subtransdate);

				boolean flag = this.orderFormService.update(of); // 更新订单流水号
				if (flag) {
					out_trade_no = "order-" + trade_no + "-" + of.getId().toString();
				}
			}

			if ("cash".equals(type)) {
				pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());

				pd.setTrade_order_id(trade_order_id);
				pd.setSubtansdate(subtransdate);

				boolean flag = this.predepositService.update(pd);
				if (flag) {
					out_trade_no = "pd-" + trade_no + "-" + pd.getId().toString();
				}
			}
			if ("gold".equals(type)) {
				gold.setGold_sn("gold-" + trade_no + "-" + gold.getId().toString());

				gold.setTrade_order_id(trade_order_id);
				gold.setSubtansdate(subtransdate);

				boolean flag = this.goldRecordService.update(gold);
				if (flag) {
					out_trade_no = "gold-" + trade_no + "-" + gold.getId().toString();
				}
			}
			if ("integral".equals(type)) {
				ig_order.setIgo_order_sn("igo-" + trade_no + "-" + ig_order.getId().toString());

				ig_order.setTrade_order_id(trade_order_id);
				ig_order.setSubtansdate(subtransdate);

				boolean flag = this.integralGoodsOrderService.update(ig_order);
				if (flag) {
					out_trade_no = "igo-" + trade_no + "-" + ig_order.getId().toString();
				}
			}
			// 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
			String body = type;
			// 订单总金额，显示在支付宝收银台里的“应付总额”里
			String total_fee = "";
			if ("goods".equals(type)) {
				double total_price = this.orderFormtools.query_order_price(CommUtil.null2String(of.getId()));
				total_fee = CommUtil.null2String(total_price);
			}
			if ("cash".equals(type)) {
				total_fee = CommUtil.null2String(pd.getPd_amount());
			}
			if ("gold".equals(type)) {
				total_fee = CommUtil.null2String(gold.getGold_money());
			}
			if ("integral".equals(type)) {
				total_fee = CommUtil.null2String(ig_order.getIgo_trans_fee());
			}
			if ("group".equals(type)) {
				total_fee = CommUtil.null2String(of.getTotalPrice());
			}
			// 获得支付表单字符串 0:无卡支付 1:有卡支付
			if ("1".equals(cardType)) {
				result = ChinaPayService.getPayFormStr(url, payment, out_trade_no, body, total_fee, paySource,
						trade_order_id,gate_id,subtransdate);
			} else {
				result = ChinaPayNoCardService.getPayFormStr(url, payment, out_trade_no, body, total_fee, paySource,
						trade_order_id,subtransdate);
			}

		} else {
			result = "该订单已经完成支付！";
		}
		return result;
	}
}
