package com.smi.am.service.vo;

import java.util.Date;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class UserResVo {

	@ApiModelProperty(value = "用户角色名称")
	private String uRoleName;

	@ApiModelProperty(value = "工号")
	private String uJobnum;

	@ApiModelProperty(value = "部门名称")
	private String uDepartName;

	@ApiModelProperty(value = "用户姓名")
	private String uName;

	@ApiModelProperty(value = "最后登录时间")
	private Date uLastlogindate;

	private String uUsername;

	public String getuRoleName() {
		return uRoleName;
	}

	public void setuRoleName(String uRoleName) {
		this.uRoleName = uRoleName;
	}

	public String getuJobnum() {
		return uJobnum;
	}

	public void setuJobnum(String uJobnum) {
		this.uJobnum = uJobnum;
	}

	public String getuDepartName() {
		return uDepartName;
	}

	public void setuDepartName(String uDepartName) {
		this.uDepartName = uDepartName;
	}

	public String getuName() {
		return uName;
	}

	public void setuName(String uName) {
		this.uName = uName;
	}

	public Date getuLastlogindate() {
		return uLastlogindate;
	}

	public void setuLastlogindate(Date uLastlogindate) {
		this.uLastlogindate = uLastlogindate;
	}

	public String getuUsername() {
		return uUsername;
	}

	public void setuUsername(String uUsername) {
		this.uUsername = uUsername;
	}

}
