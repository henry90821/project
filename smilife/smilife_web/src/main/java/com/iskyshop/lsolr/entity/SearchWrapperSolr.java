package com.iskyshop.lsolr.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.lsolr.util.SolrServerFactory;
import com.iskyshop.lsolr.util.SolrUtil;
import com.iskyshop.lucene.LuceneVo;

/**
 * 了解SolrQuery的使用详情
 * 过滤器的使用
 * 排序的使用
 * @author shiyl
 */
public class SearchWrapperSolr implements ISearchWrapper{
	private String indexPath;
	private HttpSolrServer server;
	private IParamWrapper queryWrapper;
	public SearchWrapperSolr(String indexPath,IParamWrapper queryWrapper){
		this.indexPath = indexPath;
		this.queryWrapper = queryWrapper;
		this.server = SolrServerFactory.getServer(this.indexPath);
	}
	
	
	@Override
	public void close() {
//		if(server!=null){
//			server.shutdown();
//		}
	}
	
	@Override
	public QryResultWrapper searchLucene(String filterFieldName, int n)
			throws Exception {
		SolrQuery query = constructQuery(queryWrapper);
		query.setRows(n);
		QueryResponse resp = SolrUtil.query(server, query);
		return toWrapper(resp);
	}

	@Override
	public QryResultWrapper searchLucene(int n) throws Exception {
		String filterFieldName = null;
		return searchLucene(filterFieldName, n);
	}

	@Override
	public QryResultWrapper searchByPage(int currentPage, int pageSize) throws Exception {
		SolrQuery query = constructQuery(queryWrapper);
		query.setStart((currentPage-1)*pageSize);
		query.setRows(pageSize);
		QueryResponse resp = SolrUtil.query(server, query);
		return toWrapper(resp);
	}
	
	@Override
	public IParamWrapper getQueryWrapper() {
		return queryWrapper;
	}
	
	private QryResultWrapper toWrapper(QueryResponse resp){
		SolrDocumentList docList = resp.getResults();
		Map<String, Map<String, List<String>>>  highlighting = resp.getHighlighting();
		return new QryResultWrapper((int)docList.getNumFound(), parseDocument(docList,highlighting));
	}
	private List<LuceneVo> parseDocument(SolrDocumentList docList,Map<String, Map<String, List<String>>>  highlighting) {
		List<LuceneVo> resList = new ArrayList<>();
		if(docList!=null&&docList.size()>0){
			for(SolrDocument doc:docList){
				resList.add(parseDocument(doc,highlighting));
			}
		}
		return resList;
	}
	
	private LuceneVo parseDocument(SolrDocument doc,Map<String, Map<String, List<String>>>  highlighting) {
		LuceneVo vo = new LuceneVo();
		
		// 商品id，名称，图片
		vo.setVo_id(CommUtil.null2Long(doc.get(LuceneVo.ID)));
		
		// 对商品名称进行关键字高亮
		String title = null;
		try {
			title = highlighting.get(doc.get(LuceneVo.ID)+"").get(LuceneVo.TITLE).get(0);
		} catch (Exception e) {}
		
		if (title == null) {
			vo.setVo_title((String)doc.get(LuceneVo.TITLE));
		} else {
			vo.setVo_title(title);
		}
		
		vo.setVo_main_photo_url((String)doc.get(LuceneVo.MAIN_PHOTO_URL));
		vo.setVo_photos_url((String)doc.get(LuceneVo.PHOTOS_URL));
		// 价格，销量，评论，类型
		vo.setVo_store_price(CommUtil.null2Double(doc.get(LuceneVo.STORE_PRICE)));
		vo.setVo_goods_salenum(CommUtil.null2Int(doc.get(LuceneVo.GOODS_SALENUM)));
		vo.setVo_goods_evas(CommUtil.null2Int(doc.get(LuceneVo.GOODS_EVAS)));
		vo.setVo_goods_type(CommUtil.null2Int(doc.get(LuceneVo.GOODS_TYPE)));
		vo.setVo_whether_active(CommUtil.null2Int(doc.get(LuceneVo.WHETHER_ACTIVE)));
		vo.setVo_f_sale_type(CommUtil.null2Int(doc.get(LuceneVo.F_SALE_TYPE)));
		vo.setSeckill_buy(CommUtil.null2Int(doc.get(LuceneVo.SECKILL_BUY)));
		vo.setVo_goods_config(CommUtil.null2String(doc.get(LuceneVo.CONFIG_CODE)));
		vo.setVo_goods_inventory(CommUtil.null2Int(doc.get(LuceneVo.GOODS_INVENTORY)));
		return vo;
	}
	
	private static SolrQuery constructQuery(IParamWrapper queryWrapper){
		SolrQuery query = new SolrQuery(queryWrapper.getParams());
		
		if(queryWrapper.getHighlighterParam()!=null){//高亮处理
			HighlighterParam highParam = queryWrapper.getHighlighterParam();
			query.setHighlight(true);//开启高亮  
            query.setHighlightFragsize(highParam.getTextmaxlength());//返回的字符个数  
//            query.setHighlightRequireFieldMatch(true); 
            query.setHighlightSimplePost(highParam.getSuffixHTML());    //前缀  
            query.setHighlightSimplePre(highParam.getPrefixHTML());    //后缀  
            query.setParam("hl.fl", highParam.getFieldName());
            
            query.setParam("defType", "edismax");
            query.setParam("mm", "0<100% 2<85%");//[1,2]100%,[3,)85%,
		}
		
		if(queryWrapper.getSortWrapper()!=null){//排序处理
			SortWrapper sortWrapper = queryWrapper.getSortWrapper();
			query.addSort(sortWrapper.getField(), sortWrapper.isReverse()?SolrQuery.ORDER.desc:SolrQuery.ORDER.asc);
		}
		
		return query;
	}
	
//	/**
//	 * 重新将需要查询的文本内容解析成分词
//     * @param core
//     * @param searchText
//     * @return
//     * @throws SolrServerException
//     */
//    private static String analysisSearchText(HttpSolrServer core, String searchText) throws SolrServerException, UnsupportedEncodingException {
//        StringBuilder strSearchText = new StringBuilder();
//        final String STR_FIELD_TYPE = "text_cn";
//        SolrQuery queryAnalysis = new SolrQuery();
//        queryAnalysis.add(CommonParams.QT, "/analysis/field"); // query type
//        queryAnalysis.add(AnalysisParams.FIELD_VALUE, searchText);
//        queryAnalysis.add(AnalysisParams.FIELD_TYPE, STR_FIELD_TYPE);
//        QueryResponse responseAnalysis = core.query(queryAnalysis);
//        //对响应进行解析
//        NamedList<Object> analysis = (NamedList<Object>) responseAnalysis.getResponse().get("analysis");// analysis node
//        NamedList<Object> field_types = (NamedList<Object>) analysis.get("field_types");// field_types node
//        NamedList<Object> fieldType = (NamedList<Object>) field_types.get(STR_FIELD_TYPE);// text_cn node
//        NamedList<Object> index = (NamedList<Object>) fieldType.get("index");// index node
//        List<SimpleOrderedMap<String>> list = (ArrayList<SimpleOrderedMap<String>>)index.get("org.wltea.analyzer.lucene.IKTokenizer");// tokenizer node
//        // 在每个词条中间加上空格，为每个词条进行或运算
//        for(Iterator<SimpleOrderedMap<String>> iter = list.iterator(); iter.hasNext();)
//        {
//            strSearchText.append(iter.next().get("text") + " ");
//        }
//        return strSearchText.toString();
//    }
	
}
