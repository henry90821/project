package com.iskyshop.lsolr.entity;

import com.iskyshop.lsolr.util.ISearchEngine;
import com.iskyshop.lsolr.util.SolrSearchEngine;

public class WrapperFactory {
	
	public static IParamWrapper newParamWrapper(String params,String parseFieldName,Boolean allowLeadingWildcard,HighlighterParam highlighterParam,SortWrapper sortWrapper)throws Exception{
		return new ParamWrapper(params, parseFieldName,allowLeadingWildcard,highlighterParam,sortWrapper);
	}
	
	public static ISearchWrapper newSearchWrapper(String indexPath,IParamWrapper queryWrapper) throws Exception{
//		return new SearchWrapper(indexPath,queryWrapper);
		return new SearchWrapperSolr(indexPath,queryWrapper);
	}
	
	public static IWriterWrapper newWriterWrapper(String indexPath){
//		return new WriterWrapper(indexPath);
		return new WriterWrapperSolr(indexPath);
	}
	
	public static ISearchEngine newSearchEngine(){
//		return new LuceneSearchEngine();
		return new SolrSearchEngine();
	}
	
}
