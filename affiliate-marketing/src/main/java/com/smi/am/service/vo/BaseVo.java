package com.smi.am.service.vo;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 基础vo
 * @author smi
 *
 */
public class BaseVo {
	
	@ApiModelProperty(value="页码")
	private Integer pageNum; //页码
	
	@ApiModelProperty(value="每页显示数量")
	private Integer pageSize; //每页显示数量
	
    @ApiModelProperty(value = "总记录数")
    private long total;
    
    @ApiModelProperty(value = "总页数")
    private int pages;


	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}
	
	

}
