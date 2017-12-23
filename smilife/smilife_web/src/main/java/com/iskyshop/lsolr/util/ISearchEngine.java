package com.iskyshop.lsolr.util;

/**
 * 搜索引擎工具接口定义
 * @author shiyl
 *
 */
public interface ISearchEngine {

	public boolean checkIndexPathExist(String indexPath);

//	public void throwException(Exception e);

	public void throwException(String message, Exception e);
	
}
