package com.iskyshop.smilife.payCenter;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iskyshop.bcp.service.IFeeManageservice;
import com.iskyshop.core.domain.IdEntity;
import com.iskyshop.core.exception.SmiBusinessException;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
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
import com.iskyshop.foundation.domain.RefundLog;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.events.publisher.SynchronizeOrderPublisher;
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
import com.iskyshop.foundation.service.IRefundLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.RefundTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.pay.tenpay.util.TenpayUtil;
import com.iskyshop.smilife.common.CommUtils;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ChannelEnum;
import com.iskyshop.smilife.enums.ErrorEnum;
import com.iskyshop.smilife.enums.OrderTypeEnum;
import com.iskyshop.smilife.enums.PayTypeEnum;
import com.iskyshop.smilife.enums.PayUrlEnum;
import com.iskyshop.smilife.enums.RefundModeEnum;
import com.iskyshop.smilife.enums.RefundTypeEnum;
import com.iskyshop.smilife.enums.SystemEnum;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.smilife.bcp.dto.common.EInterfaceState;
import com.smilife.bcp.dto.request.Commodity;
import com.smilife.bcp.dto.request.CommodityCancle;
import com.smilife.bcp.dto.request.OrderCancleReq;
import com.smilife.bcp.dto.response.ResultDTO;
import com.smilife.bcp.service.FeeManageConnector;
import com.smilife.bcp.service.common.BusinessHandler;
import com.tydic.framework.util.MD5Util;
import com.tydic.framework.util.PropertyUtil;


/**
 * 
 * 支付中心接口服务层
 * 
 */
@Service
@Transactional
public class PayCenterServiceImpl implements IpayCenterService {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IUserService userService;
	@Autowired
	private FeeManageConnector feeManageConnector;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private SynchronizeOrderPublisher synchronizeOrderPublisher;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IPayoffLogService payoffservice;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IIntegralGoodsService integralGoodsService;
	@Autowired
	private IFeeManageservice feeManageservice;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private RefundTools refundTools;
	@Autowired
	private IRefundLogService refundLogService;
	
	/** 请求编码 utf-8 */
	public static final String UTF_8 = "utf-8";
	
	/** 支付中心服务地址 */
	public static final String PAY_CENTER_URL = "pay_center_url";

	
	
	/**
     * web和 h5统一支付接口：向支付中心提交订单信息，即所谓的支付下单
     * 
     * @param user
     *            下单用户.对于充值订单，user参数不会被使用
     * @param orderId
     *            支付订单号，如购物订单的order_id字段的值，充值订单的pd_sn字段的值，积分兑换订单的igo_order_sn字段的值
     * @param openId
     *            微信公众号支付时必填。若不填，则应传空串，而不是null，否则会出现签名错误
     * @param channel
     *            支付渠道（不是订单的下单渠道），如：ChannelEnum.APP.name()
     * @param orderType
     *            订单类型，如：OrderTypeEnum.SHOPPING.getIndex()
     * @param title
     *            下单标题（暂时无用，传空串）
     * @param returnUrl
     *            充值成功后，支付中心将跳转到的业务系统页面地址，如支付成功后跳转到业务系统的订单列表页面
     * @return 
     *          若成功，则在返回json字符串中会包含支付中心内置的支付页面的url，如：{"code":0,"msg":"OK","url":"https://xmpay.smi170.com/webpay.do?order_id=44524"}
     */
	public Result placeOrder(User user, String orderId, String openId, String channel, String orderType, String title, String returnUrl) {
		Result result = new Result();
		Long startTime = new Date().getTime();
		logger.info("web和 h5支付下单参数：user=" + (user == null? "": user.getUserName()) + ", orderId=" + orderId + ", openId=" + openId
				+ ", channel=" + channel + ", orderType=" + orderType + ", title=" + title + ", returnUrl=" + returnUrl);
		try {
			// 校验输入参数
			if (this.validate(orderId, openId, channel, orderType)) {
				logger.error("支付下单参数校验失败：orderId=" + orderId + ", openId=" + openId + ", channel=" + channel);
				return result.set(ErrorEnum.REQUEST_ERROR).setMsg("支付下单参数校验失败");
			}
			
			Object param = null;
			if (OrderTypeEnum.CHARGE.getIndex().equals(orderType)) {// 充值订单，查询出的订单信息为Predeposit
				param = this.predepositService.getObjByProperty(null, "pd_sn", orderId);
			} else if (OrderTypeEnum.INTEGRAL.getIndex().equals(orderType)) {// 积分兑换
				param = this.integralGoodsOrderService.getObjByProperty(null, "igo_order_sn", orderId);
			} else {// 非充值订单，查询出的订单信息为OrderForm
				param = this.orderFormService.getObjByProperty(null, "order_id", orderId);
			}
			
			if (param == null) {
				logger.error("支付下单时未查询到订单信息：orderType=" + orderType + "orderId=" + orderId);
				return result.set(ErrorEnum.REQUEST_ERROR).setMsg("支付下单时未查询到订单信息");
			} else { // 校验订单状态				
				if (OrderTypeEnum.CHARGE.getIndex().equals(orderType)) {// 充值订单，查询出的订单信息为Predeposit
					// 支付状态，0为等待支付，1为线下提交支付完成申请，2为支付成功
					if (((Predeposit) param).getPd_pay_status() != 0) {
						return result.set(ErrorEnum.REQUEST_ERROR).setMsg("该订单不是待支付状态");
					}
				} else if (OrderTypeEnum.INTEGRAL.getIndex().equals(orderType)) {// 积分兑换
					if (((IntegralGoodsOrder) param).getIgo_status() != 0) {
						return result.set(ErrorEnum.REQUEST_ERROR).setMsg("该订单不是待支付状态");
					}
				} else {
					if (((OrderForm) param).getOrder_status() != 10) {
						return result.set(ErrorEnum.REQUEST_ERROR).setMsg("该订单不是待支付状态");
					}
				}
			}
			
			// 订单时效校验，失效订单不走支付中心，直接返回
			Long invalidTime = this.validPrescription(param, orderType);
			if (invalidTime == null) {
				return result.set(ErrorEnum.REQUEST_ERROR).setMsg("该下单请求已失效，请重新向支付中心发起下单请求");
			}
			
			// 请求支付中心接口支付
			Object obj = this.pay(user, param, openId, channel, orderType, "", title, returnUrl, "", invalidTime, null);
			if (obj == null) {
				String errMsg = "支付中心异常：向支付中心下单时，支付中心返回null";
				logger.error(errMsg);
				return result.set(ErrorEnum.SYSTEM_ERROR).setMsg(errMsg);
			}			
			logger.info("支付下单时，支付中心返回数据：" + JSON.toJSONString(obj)); // 如：{"code":0,"msg":"OK","url":"https://xmpay.smi170.com/webpay.do?order_id=44524"}
			
			// 解析并设置支付中心返回结果
			this.analysisPayCenterJson(obj, result);
		} catch (Exception e) {
			logger.error("支付下单异常", e);
			result.set(ErrorEnum.SYSTEM_ERROR).setMsg("支付下单异常");
		}
		
		logger.info("================= 支付下单总时长：" + (new Date().getTime() - startTime) + "(毫秒)==================");
		
		return result;
	}

	
	
