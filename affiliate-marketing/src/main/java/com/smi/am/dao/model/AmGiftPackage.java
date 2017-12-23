package com.smi.am.dao.model;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

public class AmGiftPackage {
	private Integer gpId;

	private String gpName;

	private String gpChannel;

	private String gpSendnum;

	private String gpActivityarea;

	private String gpActivityshop;

	private String gpActivityzone;

	private Date gpStarttime;

	private Date gpEndtime;

	private Integer gpProvideall;

	private String gpStatus;

	private Date gpCreatedate;

	private String gpCreateuser;

	private Date gpLastmoddate;

	private String gpLastmoduser;

	private String gpRemark;

	private byte[] gpProvideuser;

	private Integer preSendNum;

	private Integer sendNum;

	private Integer gpDeliveringWay;

	private Integer gpIsDetele;

	private Integer gpRemainNum;

	public Integer getGpId() {
		return gpId;
	}

	public void setGpId(Integer gpId) {
		this.gpId = gpId;
	}

	public String getGpName() {
		return gpName;
	}

	public void setGpName(String gpName) {
		this.gpName = gpName == null ? null : gpName.trim();
	}

	public String getGpChannel() {
		return gpChannel;
	}

	public void setGpChannel(String gpChannel) {
		this.gpChannel = gpChannel == null ? null : gpChannel.trim();
	}

	public String getGpSendnum() {
		return gpSendnum;
	}

	public void setGpSendnum(String gpSendnum) {
		this.gpSendnum = gpSendnum;
	}

	public String getGpActivityarea() {
		return gpActivityarea;
	}

	public void setGpActivityarea(String gpActivityarea) {
		this.gpActivityarea = gpActivityarea == null ? null : gpActivityarea.trim();
	}

	public String getGpActivityshop() {
		return gpActivityshop;
	}

	public void setGpActivityshop(String gpActivityshop) {
		this.gpActivityshop = gpActivityshop == null ? null : gpActivityshop.trim();
	}

	public Date getGpStarttime() {
		return gpStarttime;
	}

	public void setGpStarttime(Date gpStarttime) {
		this.gpStarttime = gpStarttime;
	}

	public Date getGpEndtime() {
		return gpEndtime;
	}

	public void setGpEndtime(Date gpEndtime) {
		this.gpEndtime = gpEndtime;
	}

	public Integer getGpProvideall() {
		return gpProvideall;
	}

	public void setGpProvideall(Integer gpProvideall) {
		this.gpProvideall = gpProvideall;
	}

	public String getGpStatus() {
		return gpStatus;
	}

	public void setGpStatus(String gpStatus) {
		this.gpStatus = gpStatus == null ? null : gpStatus.trim();
	}

	public Date getGpCreatedate() {
		return gpCreatedate;
	}

	public void setGpCreatedate(Date gpCreatedate) {
		this.gpCreatedate = gpCreatedate;
	}

	public String getGpCreateuser() {
		return gpCreateuser;
	}

	public void setGpCreateuser(String gpCreateuser) {
		this.gpCreateuser = gpCreateuser == null ? null : gpCreateuser.trim();
	}

	public Date getGpLastmoddate() {
		return gpLastmoddate;
	}

	public void setGpLastmoddate(Date gpLastmoddate) {
		this.gpLastmoddate = gpLastmoddate;
	}

	public String getGpLastmoduser() {
		return gpLastmoduser;
	}

	public void setGpLastmoduser(String gpLastmoduser) {
		this.gpLastmoduser = gpLastmoduser == null ? null : gpLastmoduser.trim();
	}

	public String getGpRemark() {
		return gpRemark;
	}

	public void setGpRemark(String gpRemark) {
		this.gpRemark = gpRemark == null ? null : gpRemark.trim();
	}

	public byte[] getGpProvideuser() {
		return gpProvideuser;
	}

	public void setGpProvideuser(byte[] gpProvideuser) {
		this.gpProvideuser = gpProvideuser;
	}

	public Integer getPreSendNum() {
		return preSendNum;
	}

	public void setPreSendNum(Integer preSendNum) {
		this.preSendNum = preSendNum;
	}

	public Integer getSendNum() {
		return sendNum;
	}

	public void setSendNum(Integer sendNum) {
		this.sendNum = sendNum;
	}

	public Integer getGpDeliveringWay() {
		return gpDeliveringWay;
	}

	public void setGpDeliveringWay(Integer gpDeliveringWay) {
		this.gpDeliveringWay = gpDeliveringWay;
	}

	public Integer getGpIsDetele() {
		return gpIsDetele;
	}

	public void setGpIsDetele(Integer gpIsDetele) {
		this.gpIsDetele = gpIsDetele;
	}

	public Integer getGpRemainNum() {
		return gpRemainNum;
	}

	public void setGpRemainNum(Integer gpRemainNum) {
		this.gpRemainNum = gpRemainNum;
	}

	public String getGpActivityzone() {
		return gpActivityzone;
	}

	public void setGpActivityzone(String gpActivityzone) {
		this.gpActivityzone = gpActivityzone;
	}

	public byte[] listToByte(String[] list) throws UnsupportedEncodingException {
		  StringBuffer sb=new StringBuffer();
		    for(String s: list){
		      sb.append(s);
		      sb.append(",");
		    }
		  String str=sb.delete(sb.length()-1, sb.length()).toString();
		  byte[] b = str.getBytes("utf-8");
		 return b;
	}
	
	public String byteToStr(byte[] b) throws UnsupportedEncodingException{
		String str="";
		if(b !=null){
		 str=new String(b,"utf-8");
		}
		return str;
	}
}