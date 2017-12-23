package com.iskyshop.lsolr.entity;

import java.util.List;

import com.iskyshop.lucene.LuceneVo;

public class QryResultWrapper {
	
	public QryResultWrapper(int totalHits,List<LuceneVo> luceneVoList){
		this.totalHits = totalHits;
		this.luceneVoList = luceneVoList;
	}
	private int totalHits;
	private List<LuceneVo> luceneVoList;
	public List<LuceneVo> getLuceneVoList(){
		return luceneVoList;
	}
	
	public int getTotalHits() {
		return totalHits;
	}
	
}
