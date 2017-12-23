package com.iskyshop.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: FTPServer.java
 * </p>
 * 
 * <p>
 * Description:图片资源FTP服务器管理类，可以使用任意多个FTP服务器管理图片，使用FTP上传、使用FTP服务器地址或者ip浏览图片， 达到图片资源分布式管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author hezeng
 * 
 * @date 2014-7-1
 * 
 * @version iskyshop_b2b2c v1.0 集群版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "ftp_server")
public class FTPServer extends IdEntity {
	@Column(columnDefinition = "int default 0")
	private int ftp_sequence;// 排序
	private String ftp_name;// FTP服务器名称
	private String ftp_addr;// FTP服务器地址,优先使用服务器地址访问，如果没有域名，则使用服务器ip访问，需带有http://字样
	private String ftp_ip;// FTP ip地址,用于图片上传，不可带有http://字样
	private String ftp_username;// 链接FTP用户名
	private String ftp_password;// 链接FTP用户密码
	@OneToMany(mappedBy = "ftp", fetch = FetchType.LAZY)
	List<User> ftp_users = new ArrayList<User>();// ftp用户,创建用户时系统为用户分配一个没有到达用户数量上限的ftp服务器
	@Column(columnDefinition = "int default 0")
	private int ftp_type;// 服务器类型，0为用户服务器，1为系统服务器，系统服务器可以有多个，但同时只能使用一个
	@Column(columnDefinition = "int default 0")
	private int ftp_system;// 是否为当前使用的系统服务器，1为是，系统可以有多个系统ftp，只能同时使用一个
	@Column(columnDefinition = "int default 100")
	private int ftp_amount;// ftp所拥有用户数量上限，默认100
	@Transient
	private int user_Count; //ftp服务器关联的用户数量

	public int getFtp_sequence() {
		return ftp_sequence;
	}

	public void setFtp_sequence(int ftp_sequence) {
		this.ftp_sequence = ftp_sequence;
	}

	public int getFtp_type() {
		return ftp_type;
	}

	public void setFtp_type(int ftp_type) {
		this.ftp_type = ftp_type;
	}

	public int getFtp_system() {
		return ftp_system;
	}

	public void setFtp_system(int ftp_system) {
		this.ftp_system = ftp_system;
	}

	public int getFtp_amount() {
		return ftp_amount;
	}

	public void setFtp_amount(int ftp_amount) {
		this.ftp_amount = ftp_amount;
	}

	public List<User> getFtp_users() {
		return ftp_users;
	}

	public void setFtp_users(List<User> ftp_users) {
		this.ftp_users = ftp_users;
	}

	public String getFtp_name() {
		return ftp_name;
	}

	public void setFtp_name(String ftp_name) {
		this.ftp_name = ftp_name;
	}

	public String getFtp_addr() {
		return ftp_addr;
	}

	public void setFtp_addr(String ftp_addr) {
		this.ftp_addr = ftp_addr;
	}

	public String getFtp_ip() {
		return ftp_ip;
	}

	public void setFtp_ip(String ftp_ip) {
		this.ftp_ip = ftp_ip;
	}

	public String getFtp_username() {
		return ftp_username;
	}

	public void setFtp_username(String ftp_username) {
		this.ftp_username = ftp_username;
	}

	public String getFtp_password() {
		return ftp_password;
	}

	public void setFtp_password(String ftp_password) {
		this.ftp_password = ftp_password;
	}

	public int getUser_Count() {
		return user_Count;
	}

	public void setUser_Count(int user_Count) {
		this.user_Count = user_Count;
	}

}
