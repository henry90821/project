package com.iskyshop.lsolr.entity;

import org.apache.lucene.search.SortField;

public class SortWrapper{
	/**待排序字段*/
	private String field;
	/**字段数据类型*/
	private SortField.Type type;
	/**是否倒叙,true倒叙desc,false升序asc*/
	private boolean reverse;
	
	public SortWrapper(String field, SortField.Type type, boolean reverse){
		this.field = field;
		this.type = type;
		this.reverse = reverse;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SortWrapper [field=");
		builder.append(field);
		builder.append(", type=");
		builder.append(type);
		builder.append(", reverse=");
		builder.append(reverse);
		builder.append("]");
		return builder.toString();
	}

	public String getField() {
		return field;
	}

	public SortField.Type getType() {
		return type;
	}

	public boolean isReverse() {
		return reverse;
	}

}
