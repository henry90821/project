package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: Accessory.java
 * </p>
 * 
 * <p>
 * Description:系统附件管理类，用来管理系统所有附件信息，包括图片附件、rar附件等等
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
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "accessory")
public class Accessory extends IdEntity {
	private String name; // 附件名称
	private String path; // 存放路径
	@Column(precision = 12, scale = 2)
	private BigDecimal size; // 附件大小
	private int width; // 宽度
	private int height; // 高度
	private String ext; // 扩展名，不包括.
	private String info; // 取值：eva_temp：表示为临时图片，如用户晒单时上传的临时图片，这类图片会在半小时任务中被清除；eva_img：表示为用户已提交保存的正式图片
	@ManyToOne(fetch = FetchType.LAZY)
	private User user; // 附件对应的用户，可以精细化管理用户附件
	@ManyToOne(fetch = FetchType.LAZY)
	private Album album; // 图片对应的相册
	@OneToOne(mappedBy = "album_cover", fetch = FetchType.LAZY)
	private Album cover_album; // 相册封面对应的图片
	@ManyToOne(fetch = FetchType.LAZY)
	private SysConfig config; // 对应登录页左侧图片
	@OneToMany(mappedBy = "goods_main_photo")
	private List<Goods> goods_main_list = new ArrayList<Goods>();
	@ManyToMany(mappedBy = "goods_photos")
	private List<Goods> goods_list = new ArrayList<Goods>(); // 商品列表

	public Accessory(Long id, Date addTime) {
		super(id, addTime);
	}

	public Accessory(Long id) {
		super.setId(id);
	}

	public Accessory() {
		super();
	}

	public void setSize(BigDecimal size) {
		this.size = size;
	}

	public List<Goods> getGoods_main_list() {
		return goods_main_list;
	}

	public void setGoods_main_list(List<Goods> goods_main_list) {
		this.goods_main_list = goods_main_list;
	}

	public List<Goods> getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public BigDecimal getSize() {
		return size;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public Album getCover_album() {
		return cover_album;
	}

	public void setCover_album(Album cover_album) {
		this.cover_album = cover_album;
	}

	public SysConfig getConfig() {
		return config;
	}

	public void setConfig(SysConfig config) {
		this.config = config;
	}
}
