package com.iskyshop.lsolr.entity;

/**
 * 高亮显示参数配置
 * @author shiyl
 *
 */
public class HighlighterParam {
	private static String defult_prefixHTML = "<font color='red'>"; // 高亮html前置
	private static String defult_suffixHTML = "</font>"; // 高亮html后置
	private int defult_textmaxlength = 100; // 截取字符串长度，该长度类关键词高亮显示
	
	private String keyword;
	private String fieldName;
	private String prefixHTML;
	private String suffixHTML;
	private int textmaxlength;
	
	public HighlighterParam(String keyword,String fieldName){
		this.keyword = keyword;
		this.fieldName = fieldName;
		this.prefixHTML = defult_prefixHTML;
		this.suffixHTML = defult_suffixHTML;
		this.textmaxlength = defult_textmaxlength;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HighlighterParam [keyword=");
		builder.append(keyword);
		builder.append(", fieldName=");
		builder.append(fieldName);
		builder.append(", prefixHTML=");
		builder.append(prefixHTML);
		builder.append(", suffixHTML=");
		builder.append(suffixHTML);
		builder.append(", textmaxlength=");
		builder.append(textmaxlength);
		builder.append("]");
		return builder.toString();
	}

	
	public String getKeyword() {
		return keyword;
	}


	public String getFieldName() {
		return fieldName;
	}


	public String getPrefixHTML() {
		return prefixHTML;
	}


	public String getSuffixHTML() {
		return suffixHTML;
	}


	public int getTextmaxlength() {
		return textmaxlength;
	}

}
