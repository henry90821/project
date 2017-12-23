package com.iskyshop.lsolr.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.lsolr.util.SolrServerFactory;
import com.iskyshop.lsolr.util.SolrUtil;
import com.iskyshop.lucene.LuceneVo;

public class WriterWrapperSolr implements IWriterWrapper{
	private Logger logger = Logger.getLogger(this.getClass());
	
	private String indexPath;
	private HttpSolrServer server;
	
	WriterWrapperSolr(String indexPath){
		this.indexPath = indexPath;
		this.server = SolrServerFactory.getServer(this.indexPath);
	}

	@Override
	public void addDocument(LuceneVo luceneVo) throws SolrServerException, IOException {
		SolrInputDocument data = builderDocument(luceneVo);
		SolrUtil.add(server, data);
	}

	@Override
	public void addDocument(List<LuceneVo> luceneVoList) throws SolrServerException, IOException {
		if(luceneVoList!=null&&luceneVoList.size()>0){
			List<SolrInputDocument> dataList = new ArrayList<>();
			for (LuceneVo lucenceVo : luceneVoList) {
				SolrInputDocument document = builderDocument(lucenceVo);
				dataList.add(document);
			}
			SolrUtil.add(server, dataList);
		}
	}

	@Override
	public void commit() throws SolrServerException, IOException{
		server.commit();
	}

	@Override
	public void updateDocument(String idValue, LuceneVo vo, String idKey) throws SolrServerException, IOException {
		SolrInputDocument data = builderDocument(vo);
		SolrUtil.update(server, data);
	}

	@Override
	public void deleteDocuments(String idValue,String idKey) throws SolrServerException, IOException {
		SolrUtil.delete(server, idValue);
	}

	@Override
	public void iSNotUnlock() {
//		server.shutdown();
	}
	
	@Override
	public void rollBack(String info) {
		try {
			server.rollback();
		} catch (SolrServerException | IOException e) {
			logger.error("操作发生异常,info is:"+info+",执行回滚失败,indexPath is:"+indexPath, e);
		}
		
	}
	
	private static SolrInputDocument builderDocument(LuceneVo luceneVo) {
		SolrInputDocument document = new SolrInputDocument();
		if ("goods".equals(luceneVo.getVo_type())) {
			if (luceneVo.getVo_main_photo_url() != null) {
				document.addField(LuceneVo.MAIN_PHOTO_URL,CommUtil.null2String(luceneVo.getVo_main_photo_url()));
			}
			document.addField(LuceneVo.ID, String.valueOf(luceneVo.getVo_id()));
			document.addField(LuceneVo.TITLE, Jsoup.clean(luceneVo.getVo_title(), Whitelist.none()),10F);
			document.addField(LuceneVo.CONTENT, Jsoup.clean(luceneVo.getVo_content(), Whitelist.none()));
			document.addField(LuceneVo.TYPE, luceneVo.getVo_type());
			document.addField(LuceneVo.ADD_TIME, luceneVo.getVo_add_time());
			document.addField(LuceneVo.GOODS_SALENUM, luceneVo.getVo_goods_salenum());
			document.addField(LuceneVo.GOODS_COLLECT, luceneVo.getVo_goods_collect());
			document.addField(LuceneVo.WELL_EVALUATE, luceneVo.getVo_well_evaluate());
			document.addField(LuceneVo.STORE_PRICE, luceneVo.getVo_store_price());
			document.addField(LuceneVo.GOODS_INVENTORY, luceneVo.getVo_goods_inventory());// 库存改为数值型字段
			document.addField(LuceneVo.GOODS_TYPE, CommUtil.null2String(luceneVo.getVo_goods_type()));
			document.addField(LuceneVo.PHOTOS_URL, CommUtil.null2String(luceneVo.getVo_photos_url()));
			document.addField(LuceneVo.GOODS_EVAS, CommUtil.null2String(luceneVo.getVo_goods_evas()));
			{
				// 由于品牌名较短，所以此处即使不加强该域的权重，该域的优先级也较高。
				// 此域采用不分词分析器
				String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
				Pattern pat = Pattern.compile(regEx);
				Matcher mat = pat.matcher(CommUtil.null2String(luceneVo.getVo_goods_brandname()));
				String gb_name = mat.replaceAll("").trim().toLowerCase();
				document.addField(LuceneVo.GOODS_BRAND, gb_name);
			}
			document.addField(LuceneVo.GOODS_CLASS, CommUtil.null2String(luceneVo.getVo_goods_class()));
			document.addField(LuceneVo.GOODS_TRANSFEE,CommUtil.null2String(luceneVo.getVo_goods_transfee()));
			document.addField(LuceneVo.GOODS_COD, CommUtil.null2String(luceneVo.getVo_goods_cod()));
			document.addField(LuceneVo.WHETHER_ACTIVE,CommUtil.null2String(luceneVo.getVo_whether_active()));
			document.addField(LuceneVo.F_SALE_TYPE, CommUtil.null2String(luceneVo.getVo_f_sale_type()));
			document.addField(LuceneVo.SECKILL_BUY, luceneVo.getSeckill_buy());
			document.addField(LuceneVo.CONFIG_CODE, CommUtil.null2String(luceneVo.getVo_goods_config()));
		}

		if ("lifegoods".equals(luceneVo.getVo_type())) {
			if (luceneVo.getVo_main_photo_url() != null) {
				document.addField(LuceneVo.MAIN_PHOTO_URL,CommUtil.null2String(luceneVo.getVo_main_photo_url()));
			}
			document.addField(LuceneVo.ID, String.valueOf(luceneVo.getVo_id()));
			document.addField(LuceneVo.TITLE, Jsoup.clean(luceneVo.getVo_title(), Whitelist.none()),10);
			document.addField(LuceneVo.CONTENT, Jsoup.clean(luceneVo.getVo_content(), Whitelist.none()));
			document.addField(LuceneVo.TYPE, luceneVo.getVo_type());
			document.addField(LuceneVo.STORE_PRICE, luceneVo.getVo_store_price());
			document.addField(LuceneVo.ADD_TIME, CommUtil.null2String(luceneVo.getVo_add_time()));
			document.addField(LuceneVo.GOODS_SALENUM,CommUtil.null2String(luceneVo.getVo_goods_salenum()));
			document.addField(LuceneVo.CAT, CommUtil.null2String(luceneVo.getVo_cat()));
			document.addField(LuceneVo.GOODS_RATE, CommUtil.null2String(luceneVo.getVo_rate()));
			document.addField(LuceneVo.COST_PRICE, CommUtil.null2String(luceneVo.getVo_cost_price()));
			document.addField(LuceneVo.GOODS_AREA, CommUtil.null2String(luceneVo.getVo_goods_area()));
		}
		
		return document;
	}

}
