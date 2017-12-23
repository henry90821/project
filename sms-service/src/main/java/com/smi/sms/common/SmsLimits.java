package com.smi.sms.common;

import java.util.Date;

import com.smi.tools.kits.DateKit;

public class SmsLimits {
	
	private String phoneNo;
	
	private int daysCount;
	
	private int minutesCount;
	
	private Date minutesStartTime;

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public int getDaysCount() {
		return daysCount;
	}

	public void setDaysCount(int daysCount) {
		this.daysCount = daysCount;
	}

	public int getMinutesCount() {
		return minutesCount;
	}

	public void setMinutesCount(int minutesCount) {
		this.minutesCount = minutesCount;
	}

	public Date getMinutesStartTime() {
		return minutesStartTime;
	}

	public void setMinutesStartTime(Date minutesStartTime) {
		this.minutesStartTime = minutesStartTime;
	}
	
	/**
	 * 获取时间差，单位 分钟
	 * @return 分钟数
	 */
	public long getDiffMinutes() {
		return DateKit.diff(minutesStartTime, new Date(), 3);
	}
}
