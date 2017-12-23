package com.iskyshop.foundation.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: Res.java
 * </p>
 * 
 * <p>
 * Description:
 * 系统权限资源管理控制器，用来记录系统权限资源信息，系统权限资源对应URL信息，SpringSecurity根据权限资源信息进行URL拦截处理
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
 * @author erikzhang
 * 
 * @date 2014-4-25
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate=true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "res")
public class Res extends IdEntity {
	private static final long serialVersionUID = 7146004652875914011L;
	private String resName;
	private String type;// 这里只需要进行URL类型的过滤
	private String value;
	@ManyToMany(mappedBy = "reses", targetEntity = Role.class, fetch = FetchType.EAGER)
	private List<Role> roles = new ArrayList<Role>();
	private int sequence;
	private String info;

	public Res() {
		super();
	}

	public Res(Long id, String value) {
		super.setId(id);
		this.value = value;
	}

	public Res(Long id, Date addTime) {
		super(id, addTime);
	}

	@Transient
	public String getRoleAuthorities() {
		if(roles.size() > 0) {
			StringBuilder sb = new StringBuilder();
			
			for (Role role : roles) {
				sb.append(",").append(role.getRoleCode());
			}
			return sb.substring(1);
		} else {
			return "";
		}
	}

	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

}
