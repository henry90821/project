package com.iskyshop.kuaidi100.pojo;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * <p>
 * Title: NoticeResponse.java
 * </p>
 * 
 * <p>
 * Description: 该实体类来自快递100提供的接口
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
 * 
 * @version iskyshop_b2b2c 2015
 */
public class NoticeResponse {
	private static XStream xstream;

	private Boolean result;
	private String returnCode;
	private String message;

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private static XStream getXStream() {
		if (xstream == null) {
			xstream = new XStream();
			xstream.autodetectAnnotations(true);
			xstream.alias("pushResponse", NoticeResponse.class);
		}
		return xstream;
	}

	/**
	 * 转换为xml文件
	 * 
	 * @return 转换后的xml字符串
	 */
	public String toXml() {
		return "<?xml version='1.0' encoding='UTF-8'?>\r\n" + getXStream().toXML(this);
	}

	/**
	 * 从xml中解析出通知消息
	 * 
	 * @param sXml
	 *            xml字符串
	 * @return 通知响应
	 */
	public static NoticeResponse fromXml(String sXml) {
		return (NoticeResponse) getXStream().fromXML(sXml);
	}

}
