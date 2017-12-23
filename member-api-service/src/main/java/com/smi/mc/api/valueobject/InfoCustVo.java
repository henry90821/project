package com.smi.mc.api.valueobject;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 会员资料信息结果值对象<br/>
 * Created by Andriy on 16/9/19.
 */
@JsonSerialize
@JsonInclude(value = JsonInclude.Include.ALWAYS)
@ApiModel(value = "会员资料信息结果值对象")
public class InfoCustVo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 会员标识
	 */
	@ApiModelProperty(value = "会员标识")
    private String custId;

	/**
	 * 会员姓名
	 */
	@ApiModelProperty(value = "会员姓名")
    private String custName;

	/**
	 * 会员等级
	 */
	@ApiModelProperty(value = "会员等级")
    private String custVipLevel;

	/**
	 * 会员类型
	 */
	@ApiModelProperty(value = "会员类型")
    private String custType;

	/**
	 * 会员卡类型
	 */
	@ApiModelProperty(value = "会员卡类型")
    private String cardType;

	/**
	 * 会员卡号
	 */
	@ApiModelProperty(value = "会员卡号")
    private String custNbr;

	/**
	 * 会员联系电话
	 */
	@ApiModelProperty(value = "会员联系电话")
    private String contactMobile;

	/**
	 * 会员联系地址
	 */
	@ApiModelProperty(value = "会员联系地址")
    private String contactAddr;

	/**
	 * 性别
	 */
	@ApiModelProperty(value = "性别")
    private String sex;

	/**
	 * 生日
	 */
	@ApiModelProperty(value = "生日")
    private Date birthdate;

	/**
	 * 电子邮箱
	 */
	@ApiModelProperty(value = "电子邮箱")
    private String email;

	/**
	 * 昵称
	 */
	@ApiModelProperty(value = "昵称")
    private String nickName;

	/**
	 * 创建渠道
	 */
	@ApiModelProperty(value = "昵称")
    private String orgId;

	/**
	 * 是否黑名单
	 */
	@ApiModelProperty(value = "是否黑名单")
    private String blacklist;

	/**
	 * 证件类型
	 */
	@ApiModelProperty(value = "证件类型")
	private String certiType;
	
	/**
	 * 证件号码
	 */
	@ApiModelProperty(value = "证件号码")
	private String certiNbr;
	
	/**
	 * 证件地址
	 */
	@ApiModelProperty(value = "证件地址")
	private String certiAddr;
	
	/**
	 * 入会时间
	 */
	@ApiModelProperty(value = "入会时间")
	private Date crtDate;
	
	/**
	 * 最后更新时间
	 */
	@ApiModelProperty(value = "最后更新时间")
	private Date modDate;
	
	/**
	 * 是否需要同步密码(1:完全同步成功;2:密码未同步;3:资料未同步)
	 */
	@ApiModelProperty(value = "是否需要同步密码(1:完全同步成功;2:密码未同步;3:资料未同步)")
	private Short issync;
	
	
    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId == null ? null : custId.trim();
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName == null ? null : custName.trim();
    }

    public String getCustVipLevel() {
        return custVipLevel;
    }

    public void setCustVipLevel(String custVipLevel) {
        this.custVipLevel = custVipLevel == null ? null : custVipLevel.trim();
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType == null ? null : custType.trim();
    }


    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType == null ? null : cardType.trim();
    }

    public String getCustNbr() {
        return custNbr;
    }

    public void setCustNbr(String custNbr) {
        this.custNbr = custNbr == null ? null : custNbr.trim();
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile == null ? null : contactMobile.trim();
    }

    public String getContactAddr() {
        return contactAddr;
    }

    public void setContactAddr(String contactAddr) {
        this.contactAddr = contactAddr == null ? null : contactAddr.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }


    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId == null ? null : orgId.trim();
    }


    public String getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(String blacklist) {
        this.blacklist = blacklist == null ? null : blacklist.trim();
    }

	public String getCertiType() {
		return certiType;
	}

	public void setCertiType(String certiType) {
		this.certiType = certiType;
	}

	public String getCertiNbr() {
		return certiNbr;
	}

	public void setCertiNbr(String certiNbr) {
		this.certiNbr = certiNbr;
	}

	public String getCertiAddr() {
		return certiAddr;
	}

	public void setCertiAddr(String certiAddr) {
		this.certiAddr = certiAddr;
	}

	public Date getCrtDate() {
		return crtDate;
	}

	public void setCrtDate(Date crtDate) {
		this.crtDate = crtDate;
	}

	public Short getIssync() {
		return issync;
	}

	public void setIssync(Short issync) {
		this.issync = issync;
	}

	public Date getModDate() {
		return modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}


}