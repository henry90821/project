package com.smi.mc.vo.cust;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 账户值对象<br/>
 */
@JsonSerialize
@JsonInclude(value = JsonInclude.Include.ALWAYS)
@ApiModel(value = "账户值对象")
public class InfoLoginUserVo {
	
	@ApiModelProperty(value = "账户id")
    private String loginUserId;

	@ApiModelProperty(value = "会员标识")
    private String custId;

	@ApiModelProperty(value = "登录类型")
    private String loginUserType;

	@ApiModelProperty(value = "系统标识")
    private String systemId;

	@ApiModelProperty(value = "账户")
    private String loginUser;

	@ApiModelProperty(value = "状态")
    private String statusCd;

	@ApiModelProperty(value = "创建日期")
    private Date crtDate;

	@ApiModelProperty(value = "修改日期")
    private Date modDate;

    public String getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId == null ? null : loginUserId.trim();
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId == null ? null : custId.trim();
    }

    public String getLoginUserType() {
        return loginUserType;
    }

    public void setLoginUserType(String loginUserType) {
        this.loginUserType = loginUserType == null ? null : loginUserType.trim();
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId == null ? null : systemId.trim();
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser == null ? null : loginUser.trim();
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd == null ? null : statusCd.trim();
    }

    public Date getCrtDate() {
        return crtDate;
    }

    public void setCrtDate(Date crtDate) {
        this.crtDate = crtDate;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }
}