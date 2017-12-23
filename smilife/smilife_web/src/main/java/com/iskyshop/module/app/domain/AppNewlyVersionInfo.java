package com.iskyshop.module.app.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;


/**
 * APP安装包的版本信息
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate=true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "appnewlyversioninfo")
public class AppNewlyVersionInfo extends IdEntity {	
	
	/**
	 * 平台类型（1：android  2:ios）
	 */
	@Column(columnDefinition = "int default 1")
	private int platformFlag;
	/**
	 * 版本号
	 */
	@Column(columnDefinition = "int default 0")
	private int verNumber;
	/**
	 * 版本号名称
	 */
	private String verName;
	/**
	 * 安装包路径
	 */
	private String installUrl;
	
	/**
	 * 是否强制更新（0：强制更新   1：不强制更新）
	 */
	@Column(columnDefinition = "int default 0")
	private int updateFlag;
	
	/**
	 * 版本描述
	 */
	@Column(length = 500)
	private String verDesc;
	
	/**
	 * apk对应的下载渠道id（如：wandoujia）
	 */
	private String channelId;

	public int getPlatformFlag() {
		return platformFlag;
	}

	public void setPlatformFlag(int platformFlag) {
		this.platformFlag = platformFlag;
	}

	public int getVerNumber() {
		return verNumber;
	}

	public void setVerNumber(int verNumber) {
		this.verNumber = verNumber;
	}

	public String getVerName() {
		return verName;
	}

	public void setVerName(String verName) {
		this.verName = verName;
	}

	public String getInstallUrl() {
		return installUrl;
	}

	public void setInstallUrl(String installUrl) {
		this.installUrl = installUrl;
	}

	public int getUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(int updateFlag) {
		this.updateFlag = updateFlag;
	}

	public String getVerDesc() {
		return verDesc;
	}

	public void setVerDesc(String verDesc) {
		this.verDesc = verDesc;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}


	
}
