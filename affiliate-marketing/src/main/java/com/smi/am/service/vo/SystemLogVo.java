package com.smi.am.service.vo;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 系统日志实体类
 * 
 * @author pankai
 *
 */
@JsonSerialize
@ApiModel(value = "日志信息值对象")
public class SystemLogVo {

	@ApiModelProperty(value = "用户账号")
	private String slUsername;

	@ApiModelProperty(value = "姓名")
	private String slName;

	@ApiModelProperty(value = "角色类型")
	private String slRoleName;

	@ApiModelProperty(value = "登录ip")
	private String slLoginIp;

	@ApiModelProperty(value = "操作日期")
	private Date slOperateDate;

	@ApiModelProperty(value = "日志标题")
	private String slLogTitle;

	@ApiModelProperty(value = "日志详情")
	private String slLogDetail;

	public String getSlUsername() {
		return slUsername;
	}

	public void setSlUsername(String slUsername) {
		this.slUsername = slUsername;
	}

	public String getSlLogTitle() {
		return slLogTitle;
	}

	public void setSlLogTitle(String slLogTitle) {
		this.slLogTitle = slLogTitle;
	}

	public String getSlLogDetail() {
		return slLogDetail;
	}

	public void setSlLogDetail(String slLogDetail) {
		this.slLogDetail = slLogDetail;
	}

	public String getSlName() {
		return slName;
	}

	public void setSlName(String slName) {
		this.slName = slName;
	}

	public String getSlRoleName() {
		return slRoleName;
	}

	public void setSlRoleName(String slRoleName) {
		this.slRoleName = slRoleName;
	}


	public String getSlLoginIp() {
		return slLoginIp;
	}

	public void setSlLoginIp(String slLoginIp) {
		this.slLoginIp = slLoginIp;
	}

	public Date getSlOperateDate() {
		return slOperateDate;
	}

	public void setSlOperateDate(Date slOperateDate) {
		this.slOperateDate = slOperateDate;
	}

}
