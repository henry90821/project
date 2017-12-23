package com.iskyshop.lsolr.entity;

public interface IParamWrapper {

	String getParams();

	String getParseFieldName();

	Boolean getAllowLeadingWildcard();
	
	/**高亮信息*/
	HighlighterParam getHighlighterParam();
	
	/**排序信息*/
	SortWrapper getSortWrapper();
	
}
