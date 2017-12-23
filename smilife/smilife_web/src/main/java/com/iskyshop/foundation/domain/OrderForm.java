package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.iskyshop.core.annotation.Lock;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;
import com.iskyshop.core.tools.CommUtil;

/**
 *
 * <p>
 * Title: OrderForm.java
 * </p>
 *
 * <p>
 * Description: 系统订单管理类，包括购物订单、手机充值订单、团购订单等等，该类对应的数据表是商城系统中最大也是最重要的数据库。 系統接入殴飞充值，充值按照以下流程完成：1、运营商向殴飞申请开通接口并在b2b2c系统中配置对应的用户名
 * 、密码；2、 运营商在殴飞平台充值一定款项；3、运营商配置充值金额，比如充值100元，殴飞收取98.3，运营商配置为98.5，一单赚2毛；4、用户从平台充值，
 * 首先查询殴飞账户余额，余额足够则跳转到付款界面，向运营商账户付款(目前只支持预存款付款)，付款成功后调用殴飞接口完成充值；
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
 * @author erikzhang、hezeng
 *
 * @date 2014-4-25
 *
 *
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "orderform")
public class OrderForm extends IdEntity {
	private String trade_no; // 交易流水号,在线支付时每次随机生成唯一的号码，重复提交时替换当前订单的交易流水号

	private String trade_order_id; // 提交chinapay的订单id
	private String bank_gate_id; // 返回的网关号
	private String subtansdate; // 提交的时间
	private String rettansdate; // 返回的时间

	private String order_id; // 订单号
	@Index(name = "main_order_id_index")
	@Column(name = "main_order_id")
	private String mainOrderId; // 主订单的order_id(用户一次购买所生成的所有订单（包括主订单）的此字段都为主订单的order_id)
	@Column(columnDefinition = "int default 0")
	private int order_main; // 是否为主订单，1为主订单，主订单用在买家用户中心显示订单内容
	@Column(columnDefinition = "LongText")
	private String child_order_detail; // 子订单详情，如果该订单为主订单，则记录
	private String out_order_id; // 外部单号
	private String order_type; // 订单来源的类型，分为web:PC网页订单，weixin:手机网页订单,android:android手机客户端订单，ios:iOS手机客户端订单,app2.0：星美生活APP2.0
	@Column(columnDefinition = "int default 0")
	private int order_cat; // 订单分类，0为购物订单，1为手机充值订单
							// 2为生活类团购订单
	@Column(columnDefinition = "LongText")
	private String goods_info; // 使用json管理"[{"goods_id":1,"goods_name":"佐丹奴男装翻领T恤
								// 条纹修身商务男POLO纯棉短袖POLO衫","goods_type":"group","goods_choice_type":1,"goods_cat":0,"goods_commission_rate":"0.8","goods_commission_price":"16.00","goods_payoff_price":"234""goods_type":"combin","goods_count":2,"goods_price":100,"goods_all_price":200,"goods_gsp_ids":"/1/3","goods_gsp_val":"尺码：XXL","goods_mainphoto_path":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg","goods_return_status",
								// 商品退货状态
								// 当有此字段为“”时可退货状态
								// 5为已申请退货，6商家已同意退货，7用户填写退货物流，8商家已确认，提交平台已退款，9退款完成
								// -1已经超出可退货时间,"goods_complaint_status"
								// 没有此字段时可投诉
								// 投诉后的状态为1不可投诉}]"
	@Column(columnDefinition = "LongText")
	private String return_goods_info; // 退货商品详细，return_goods_id:退货商品id,
										// return_goods_count：退货数量，return_goods_commission_rate：退货商品佣金率，return_goods_price：退货商品单价，return_goods_content:退货商品说明。使用json管理"[{"return_goods_id":1,"return_goods_count":3,"return_goods_commission_rate":"0.8","return_goods_price":100},{"return_goods_id":1,"return_goods_count":3,"return_goods_commission_rate":"0.8","return_goods_price":100}]"
	@Column(columnDefinition = "LongText")
	private String group_info; // 生活类团购
								// 团购详情
								// GroupLifeGoods
								// 使用json管理"{"goods_id":1,"goods_name":"最新电影票xx影视城
								// 团购优惠","goods_type":"0为经销商发布
								// 1为自营发布","goods_price","10
								// 单价","goods_count","1数量","goods_total_price","10
								// 总价"，"goods_mainphoto_path":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg"}"
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal totalPrice; // 订单总价格
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_amount; // 商品总价格
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal commission_amount; // 该订单佣金总费用，
	@Column(columnDefinition = "LongText")
	private String msg; // 订单附言
	private String payType; // 支付类型，手机端支付时选设置订单支付类型，根据支付类型再进行支付,online:在线支付，payafter:货到付款
	@ManyToOne(fetch = FetchType.EAGER)
	private Payment payment; // 支付方式
	private String transport; // 配送方式，如：快递[10.0元]
	private String shipCode; // 物流单号
	private Date return_shipTime; //买家退货发货截止时间

	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal ship_price; // 配送价格
	@Lock
	private int order_status; // 订单状态，0为订单取消，10为已提交待付款，15为线下付款提交申请(已经取消该付款方式)，16为货到付款，20为已付款待发货，25为申请退款审核中,27为退款审核不通过，30为已发货待收货，
	// 35,自提点已经收货，40为已收货, 50买家评价完毕 ,65订单不可评价，到达设定时间，系统自动关闭订单相互评价功能，80为审核通过退款中,85退款已提交，87退款失败，90为退款完成
	private String user_id; // 买家用户id
	private String user_name; // 买家用户姓名
	private String store_id; // 订单对应的卖家店铺
	private String store_name; // 订单对应的卖家店铺名称
	private Date payTime; // 付款时间
	private Date shipTime; // 发货时间
	private Date confirmTime; // 确认收货时间
	private Date finishTime; // 完成时间。用户评价完后会赋值此字段
	@Column(columnDefinition = "LongText")
	private String express_info; // 物流公司信息json{"express_company_id":1,"express_company_name":"顺丰快递","express_company_mark":"shunfeng","express_company_type":"EXPRESS"}
	private String receiver_Name; // 收货人姓名,确认订单后，将买家的收货地址所有信息添加到订单中，该订单与买家收货地址没有任何关联
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "receiver_area_id", referencedColumnName = "id")
	private Area receiverArea; // 收货人关联的区域对象
	private String receiver_area; // 收货人地区,例如：辽宁省沈阳市铁西区
	private String receiver_area_info; // 收货人详细地址，例如：凌空二街56-1号，4单元2楼1号
	private String receiver_zip; // 收货人邮政编码
	private String receiver_telephone; // 收货人联系电话
	private String receiver_mobile; // 收货人手机号码
	private String receiver_card; // 收货人身份证

	private int invoiceType; // 发票类型，0为个人，1为单位
	private String invoice; // 发票信息
	
	@Column(columnDefinition = "LongText")
	private String pay_msg; // 支付相关说明，比如汇款账号、时间等
	@Column(columnDefinition = "bit default 0")
	private boolean auto_confirm_email; // 是否已发 “订单自动确认收货”的邮件通知
	@Column(columnDefinition = "bit default 0")
	private boolean auto_confirm_sms; // 是否已发 “订单自动确认收货”的短信通知
	@Column(columnDefinition = "int default 0")
	private int order_confirm_delay; //延长收货的天数（由卖家调整）。若在订单发货(shipTime)后的（系统默认收货时长SysConfig.auto_order_confirm() + 此字段的天数）天内用户没有确认收货，则系统自动确认收货
	
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<OrderFormLog> ofls = new ArrayList<OrderFormLog>(); // 订单日志
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<Evaluate> evas = new ArrayList<Evaluate>(); // 订单对应的评价
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<Complaint> complaints = new ArrayList<Complaint>(); // 投诉管理类

	/**
	 * 优惠券信息
	 * e.g. {"couponinfo_id":1,"couponinfo_sn":"ljsa-123l-8weo-s28s","coupon_amount":123,"coupon_goods_rate":0.98,"coupon_return_amount":0,"coupon_order_amount":100}
	 * coupon_goods_rate对于当前订单商品价格的比例
	 */
	@Column(columnDefinition = "LongText")
	private String coupon_info;

	@Column(columnDefinition = "LongText")
	private String order_seller_intro; // 订单卖家给予的说明，用在虚拟商品信息，比如购买充值卡，卖家发货时在这里给出对应的卡号和密钥
	@Column(columnDefinition = "int default 0")
	private int order_form; // 订单种类，0为商家商品订单，1为平台自营商品订单
	private Long eva_user_id; // 保存点击确认发货的管理员id，
	// 以下为手机充值订单信息
	private String rc_mobile; // 充值手机号
	@Column(columnDefinition = "int default 0")
	private int rc_amount; // 充值金额,只能为整数
	@Column(precision = 12, scale = 2)
	private BigDecimal rc_price; // 充值rc_amount金额需要缴纳的费用
	@Column(precision = 12, scale = 2)
	private BigDecimal out_price; // 充值rc_amount金额第三方平台收取的价格，通过接口自动获取
	@Column(columnDefinition = "varchar(255) default 'mobile'")
	private String rc_type; // 充值类型，默认为mobile，手机直充，目前仅支持该类型
	@Column(columnDefinition = "int default 0")
	//以上为手机充值订单信息
	private int operation_price_count; // 商家手动调整费用次数，0为未调整过，用于显示调整过订单费用的账单显示
	private String delivery_time; // 快递配送时间，给商家发货时候使用
	@Column(columnDefinition = "int default 0")
	private int delivery_type;// 配送方式，0为快递配送，1为自提点自提     2为店铺自提 ,3为星美直送
	private Long delivery_address_id;// 自提点id
	@Column(columnDefinition = "LongText")
	private String delivery_info; // 自提点信息，使用json管理{"id":1,"da_name":"兴华南街自提点","":""},用于在订单中显示

	/**
	 * 满减优惠信息
	 * e.g. {"prices_2":[1.00],"2":[5487],"return_2":0.0,"reduce_2":1.0,"counts_2":[3],"all_2":3.0,"enouhg_2":"2.0","tk_2":[5487]}
	 */
	@Column(columnDefinition = "LongText")
	private String enough_reduce_info;
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal enough_reduce_amount; // 满就减的金额
	
	
	@Column(columnDefinition = "LongText")
	private String gift_infos; // 满就送赠送商品信息
								// [{"goods_id":"1"
								// ,"goods_name":"鳄鱼褐色纯皮裤腰带",store_domainPath这个是店铺二级域名路径
								// goods_domainPath商品二级域名路径
								// "goods_main_photo":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg","goods_price":"54.30","buyGify_id":1}]
								// 参加的商品
	@Column(columnDefinition = "int default 0")
	private int whether_gift; // 订单是否有满就送活动。0为没有,1为有
	
	
	private Long ship_addr_id; // 发货地址Id
	@Column(columnDefinition = "LongText")
	private String ship_addr; // 发货详细地址
	


	/**
	 * 秒杀信息，例："{good_id:111}"
	 */
	private String seckill_info;

	@OneToMany(mappedBy = "orderForm", fetch = FetchType.LAZY)
	private List<OrderSyn> orderSynList;

	@OneToMany(mappedBy = "orderForm", fetch = FetchType.LAZY)
	private List<OrderSynLog> orderSynLogList;

	@JoinColumn(name = "configCode", columnDefinition = "varchar(64)", referencedColumnName = "configCode")
	@ManyToOne
	private GoodsConfig goodsConfig;

	private String refund_cause;//退款原因
	@Column(precision = 12, scale = 2)
	private BigDecimal refund_amount = new BigDecimal(0.00);//退款金额（平台确认退款操作存入）
	@Column(precision = 12, scale = 2)
	private BigDecimal enough_reduced_amount = new BigDecimal(0.00);//已退满减金额
	@Column(precision = 12, scale = 2)
	private BigDecimal freighted_amount = new BigDecimal(0.00);// 已退运费金额
	@Column(name = "alipay_batch_no")
	private String alipayBatchNo; // 支付宝退款批次号
	@Column(precision = 12, scale = 2)
	private BigDecimal discount_coupon = new BigDecimal(0.00);// 已退优惠券金额

	/**
	 * 运费信息
	 * e.g. [{
			    "trans_id":111,//动态模板id
				"trans_weight": 1,//首件或首重首立方米
				"trans_fee": 13.50,//价格
				"trans_add_weight": 1,//续件或续重或续立方米
				"trans_add_fee": 2.00,//价格
				"goods_info": [
					{"goods_id":5565,"count":1.0},//goods_id:商品id;count:商品数量或重量或体积
					{"goods_id":5563,"count":1.0}
			    ],
			    "tk_goods_info":[5565]
			},{
			    "trans_id":0,//静态模板id，固定为0
				"trans_fee": 13.50,//最高价
				"goods_info": [{"goods_id":5486}],
			    "tk_goods_info":[5486]
			}
			]
	 */
	@Column(columnDefinition = "LongText")
	private String freight_info;

	private String payCenterNo;// 支付中心交易流水号,用支付中心进行支付会保存此流水号，不走支付中心的支付，此字段为空
	
	private String refundPayCenterNo; // 请求支付中心退款流水号
	
	public OrderForm() {
		super();
	}

	public OrderForm(Long id, Date addTime) {
		super(id, addTime);
	}

	public OrderForm(Long id,String order_id, BigDecimal totalPrice, String user_id,
			String user_name, String store_id, boolean auto_confirm_email, 
			boolean auto_confirm_sms, Date shipTime, int order_confirm_delay,
			int order_status, Date confirmTime, Date return_shipTime, 
			int order_form, String payType, BigDecimal commission_amount,
			String goods_info,BigDecimal goods_amount, BigDecimal ship_price, 
			String order_type) {
		super(id);
		this.order_id = order_id;
		this.order_type = order_type;
		this.goods_info = goods_info;
		this.totalPrice = totalPrice;
		this.goods_amount = goods_amount;
		this.commission_amount = commission_amount;
		this.return_shipTime = return_shipTime;
		this.ship_price = ship_price;
		this.order_status = order_status;
		this.user_id = user_id;
		this.user_name = user_name;
		this.store_id = store_id;
		this.payType = payType;
		this.shipTime = shipTime;
		this.confirmTime = confirmTime;
		this.auto_confirm_email = auto_confirm_email;
		this.auto_confirm_sms = auto_confirm_sms;
		this.order_form = order_form;
		this.order_confirm_delay = order_confirm_delay;
	}

	public Long getDelivery_address_id() {
		return delivery_address_id;
	}

	public void setDelivery_address_id(Long delivery_address_id) {
		this.delivery_address_id = delivery_address_id;
	}

	public int getOrder_confirm_delay() {
		return order_confirm_delay;
	}

	public void setOrder_confirm_delay(int order_confirm_delay) {
		this.order_confirm_delay = order_confirm_delay;
	}

	public Long getShip_addr_id() {
		return ship_addr_id;
	}

	public void setShip_addr_id(Long ship_addr_id) {
		this.ship_addr_id = ship_addr_id;
	}

	public String getShip_addr() {
		return ship_addr;
	}

	public void setShip_addr(String ship_addr) {
		this.ship_addr = ship_addr;
	}

	public int getWhether_gift() {
		return whether_gift;
	}

	public void setWhether_gift(int whether_gift) {
		this.whether_gift = whether_gift;
	}

	public String getGift_infos() {
		return gift_infos;
	}

	public void setGift_infos(String gift_infos) {
		this.gift_infos = gift_infos;
	}

	public BigDecimal getEnough_reduce_amount() {
		return enough_reduce_amount;
	}

	public void setEnough_reduce_amount(BigDecimal enough_reduce_amount) {
		this.enough_reduce_amount = enough_reduce_amount;
	}

	public String getEnough_reduce_info() {
		return enough_reduce_info;
	}

	public void setEnough_reduce_info(String enough_reduce_info) {
		this.enough_reduce_info = enough_reduce_info;
	}

	public String getDelivery_info() {
		return delivery_info;
	}

	public void setDelivery_info(String delivery_info) {
		this.delivery_info = delivery_info;
	}

	public String getDelivery_time() {
		return delivery_time;
	}

	public void setDelivery_time(String delivery_time) {
		this.delivery_time = delivery_time;
	}

	public int getDelivery_type() {
		return delivery_type;
	}

	public void setDelivery_type(int delivery_type) {
		this.delivery_type = delivery_type;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public Date getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	public String getGroup_info() {
		return group_info;
	}

	public void setGroup_info(String group_info) {
		this.group_info = group_info;
	}

	public int getOperation_price_count() {
		return operation_price_count;
	}

	public void setOperation_price_count(int operation_price_count) {
		this.operation_price_count = operation_price_count;
	}

	public int getOrder_main() {
		return order_main;
	}

	public void setOrder_main(int order_main) {
		this.order_main = order_main;
	}

	public String getChild_order_detail() {
		return child_order_detail;
	}

	public void setChild_order_detail(String child_order_detail) {
		this.child_order_detail = child_order_detail;
	}

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	public String getRc_mobile() {
		return rc_mobile;
	}

	public void setRc_mobile(String rc_mobile) {
		this.rc_mobile = rc_mobile;
	}

	public int getRc_amount() {
		return rc_amount;
	}

	public void setRc_amount(int rc_amount) {
		this.rc_amount = rc_amount;
	}

	public BigDecimal getRc_price() {
		return rc_price;
	}

	public void setRc_price(BigDecimal rc_price) {
		this.rc_price = rc_price;
	}

	public BigDecimal getOut_price() {
		return out_price;
	}

	public void setOut_price(BigDecimal out_price) {
		this.out_price = out_price;
	}

	public String getRc_type() {
		return rc_type;
	}

	public void setRc_type(String rc_type) {
		this.rc_type = rc_type;
	}

	public int getOrder_cat() {
		return order_cat;
	}

	public void setOrder_cat(int order_cat) {
		this.order_cat = order_cat;
	}

	public String getReturn_goods_info() {
		return return_goods_info;
	}

	public void setReturn_goods_info(String return_goods_info) {
		this.return_goods_info = return_goods_info;
	}

	public Long getEva_user_id() {
		return eva_user_id;
	}

	public void setEva_user_id(Long eva_user_id) {
		this.eva_user_id = eva_user_id;
	}

	public BigDecimal getCommission_amount() {
		return commission_amount;
	}

	public void setCommission_amount(BigDecimal commission_amount) {
		this.commission_amount = commission_amount;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getCoupon_info() {
		return coupon_info;
	}

	public void setCoupon_info(String coupon_info) {
		this.coupon_info = coupon_info;
	}

	public String getExpress_info() {
		return express_info;
	}

	public void setExpress_info(String express_info) {
		this.express_info = express_info;
	}

	public String getGoods_info() {
		return goods_info;
	}

	public void setGoods_info(String goods_info) {
		this.goods_info = goods_info;
	}

	public String getReceiver_Name() {
		return receiver_Name;
	}

	public void setReceiver_Name(String receiver_Name) {
		this.receiver_Name = receiver_Name;
	}

	public String getReceiver_area() {
		return receiver_area;
	}

	public void setReceiver_area(String receiver_area) {
		this.receiver_area = receiver_area;
	}

	public String getReceiver_area_info() {
		return receiver_area_info;
	}

	public void setReceiver_area_info(String receiver_area_info) {
		this.receiver_area_info = receiver_area_info;
	}

	public String getReceiver_zip() {
		return receiver_zip;
	}

	public void setReceiver_zip(String receiver_zip) {
		this.receiver_zip = receiver_zip;
	}

	public String getReceiver_telephone() {
		return receiver_telephone;
	}

	public void setReceiver_telephone(String receiver_telephone) {
		this.receiver_telephone = receiver_telephone;
	}

	public String getReceiver_mobile() {
		return receiver_mobile;
	}

	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public int getOrder_form() {
		return order_form;
	}

	public void setOrder_form(int order_form) {
		this.order_form = order_form;
	}

	public Date getReturn_shipTime() {
		return return_shipTime;
	}

	public void setReturn_shipTime(Date return_shipTime) {
		this.return_shipTime = return_shipTime;
	}

	public List<Complaint> getComplaints() {
		return complaints;
	}

	public void setComplaints(List<Complaint> complaints) {
		this.complaints = complaints;
	}

	public List<Evaluate> getEvas() {
		return evas;
	}

	public void setEvas(List<Evaluate> evas) {
		this.evas = evas;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getShip_price() {
		return ship_price;
	}

	public void setShip_price(BigDecimal ship_price) {
		this.ship_price = ship_price;
	}

	public int getOrder_status() {
		return order_status;
	}

	public void setOrder_status(int order_status) {
		this.order_status = order_status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = CommUtil.convertScriptTag(msg);
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getShipCode() {
		return shipCode;
	}

	public void setShipCode(String shipCode) {
		this.shipCode = shipCode;
	}

	public Date getShipTime() {
		return shipTime;
	}

	public void setShipTime(Date shipTime) {
		this.shipTime = shipTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public int getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(int invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public List<OrderFormLog> getOfls() {
		return ofls;
	}

	public void setOfls(List<OrderFormLog> ofls) {
		this.ofls = ofls;
	}

	public String getPay_msg() {
		return pay_msg;
	}

	public void setPay_msg(String pay_msg) {
		this.pay_msg = CommUtil.convertScriptTag(pay_msg);
	}

	public BigDecimal getGoods_amount() {
		return goods_amount;
	}

	public void setGoods_amount(BigDecimal goods_amount) {
		this.goods_amount = goods_amount;
	}

	public boolean isAuto_confirm_email() {
		return auto_confirm_email;
	}

	public void setAuto_confirm_email(boolean auto_confirm_email) {
		this.auto_confirm_email = auto_confirm_email;
	}

	public boolean isAuto_confirm_sms() {
		return auto_confirm_sms;
	}

	public void setAuto_confirm_sms(boolean auto_confirm_sms) {
		this.auto_confirm_sms = auto_confirm_sms;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getOut_order_id() {
		return out_order_id;
	}

	public void setOut_order_id(String out_order_id) {
		this.out_order_id = out_order_id;
	}

	public String getOrder_seller_intro() {
		return order_seller_intro;
	}

	public void setOrder_seller_intro(String order_seller_intro) {
		this.order_seller_intro = order_seller_intro;
	}

	public String getTrade_order_id() {
		return trade_order_id;
	}

	public void setTrade_order_id(String trade_order_id) {
		this.trade_order_id = trade_order_id;
	}

	public String getBank_gate_id() {
		return bank_gate_id;
	}

	public void setBank_gate_id(String bank_gate_id) {
		this.bank_gate_id = bank_gate_id;
	}

//	public String getCrmOrderId() {
//		return crmOrderId;
//	}
//
//	public void setCrmOrderId(String crmOrderId) {
//		this.crmOrderId = crmOrderId;
//	}

	public String getSeckill_info() {
		return seckill_info;
	}

	public void setSeckill_info(String seckill_info) {
		this.seckill_info = seckill_info;
	}

	public String getMainOrderId() {
		return mainOrderId;
	}

	public void setMainOrderId(String mainOrderId) {
		this.mainOrderId = mainOrderId;
	}

	public List<OrderSyn> getOrderSynList() {
		return orderSynList;
	}

	public void setOrderSynList(List<OrderSyn> orderSynList) {
		this.orderSynList = orderSynList;
	}

	public List<OrderSynLog> getOrderSynLogList() {
		return orderSynLogList;
	}

	public void setOrderSynLogList(List<OrderSynLog> orderSynLogList) {
		this.orderSynLogList = orderSynLogList;
	}

	public GoodsConfig getGoodsConfig() {
		return goodsConfig;
	}

	public void setGoodsConfig(GoodsConfig goodsConfig) {
		this.goodsConfig = goodsConfig;
	}

	public String getReceiver_card() {
		return receiver_card;
	}

	public void setReceiver_card(String receiver_card) {
		this.receiver_card = receiver_card;
	}

	public Area getReceiverArea() {
		return receiverArea;
	}

	public void setReceiverArea(Area receiverArea) {
		this.receiverArea = receiverArea;
	}

	public String getSubtansdate() {
		return subtansdate;
	}

	public void setSubtansdate(String subtansdate) {
		this.subtansdate = subtansdate;
	}

	public String getRettansdate() {
		return rettansdate;
	}

	public void setRettansdate(String rettansdate) {
		this.rettansdate = rettansdate;
	}

	public String getFreight_info() {
		return freight_info;
	}

	public void setFreight_info(String freight_info) {
		this.freight_info = freight_info;
	}

	public String getPayCenterNo() {
		return payCenterNo;
	}

	public void setPayCenterNo(String payCenterNo) {
		this.payCenterNo = payCenterNo;
	}

	public String getRefund_cause()
	{
		return refund_cause;
	}

	public void setRefund_cause(String refund_cause)
	{
		this.refund_cause = refund_cause;
	}

	public BigDecimal getRefund_amount()
	{
		return refund_amount;
	}

	public void setRefund_amount(BigDecimal refund_amount)
	{
		this.refund_amount = refund_amount;
	}

	public BigDecimal getEnough_reduced_amount()
	{
		return enough_reduced_amount;
	}

	public void setEnough_reduced_amount(BigDecimal enough_reduced_amount)
	{
		this.enough_reduced_amount = enough_reduced_amount;
	}

	public BigDecimal getFreighted_amount()
	{
		return freighted_amount;
	}

	public void setFreighted_amount(BigDecimal freighted_amount)
	{
		this.freighted_amount = freighted_amount;
	}

	public String getAlipayBatchNo()
	{
		return alipayBatchNo;
	}

	public void setAlipayBatchNo(String alipayBatchNo)
	{
		this.alipayBatchNo = alipayBatchNo;
	}

	public BigDecimal getDiscount_coupon()
	{
		return discount_coupon;
	}

	public void setDiscount_coupon(BigDecimal discount_coupon)
	{
		this.discount_coupon = discount_coupon;
	}

	public String getRefundPayCenterNo() {
		return refundPayCenterNo;
	}

	public void setRefundPayCenterNo(String refundPayCenterNo) {
		this.refundPayCenterNo = refundPayCenterNo;
	}
}