	/**
     * APP 支付下单接口
     * 
     * @param payType
     *            支付方式，必填。如：PayTypeEnum.ALI.name()。WX(微信支付，均可使用)、ALI(支付宝支付，均可使用)、YE(余额支付，购票、购物使用)
     * @param payPwd
     *            支付密码
     * @param user
     *            下单用户
     * @param orderId
     *            支付订单号
     * @param orderType
     *            订单类型，如：OrderTypeEnum.SHOPPING.getIndex()
     * @param title
     *            下单标题（暂时无用，传空串）
     * @param instNumber
     *            分期数(随变花支付必填)
     * @return
     */
	public Result appPlaceOrder(User user, String orderId, String payType, String payPwd, String orderType, String title, Integer instNumber) {
		Result result = new Result();
		Long startTime = new Date().getTime();
		logger.info("app支付下单请求参数：user=" + (user == null? "": user.getUserName()) + ", orderId=" + orderId + ", payType=" + payType + ", payPwd=" + (StringUtils.isNullOrEmpty(payPwd)? payPwd: "******") + ", orderType=" + orderType + ", title=" + title + ", instNumber=" + instNumber);
		
		try {
			// 校验输入参数
			if (this.appValidate(user, orderId, payType, payPwd, instNumber, orderType)) {
				logger.error("app支付下单请求参数校验失败：user=" + (user == null? "": user.getUserName()) + ", orderId=" + orderId + ", payType=" + payType + ", payPwd=" + (StringUtils.isNullOrEmpty(payPwd)? payPwd: "******") + ", instNumber=" + instNumber);
				return result.set(ErrorEnum.REQUEST_ERROR).setMsg("app支付下单请求参数校验失败");
			}
			
			Object param = null;
			if (OrderTypeEnum.CHARGE.getIndex().equals(orderType)) {// 充值订单，查询出的订单信息为Predeposit
				param = this.predepositService.getObjByProperty(null, "pd_sn", orderId);
			} else if (OrderTypeEnum.INTEGRAL.getIndex().equals(orderType)) {// 积分兑换
				param = this.integralGoodsOrderService.getObjByProperty(null, "igo_order_sn", orderId);
			} else {// 非充值订单，查询出的订单信息为OrderForm
				param = this.orderFormService.getObjByProperty(null, "order_id", orderId);
			}
			
			if (param == null) {
				logger.error("app支付下单时未查询到订单信息：orderType=" + orderType + "orderId=" + orderId);
				return result.set(ErrorEnum.REQUEST_ERROR).setMsg("未查询到该订单");
			} else {
				if (OrderTypeEnum.CHARGE.getIndex().equals(orderType)) {
					if (((Predeposit) param).getPd_pay_status() != 0) {
						return result.set(ErrorEnum.REQUEST_ERROR).setMsg("该订单不是待支付状态");
					}
				} else if (OrderTypeEnum.INTEGRAL.getIndex().equals(orderType)) { // 积分兑换
					if (((IntegralGoodsOrder) param).getIgo_status() != 0) {
						return result.set(ErrorEnum.REQUEST_ERROR).setMsg("该订单不是待支付状态");
					}
				} else {
					if (((OrderForm) param).getOrder_status() != 10) {
						return result.set(ErrorEnum.REQUEST_ERROR).setMsg("该订单不是待支付状态");
					}
				}
			}
			
			// 订单时效校验，失效订单不走支付中心，直接返回
			Long invalidTime = this.validPrescription(param, orderType);
			if (invalidTime == null) {
				return result.set(ErrorEnum.REQUEST_ERROR).setMsg("该下单请求已失效，请重新向支付中心发起下单请求");
			}
			
			// 请求支付中心接口支付
			Object obj = this.pay(user, param, "", ChannelEnum.APP.name(), orderType, payType, title, "", payPwd, invalidTime, instNumber);
			if (obj == null) {
				String errMsg = "app支付中心异常：向支付中心下单时，支付中心返回null";
				logger.error(errMsg);
				return result.set(ErrorEnum.SYSTEM_ERROR).setMsg(errMsg);
			}
			logger.info("app支付下单，支付中心返回数据：" + JSON.toJSONString(obj));
			
			// 解析并设置支付中心返回结果
			this.analysisPayCenterJson(obj, result);
		} catch (Exception e) {
			logger.error("app支付下单异常；", e);
			result.set(ErrorEnum.SYSTEM_ERROR).setMsg("app支付下单异常");
		}
		
		logger.info("================= app请求支付中心总时长：" + (new Date().getTime() - startTime) + "(毫秒) ===========");
		
		return result;
	}

	
	
	/**
     * 退款接口
     *
     * @param refundFee
     *            退款金额
     * @param refundNo
     *            退款单号，向支付中心下退款单时生成
     * @param originalNo
     *            原订单号，即OrderForm的order_id字段的值
     * @param refundType
     *            退款类型，如：RefundTypeEnum.GoodsRefund.getCode()
     * @param refundMode
     *            退款方式，部分退和全额退。如：RefundModeEnum.Full.getCode()
     * @param refundId
     *            如果是商品退款传ReturnGoodsLog的主键；如果是团购消费码就传GroupInfo的主键id ；如果是订单退款，则传待退款订单的主键id
     * @return
     */
	public Result refund(String refundFee, String originalNo, String returnRemark, String refundType, String refundMode, String refundId) {
		Result result = new Result();
		Long startTime = new Date().getTime();
		logger.info("支付中心退款下单参数：refundFee=" + refundFee + ", originalNo=" + originalNo + ", refundType=" + refundType + ", returnRemark" + returnRemark + ", refundMode=" + refundMode + ", refundId=" + refundId);
		
		try {
			// 校验输入参数
			if (this.refundValidate(refundFee, originalNo, refundType, refundMode)) {
				logger.error("支付中心退款参数校验失败：refundFee=" + refundFee + ", originalNo=" + originalNo + ", refundType=" + refundType + ", refundMode=" + refundMode);
				return result.set(ErrorEnum.REQUEST_ERROR).setMsg("支付中心退款参数校验失败");
			}
			
			// 请求支付中心接口支付
			Object obj = this.sendRefund(refundFee, originalNo, returnRemark, refundType, refundMode, refundId);
			if (obj == null) {
				return result.set(ErrorEnum.REQUEST_ERROR).setMsg("支付中心异常，返回结果为null");
			}
			
			logger.info("支付中心退款下单，支付中心返回数据：" + JSON.toJSONString(obj));
			
			// 解析并设置支付中心返回结果
			this.analysisPayCenterJson(obj, result);
		} catch (Exception e) {
			logger.error("支付中心退款异常", e);
			result.set(ErrorEnum.SYSTEM_ERROR).setMsg("支付中心退款异常");
		}
		
		logger.info("=================请求支付中心退款总时长：" + (new Date().getTime() - startTime) + "(毫秒)===========");
		
		return result;
	}



	/**
	 * 解析并设置支付中心返回结果
	 * @param payCenterJson
	 * @param result
	 */
	private void analysisPayCenterJson(Object payCenterJson, Result result) {
		JSONObject obj = (JSONObject)payCenterJson;
		// 获取结果码
		String code = obj.get("code").toString();
		// 如果不成功，则将支付中心错误描述信息设置到接口返回对象中
		if ("0".equals(code)) {
			result.set(ErrorEnum.SUCCESS);			
		} else {
			String msg = obj.get("msg").toString();
			if (!StringUtils.isNullOrEmpty(obj.get("errDetail"))) {
				msg = obj.get("errDetail").toString();
			}
			result.set(ErrorEnum.REQUEST_ERROR).setMsg(msg);
		}
		// 设置返回数据
		result.setData(payCenterJson);
	}

	
	
