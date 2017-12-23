package com.smi.am.service.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value = "优惠券信息父类值对象")
public class CouponsParentVo {
	
	@NotEmpty
	@Length(min = 2, max = 20, message = "优惠券名称长度必须位于2到20个字符之间")  
	@ApiModelProperty(value="优惠券名称")
	private String cCouponsname;
	
	@NotNull
	@ApiModelProperty(value="优惠券类别ID")
	private Integer cCouponstype;
	
	@Pattern(regexp="^-?\\d+$",message="请输入数字")
	@ApiModelProperty(value="投放渠道IDS,1:星美生活;2:满天星")
	private String  cChannel;
	
	@NotNull
	@ApiModelProperty(value="使用渠道,1:线上;2:线下")
	private Integer cUserchannel;
	
	@NotEmpty
	@Length(min = 1, max = 10, message = "优惠信息长度必须位于1到10个字符之间")  
	@ApiModelProperty(value="优惠信息")
	private String cCouponsmsg;
	
	@NotNull
	@ApiModelProperty(value="可用订单金额")
	private Double cAvaliabledorderamt;

	@NotNull
	@ApiModelProperty(value="单日限用")
	private Integer cDailylimit;
	
	@NotNull
	@ApiModelProperty(value="预发数量")
	private Integer cPresendcount;
	
	@NotNull
	@ApiModelProperty(value="是否永久有效,1:永久有效;2:非永久有效")
	private Integer cPermanent;
	
	@ApiModelProperty(value="使用期限开始日期")
	private Date cLimitstart;

	@ApiModelProperty(value="使用期限结束日期")
	private Date cLimitend;
	
	@NotEmpty
	@ApiModelProperty(value="活动区域ID")
	private String cActivityarea;

	@NotNull
	@ApiModelProperty(value="活动开始时间")
	private Date cActivitystarttime;

	@NotNull
	@ApiModelProperty(value="活动结束时间")
	private Date cActivityendtime;
	
	@NotNull
	@ApiModelProperty(value="审核状态,1:未提交;2:审核中;3:未通过;4:待发放;5:已发放;6:已过期")
	private Integer cAuditstatus;



	public String getcCouponsname() {
		return cCouponsname;
	}

	public void setcCouponsname(String cCouponsname) {
		this.cCouponsname = cCouponsname;
	}

	public Integer getcCouponstype() {
		return cCouponstype;
	}

	public void setcCouponstype(Integer cCouponstype) {
		this.cCouponstype = cCouponstype;
	}

	public String getcChannel() {
		return cChannel;
	}

	public void setcChannel(String cChannel) {
		this.cChannel = cChannel;
	}

	public Integer getcUserchannel() {
		return cUserchannel;
	}

	public void setcUserchannel(Integer cUserchannel) {
		this.cUserchannel = cUserchannel;
	}

	public String getcCouponsmsg() {
		return cCouponsmsg;
	}

	public void setcCouponsmsg(String cCouponsmsg) {
		this.cCouponsmsg = cCouponsmsg;
	}

	public Double getcAvaliabledorderamt() {
		return cAvaliabledorderamt;
	}

	public void setcAvaliabledorderamt(Double cAvaliabledorderamt) {
		this.cAvaliabledorderamt = cAvaliabledorderamt;
	}

	public Integer getcDailylimit() {
		return cDailylimit;
	}

	public void setcDailylimit(Integer cDailylimit) {
		this.cDailylimit = cDailylimit;
	}


	public Integer getcPresendcount() {
		return cPresendcount;
	}

	public void setcPresendcount(Integer cPresendcount) {
		this.cPresendcount = cPresendcount;
	}

	public Integer getcPermanent() {
		return cPermanent;
	}

	public void setcPermanent(Integer cPermanent) {
		this.cPermanent = cPermanent;
	}

	public String getcActivityarea() {
		return cActivityarea;
	}

	public void setcActivityarea(String cActivityarea) {
		this.cActivityarea = cActivityarea;
	}


	public Integer getcAuditstatus() {
		return cAuditstatus;
	}

	public void setcAuditstatus(Integer cAuditstatus) {
		this.cAuditstatus = cAuditstatus;
	}

	public Date getcLimitstart() {
		return cLimitstart;
	}

	public void setcLimitstart(Date cLimitstart) {
		this.cLimitstart = cLimitstart;
	}

	public Date getcLimitend() {
		return cLimitend;
	}

	public void setcLimitend(Date cLimitend) {
		this.cLimitend = cLimitend;
	}

	public Date getcActivitystarttime() {
		return cActivitystarttime;
	}

	public void setcActivitystarttime(Date cActivitystarttime) {
		this.cActivitystarttime = cActivitystarttime;
	}

	public Date getcActivityendtime() {
		return cActivityendtime;
	}

	public void setcActivityendtime(Date cActivityendtime) {
		this.cActivityendtime = cActivityendtime;
	}
	
	
}
