package com.iskyshop.lsolr.entity;

public class ParamWrapper implements IParamWrapper{
	private String params;
	private String parseFieldName;
	private Boolean allowLeadingWildcard;
	
	private HighlighterParam highlighterParam;
	private SortWrapper sortWrapper;
	
	ParamWrapper(String params,String parseFieldName,Boolean allowLeadingWildcard,HighlighterParam highlighterParam,SortWrapper sortWrapper) throws Exception{
		this.params = params;
		this.parseFieldName = parseFieldName;
		this.allowLeadingWildcard = allowLeadingWildcard;
		this.highlighterParam = highlighterParam;
		this.sortWrapper = sortWrapper;
	}
	
	


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParamWrapper [params=");
		builder.append(params);
		builder.append(", parseFieldName=");
		builder.append(parseFieldName);
		builder.append(", allowLeadingWildcard=");
		builder.append(allowLeadingWildcard);
		builder.append(", highlighterParam=");
		builder.append(highlighterParam);
		builder.append(", sortWrapper=");
		builder.append(sortWrapper);
		builder.append("]");
		return builder.toString();
	}




	@Override
	public String getParams() {
		return params;
	}
	
	@Override
	public String getParseFieldName() {
		return parseFieldName;
	}
	
	@Override
	public Boolean getAllowLeadingWildcard() {
		return allowLeadingWildcard;
	}
	
	@Override
	public HighlighterParam getHighlighterParam() {
		return highlighterParam;
	}

	@Override
	public SortWrapper getSortWrapper() {
		return sortWrapper;
	}
	
	
}
