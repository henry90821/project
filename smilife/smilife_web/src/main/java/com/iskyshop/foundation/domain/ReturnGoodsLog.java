package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: ReturnGoodsLog.java
 * </p>
 * 
 * <p>
 * Description: 退货商品日志类,用来记录所有退货操作
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
 * @author jinxinzhe
 * 
 * @date 2014-5-12
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "returngoods_log")
public class ReturnGoodsLog extends IdEntity {
	private String return_service_id; // 退货服务单号
	private Long goods_id;// 退货的商品id
	private String goods_name;// 商品的名称
	private String goods_count;// 商品数量
	private String goods_price;// 商品价格
	private String goods_all_price;// 商品总价格
	private String goods_mainphoto_path;// 商品图片路径
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_commission_rate;// 退货商品的佣金比例
	private String goods_return_status;// 退货商品状态 -2为超过退货时间未能输入退货物流 -1为申请被拒绝
										// 1为可以退货 5为退货申请中 6为审核通过可进行退货 7为退货中
										// 10为退货完成,等待退款，11为平台退款完成
	private long return_order_id;// 商品对应的订单id
	private int goods_type;// 商品的类型 0为自营 1为第三方经销商
	private String return_content;// 退货描述
	private String user_name;// 申请退货的用户姓名
	private Long store_id; // 商品对应的店铺id
	private Long user_id;// 所属用户id
	private String self_address;// 收货时向买家发送的收货地址，买家通过此将货物发送给卖家
	@Column(columnDefinition = "LongText")
	private String return_express_info;// 退货物流公司信息json{"express_company_id":1,"express_company_name":"顺丰快递","express_company_mark":"shunfeng","express_company_type":"EXPRESS"}
	private String express_code;// 快递号
	@Column(columnDefinition = "int default 0")
	private int refund_status;// 退款状态 0为未退款 1为退款完成2退款请求已提交，等到银行退款（银联）

	private String return_main_order_id;// 商品对应的主订单的id（注意：不是订单的order_id值，而是订单的id字段的值）

	@Column(name = "alipay_batch_no")
	private String alipayBatchNo; // 支付宝退款批次号

	@Column(name = "return_remark")
	private String returnRemark; // 退款备注

	@Column(name = "refund", precision = 12, scale = 2)
	private BigDecimal refund;// 实际退款金额

	@Column(name = "coupon_discount", precision = 12, scale = 2)
	private BigDecimal couponDiscount;// 优惠券折扣

	@Column(name = "enough_reduce_discount", precision = 12, scale = 2)
	private BigDecimal enoughReduceDiscount;// 满减折扣
	
	private String refundPayCenterNo; // 请求支付中心退款流水号
	

	public ReturnGoodsLog() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ReturnGoodsLog(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public BigDecimal getGoods_commission_rate() {
		return goods_commission_rate;
	}

	public void setGoods_commission_rate(BigDecimal goods_commission_rate) {
		this.goods_commission_rate = goods_commission_rate;
	}

	public int getRefund_status() {
		return refund_status;
	}

	public void setRefund_status(int refund_status) {
		this.refund_status = refund_status;
	}

	public String getReturn_express_info() {
		return return_express_info;
	}

	public void setReturn_express_info(String return_express_info) {
		this.return_express_info = return_express_info;
	}

	public String getReturn_service_id() {
		return return_service_id;
	}

	public void setReturn_service_id(String return_service_id) {
		this.return_service_id = return_service_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getExpress_code() {
		return express_code;
	}

	public void setExpress_code(String express_code) {
		this.express_code = express_code;
	}

	public String getSelf_address() {
		return self_address;
	}

	public void setSelf_address(String self_address) {
		this.self_address = self_address;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getReturn_content() {
		return return_content;
	}

	public void setReturn_content(String return_content) {
		this.return_content = return_content;
	}

	public int getGoods_type() {
		return goods_type;
	}

	public void setGoods_type(int goods_type) {
		this.goods_type = goods_type;
	}

	public long getReturn_order_id() {
		return return_order_id;
	}

	public void setReturn_order_id(long return_order_id) {
		this.return_order_id = return_order_id;
	}

	public Long getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getGoods_count() {
		return goods_count;
	}

	public void setGoods_count(String goods_count) {
		this.goods_count = goods_count;
	}

	public String getGoods_price() {
		return goods_price;
	}

	public void setGoods_price(String goods_price) {
		this.goods_price = goods_price;
	}

	public String getGoods_all_price() {
		return goods_all_price;
	}

	public void setGoods_all_price(String goods_all_price) {
		this.goods_all_price = goods_all_price;
	}

	public String getGoods_mainphoto_path() {
		return goods_mainphoto_path;
	}

	public void setGoods_mainphoto_path(String goods_mainphoto_path) {
		this.goods_mainphoto_path = goods_mainphoto_path;
	}

	public String getGoods_return_status() {
		return goods_return_status;
	}

	public void setGoods_return_status(String goods_return_status) {
		this.goods_return_status = goods_return_status;
	}

	public String getReturn_main_order_id() {
		return return_main_order_id;
	}

	public void setReturn_main_order_id(String return_main_order_id) {
		this.return_main_order_id = return_main_order_id;
	}

	public String getAlipayBatchNo() {
		return alipayBatchNo;
	}

	public void setAlipayBatchNo(String alipayBatchNo) {
		this.alipayBatchNo = alipayBatchNo;
	}

	public String getReturnRemark() {
		return returnRemark;
	}

	public void setReturnRemark(String returnRemark) {
		this.returnRemark = returnRemark;
	}

	public BigDecimal getRefund() {
		return refund;
	}

	public void setRefund(BigDecimal refund) {
		this.refund = refund;
	}

	public BigDecimal getCouponDiscount() {
		return couponDiscount;
	}

	public void setCouponDiscount(BigDecimal couponDiscount) {
		this.couponDiscount = couponDiscount;
	}

	public BigDecimal getEnoughReduceDiscount() {
		return enoughReduceDiscount;
	}

	public void setEnoughReduceDiscount(BigDecimal enoughReduceDiscount) {
		this.enoughReduceDiscount = enoughReduceDiscount;
	}

	public String getRefundPayCenterNo() {
		return refundPayCenterNo;
	}

	public void setRefundPayCenterNo(String refundPayCenterNo) {
		this.refundPayCenterNo = refundPayCenterNo;
	}

}
