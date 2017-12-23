package com.smi.mc.vo;

import com.smilife.core.common.valueobject.BaseValueObject;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 自定义响应结构
 */

public class SmiResult<T> extends BaseValueObject {

    // 响应中的数据
    @ApiModelProperty(value = "响应中的业务数据") 
    private T data;
    
    @ApiModelProperty(value = "总记录数")
    private long total;
    
    @ApiModelProperty(value = "总页数")
    private int pages;

    @ApiModelProperty(value = "一页记录数")
    private int pageSize;
    


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

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
