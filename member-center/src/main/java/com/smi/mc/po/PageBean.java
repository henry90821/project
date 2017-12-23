package com.smi.mc.po;

public class PageBean {

	private int page; // 页数
	private int rows; // 每页显示的行数
	private int total; // 总数
	private int pages; // 总页数
	private int pagesize;// 当前页的条数
	// 构造函数，初始化赋值

	public PageBean(int page, int rows) {
		super();
		this.page = page;
		this.rows = rows;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getStart() {
		return (page - 1) * rows + 1;
	}

	public int getEnd() {
		return (page - 1) * rows + rows;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize, int pageIndex, int total) {
		int pageNum = pageIndex * pagesize;
		if (total / pageNum < 1) {
			int num = total % pagesize;
			this.pagesize = num;
		} else {
			this.pagesize = pagesize;
		}
	}

}
