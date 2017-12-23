package com.iskyshop.msg;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.PopupAuthenticator;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserService;
import com.smi.tools.http.HttpKit;
import com.tydic.framework.util.PropertyUtil;

/**
 * 系统手机短信、邮件发送工具类。其中收费工具类只作为商家和用户在交易中的发送工具类，发送的短信邮件均收费，需要商家在商家中心购买相应数量的短信和邮件， 在短信和邮件数量允许的情况下才能发送
 *
 */
@Component
public class MsgTools {
	private static Logger logger = Logger.getLogger(MsgTools.class);
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;

	
	/**
	 * 收费短信发送方法，第三方商家专用
	 * 
	 * @param webPath
	 * @param mark
	 * @param mobile
	 * @param map
	 *            参数map数据：
	 *               buyer_id:如果有买家，则为买家user.id
	 *               seller_id:如果有卖家,则为卖家的user.id 
	 *               sender_id:发送者的user.id 
	 *               receiver_id:接收者的user.id
	 *               order_id:如果有订单，则为订单order.id 
	 *               childorder_id：如果有子订单则为子订单id 
	 *               goods_id:商品的id 
	 *               self_goods: 如果是自营商品，则在邮件或者短信中显示平台名称SysConfig.title
	 * @param order_id
	 *            ：订单id。可以为null。若不为null，则会在模板所关联的Context中添加此订单相关的对象（如：卖家、买家、订单和店铺信息(如是非自营店的话)）
	 * @param store_id 要发送短信的第三方店铺的id，不能为null
	 */
	public boolean sendSmsCharge(String webPath, String mark, String mobile, Map map, String order_id, String store_id) {
		if (this.configService.getSysConfig().isSmsEnbale()) {
			Template template = this.templateService.getObjByProperty(null, "mark", mark);
			if(template != null && template.isOpen() && "sms".equals(template.getType()) && !StringUtils.isNullOrEmpty(store_id)) {
				Map function_map = null;
				List<Map> function_maps = null;
				Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));
				if (store != null && store.getStore_sms_count() > 0) {
					//下面判断商家是否已禁止了当前短信模板，若禁止了，则表示不发送此短信
					if(!StringUtils.isNullOrEmpty(store.getSms_email_info())) {
						function_maps = (List<Map>) Json.fromJson(store.getSms_email_info());
						for (Map temp_map2 : function_maps) {
							if ("sms".equals(CommUtil.null2String(temp_map2.get("type"))) && mark.equals(CommUtil.null2String(temp_map2.get("mark")))) {
								if (CommUtil.null2Int(temp_map2.get("sms_open")) == 0) {// 验证功能是否开启，0：关闭,1：开启
									logger.info("店铺(id=" + store_id + ")已禁止发送该短信(mark=" + mark + ")");
									return false;
								} 
								function_map = temp_map2;
								break;
							}
						}
					}
				} else {
					logger.info("未找到商家店铺或商家店铺没有购买短信流量：store_id=" + store_id);
					return false;
				}				

				String content = buildContextVariables(template, map, webPath, order_id);
				if(content != null) {
					if (this.sendSMS(mobile, content)) {//？？？？这块代码会出现并发问题，比如多个用户同时下单某个店铺中的商品，则下面的代码会有并发问题
						store.setStore_sms_count(store.getStore_sms_count() - 1);// 商家短信数量减1
						if(function_map != null) {
							function_map.put("sms_count", CommUtil.null2Int(function_map.get("sms_count")) + 1);// 商家功能发送邮件数量加1
							String sms_email_json = Json.toJson(function_maps, JsonFormat.compact());
							store.setSms_email_info(sms_email_json);
						}
						store.setSend_sms_count(store.getSend_sms_count() + 1);
						this.storeService.update(store);
					}
				}
			} else {
				logger.info("未找到短信模板或模板未启用或不是短信类型的模板或参数store_id为空：mark=" + mark + ", store_id=" + store_id);
			}			
		} else {
			logger.info("系统未开启短信发送功能！");
		}
		
		return false;
	}

	
	/**
	 * 收费邮件发送方法，第三方商家专用
	 * @param webPath 商城sysconfig.getAddress()的值
	 * @param mark 模板名称
	 * @param email 收件人邮箱地址
	 * @param map 为模板中的变量提供值的各对象的标识集合：
	 *               buyer_id:如果有买家，则为买家user.id
	 *               seller_id:如果有卖家,则为卖家的user.id 
	 *               sender_id:发送者的user.id 
	 *               receiver_id:接收者的user.id
	 *               order_id:如果有订单，则为订单order.id 
	 *               childorder_id：如果有子订单则为子订单id 
	 *               goods_id:商品的id 
	 *               self_goods: 如果是自营商品，则在邮件或者短信中显示平台名称SysConfig.title
	 * @param order_id 购物订单的order_id字段的值，即购物订单号。可以为null。若不为null，则会在模板所关联的Context中添加此订单相关的对象（如：卖家、买家、订单和店铺信息(如是非自营店的话)）
	 * @param store_id  要发送邮件的第三方店铺的id(用于判断此店铺是否还有发邮件的额度)，不能为null
	 * @return true：发送成功， false：发送失败
	 */
	public boolean sendEmailCharge(String webPath, String mark, String email, Map map, String order_id, String store_id) {
		if (this.configService.getSysConfig().isEmailEnable()) {
			Template template = this.templateService.getObjByProperty(null, "mark", mark);
			if(template != null && template.isOpen() && "email".equals(template.getType()) && !StringUtils.isNullOrEmpty(store_id)) {
				Map function_map = null;
				List<Map> function_maps = null;
				Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));
				if (store != null && store.getStore_email_count() > 0) {
					//下面判断商家是否已禁止了当前邮件模板，若禁止了，则表示不发送此邮件
					if(!StringUtils.isNullOrEmpty(store.getSms_email_info())) {
						function_maps = (List<Map>) Json.fromJson(store.getSms_email_info());
						for (Map temp_map2 : function_maps) {
							if ("email".equals(CommUtil.null2String(temp_map2.get("type"))) && mark.equals(CommUtil.null2String(temp_map2.get("mark")))) {
								if (CommUtil.null2Int(temp_map2.get("email_open")) == 0) {// 验证功能是否开启，0：关闭,1：开启
									logger.info("店铺(id=" + store_id + ")已禁止发送该邮件(mark=" + mark + ")");
									return false;
								} 
								function_map = temp_map2;
								break;
							}
						}
					}
				} else {
					logger.info("未找到商家店铺或商家店铺没有购买邮件流量：store_id=" + store_id);
					return false;
				}				

				String content = buildContextVariables(template, map, webPath, order_id);
				if(content != null) {
					if (this.sendEmail(email, template.getTitle(), content)) {//？？？？这块代码会出现并发问题，比如多个用户同时下单某个店铺中的商品，则下面的代码会有并发问题
						store.setStore_email_count(store.getStore_email_count() - 1);// 商家邮件数量减1
						if(function_map != null) {
							function_map.put("email_count", CommUtil.null2Int(function_map.get("email_count")) + 1);// 商家功能发送邮件数量加1
							String sms_email_json = Json.toJson(function_maps, JsonFormat.compact());
							store.setSms_email_info(sms_email_json);
						}
						store.setSend_email_count(store.getSend_email_count() + 1);
						this.storeService.update(store);
					}
				}
			} else {
				logger.info("未找到邮件模板或模板未启用或不是邮件类型的模板或参数store_id为空：mark=" + mark + ", store_id=" + store_id);
			}			
		} else {
			logger.info("系统未开启邮件发送功能！");
		}
		
		return false;
	}

	
	/**
	 * 免费发送短信（用于自营发短信）
	 * @param webPath 商城sysconfig.getAddress()的值
	 * @param mark 模板名称
	 * @param mobile  短信接收人的手机号，函数中不再校验此参数
	 * @param map 为模板中的变量提供值的各对象的标识集合：
	 *               buyer_id:如果有买家，则为买家user.id
	 *               seller_id:如果有卖家,则为卖家的user.id 
	 *               sender_id:发送者的user.id 
	 *               receiver_id:接收者的user.id
	 *               order_id:如果有订单，则为订单order.id 
	 *               childorder_id：如果有子订单则为子订单id 
	 *               goods_id:商品的id 
	 *               self_goods: 如果是自营商品，则在邮件或者短信中显示平台名称SysConfig.title 
	 * @param order_id 购物订单的order_id字段的值，即购物订单号。可以为null。若不为null，则会在模板所关联的Context中添加此订单相关的对象（如：卖家、买家、订单和店铺信息(如是非自营店的话)）
	 * @return true：发送成功， false：发送失败
	 */
	public boolean sendSmsFree(String webPath, String mark, String mobile, Map map, String order_id) {
		if (this.configService.getSysConfig().isSmsEnbale()) {
			Template template = this.templateService.getObjByProperty(null, "mark", mark);
			if (template != null && template.isOpen() && "sms".equals(template.getType()) && !StringUtils.isNullOrEmpty(mobile)) {
				String content = buildContextVariables(template, map, webPath, order_id);
				if(content != null) {
					return this.sendSMS(mobile, content);
				}
			} else {
				logger.info("未找到短信模板或模板未启用或不是短信类型的模板或短信接收者手机号为空：mark=" + mark + ", mobile=" + mobile);
			}
		} else {
			logger.info("系统未开启短信发送功能！");
		}
		
		return false;
	}

	
	/**
	 * 免费发送邮件（用于自营发邮件）。若商城未启用邮件功能或未找到对应的邮件模板或邮件模板未启用，则不做任何处理
	 * @param webPath 商城sysconfig.getAddress()的值
	 * @param mark 模板名称
	 * @param email 收件人邮箱地址
	 * @param map 为模板中的变量提供值的各对象的标识集合：
	 *               buyer_id:如果有买家，则为买家user.id
	 *               seller_id:如果有卖家,则为卖家的user.id 
	 *               sender_id:发送者的user.id 
	 *               receiver_id:接收者的user.id
	 *               order_id:如果有订单，则为订单order.id 
	 *               childorder_id：如果有子订单则为子订单id 
	 *               goods_id:商品的id 
	 *               self_goods: 如果是自营商品，则在邮件或者短信中显示平台名称SysConfig.title
	 * @param order_id  购物订单的order_id字段的值，即购物订单号。可以为null。若不为null，则会在模板所关联的Context中添加此订单相关的对象（如：卖家、买家、订单和店铺信息(如是非自营店的话)）
	 * @return true：发送成功， false：发送失败
	 */
	public boolean sendEmailFree(String webPath, String mark, String email, Map map, String order_id) {
		if (this.configService.getSysConfig().isEmailEnable()) {
			Template template = this.templateService.getObjByProperty(null, "mark", mark);
			if (template != null && template.isOpen() && "email".equals(template.getType())) {
				String content = buildContextVariables(template, map, webPath, order_id);
				if(content != null) {
					return this.sendEmail(email, template.getTitle(), content);
				}
			} else {
				logger.info("未找到邮件模板或模板未启用或不是邮件类型的模板：mark=" + mark);
			}
		} else {
			logger.error("商城未开启邮件功能");
		}
		
		return false;
	}


	/**
	 * 根据map构建模板中变量所依赖的EvaluationContext对象
	 * @param template
	 * @param map
	 * @param webPath
	 * @param order_id
	 * @return
	 */
	private String buildContextVariables(Template template, Map map, String webPath, String order_id) {
		ExpressionParser exp = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext();
		if (!StringUtils.isNullOrEmpty(order_id)) {
			OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
			context.setVariable("order", order);
			
			User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
			context.setVariable("buyer", buyer);
			
			if (order.getStore_id() != null) {
				Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
				context.setVariable("store", store);
				User seller = this.userService.getObjById(store.getUser().getId());
				context.setVariable("seller", seller);
			}
		}
		
		if(map != null) {
			if (map.get("receiver_id") != null) {
				Long receiver_id = CommUtil.null2Long(map.get("receiver_id"));
				User receiver = this.userService.getObjById(receiver_id);
				context.setVariable("receiver", receiver);
			}
			
			if (map.get("sender_id") != null) {
				Long sender_id = CommUtil.null2Long(map.get("sender_id"));
				User sender = this.userService.getObjById(sender_id);
				context.setVariable("sender", sender);
			}
			
			if (map.get("buyer_id") != null) {
				Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
				User buyer = this.userService.getObjById(buyer_id);
				context.setVariable("buyer", buyer);
			}
			
			if (map.get("seller_id") != null) {
				Long seller_id = CommUtil.null2Long(map.get("seller_id"));
				User seller = this.userService.getObjById(seller_id);
				context.setVariable("seller", seller);
			}
			
			if (map.get("order_id") != null) {
				Long order_id_temp = CommUtil.null2Long(map.get("order_id"));
				OrderForm orderForm = this.orderFormService.getObjById(order_id_temp);
				context.setVariable("orderForm", orderForm);
			}
			
			if (map.get("childorder_id") != null) {
				Long childorder_id = CommUtil.null2Long(map.get("childorder_id"));
				OrderForm orderForm = this.orderFormService.getObjById(childorder_id);
				context.setVariable("child_orderForm", orderForm);
			}
			
			if (map.get("goods_id") != null) {
				Long goods_id = CommUtil.null2Long(map.get("goods_id"));
				Goods goods = this.goodsService.getObjById(goods_id);
				context.setVariable("goods", goods);
			}
			
			if (map.get("code") != null) {//比如：团购消费码
				context.setVariable("code", map.get("code").toString());
			}
			
			if (map.get("self_goods") != null) {//如：取值sysConfig().getTitle()
				context.setVariable("seller", map.get("self_goods").toString());
			}
		}
		
		context.setVariable("config", this.configService.getSysConfig());
		context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
		context.setVariable("webPath", webPath);
		
		Expression ex = exp.parseExpression(template.getContent(), new SpelTemplate());
		try {
			return ex.getValue(context, String.class);
		} catch (EvaluationException e) {
			logger.error("解析模板错误mark=" + template.getMark(), e);
			return null;
		}
	}

	
	/**
	 * 免费发送短信。不抛异常
	 * 
	 * @param mobile
	 * @param content
	 * @return true:表示要发送的短信已成功提交给短信服务器。false:发送短信失败
	 */
	public boolean sendSMS(String mobile, String content) {
		if (this.configService.getSysConfig().isSmsEnbale()) {
			//调用短信接口发送短信,渠道码 (101:星美汇；102:爱星美；103:星美生活客户端)
			return this.sendSMS(mobile, content, "101");	
		} else {
			logger.error("商城未开启短信服务");
			return false;
		}
	}
	
	
	/**
	 * 发送短信底层工具。不抛异常
	 * @param mobile 手机号
	 * @param content 内容
	 * @param channel 渠道码 (101:星美汇；102:爱星美；103:星美生活客户端)
	 * @return 要发送的短信已成功提交给短信服务器，则返回true；失败则返回false  
	 */
	private boolean sendSMS(String mobile, String content, String channel) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		String requestUrl = PropertyUtil.getProperty("sms.senUrl");
		paramsMap.put("phone", mobile);
		paramsMap.put("content", content);	
		paramsMap.put("accounttype", "1"); //1：非营销短信（营销短信一般都是批量发送的）
		paramsMap.put("channel", channel); 
		
		logger.info("开始发送短信：mobile=" + mobile + ", content=" + content);
		
		try {
			String resultString = HttpKit.post(requestUrl, paramsMap);
			
			logger.info("短信服务器返回的结果：mobile=" + mobile + ", response=" + resultString);
			
			Map obj = Json.fromJson(Map.class, resultString);
			
			if(!"22".equals(obj.get("code"))) {
				logger.error("发送短信失败：" + obj.get("msg").toString());
				return false;
			}
		} catch (IOException e) {
			logger.error("发送短信异常：" + e);
			return false;
		}
		
		return true;
	}
	

	/**
	 * 免费发送邮件。不抛异常
	 * @param email  目标邮件地址
	 * @param subject  邮件主题
	 * @param content  邮件内容
	 * @return 发送成功，则返回true；发送失败（如系统未开启此功能、发送邮件失败等），则返回false
	 */
	public boolean sendEmail(String email, String subject, String content) {
		boolean ret = false;
		if (this.configService.getSysConfig().isEmailEnable()) {
			String username = "";
			String password = "";
			String smtp_server = "";
			String from_mail_address = "";
			username = this.configService.getSysConfig().getEmailUserName();
			password = this.configService.getSysConfig().getEmailPws();
			smtp_server = this.configService.getSysConfig().getEmailHost();
			from_mail_address = this.configService.getSysConfig().getEmailUser();
			String to_mail_address = email;
			
			logger.info("开始发送邮件：to_email=" + email + ", subject=" + subject + ", content=" + content);
			
			if (!StringUtils.isNullOrEmpty(username) && !StringUtils.isNullOrEmpty(password) && !StringUtils.isNullOrEmpty(smtp_server)
					&& !StringUtils.isNullOrEmpty(to_mail_address)) {
				Authenticator auth = new PopupAuthenticator(username, password);
				Properties mailProps = new Properties();
				mailProps.put("mail.smtp.auth", "true");
				mailProps.put("username", username);
				mailProps.put("password", password);
				mailProps.put("mail.smtp.host", smtp_server);
				Session mailSession = Session.getInstance(mailProps, auth);
				MimeMessage message = new MimeMessage(mailSession);
				try {					
					message.setFrom(new InternetAddress(from_mail_address));
					message.setRecipient(Message.RecipientType.TO, new InternetAddress(to_mail_address));
					message.setSubject(subject);
					MimeMultipart multi = new MimeMultipart("related");
					BodyPart bodyPart = new MimeBodyPart();
					bodyPart.setDataHandler(new DataHandler(content, "text/html;charset=UTF-8"));// 网页格式
					multi.addBodyPart(bodyPart);
					message.setContent(multi);
					message.saveChanges();
					Transport.send(message);
					logger.info("发送邮件成功：to_email=" + email);
					ret = true;
				} catch (Exception e) {
					logger.error("发送电子邮件失败", e);
				} 
			} else {
				logger.error("请确保系统中已正确配置了电子邮件发送者的相关信息，且收件者邮箱地址不空！");
			}
		} else {
			logger.error("商城未开启邮件功能");
		}

		return ret;
	}

	
	/**
	 * 解析json工具。不会返回null
	 * 
	 * @param json
	 * @return
	 */
	private Map queryJson(String json) {
		Map map = new HashMap();
		if (!StringUtils.isNullOrEmpty(json)) {
			map = Json.fromJson(HashMap.class, json);
		}
		return map;
	}

}
