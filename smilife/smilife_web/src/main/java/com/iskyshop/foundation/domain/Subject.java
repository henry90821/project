package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: Subject.java
 * </p>
 * 
 * <p>
 * Description: 商城主题类
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
 * @author hezeng
 * 
 * @date 2014-11-11
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate=true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "subject")
public class Subject extends IdEntity {
	private String title;// 主题名称
	@Column(columnDefinition = "int default 0")
	private int sequence;// 序号
	@OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Accessory banner;// 主题横幅
	@Column(columnDefinition = "int default 1")
	private int display;// 是否显示H5
	@Column(columnDefinition = "int default 0")
	private int displayAPP20;// 是否显示App2.0
	@Column(name="ifc_type",columnDefinition = "VARCHAR(10)")
	private String ifcType;  //用来判断该商品为PC端的还是为手机端。PC:表示PC端；H5：表示Wap端
	@Column(columnDefinition = "LongText")
	private String subject_detail;// 专题详情，使用json管理[{"type":"goods","goods_ids":",16,14,11,4,5"},
									// {"type":"img","img_url":"http://localhost/upload/subject/85c8f939-b099-4821-9f9a-e88dfe3f8ae0.jpg","id":938,"areaInfo":"226_65_366_168=http://www.baidu.com-"}]
	@Column(columnDefinition = "LongText")
	private String seo_keywords;// SEO关键字
	@Column(columnDefinition = "LongText")
	private String seo_description;// SEO描述
	private String sub_title; //品牌标题

	public String getSeo_keywords() {
		return seo_keywords;
	}

	public void setSeo_keywords(String seo_keywords) {
		this.seo_keywords = seo_keywords;
	}

	public String getSeo_description() {
		return seo_description;
	}

	public void setSeo_description(String seo_description) {
		this.seo_description = seo_description;
	}

	public String getSub_title() {
		return sub_title;
	}

	public void setSub_title(String sub_title) {
		this.sub_title = sub_title;
	}

	public Subject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getIfcType() {
		return ifcType;
	}

	public void setIfcType(String ifcType) {
		this.ifcType = ifcType;
	}

	public Subject(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getDisplay() {
		return display;
	}

	public void setDisplay(int display) {
		this.display = display;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Accessory getBanner() {
		return banner;
	}

	public void setBanner(Accessory banner) {
		this.banner = banner;
	}

	public String getSubject_detail() {
		return subject_detail;
	}

	public void setSubject_detail(String subject_detail) {
		this.subject_detail = subject_detail;
	}

	public int getDisplayAPP20() {
		return displayAPP20;
	}

	public void setDisplayAPP20(int displayAPP20) {
		this.displayAPP20 = displayAPP20;
	}
	

}
