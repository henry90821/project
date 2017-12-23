package com.smi.mc.common;

import java.io.Serializable;

public class PageMap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int startNum;
	private int pageSize;
	private int totalRecord;
	private int totalPage;

	public PageMap() {
		this.startNum = 0;
		this.pageSize = 10;
		this.totalRecord = -1;
	}

	public int getStartNum() {
		return this.startNum;
	}

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalRecord() {
		return this.totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;

		int totalPage = (totalRecord % this.pageSize == 0) ? totalRecord / this.pageSize
				: totalRecord / this.pageSize + 1;
		setTotalPage(totalPage);
	}

	public int getTotalPage() {
		return this.totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
}