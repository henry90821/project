package com.iskyshop.foundation.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;
import com.iskyshop.manage.admin.tools.HtmlFilterTools;

/**
 * 
 * <p>
 * Title: Message.java
 * </p>
 * 
 * <p>
 * Description:站内短信类，用户之间可以根据用户名发送站内短信息
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
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate=true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "message")
public class Message extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User fromUser;// 发送用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User toUser;// 接收用户
	private int status;// 短信状态,0是未读，1为已读
	@Column(columnDefinition = "int default 0")
	private int reply_status;// 短信回复状态，0为没有回复，1为有新回复
	private String title;// 短信标题
	@Column(columnDefinition = "LongText")
	private String content;// 短信内容
	@ManyToOne(fetch = FetchType.LAZY)
	private Message parent;
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	List<Message> replys = new ArrayList<Message>();// 短信回复，和parent进行映射
	private int type;// 短信类型，0为系统短信，1为用户之间的短信
	@Column(columnDefinition = "int default 0")
	private int msg_cat;// 0为第一次发送的短信，1为短信回复

	public Message() {
		super();
	}

	public Message(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public int getMsg_cat() {
		return msg_cat;
	}

	public void setMsg_cat(int msg_cat) {
		this.msg_cat = msg_cat;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	public User getToUser() {
		return toUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		content = HtmlFilterTools.delAllTag(content);
		this.content = content;
	}

	public Message getParent() {
		return parent;
	}

	public void setParent(Message parent) {
		this.parent = parent;
	}

	public List<Message> getReplys() {
		return replys;
	}

	public void setReplys(List<Message> replys) {
		this.replys = replys;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getReply_status() {
		return reply_status;
	}

	public void setReply_status(int reply_status) {
		this.reply_status = reply_status;
	}
}
