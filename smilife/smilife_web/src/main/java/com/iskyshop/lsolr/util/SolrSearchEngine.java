package com.iskyshop.lsolr.util;

import org.apache.log4j.Logger;

public class SolrSearchEngine implements ISearchEngine{
	private Logger logger = Logger.getLogger(this.getClass());
	/**
	 * 校验索引文件是否存在
	 * @return true存在,false不存在
	 */
	@Override
	public boolean checkIndexPathExist(String indexPath) {
		return true;
	}
	
//	@Override
//	public void throwException(Exception e) {
////		throw new RuntimeException(e);
//		logger.error("Solr引擎执行发生异常",e);
//	}
	
	@Override
	public void throwException(String message, Exception e) {
//		throw new RuntimeException(message, e);
		logger.error("Solr引擎执行发生异常,message is:"+message,e);
	}
	
	
}
