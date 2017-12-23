package com.smi.am.dao.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 系统日志实体类
 * 
 * @author pankai
 *
 */
public class SystemLog {

	private int slId; // 系统日志id
	private String slUsername;// 账号
	private String slName;// 姓名
	private String slRoleName;// 角色名称
	private int slRoleType; // 角色类型
	private String slLoginIp;// 操作ip
	private String slLogTitle; // 日志标题
	private String slLogDetail;// 日志详情
	private Date slOperateDate;// 操作日期
	private String slRemark;// 备注
	private int areaId;//所属区域ID
	
	

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public int getSlId() {
		return slId;
	}

	public void setSlId(int slId) {
		this.slId = slId;
	}

	public String getSlUsername() {
		return slUsername;
	}

	public void setSlUsername(String slUsername) {
		this.slUsername = slUsername;
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

	public int getSlRoleType() {
		return slRoleType;
	}

	public void setSlRoleType(int slRoleType) {
		this.slRoleType = slRoleType;
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

	public Date getSlOperateDate() {
		return slOperateDate;
	}

	public void setSlOperateDate(Date slOperateDate) {
		this.slOperateDate = slOperateDate;
	}

	public String getSlRemark() {
		return slRemark;
	}

	public void setSlRemark(String slRemark) {
		this.slRemark = slRemark;
	}

	public String getSlLoginIp() {
		return slLoginIp;
	}

	public void setSlLoginIp(String slLoginIp) {
		this.slLoginIp = slLoginIp;
	}

	@Override
	public String toString() {
		return "SystemLog [slId=" + slId + ", slUsername=" + slUsername + ", slName=" + slName + ", slRoleName="
				+ slRoleName + ", slRoleType=" + slRoleType + ", slLoginIp=" + slLoginIp + ", slLogTitle=" + slLogTitle
				+ ", slLogDetail=" + slLogDetail + ", slOperateDate=" + slOperateDate + ", slRemark=" + slRemark + "]";
	}

}
