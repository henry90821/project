package com.smi.am.service.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize
@ApiModel(value="用户信息值对象")
public class UserVo  extends BaseVo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="用户ID")
    private Integer uId;

	@ApiModelProperty(value="用户角色类型")
    private Integer uRoletype;

	@ApiModelProperty(value="用户角色名称")
    private String uRoleName;
	
	@ApiModelProperty(value="工号")
    private String uJobnum;

	@ApiModelProperty(value="部门id")
    private Integer uDepartid;
	
	@ApiModelProperty(value="部门名称")
    private String uDepartName;

	@ApiModelProperty(value="用户姓名")
    private String uName;

	@ApiModelProperty(value="账号名")
    private String uUsername;

	/*@ApiModelProperty(value="密码")
    private String uPassword;*/

	
	/*@ApiModelProperty(value="创建人")
    private String uCreateuser;

	@ApiModelProperty(value="创建时间")
    private Date uCreatedate;

	@ApiModelProperty(value="更新时间")
    private Date uLastmoddate;

	@ApiModelProperty(value="更新人")
    private String uLastmoduser;*/

	@ApiModelProperty(value="最后登录时间")
    private Date uLastlogindate;
	
	@ApiModelProperty(value="所属活动区域ID")
	private Integer areaId;


	
	
    public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

    public String getuDepartName() {
		return uDepartName;
	}

	public void setuDepartName(String uDepartName) {
		this.uDepartName = uDepartName;
	}

	public Integer getuId() {
        return uId;
    }

    public void setuId(Integer uId) {
        this.uId = uId;
    }

    public Integer getuRoletype() {
        return uRoletype;
    }

    public void setuRoletype(Integer uRoletype) {
        this.uRoletype = uRoletype;
    }

    public String getuJobnum() {
        return uJobnum;
    }

    public void setuJobnum(String uJobnum) {
        this.uJobnum = uJobnum == null ? null : uJobnum.trim();
    }

    public Integer getuDepartid() {
        return uDepartid;
    }

    public void setuDepartid(Integer uDepartid) {
        this.uDepartid = uDepartid;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName == null ? null : uName.trim();
    }

    public String getuUsername() {
        return uUsername;
    }

    public void setuUsername(String uUsername) {
        this.uUsername = uUsername == null ? null : uUsername.trim();
    }



    public Date getuLastlogindate() {
        return uLastlogindate;
    }

    public void setuLastlogindate(Date uLastlogindate) {
        this.uLastlogindate = uLastlogindate;
 
    }

	public String getuRoleName() {
		return uRoleName;
	}

	public void setuRoleName(String uRoleName) {
		this.uRoleName = uRoleName;
	}
    

}