	/**
	 * 向支付中心发送退款请求
	 * @param refundFee
	 * @param originalNo 主订单的order_id
	 * @param returnRemark
	 * @param refundType 退款类型，如：RefundTypeEnum.GoodsRefund.getCode()
	 * @param refundMode 退款方式，部分退和全额退。如：RefundModeEnum.Full.getCode()
	 * @param refundId 如果是商品退款传ReturnGoodsLog的主键；如果是团购消费码就传GroupInfo的主键id ；如果是订单退款，则传待退款订单的主键id
	 * @return
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	private Object sendRefund(String refundFee, String originalNo, String returnRemark, String refundType, String refundMode,
			String refundId) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		
		String refundNo = CommUtil.formatTime("yyyyMMddHHmmssSSS", new Date()) + TenpayUtil.buildRandom(6) + refundType; // 生成退款单号
		
		OrderForm orderForm = this.orderFormService.getObjByProperty(null, "order_id", originalNo); // 查询订单信息
		
		if (RefundTypeEnum.GroupSNRefund.getCode().equals(refundType)) {
			// 团购消费码
			GroupInfo groupInfo = this.groupinfoService.getObjById(CommUtil.null2Long(refundId));
			if (groupInfo != null) {
				// 将订单类型评价在退款单号后，回调时通过该字段最后一位区分不同退款，0:商品退款；1：团购消费码退款；2：订单退款
				groupInfo.setRefundPayCenterNo(refundNo);
				// 设置退款备注
				groupInfo.setReturnRemark(returnRemark);
				// 设置退款金额
				groupInfo.setAmount(new BigDecimal(refundFee));
				this.groupinfoService.update(groupInfo);
			}
		} else if (RefundTypeEnum.GoodsRefund.getCode().equals(refundType)) {
			// 商品退款
			ReturnGoodsLog returnGoodsLog = this.returnGoodsLogService.getObjById(CommUtil.null2Long(refundId));
			if (returnGoodsLog != null) {
				// 将订单类型评价在退款单号后，回调时通过该字段最后一位区分不同退款，0:商品退款；1：团购消费码退款；2：订单退款
				returnGoodsLog.setRefundPayCenterNo(refundNo);
				// 设置退款备注
				returnGoodsLog.setReturnRemark(returnRemark);
				// 设置退款金额
				returnGoodsLog.setRefund(new BigDecimal(refundFee));
				this.returnGoodsLogService.update(returnGoodsLog);
			}
		} else if (RefundTypeEnum.OrderRefund.getCode().equals(refundType)) {
			OrderForm refundorderForm = this.orderFormService.getObjById(CommUtil.null2Long(refundId));// 待退款的订单Id
			// 将订单类型评价在退款单号后，回调时通过该字段最后一位区分不同退款，0:商品退款；1：团购消费码退款；2：订单退款
			refundorderForm.setRefundPayCenterNo(refundNo);
			// 设置退款金额
			refundorderForm.setRefund_amount(new BigDecimal(CommUtil.null2Double(refundFee)));
			// 设置退款备注
			refundorderForm.setRefund_cause(returnRemark);
			// 更新订单信息
			this.orderFormService.update(refundorderForm);
		}
		// 查询用户信息
		User user = userService.getObjById(CommUtil.null2Long(orderForm.getUser_id()));

		// 构建退款参数
		Map<String, Object> paramMap = this.buildRefundParams(user, refundFee, refundNo, originalNo, refundMode, orderForm, returnRemark, refundType, refundId);

		// 拼接支付中心请求地址
		String url = PropertyUtil.getProperty(PAY_CENTER_URL) + PayUrlEnum.REFUND_URL.getIndex();
		// 请求接口返回数据
		Long sendPostStartTime = new Date().getTime();
		String jsonStr = CommUtils.sendPost(url, paramMap, UTF_8);
		logger.info("================== 支付中心退款下单，请求支付中心时长：" + (new Date().getTime() - sendPostStartTime) + "(毫秒) =====================");
		return JSON.parse(jsonStr);
	}

	
	
	/**
	 * 组装退款参数
	 * 
	 * @param user
	 * @param refundFee
	 * @param refundNo
	 * @param originalNo
	 * @param refundType 退款类型，如：RefundTypeEnum.GoodsRefund.getCode()
	 * @param refundMode
	 *            退款方式：1:部分退 2：全部退。如：RefundModeEnum.Full.getCode()
	 * @param refundId
	 *            如果是商品退款传ReturnGoodsLog的主键；如果是团购消费码就传GroupInfo的主键id ；如果是订单退款，则传待退款订单的主键id
	 * @return
	 */
	private Map<String, Object> buildRefundParams(User user, String refundFee, String refundNo, String originalNo,
			String refundMode, OrderForm orderForm, String returnRemark, String refundType, String refundId) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 系统编码，区分不同系统
		map.put("appcode", SystemEnum.SMI_MALL.getIndex());
		// 请求序列，规则为渠道编码+yyyyMMddhhmmss+6位随机数
		String reqno = this.getReqno(SystemEnum.SMI_MALL.getIndex());
		map.put("reqno", reqno);
		// 用户唯一标识
		map.put("custId", user.getCustId());
		// 金额，必须是正整数，单位为分
		BigDecimal amount = new BigDecimal(CommUtil.mul(CommUtil.null2Double(refundFee), 100));
		map.put("refundFee", amount.intValue());
		// 退款单号，确保系统中唯一
		map.put("refundNo", refundNo);
		// 原来下单的订单号
		map.put("billNo", originalNo);
		// 描述备注信息
		map.put("memo", returnRemark);
		// 支付类型
		String payType = orderForm.getPayment() == null ? "" : orderForm.getPayment().getMark();
		if (!StringUtils.isNullOrEmpty(payType) && "balance".equals(payType)) {
			// 封装报文
			List<Map<String, Object>> params = this.buildCancelInfo(refundMode, refundFee, orderForm, refundType, refundId);
			// 支付的退单数据包
			map.put("packageValue", JSON.toJSONString(params));
		}
		// 签名，由业务系统根据签收规则生成：md5(md5(key+query)+key)，此字段应放到最后，因为sign的生成是根据其他所有参数来的。
		map.put("sign", this.buildSign(SystemEnum.SMI_MALL.getDescr(), map));
		// 打印退款下单报文
		logger.info("退款下单发送报文：" + JSON.toJSONString(map));
		return map;
	}

	
	
	/**
	 * 封装退款报文（余额退款时才会用到）
	 *
	 * @param refundMode
	 *            退款方式：如：RefundModeEnum.Partial.getCode()(部分退)
	 * @param refundFee
	 * @param orderForm
	 * @param refundType
	 *            退款类型：如：RefundTypeEnum.GoodsRefund.getCode()(商品退货退款)
	 * @param refundId
	 *            如果是商品退款传ReturnGoodsLog的主键；如果是团购消费码就传GroupInfo的主键id ；如果是订单退款，则传待退款订单的主键id
	 * @return
	 */
	private List<Map<String, Object>> buildCancelInfo(String refundMode, String refundFee, OrderForm orderForm, String refundType, String refundId) {
		OrderCancleReq cancelInfo = new OrderCancleReq();
		// 默认方式，退订支付方式，默认102：原方式(101：现金;102：原方式)
		cancelInfo.setCancelType("102");
		cancelInfo.setOrderId(SystemEnum.SMI_MALL.getIndex() + orderForm.getOrder_id());
		cancelInfo.setDesc("退款");

		List<CommodityCancle> commoditys = new ArrayList<CommodityCancle>();
		// 0:商品退款；1：团购消费码退款；2：订单退款
		if (RefundTypeEnum.GoodsRefund.getCode().equals(refundType)) {// 商品退款
			// 商品退款
			ReturnGoodsLog returnGoodsLog = this.returnGoodsLogService.getObjById(CommUtil.null2Long(refundId));
			// 组装报文
			feeManageservice.buildGoodsCommodity(commoditys, returnGoodsLog, refundFee);
		} else if (RefundTypeEnum.GroupSNRefund.getCode().equals(refundType)) {// 1：团购消费码退款
			// 团购消费码
			GroupInfo groupInfo = this.groupinfoService.getObjById(CommUtil.null2Long(refundId));
			// 组装报文
			feeManageservice.buildGroupInfoCommodity(orderForm, commoditys, groupInfo, refundFee);
		} else if (RefundTypeEnum.OrderRefund.getCode().equals(refundType) && RefundModeEnum.Partial.getCode().equals(refundMode)) {// 2：订单退款 且 为部分退款（全部退款不用组装报文）
			OrderForm cancleOrder = (OrderForm) orderForm;
			if (!StringUtils.isNullOrEmpty(cancleOrder.getChild_order_detail())) {// 如果有子订单
				cancleOrder = orderFormService.getObjById(CommUtil.null2Long(refundId));
			}
			// 组装报文
			refundFee = CommUtil.null2String(feeManageservice.assembleDatagramData(commoditys, cancleOrder, refundFee));
		}

		if (RefundModeEnum.Partial.getCode().equals(refundMode)) {
			cancelInfo.setCancelClass("101"); // 部分退
			cancelInfo.setCancelCharge(refundFee);
		} else if (RefundModeEnum.Full.getCode().equals(refundMode)) {
			cancelInfo.setCancelClass("102"); // 全部退
			cancelInfo.setCancelCharge("");// 因为计费中心对于全部退款时会校验退款金额（他们的代码问题，实际上对于全额退款是不需要校验金额的），经测试，传空串即可
		}
		cancelInfo.setCommodity(commoditys);

		return BusinessHandler.handParam(cancelInfo, OrderCancleReq.TYPE, OrderCancleReq.ATT_KEY_NAME);
	}

	
	
	/**
	 * 请求支付中心接口完成 支付下单
	 *
	 * @param user 下单用户.对于充值订单，user参数不会被使用
	 * @param order  订单，可能是以下类型的对象：OrderForm、Predeposit、IntegralGoodsOrder
	 * @param openId 微信公众号支付时必填。若不填，则应传空串，而不是null，否则会出现签名错误
	 * @param channel  支付渠道（不是订单的下单渠道），如：ChannelEnum.APP.name()
	 * @param orderType 订单类型，如：OrderTypeEnum.SHOPPING.getIndex()
	 * @param payType
	 *            支付方式，如：PayTypeEnum.YE.name()、PayTypeEnum.ALI.name()。channel为app时必需传此参数，其他下单渠道因为是统一调用支付中心的支付页面,故不用传此参数。
	 * @param title 下单标题（暂时无用，传空串）
	 * @param returnUrl 充值成功后，支付中心将跳转到的业务系统页面地址，如支付成功后跳转到业务系统的订单列表页面
	 * @param payPwd 
	 * @param invalidTime 支付下单请求的时效性
	 * @param instNumber
	 *            随变花分期数(随变花支付必填)
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	private Object pay(User user, Object order, String openId, String channel, String orderType, String payType,
			String title, String returnUrl, String payPwd, Long invalidTime, Integer instNumber) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		if(instNumber == null) {//若未传instNumber，则必须将此参数置为一个非空值，否则服务端参数校验会失败
			instNumber = 0;
		}
		
		// 构建下单参数
		Map<String, Object> paramMap = this.buildOrderParams(user, order, openId, channel, orderType, payType, title, returnUrl, payPwd, invalidTime, instNumber);

		// 拼接支付中心请求地址
		String url = PropertyUtil.getProperty(PAY_CENTER_URL) + PayUrlEnum.BILL_URL.getIndex();
		
		// 请求接口返回数据
		Long sendPostStartTime = new Date().getTime();
		
		String jsonStr = CommUtils.sendPost(url, paramMap, UTF_8);
		
		logger.info("================== 请求支付中心时长：" + (new Date().getTime() - sendPostStartTime) + "(毫秒)===================");
		
		logger.info("支付下单，支付中心的响应：" + jsonStr);
		
		return JSON.parse(jsonStr);
	}
	
	

	/**
	 * web和 h5支付下单时构建请求参数
	 * 
	 * @param user 对于充值订单，此参数不会被使用
	 * @param order
	 * @param openId
	 * @param channel 支付渠道（不是订单的下单渠道），如：ChannelEnum.APP.name()
	 * @param orderType 订单类型，如：OrderTypeEnum.SHOPPING.getIndex()
	 * @param payType 付方式，如：PayTypeEnum.YE.name()、PayTypeEnum.ALI.name()。channel为app时必需传此参数，其他下单渠道因为是统一调用支付中心的支付页面,故不用传此参数。
	 * @param title
	 * @param returnUrl 充值成功后，支付中心将跳转到的业务系统页面地址，如支付成功后跳转到业务系统的订单列表页面
	 * @param payPwd
	 * @param invalidTime
	 * @param instNumber 随变花分期数(随变花支付必填)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, Object> buildOrderParams(User user, Object order, String openId, String channel, String orderType,
			String payType, String title, String returnUrl, String payPwd, Long invalidTime, Integer instNumber) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 系统编码，区分不同系统
		map.put("appcode", SystemEnum.SMI_MALL.getIndex());
		// 请求序列，规则为渠道编码+yyyyMMddhhmmss+6位随机数
		String reqno = this.getReqno(SystemEnum.SMI_MALL.getIndex());
		map.put("reqno", reqno);
		// 根据不同场景选择不同的下单方式APP、 WX、WEB、WAP
		map.put("channel", channel);
		// 支付方式
		if (ChannelEnum.APP.name().equals(channel)) {
			map.put("payType", payType);
		}
		// 订单类型
		map.put("billType", orderType);
		// 支付金额，必须是正整数，单位为分
		if (OrderTypeEnum.CHARGE.getIndex().equals(orderType)) {// 充值订单，查询出的订单信息为Predeposit
			Predeposit predeposit = (Predeposit) order;
			// 支付金额，必须是正整数，单位为分
			map.put("totalFee", predeposit.getPd_amount().multiply(new BigDecimal(100)).intValue());
			// 业务订单号
			map.put("billNo", predeposit.getPd_sn());
			// 用户唯一标识,设置被充值人的custId
			map.put("custId", (predeposit.getPd_user() == null ? new User() : predeposit.getPd_user()).getCustId());
		} else if (OrderTypeEnum.INTEGRAL.getIndex().equals(orderType)) {// 积分兑换
			IntegralGoodsOrder integralGoodsOrder = (IntegralGoodsOrder) order;
			// 支付金额，必须是正整数，单位为（分）
			// 转换订单总价类型，单位是（元）
			if (integralGoodsOrder.getIgo_trans_fee() != null) {
				BigDecimal totalAmount = integralGoodsOrder.getIgo_trans_fee().multiply(new BigDecimal(100));
				map.put("totalFee", totalAmount.intValue());
			}
			// 业务订单号
			map.put("billNo", integralGoodsOrder.getIgo_order_sn());
			// 用户唯一标识
			map.put("custId", user.getCustId());

		} else {// 非充值订单，查询出的订单信息为OrderForm
			OrderForm orderForm = (OrderForm) order;
			// 获取订单总价
			double order_total_price = CommUtil.null2Double(orderForm.getTotalPrice());
			// 如果有子订单获取所有订单的总价之后
			if (!StringUtils.isNullOrEmpty(orderForm.getChild_order_detail()) && orderForm.getOrder_cat() != 2) {
				order_total_price = this.orderFormTools.query_order_price(CommUtil.null2String(orderForm.getId()));
			}
			// 转换订单总价类型，单位是（元）
			BigDecimal totalAmount = new BigDecimal(CommUtil.mul(order_total_price, 100));
			// 支付金额，必须是正整数，单位为（分）
			map.put("totalFee", totalAmount.intValue());
			// 业务订单号
			map.put("billNo", orderForm.getOrder_id());
			// 用户唯一标识
			map.put("custId", user.getCustId());
		}
		// 订单描述或商品名称
		map.put("title", this.getTitle(orderType, order));
		// 回调商户页面
		map.put("returnUrl", returnUrl);
		// 用户相对于微信公众号的唯一id
		map.put("openId", openId);
		// payType为YE时需填写commodity 此时order为OrderForm类型
		if (OrderTypeEnum.SHOPPING.getIndex().equals(orderType)) {
			List<Commodity> commoditys = new ArrayList<Commodity>();
			// 主订单商品信息
			commoditys.addAll(feeManageservice.getOrderCommodities((OrderForm) order));
			// 子订单商品信息
			if (!StringUtils.isNullOrEmpty(((OrderForm) order).getChild_order_detail())) {
				List<Map> childMaps = this.orderFormTools.queryGoodsInfo(((OrderForm) order).getChild_order_detail());
				for (Map childMap : childMaps) {
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(childMap.get("order_id")));
					commoditys.addAll(feeManageservice.getOrderCommodities(child_order));
				}
			}
			map.put("commodity", JSON.toJSONString(commoditys));
		}
		// 支付密码
		map.put("payPwd", payPwd);
		// 订单失效时间
		map.put("expDate", invalidTime);
		// 随变花分期数
		map.put("instNumber", instNumber);
		// 签名，由业务系统根据签收规则生成：md5(key+md5(query+key))
		map.put("sign", this.buildSign(SystemEnum.SMI_MALL.getDescr(), map));
		
		logger.info("请求支付中心支付下单的报文：" + JSON.toJSONString(map));
		return map;
	}

	
	
	/**
	 * 获取支付订单描述信息
	 *
	 * @param orderType
	 * @param order
	 * @return
	 */
	private String getTitle(String orderType, Object order) {
		String title = "星美商城购物";
		// 充值订单，查询出的订单信息为Predeposit
		if (OrderTypeEnum.CHARGE.getIndex().equals(orderType)) {
			Predeposit predeposit = (Predeposit) order;
			title = "星美充值(" + predeposit.getPd_amount() + "元)";
		}
		// 积分兑换
		else if (OrderTypeEnum.INTEGRAL.getIndex().equals(orderType)) {
			IntegralGoodsOrder integralGoodsOrder = (IntegralGoodsOrder) order;
			title = (orderFormTools.queryGoodsInfo(integralGoodsOrder.getGoods_info()).get(0) == null ? "积分兑换"
					: orderFormTools.queryGoodsInfo(integralGoodsOrder.getGoods_info()).get(0).get("ig_goods_name")).toString();
		}
		// 非充值订单，查询出的订单信息为OrderForm
		else {
			OrderForm orderForm = (OrderForm) order;
			if (orderForm.getOrder_cat() == 2) {
				title = (orderFormTools.queryGroupInfo(orderForm.getGroup_info()) == null ? "星美团购" : orderFormTools
						.queryGroupInfo(orderForm.getGroup_info()).get("goods_name")).toString();
			} else {
				title = (orderFormTools.queryGoodsInfo(orderForm.getGoods_info()).get(0) == null ? "星美购物" : orderFormTools
						.queryGoodsInfo(orderForm.getGoods_info()).get(0).get("goods_name")).toString();
			}
		}
		return title;
	}

	
	
	/**
	 * 签名，由业务系统根据签收规则生成：md5(md5(key+query)+key)
	 *
	 * @param key
	 * @param appcode
	 * @param reqno
	 * @return
	 */
	private String buildSign(String key, Map<String, Object> params) {
		String queryStr = CommUtils.createLinkString(params);
		return MD5Util.getMD5String(key + MD5Util.getMD5String(queryStr + key));
	}

	
	
	/**
	 * 生成请求序列,渠道编码+yyyyMMddhhmmss+6位随机数
	 *
	 * @param sysCode
	 * @return
	 */
	private String getReqno(String sysCode) {
		return sysCode + TenpayUtil.getCurrTime() + TenpayUtil.buildRandom(6);
	}


	
	
	/**
	 * 校验web 和 h5 支付下单接口的输入参数
	 * 
	 * @return  true:校验不通过, false：校验通过
	 */
	private boolean validate(String orderId, String openId, String channel, String orderType) {
		boolean result = false;
		if (StringUtils.isNullOrEmpty(orderId) || OrderTypeEnum.getDescr(orderType) == null || ChannelEnum.parse(channel) == null) {
			result = true;
		} else if (ChannelEnum.WX.name().equals(channel)) {// 微信时，必需传openId；
			if (StringUtils.isNullOrEmpty(openId)) {
				result = true;
			}
		}
		return result;
	}

	
	
	/**
	 * app支付下单接口参数校验
	 *
	 * @param user
	 * @param orderId
	 * @param payType
	 * @param payPwd
	 * @return true:校验不通过
	 */
	private boolean appValidate(User user, String orderId, String payType, String payPwd, Integer instNumber, String orderType) {
		boolean result = false;
		if (StringUtils.isNullOrEmpty(orderId) || PayTypeEnum.parse(payType) == null || OrderTypeEnum.getDescr(orderType) == null) {
			result = true;
		} else if ((PayTypeEnum.YE.name().equals(payType) || PayTypeEnum.YP.name().equals(payType))) {
			if (StringUtils.isNullOrEmpty(payPwd)) {// payType为YE、YP时payPwd必填
				result = true;
			}
		} else if (PayTypeEnum.SBH.name().equals(payType)) {
			if (StringUtils.isNullOrEmpty(payPwd) || null == instNumber) {// payType为SBH时payPwd、instNumber必填
				result = true;
			}
		}
		return result;
	}

	
	
	/**
	 * 退款参数校验
	 * 
	 * @param refundFee
	 * @param originalNo
	 * @param refundType
	 * @param refundMode
	 * @return
	 */
	private boolean refundValidate(String refundFee, String originalNo, String refundType, String refundMode) {
		if (StringUtils.isNullOrEmpty(refundFee) || StringUtils.isNullOrEmpty(originalNo) || RefundTypeEnum.parseCode(refundType) == null || RefundModeEnum.parseCode(refundMode) == null) {
			return true;
		}
		
		return false;
	}

	
	
	/**
     * 支付回调
     * 
     * @param code
     *            ：0表示成功
     * @param msg
     *            ：成功信息
     * @param errDetail
     *            ：错误信息
     * @param tradeNo
     *            ：支付中心对应的流水号，如：1000420160901001522223270560
     * @param thirdNo
     *            ：第三方支付的订单号，如2016090121001004870224570589
     * @param billNo
     *            ：当前业务系统中的订单号，如：20160901001522223270560(购物订单的订单号)，pd20160901000717632213599(充值订单的订单号)
     * @param billType
     *            ：订单类型，如：OrderTypeEnum.CHARGE（充值订单）、OrderTypeEnum.SHOPPING（购物订单）、OrderTypeEnum.INTEGRAL（积分订单）
     * @param payType
     *            ：支付方式，如：PayTypeEnum.YE、PayTypeEnum.ALI、PayTypeEnum.WX、PayTypeEnum.UN、PayTypeEnum.SBH、PayTypeEnum.YP
     * @param channel
     *            ：订单的支付渠道（不是订单的下单渠道），如：ChannelEnum.WX、ChannelEnum.APP、ChannelEnum.WEB、ChannelEnum.WAP
     */
	public Result payCallBack(HttpServletRequest request, Integer code, String msg, String errDetail,
			String tradeNo, String thirdNo, String billNo, String billType, String payType, String channel) {
		Result result = new Result();
		logger.info("支付中心支付回调参数：code=" + code + ", msg=" + msg + ", errDetail=" + errDetail + ", tradeNo=" + tradeNo + ", thirdNo="
				+ thirdNo + ", billNo=" + billNo + ", billType=" + billType + ", payType=" + payType + ", channel=" + channel);
		try {
			if (code != 0) {// 回调异常
				logger.error("支付中心支付回调异常：code=" + code + ", msg=" + msg + ", errDetail" + errDetail);
				result.set(ErrorEnum.SYSTEM_ERROR).setMsg("支付中心支付回调异常(code=" + code + ")");
			} else {// 回调正常，处理具体业务逻辑
				String errorMsg = this.dealPayBusiness(request, tradeNo, thirdNo, billNo, billType, payType, channel);
				// 充值回调时，调用了外部接口，有可能会失败，如果失败会返回对应错误信息，此处通过判断是否有错误信息来区分充值回调是否成功
				if (!StringUtils.isNullOrEmpty(errorMsg)) {
					return result.set(ErrorEnum.REQUEST_ERROR).setMsg(errorMsg);
				}
			}
		} catch (Exception e) {
			logger.error("支付中心支付回调处理逻辑异常", e);
			result.set(ErrorEnum.SYSTEM_ERROR).setMsg("支付中心支付回调处理逻辑异常：" + e.getMessage());
		}
		return result;
	}

	
	
	/**
	 * 退款回调
	 *
	 * @param request
	 * @return
	 */
	@Override
	public Result refundCallBack(Integer code, String msg, String errDetail, String refundNo, String tradeNo,
			String thirdNo, String billNo, String billType) {
		Result result = new Result();
		logger.info("支付中心退款回调参数：code=" + code + ", msg=" + msg + ", errDetail=" + errDetail + ", refundNo=" + refundNo
				+ ", tradeNo=" + tradeNo + ", thirdNo=" + thirdNo + ", billNo=" + billNo + ", billType=" + billType);
		try {
			if (code != 0) {// 回调异常
				logger.error("支付中心退款回调异常：code=" + code + ", msg=" + msg + ", errDetail" + errDetail);
				result.set(ErrorEnum.SYSTEM_ERROR).setMsg("支付中心退款回调异常(code=" + code + ")");
			} else {// 回调正常，处理具体业务逻辑
				this.dealRefundBusiness(refundNo, tradeNo, thirdNo, billNo, billType, code);
			}
		} catch (Exception e) {
			logger.error("支付中心退款回调处理异常", e);
			result.set(ErrorEnum.SYSTEM_ERROR).setMsg("支付中心退款回调处理异常：" + e.getMessage());
		}
		return result;
	}

	
	
	/**
	 * 退款成功后回调处理相关业务
	 *
	 * @param refundNo
	 * @param tradeNo
	 * @param thirdNo
	 * @param billNo
	 * @param billType
	 */
	private void dealRefundBusiness(String refundNo, String tradeNo, String thirdNo, String billNo, String billType, Integer code) {
		if (!StringUtils.isNullOrEmpty(refundNo)) {
			String refund_user_id = "";
			String obj_id = "";
			String info = "";
			String gi_id = "";
			String amount = "";
			// 获取退款订单类型。0:商品退款；1：团购消费码退款；2：订单退款
			String refundNoType = refundNo.substring(refundNo.length() - 1, refundNo.length());
			// 查询参数
			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("refundNo", refundNo);
			if (RefundTypeEnum.GoodsRefund.getCode().equals(refundNoType)) { // 0：商品退款；
				List<ReturnGoodsLog> returnGoodsLogList = this.returnGoodsLogService.query(
						"select obj from ReturnGoodsLog obj where obj.refundPayCenterNo=:refundNo", paraMap, -1, -1);
				if (returnGoodsLogList != null && returnGoodsLogList.size() > 0) {
					ReturnGoodsLog returnGoodsLog = returnGoodsLogList.get(0);
					refund_user_id = String.valueOf(returnGoodsLog.getUser_id());
					obj_id = String.valueOf(returnGoodsLog.getId());
					info = returnGoodsLog.getReturnRemark();
					amount = returnGoodsLog.getRefund() + "";
				}
			} else if (RefundTypeEnum.GroupSNRefund.getCode().equals(refundNoType)) {// 1：团购消费码退款；
				List<GroupInfo> groupInfoList = this.groupinfoService.query(
						"select obj from GroupInfo obj where obj.refundPayCenterNo=:refundNo", paraMap, -1, -1);
				if (groupInfoList != null && groupInfoList.size() > 0) {
					GroupInfo groupInfo = groupInfoList.get(0);
					refund_user_id = String.valueOf(groupInfo.getUser_id());
					gi_id = String.valueOf(groupInfo.getId());
					info = groupInfo.getReturnRemark();
					amount = groupInfo.getAmount() + "";
				}
			} else if (RefundTypeEnum.OrderRefund.getCode().equals(refundNoType)) {// 2：订单退款
				// 查询退款订单信息
				OrderForm orderForm = this.orderFormService.getObjByProperty(null, "refundPayCenterNo", refundNo);
				// 退款成功后修改满减json
				orderFormService.modifyEnoughReduceInfo(orderForm);
				// 退款成功修改运费json
				orderFormService.modifyFreightInfo(orderForm);
				// 成功后，修改优惠的活动金额
				orderFormService.modifyActivityAmount(orderForm);
				// 回退成功后更新订单状态
				orderForm.setOrder_status(90);
				// 回退成功后更新订单状态
				this.orderFormService.update(orderForm);
				// 更新订单优惠券和活动的已退款金额,商品的退款状态
				refundTools.updateOrderDiscount(orderForm, "", orderForm.getEnough_reduced_amount() + "", null);
				User user = this.userService.getObjById(CommUtil.null2Long(orderForm.getUser_id()));
				RefundLog r_log = new RefundLog();
				r_log.setAddTime(new Date());
				r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + user.getId());
				r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
				r_log.setRefund_log(orderForm.getRefund_cause());
				r_log.setRefund_type((orderForm.getPayment() == null ? new Payment() : orderForm.getPayment()).getName());
				r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
				r_log.setReturnLog_userName(user.getUserName());
				r_log.setReturnLog_userId(user.getId());
				this.refundLogService.save(r_log);
				String msg_content = "订单" + orderForm.getOrder_id() + "退款成功，退款金额" + orderForm.getRefund_amount() + "元。";
				// 发送系统站内信
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(user);
				this.messageService.save(msg);
			}
			// 处理商品退款和团购消费码退款
			this.retPaySave(amount, info, refund_user_id, obj_id, code.toString(), gi_id, refundNoType);
		}
	}

	
	
	/**
	 * 退款回调记录相关日志，
	 * 
	 * @param amount
	 * @param info
	 * @param refund_user_id
	 * @param obj_id
	 * @param status
	 * @param gi_id
	 */
	@SuppressWarnings("rawtypes")
	public void retPaySave(String amount, String info, String refund_user_id, String obj_id, String status, String gi_id,
			String refundNoType) {
		User user = this.userService.getObjById(CommUtil.null2Long(refund_user_id));
		if (RefundTypeEnum.GoodsRefund.getCode().equals(refundNoType)) {// 商品退款
			ReturnGoodsLog rgl = this.returnGoodsLogService.getObjById(CommUtil.null2Long(obj_id));
			OrderForm order = this.orderFormService.getObjById(rgl.getReturn_order_id());
			// 修改优惠券已退金额和满减已退金额
			refundTools.updateOrderDiscount(order, rgl.getCouponDiscount() + "", rgl.getEnoughReduceDiscount() + "",
					CommUtil.null2String(rgl.getGoods_id()));
			rgl.setRefund_status(1);// 退款状态 0为未退款 1为退款完成2退款请求已提交
			if ("1".equals(status)) {
				rgl.setRefund_status(2);
			}
			rgl.setGoods_return_status("11");// 平台退款完成
			rgl.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
			this.returnGoodsLogService.update(rgl);

			RefundLog r_log = new RefundLog();
			r_log.setAddTime(new Date());
			r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + user.getId());
			r_log.setReturnLog_id(rgl.getId());
			r_log.setReturnService_id(rgl.getReturn_service_id());
			r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
			r_log.setRefund_log(info);
			r_log.setRefund_type((order.getPayment() == null ? new Payment() : order.getPayment()).getName());
			r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
			r_log.setReturnLog_userName(rgl.getUser_name());
			r_log.setReturnLog_userId(rgl.getUser_id());
			this.refundLogService.save(r_log);

			OrderForm of = this.orderFormService.getObjById(rgl.getReturn_order_id());
			Goods goods = this.goodsService.getObjById(rgl.getGoods_id());
			// 如果为自营商品时不添加结算日志，只有第三方经销商的商品才有结算日志
			if (goods.getGoods_type() == 1) {
				Store store = this.goodsService.getObjById(rgl.getGoods_id()).getGoods_store();
				PayoffLog pol = new PayoffLog();
				pol.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + store.getUser().getId());
				pol.setAddTime(new Date());
				pol.setGoods_info(of.getReturn_goods_info());
				pol.setRefund_user_id(rgl.getUser_id());
				pol.setSeller(goods.getGoods_store().getUser());
				pol.setRefund_userName(rgl.getUser_name());
				pol.setReturn_service_id(rgl.getReturn_service_id());
				pol.setPayoff_type(-1);
				pol.setPl_info("退款完成");
				BigDecimal price = BigDecimal.valueOf(CommUtil.null2Double(amount)); // 商品的原价
				BigDecimal mission = BigDecimal.valueOf(CommUtil.subtract(1, rgl.getGoods_commission_rate()));// 商品的佣金比例
				BigDecimal final_money = BigDecimal.valueOf(CommUtil.subtract(0, CommUtil.mul(price, mission)));
				pol.setTotal_amount(final_money);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Map<String, Object> json = new HashMap<String, Object>();
				json.put("goods_id", rgl.getGoods_id());
				json.put("goods_name", rgl.getGoods_name());
				if (rgl.getGoods_price() != null) {
					json.put("goods_price", rgl.getGoods_price());
				} else {
					json.put("goods_price", "");
				}
				json.put("goods_mainphoto_path", rgl.getGoods_mainphoto_path());
				json.put("goods_commission_rate", rgl.getGoods_commission_rate());
				json.put("goods_count", rgl.getGoods_count());
				json.put("goods_all_price", rgl.getGoods_all_price());
				json.put("goods_commission_price", CommUtil.mul(rgl.getGoods_all_price(), rgl.getGoods_commission_rate()));
				json.put("goods_payoff_price", final_money);
				list.add(json);
				pol.setReturn_goods_info(Json.toJson(list, JsonFormat.compact()));
				pol.setO_id(CommUtil.null2String(rgl.getReturn_order_id()));
				pol.setOrder_id(of.getOrder_id());
				pol.setCommission_amount(BigDecimal.valueOf(0));
				pol.setOrder_total_price(final_money);
				pol.setShip_price(BigDecimal.valueOf(0.00));// 商品退款运费为零
				this.payoffLogService.save(pol);

				store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.subtract(store.getStore_sale_amount(), amount)));// 减少店铺本次结算总销售金额
				store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.subtract(store.getStore_payoff_amount(),
						CommUtil.mul(price, mission))));// 减少店铺本次结算总金额
				this.storeService.update(store);

				// 减少系统总销售金额、总结算金额
				SysConfig sc = this.configService.getSysConfig();
				sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_sale(), amount)));
				sc.setPayoff_all_amount(BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_amount(),
						CommUtil.mul(price, mission))));
				sc.setPayoff_all_amount_reality(BigDecimal.valueOf(CommUtil.add(pol.getReality_amount(),
						sc.getPayoff_all_amount_reality())));// 增加系统实际总结算
				this.configService.update(sc);
			}

			String msg_content = "成功为订单号：" + of.getOrder_id() + "退款" + amount + "元退款完成。";
			if ("1".equals(status)) {
				msg_content = "成功为订单号：" + of.getOrder_id() + "退款" + amount + "元提交完成。";
			}
			// 发送系统站内信
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setStatus(0);
			msg.setType(0);
			msg.setContent(msg_content);
			msg.setFromUser(SecurityUserHolder.getCurrentUser());
			msg.setToUser(user);
			this.messageService.save(msg);
		} else if (RefundTypeEnum.GroupSNRefund.getCode().equals(refundNoType)) {// 团队消费码退款
			GroupInfo gi = this.groupinfoService.getObjById(CommUtil.null2Long(gi_id));
			gi.setStatus(7); // 退款完成
			if ("1".equals(status)) {
				gi.setStatus(9);
			}
			this.groupinfoService.update(gi);
			OrderForm of = this.orderFormService.getObjById(gi.getOrder_id());
			if (of != null && of.getOrder_form() == 0) { // 商家订单生成退款结算账单
				Store store = this.storeService.getObjById(CommUtil.null2Long(of.getStore_id()));
				PayoffLog pol = new PayoffLog();
				User storeUser = store == null ? new User() : store.getUser();
				pol.setPl_sn("pl" + CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + storeUser.getId());
				pol.setAddTime(new Date());
				pol.setGoods_info(of.getReturn_goods_info());
				pol.setRefund_user_id(gi.getUser_id());
				pol.setSeller(storeUser);
				pol.setRefund_userName(gi.getUser_name());
				pol.setPayoff_type(-1);
				pol.setPl_info("退款完成");
				BigDecimal price = BigDecimal.valueOf(CommUtil.null2Double(amount)); // 商品的原价
				BigDecimal final_money = BigDecimal.valueOf(CommUtil.subtract(0, price));
				pol.setTotal_amount(final_money);
				// 将订单中group_info（{}）转换为List<Map>([{}])
				List<Map> Map_list = new ArrayList<Map>();
				Map group_map = this.orderFormTools.queryGroupInfo(of.getGroup_info());
				Map_list.add(group_map);
				pol.setReturn_goods_info(Json.toJson(Map_list, JsonFormat.compact()));
				pol.setO_id(of.getId().toString());
				pol.setOrder_id(of.getOrder_id());
				pol.setCommission_amount(BigDecimal.valueOf(0));
				pol.setOrder_total_price(final_money);
				pol.setShip_price(BigDecimal.valueOf(0.00));// 商品退款运费为零
				this.payoffLogService.save(pol);

				store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.subtract(store.getStore_sale_amount(), amount))); // 减少店铺本次结算总销售金额
				store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.subtract(store.getStore_payoff_amount(), price))); // 减少店铺本次结算总金额
				this.storeService.update(store);
				// 减少系统总销售金额、总结算金额
				SysConfig sc = this.configService.getSysConfig();
				sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_sale(), amount)));
				sc.setPayoff_all_amount(BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_amount(),
						CommUtil.mul(amount, 0))));
				sc.setPayoff_all_amount_reality(BigDecimal.valueOf(CommUtil.add(pol.getReality_amount(),
						sc.getPayoff_all_amount_reality()))); // 增加系统实际总结算
				this.configService.update(sc);
			}
			// 生成退款日志
			RefundLog r_log = new RefundLog();
			r_log.setAddTime(new Date());
			r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + user.getId());
			r_log.setReturnLog_id(gi.getId());
			r_log.setReturnService_id(gi.getGroup_sn());
			r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
			r_log.setRefund_log(info);
			r_log.setRefund_type((of.getPayment() == null ? new Payment() : of.getPayment()).getName());
			r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
			r_log.setReturnLog_userName(gi.getUser_name());
			r_log.setReturnLog_userId(gi.getUser_id());
			this.refundLogService.save(r_log);
			String msg_content = "您的团购商品：" + gi.getLifeGoods().getGg_name() + "消费码已经成功退款，退款金额为："
					+ gi.getLifeGoods().getGroup_price() + "，退款消费码:" + gi.getGroup_sn();
			if ("1".equals(status)) {
				msg_content = "您的团购商品：" + gi.getLifeGoods().getGg_name() + "消费码已经成功提交退款，退款金额为："
						+ gi.getLifeGoods().getGroup_price() + "，退款消费码:" + gi.getGroup_sn();
			}
			// 发送系统站内信
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setStatus(0);
			msg.setType(0);
			msg.setContent(msg_content);
			msg.setFromUser(SecurityUserHolder.getCurrentUser());
			msg.setToUser(user);
			this.messageService.save(msg);

		}
	}

	
	/**
	 * 支付成功后处理相关业务逻辑
	 *
	 * @param tradeNo
	 * @param thirdNo
	 * @param billNo
	 * @param billType
	 * @throws Exception
	 */
	private String dealPayBusiness(HttpServletRequest request, String tradeNo, String thirdNo, String billNo,
			String billType, String payType, String channel) throws Exception {
		String errorMsg = "";
		// 获取支付类型
		String subPayType = this.getSubPayType(payType, channel);
		if(subPayType == null) {
			logger.error("支付中心返回了商城当前系统中未配置的支付方式：payType=" + payType + ", channel=" + channel);
			SmiBusinessException ex = new SmiBusinessException("未识别的支付方式");
			throw ex;
		}
		// 充值类型
		if (OrderTypeEnum.CHARGE.getIndex().equals(billType)) {
			Predeposit predeposit = this.predepositService.getObjByProperty(null, "pd_sn", billNo);
			if (predeposit != null) {
				// 处理充值业务
				errorMsg = this.dealPredeposit(predeposit, tradeNo, thirdNo, subPayType);
			} else {
				SmiBusinessException exception = new SmiBusinessException("支付回调业务系统未查询到对应充值订单！");
				logger.error(exception.getMessage(), exception);
				throw exception;
			}
		} else if (OrderTypeEnum.SHOPPING.getIndex().equals(billType)) {// 购物类型、消费码
			OrderForm orderForm = this.orderFormService.getObjByProperty(null, "order_id", billNo);
			if (orderForm != null) {
				// 处理购物业务
				this.dealOrderForm(orderForm, billType, request, tradeNo, thirdNo, subPayType);
			} else {
				SmiBusinessException exception = new SmiBusinessException("支付回调业务系统未查询到对应购物订单！");
				logger.error(exception.getMessage(), exception);
				throw exception;
			}
		} else if (OrderTypeEnum.INTEGRAL.getIndex().equals(billType)) {
			IntegralGoodsOrder igOrder = this.integralGoodsOrderService.getObjByProperty(null, "igo_order_sn", billNo);
			// 处理积分兑换业务
			this.dealIntegralGoodsOrder(igOrder, subPayType);
		}
		
		return errorMsg;
	}
	
	

	
	/**
	 * 获取对应当前业务系统的支付类型
	 * @param payType 支付方式，如：PayTypeEnum.YE、PayTypeEnum.ALI、PayTypeEnum.WX、PayTypeEnum.UN、PayTypeEnum.SBH、PayTypeEnum.YP
	 * @param channel 订单的支付渠道（不是订单的下单渠道），如：ChannelEnum.WX、ChannelEnum.APP、ChannelEnum.WEB、ChannelEnum.WAP
	 * @return 若未找到对应的支付类型，则返回null
	 */
	private String getSubPayType(String payType, String channel) {
		String subPayType = null;
		if (!StringUtils.isNullOrEmpty(payType) && !StringUtils.isNullOrEmpty(channel)) {
			// 如果下单方式是WX
			if (ChannelEnum.WX.name().equals(channel)) {
				if (PayTypeEnum.YE.name().equals(payType)) {
					subPayType = "balance";
				} else {
					subPayType = "wx_pay";
				}

			} else {
				if (PayTypeEnum.WX.name().equals(payType)) {// 支付方式为微信
					if (ChannelEnum.APP.name().equals(channel)) { // 下单方式是APP
						subPayType = "wx_app";
					} else if (ChannelEnum.WAP.name().equals(channel) || ChannelEnum.WEB.name().equals(channel)) {// 下单方式是WAP
						subPayType = "wx_pay";
					}
				} else if (PayTypeEnum.ALI.name().equals(payType)) {// 支付方式为支付宝
					if (ChannelEnum.APP.name().equals(channel)) { // 下单方式是APP
						subPayType = "alipay_app";
					} else if (ChannelEnum.WAP.name().equals(channel)) {// 下单方式是WAP
						subPayType = "alipay_wap";
					} else if (ChannelEnum.WEB.name().equals(channel)) {// 下单方式是WEB
						subPayType = "alipay";
					}
				} else if (PayTypeEnum.YE.name().equals(payType)) {// 支付方式为余额
					subPayType = "balance";
				} else if (PayTypeEnum.UN.name().equals(payType)) {// 银联支付
					if (ChannelEnum.APP.name().equals(channel)) { // 下单方式是APP
						subPayType = "unionpay_app";
					} else if (ChannelEnum.WAP.name().equals(channel)) {// 下单方式是WAP
						subPayType = "unionpay_wap";
					} else if (ChannelEnum.WEB.name().equals(channel)) {// 下单方式是WEB
						subPayType = "chinapay";
					} else {
						subPayType = "chinapay_nocard";//????
					}
				} else if(PayTypeEnum.SBH.name().equals(payType)) {
					if(ChannelEnum.APP.name().equals(channel)) {
						subPayType = "sbh_app";
					}
				}
			}
		}
		return subPayType;
	}

	
	/**
	 * 处理充值成功后相关业务
	 *
	 * @param predeposit
	 * @param trade_no
	 */
	private String dealPredeposit(Predeposit predeposit, String tradeNo, String thirdNo, String subPayType) {
		String errorMsg = "";
		if (predeposit.getPd_pay_status() < 2) {
			// 支付回调成功后调用BCP进行生活卡充值。(异步)
			User user = this.userService.getObjById((predeposit.getPd_user() == null ? new Predeposit() : predeposit
					.getPd_user()).getId());
			ResultDTO resultDTO = feeManageConnector.recharge(user == null ? null : user.getCustId(),
					CommUtil.null2String(predeposit.getPd_amount()), predeposit.getPd_sn());
			if (null != resultDTO && EInterfaceState.SUCCESS.getState().equals(resultDTO.getResult())) {
				predeposit.setPd_status(1);
				predeposit.setPd_pay_status(2);
				predeposit.setPayCenterNo(tradeNo);// 支付中心返回的交易流水号
				predeposit.setOut_order_id(thirdNo);// 第三方返回的交易流水号
				predeposit.setPd_payment(subPayType);
				this.predepositService.update(predeposit);
				
				PredepositLog log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_amount(predeposit.getPd_amount());
				log.setPd_log_user(predeposit.getPd_user());
				log.setPd_op_type("充值");
				log.setPd_type("可用预存款");
				log.setPd_log_info("充值成功");
				log.setPredeposit(predeposit);
				this.predepositLogService.save(log);
			} else {
				PredepositLog log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_amount(predeposit.getPd_amount());
				log.setPd_log_user(predeposit.getPd_user());
				log.setPd_op_type("充值");
				log.setPd_type("可用预存款");
				log.setPd_log_info("充值失败_" + resultDTO.getMsg());
				log.setPredeposit(predeposit);
				this.predepositLogService.save(log);
				logger.info("【支付中心充值回调】 充值失败，" + resultDTO.getMsg());
				errorMsg = "【支付中心充值回调】 充值失败，" + resultDTO.getMsg();
			}
		}
		return errorMsg;
	}

	
	
	/**
	 * 处理购物付款成功后相关业务（包括消费码）
	 *
	 * @param order
	 * @param billType
	 * @param request
	 * @param trade_no
	 * @throws Exception
	 */
	private void dealOrderForm(OrderForm order, String billType, HttpServletRequest request, String tradeNo, String thirdNo,
			String subPayType) throws Exception {
		//购物
		if (order.getOrder_cat() == 0) {
			// 处理购物支付成功后相关业务
			this.generateGoodsInfo(request, order, tradeNo, thirdNo, subPayType);
		} else if (order.getOrder_cat() == 2) {// 生活类团购
			if (order.getOrder_status() != 20) {
				// 异步没有出来订单，则同步处理订单
				this.generate_groupInfos(request, order, subPayType, "支付中心在线支付", tradeNo, thirdNo, subPayType);
			}
		}
	}

	
	
	/**
	 * 处理购物支付成功后相关业务
	 *
	 * @param request
	 * @param order
	 * @param trade_no
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private void generateGoodsInfo(HttpServletRequest request, OrderForm order, String tradeNo, String thirdNo, String subPayType) throws Exception {
		User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
		if (order.getOrder_status() < 20) { // 异步没有出来订单，则同步处理订单
			Date now = new Date();
			// 更新主订单状态
			order.setOrder_status(20);
			// 支付中心交易流水号
			order.setPayCenterNo(tradeNo);
			// 第三方交易流水号
			order.setOut_order_id(thirdNo);
			order.setPayTime(now);
			Payment payment = paymentService.getObjByProperty(null, "mark", subPayType);
			// 设置支付方式
			order.setPayment(payment);
			this.orderFormService.update(order);
			
			// 主订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
			this.update_goods_inventory(order);
			
			// 记录主订单日志
			OrderFormLog main_ofl = new OrderFormLog();
			main_ofl.setAddTime(now);
			main_ofl.setLog_info("支付中心在线支付");
			main_ofl.setLog_user(buyer);
			main_ofl.setOf(order);
			this.orderFormLogService.save(main_ofl);
			
			// 子订单操作
			if (order.getOrder_main() == 1 && !"".equals(CommUtil.null2String(order.getChild_order_detail()))) { // 同步完成子订单付款状态调整
				List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
				for (Map child_map : maps) {
					// 更新子订单状态
					OrderForm child_order = this.orderFormService.getObjById(CommUtil.null2Long(child_map.get("order_id")));
					if (child_order.getOrder_status() != 20) {
						child_order.setOrder_status(20);
						child_order.setOut_order_id(order.getOut_order_id());
						child_order.setPayCenterNo(order.getPayCenterNo());
						child_order.setPayTime(now);
						child_order.setPayment(order.getPayment());
						this.orderFormService.update(child_order);  //不记录子订单的日志吗？那如果用户单独查看某子订单的日志(如：某店铺的订单)，那不是会缺日志？？？？
						// 子订单付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
						this.update_goods_inventory(child_order);
						// 付款成功，发送邮件提示
						this.send_msg_toseller(request, child_order);

					}
				}
			}
			// 主订单付款成功，发送邮件提示
			// 向加盟商家发送付款成功邮件提示，自营商品无需发送邮件提示
			this.send_msg_tobuyer(request, order);
			this.send_msg_toseller(request, order);
			

			//向买家发送账户余额变更短信通知(不区分自营还是非自营都使用平台的免费渠道发送)
			if("balance".equals(order.getPayment().getMark())) {
				boolean flag = this.msgTools.sendSmsFree(CommUtil.getURL(request), "sms_tobuyer_balance_pay_ok_notify", buyer.getMobile(),
						null, order.getId().toString());
				if(flag == false) {
					logger.error("向订单(id=" + order.getId().toString() + ")买家(id=" + buyer.getId().toString() + ")发送账户余额变更短信失败");
				}
			}
			
			// 支付成功发送同步订单事件
			this.synchronizeOrderPublisher.synchronizeOrder(order.getId());
		}
	}

	
	
	/**
	 * 处理积分兑换业务
	 *
	 * @param igOrder
	 */
	@SuppressWarnings("rawtypes")
	private void dealIntegralGoodsOrder(IntegralGoodsOrder igOrder, String subPayType) {
		if (igOrder.getIgo_status() < 20) {
			igOrder.setIgo_status(20);
			igOrder.setIgo_pay_time(new Date());
			igOrder.setIgo_payment(subPayType);
			this.integralGoodsOrderService.update(igOrder);
			List<Map> ig_maps = this.orderFormTools.query_integral_goodsinfo(igOrder.getGoods_info());
			for (Map map : ig_maps) {
				IntegralGoods goods = this.integralGoodsService.getObjById(CommUtil.null2Long(map.get("id")));
				goods.setIg_exchange_count(goods.getIg_exchange_count() + CommUtil.null2Int(map.get("ig_goods_count")));
				this.integralGoodsService.update(goods);
			}
		}
	}

	
	
	/**
	 * 付款成功，订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
	 *
	 * @param order
	 */
	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
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
			} else if (!"".equals(CommUtil.null2String(order.getSeckill_info()))) { // 增加秒杀商品销量
				Map map = Json.fromJson(Map.class, order.getSeckill_info());
				SeckillGoods seckillGoods = seckillGoodsService.getObjById(Long.valueOf(String.valueOf(map
						.get("seckill_goods_id"))));
				seckillGoods.setGg_selled_count(seckillGoods.getGg_selled_count() + goods_count);
				seckillGoodsService.update(seckillGoods);
			}
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
			List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools.queryOfGoodsGsps(
					CommUtil.null2String(order.getId()), CommUtil.null2String(goods.getId()));
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
		}
	}

	
	
	/**
	 * 在线支付回调后，商家发送短信、邮件提醒订单在线付款成功！
	 * 
	 * @param request
	 * @param order
	 * @throws Exception
	 */
	private void send_msg_toseller(HttpServletRequest request, OrderForm order) throws Exception {
		if (order.getOrder_form() == 0) {
			String webPath = CommUtil.getURL(request);
			Store store = this.storeService.getObjById(CommUtil.null2Long(order.getStore_id()));
			User seller = store.getUser();
			if(!this.msgTools.sendEmailCharge(webPath, "email_toseller_online_pay_ok_notify",
					seller.getEmail(), null, order.getId().toString(), order.getStore_id())) {
				logger.error("订单(id=" + order.getId().toString() + ")用户(id=" + order.getUser_id() + ")在线支付成功后系统向对应店铺(id=" + order.getStore_id() + ")发送邮件提醒失败");
			}
			
			if(!this.msgTools.sendSmsCharge(webPath, "sms_toseller_online_pay_ok_notify", seller.getMobile(),
					null, order.getId().toString(), order.getStore_id())) {
				logger.error("订单(id=" + order.getId().toString() + ")用户(id=" + order.getUser_id() + ")在线支付成功后系统向对应店铺(id=" + order.getStore_id() + ")发送短信提醒失败");
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
		String webPath = CommUtil.getURL(request);
		if (order.getOrder_form() == 0) {//第三方店铺订单
			if(!this.msgTools.sendEmailCharge(webPath, "email_tobuyer_online_pay_ok_notify", buyer.getEmail(),
					null, order.getId().toString(), order.getStore_id())) {
				logger.error("订单(id=" + order.getId().toString() + ")用户(id=" + order.getUser_id() + ")在线支付成功后系统向其发送邮件提醒失败");
			}
			
			if(!this.msgTools.sendSmsCharge(webPath, "sms_tobuyer_online_pay_ok_notify", buyer.getMobile(),
					null, order.getId().toString(), order.getStore_id())) {
				logger.error("订单(id=" + order.getId().toString() + ")用户(id=" + order.getUser_id() + ")在线支付成功后系统向其发送短信提醒失败");
			}
		} else {
			if(!this.msgTools.sendEmailFree(webPath, "email_tobuyer_online_pay_ok_notify", buyer.getEmail(),
					null, order.getId().toString())) {
				logger.error("订单(id=" + order.getId().toString() + ")用户(id=" + order.getUser_id() + ")在线支付成功后系统向其发送邮件提醒失败");
			}
			
			if(!this.msgTools.sendSmsFree(webPath, "sms_tobuyer_online_pay_ok_notify", buyer.getMobile(), null,
					order.getId().toString())) {
				logger.error("订单(id=" + order.getId().toString() + ")用户(id=" + order.getUser_id() + ")在线支付成功后系统向其发送短信提醒失败");
			}
		}
	}
	
	

	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	private void generate_groupInfos(HttpServletRequest request, OrderForm order, String mark, String pay_info,
			String tradeNo, String thirdNo, String subPayType) throws Exception {
		User user = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
		if (order.getOrder_status() < 20) {
			order.setOrder_status(20);
			// 支付中心流水号
			order.setPayCenterNo(tradeNo);
			// 第三方交易流水号
			order.setOut_order_id(thirdNo);
			// 设置支付类型，online:在线支付，payafter:货到付款
			order.setPayType("online");
			order.setPayTime(new Date());
			Payment payment = paymentService.getObjByProperty(null, "mark", subPayType);
			order.setPayment(payment);
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
				List<String> code_list = new ArrayList(); // 存放团购消费码
				String codes = "";
				for (int i = 0; i < count; i++) {
					GroupInfo info = new GroupInfo();
					info.setAddTime(new Date());
					info.setLifeGoods(goods);
					info.setPayment(payments.get(0));
					info.setOrder_id(order.getId());
					info.setUser_mobile(user.getMobile()); // 不同支付方式保存手机号码到团购码信息表
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
							store.getStore_sale_amount()))); // 店铺本次结算总销售金额
					store.setStore_commission_amount(BigDecimal.valueOf(CommUtil.add(order.getCommission_amount(),
							store.getStore_commission_amount()))); // 店铺本次结算总佣金
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(order.getTotalPrice(),
							store.getStore_payoff_amount()))); // 店铺本次结算总佣金
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
					plog.setTotal_amount(order.getTotalPrice()); // 该订单应结算金额：结算金额=订单总商品金额-总佣金费用
					this.payoffservice.save(plog);
				}

				// 增加系统总销售金额、总佣金
				SysConfig sc = this.configService.getSysConfig();
				sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(order.getGoods_amount(), sc.getPayoff_all_sale())));
				sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil.add(order.getCommission_amount(),
						sc.getPayoff_all_commission())));
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

				// 付款成功，发送短信团购消费码
				this.send_groupInfo_sms(request, order, buyer.getMobile(), "sms_tobuyer_online_ok_send_groupinfo",
						code_list, buyer.getId().toString(), goods.getUser());
			}
			this.send_msg_tobuyer(request, order);
		}
	}

	
	
	/**
	 * 发送短信团购消费码
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
			List<String> codes, String buyer_id, User seller) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < codes.size(); i++) {
			sb.append(codes.get(i) + ",");
		}
		
		String code = sb.substring(0, sb.length() -1);
		
		Map vars = new HashMap<String, Object>();
		vars.put("buyer_id", buyer_id);
		vars.put("seller_id", seller.getId());
		vars.put("order_id", order.getId());
		vars.put("code", code);
		
		boolean flag = false;
		if(order.getOrder_form() == 0) {//商家订单
			flag = this.msgTools.sendSmsCharge(CommUtil.getURL(request), mark, mobile, vars, order.getId().toString(), seller.getStore().getId().toString());
		} else {//自营订单
			flag = this.msgTools.sendSmsFree(CommUtil.getURL(request), mark, mobile, vars, order.getId().toString());
		}	
		
		if(!flag) {
			logger.error("向订单(id=" + order.getId() + ")用户(id=" + buyer_id + ")发送团购消费码(" + code+ ")短信失败！");
		}
	}


	
	/**
	 * 校验订单支付时效性。若返回null，则表示订单支付失效，要重新向支付中心提交订单信息
	 * @param param
	 * @param orderType
	 * @return
	 */
	private Long validPrescription(Object param, String orderType) {
		// 订单提交时间
		Date submitTime = ((IdEntity)param).getAddTime();
		
		// 有效时长
		int timeHour = this.configService.getSysConfig().getPayOrderTime();
		
		// 失效时间
		Long invalidTime = submitTime.getTime() + timeHour * 60 * 60 * 1000;
		
		// 如果当前系统时间大于失效时间，则返回null
		if ((new Date()).getTime() > invalidTime) {
			return null;
		}
		return invalidTime;
	}

}
