package com.iskyshop.lsolr.entity;

import java.util.List;

import com.iskyshop.lucene.LuceneVo;

/**
 * 搜索引擎写操作接口类
 * @author shiyl
 *
 */
public interface IWriterWrapper {
	
	void addDocument(LuceneVo luceneVo) throws Exception;
	
	void addDocument(List<LuceneVo> luceneVoList) throws Exception;

	void commit() throws Exception;

	void updateDocument(String idValue, LuceneVo vo,String idKey) throws Exception;

	void deleteDocuments(String idValue,String idKey) throws Exception;
	
	/**释放资源*/
	void iSNotUnlock();
	
	/**
	 * 回滚处理
	 * @param info 描述信息
	 */
	void rollBack(String info);
}
