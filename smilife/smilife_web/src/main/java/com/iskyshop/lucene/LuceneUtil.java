package com.iskyshop.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.nutz.lang.util.ArraySet;

import com.alibaba.fastjson.JSON;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.lsolr.entity.HighlighterParam;
import com.iskyshop.lsolr.entity.IParamWrapper;
import com.iskyshop.lsolr.entity.ISearchWrapper;
import com.iskyshop.lsolr.entity.IWriterWrapper;
import com.iskyshop.lsolr.entity.QryResultWrapper;
import com.iskyshop.lsolr.entity.SortWrapper;
import com.iskyshop.lsolr.entity.WrapperFactory;
import com.iskyshop.lsolr.util.ISearchEngine;
import com.iskyshop.lucene.pool.LuceneThreadPool;

/**
 * 
 * <p>
 * Title: LuceneUtil.java
 * </p>
 * 
 * <p>
 * Description: lucene搜索工具类,用来写入索引，搜索数据
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
public class LuceneUtil {
	private Logger logger = Logger.getLogger(this.getClass());

	private static LuceneUtil lucene = null;

	private ISearchEngine searchEngine = null;

	

	private int pageSize = 24;

	private static SysConfig sysConfig;
	private static ThreadLocal<String> indexPathThreadLocal = new ThreadLocal<String>();
	private static ThreadLocal<Integer> gcSizeThreadLocal = new ThreadLocal<Integer>();


	/** 初始化工具 * */
	public LuceneUtil() {
		searchEngine = WrapperFactory.newSearchEngine();
	}

	/**
	 * 安全懒汉式单例
	 * 
	 * @Title: instance
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return 参数
	 * @return LuceneUtil 返回类型
	 * @throws
	 */
	public static LuceneUtil instance() {
		if (lucene == null) {
			synchronized (LuceneUtil.class) {
				if (lucene == null) {
					lucene = new LuceneUtil();
				}
			}
		}
		return lucene;
	}

	public static void setGc_size(int gc_size) {
		gcSizeThreadLocal.set(gc_size);
	}

	public static Integer getGc_size() {
		return gcSizeThreadLocal.get() == null ? 0 : gcSizeThreadLocal.get();
	}

	public static void setConfig(SysConfig config) {
		LuceneUtil.sysConfig = config;
	}

	public static void setIndex_path(String index_path) {
		indexPathThreadLocal.set(index_path);
	}

	public static String getIndex_path() {
		return indexPathThreadLocal.get() == null ? "" : indexPathThreadLocal.get();
	}

	/**
	 * 此方法为特定类型的排序
	 * 
	 * @param params
	 * @param after
	 * @return
	 */
	public LuceneResult search(String keyword, int currentPage,int pageSize, String goods_inventory, String goods_type, String goods_class, String goods_transfee, String goods_cod, SortWrapper sort, String goods_cat, String goods_area, String gb_name) {
		LuceneResult pList = new LuceneResult();
		List<LuceneVo> vo_list = new ArrayList<LuceneVo>();
		ISearchWrapper isearcher = null;
		int pages = 0;
		int rows = 0;
		IParamWrapper queryWrapper = null;
		try {
			String params = "";
			String indexPath = getIndex_path();
			// 创建索引搜索器 且只读
			if (!searchEngine.checkIndexPathExist(indexPath)) {
				return pList;
			}
			// 在索引器中使用IKSimilarity相似度评估器
			// isearcher.setSimilarity(new IKSimilarity());
			// 处理查询筛选条件
			String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\"]";
			Pattern pat = Pattern.compile(regEx);
			if (!StringUtils.isNullOrEmpty(gb_name)) { // 此为品牌主页请求
				Matcher mat = pat.matcher(gb_name);
				gb_name = mat.replaceAll("").trim();
				params = "(goods_brand:" + gb_name + ")";
			}
			else {
				Matcher mat = pat.matcher(keyword);
				keyword = mat.replaceAll("").trim();
				if (!StringUtils.isNullOrEmpty(keyword) && keyword.indexOf("title:") < 0) {
					params = "(title:" + keyword + " OR content:" + keyword + " OR goods_brand: " + keyword + ")";
				}
				else {
					params = "(title:*)";
				}
			}
			if (goods_inventory != null && "0".equals(goods_inventory)) {
				params = params + " AND goods_inventory:[1 TO " + Integer.MAX_VALUE + "]";
			}
			if (goods_type != null && !"-1".equals(goods_type)) {
				params = params + " AND goods_type:" + goods_type;
			}
			if (!StringUtils.isNullOrEmpty(goods_class)) {
				params = params + " AND goods_class:" + goods_class;
			}
			if (!StringUtils.isNullOrEmpty(goods_transfee)) {
				params = params + " AND goods_transfee:" + goods_transfee;
			}
			if (!StringUtils.isNullOrEmpty(goods_cod)) {
				params = params + " AND goods_cod:" + goods_cod;
			}
			if (!StringUtils.isNullOrEmpty(goods_cat)) {
				params = params + " AND (goods_cat : " + goods_cat + ")";
			}
			if (!StringUtils.isNullOrEmpty(goods_area)) {
				params = params + " AND (goods_area : " + goods_area + ")";
			}
			
			HighlighterParam highlighterParam = null;
			if (StringUtils.isNullOrEmpty(gb_name)) { //非品牌主页请求，对商品名称进行关键字高亮显示
				if (keyword!=null&&keyword.trim().length()>0&&!"(title:*)".equals(keyword) /*&& !"(title:*)".equals(pages)*/) {
					highlighterParam = new HighlighterParam(keyword, LuceneVo.TITLE);
				}
			}
			queryWrapper = WrapperFactory.newParamWrapper(params, LuceneVo.TITLE,true,highlighterParam,sort);
			isearcher = WrapperFactory.newSearchWrapper(indexPath,queryWrapper);
			
			QryResultWrapper topDocs = null;
			
			pageSize = pageSize == 0 ? this.pageSize : pageSize;
			
			
			int start = (currentPage - 1) * pageSize;
			if (currentPage == 0) { // currentPage为零，该请求为搜索请求
				topDocs = isearcher.searchLucene(pageSize);
				pages = (topDocs.getTotalHits() + pageSize - 1) / pageSize; // 记算总页数
				rows = topDocs.getTotalHits(); // 计算总记录数
				currentPage = 1;
				start = 0;
			}
			else if (currentPage != 0) { // currentPage非零，该请求为分页请求
				topDocs = isearcher.searchLucene(start + pageSize);
				pages = (topDocs.getTotalHits() + pageSize - 1) / pageSize; // 记算总页数
				rows = topDocs.getTotalHits(); // 计算总记录数
			}
			int end = (pageSize + start) < topDocs.getTotalHits() ? (pageSize + start) : topDocs.getTotalHits();
			List<LuceneVo> scoreDocs = topDocs.getLuceneVoList();
			for (int i = start; i < end; i++) {
				LuceneVo vo = scoreDocs.get(i);
				vo_list.add(vo);
			}
			pList.setPageSize(pageSize);
			pList.setPages(pages);
			pList.setRows(rows);
			pList.setCurrentPage(currentPage);
			pList.setVo_list(vo_list);

		}
		catch (Exception e) {
			searchEngine.throwException("执行查询发生异常,indexPath:"+getIndex_path()+",查询条件:"+queryWrapper,e);
		}
		finally {
			if(isearcher!=null){
				isearcher.close();
			}
		}
		return pList;
	}

	/**
	 * 此方法为无特定类型的默认排序，
	 * 
	 * @param keyword
	 * @param pageNo
	 * @return
	 */
	public LuceneResult search(String keyword, int currentPage, int pageSize, String goods_inventory, String goods_type, String goods_class, String goods_transfee, String goods_cod, String goods_cat, String goods_area, String gb_name) {
		LuceneResult pList = new LuceneResult();
		if(StringUtils.isNullOrEmpty(keyword)) {
			return pList;
		}
		ISearchWrapper isearcher = null;
		int pages = 0;
		int rows = 0;
		IParamWrapper queryWrapper = null;
		try {
			String params = "";
			// 创建索引搜索器 且只读
			String indexPath = getIndex_path();
			if (!searchEngine.checkIndexPathExist(indexPath)) {
				return pList;
			}
			
			// 在索引器中使用IKSimilarity相似度评估器
			// isearcher.setSimilarity(new IKSimilarity());
			// 处理查询筛选条件
			String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\"]";
			Pattern pat = Pattern.compile(regEx);
			if (!StringUtils.isNullOrEmpty(gb_name)) { // 此为品牌主页请求
				Matcher mat = pat.matcher(gb_name);
				gb_name = mat.replaceAll("").trim();
				params = "(goods_brand:" + gb_name + ")";
			}
			else {
				Matcher mat = pat.matcher(keyword);
				keyword = mat.replaceAll("").trim();
				if (!StringUtils.isNullOrEmpty(keyword) && keyword.indexOf("title:") < 0) {
					params = "(title:" + keyword + " OR content:" + keyword + " OR goods_brand: " + keyword + ")";
				}
				else {
					params = "(title:*)";
				}
			}
			if (goods_inventory != null && "0".equals(goods_inventory)) {
				params = params + " AND goods_inventory:[1 TO " + Integer.MAX_VALUE + "]";
			}
			if (goods_type != null && !"-1".equals(goods_type)) {
				params = params + " AND goods_type:" + goods_type;
			}
			if (!StringUtils.isNullOrEmpty(goods_class)) {
				params = params + " AND goods_class:" + goods_class;
			}
			if (!StringUtils.isNullOrEmpty(goods_transfee)) {
				params = params + " AND goods_transfee:" + goods_transfee;
			}
			if (!StringUtils.isNullOrEmpty(goods_cod)) {
				params = params + " AND goods_cod:" + goods_cod;
			}
			if (!StringUtils.isNullOrEmpty(goods_cat)) {
				params = params + " AND (goods_cat : " + goods_cat + ")";
			}
			if (!StringUtils.isNullOrEmpty(goods_area)) {
				params = params + " AND (goods_area : " + goods_area + ")";
			}
			HighlighterParam highlighterParam = null;
			if (StringUtils.isNullOrEmpty(gb_name)) { //非品牌主页请求，对商品名称进行关键字高亮显示
				if (keyword!=null&&keyword.trim().length()>0&&!"(title:*)".equals(keyword) /*&& !"(title:*)".equals(pages)*/) {
					highlighterParam = new HighlighterParam(keyword, LuceneVo.TITLE);
				}
			}
			SortWrapper sortWrapper = null;
			queryWrapper = WrapperFactory.newParamWrapper(params, LuceneVo.TITLE,true,highlighterParam,sortWrapper);
			isearcher = WrapperFactory.newSearchWrapper(indexPath,queryWrapper);
			
			pageSize = pageSize == 0 ? this.pageSize : pageSize;
			
			QryResultWrapper topDocs = null;
			if (currentPage == 0) { // currentPage为零，该请求为搜索请求
				topDocs = isearcher.searchLucene(pageSize);
				pages = (topDocs.getTotalHits() + pageSize - 1) / pageSize; // 记算总页数
				rows = topDocs.getTotalHits(); // 计算总记录数
				currentPage = 1;
			}
			else if (currentPage != 0) { // currentPage非零，该请求为分页请求
				topDocs = isearcher.searchByPage(currentPage, pageSize);
				pages = (topDocs.getTotalHits() + pageSize - 1) / pageSize; // 记算总页数
				rows = topDocs.getTotalHits(); // 计算总记录数
			}
			
//			List<LuceneVo> vo_list = new ArrayList<LuceneVo>();
//			for (ScoreDoc sd : topDocs.getScoreDocs()) {
//				LuceneVo vo = isearcher.searchDocById(sd.doc);
//				vo_list.add(vo);
//			}
			List<LuceneVo> vo_list = topDocs.getLuceneVoList();
			
			pList.setPageSize(pageSize);;
			pList.setPages(pages);
			pList.setRows(rows);
			pList.setCurrentPage(currentPage);
			pList.setVo_list(vo_list);
		}
		catch (Exception e) {
			searchEngine.throwException("执行查询发生异常,indexPath:"+getIndex_path()+",查询条件:"+queryWrapper,e);
		}
		finally {
			if(isearcher!=null){
				isearcher.close();
			}
		}
		return pList;
	}

	/**
	 * 对关键字命中的商品进行分类提取
	 * 
	 * @param keyword
	 * @param rows
	 * @return
	 */
	public Set<String> LoadData_goods_class(String keyword) {
		ISearchWrapper isearcher = null;
		Set<String> list = new ArraySet<String>();
		try {
			// begin dengyuqi 2015-9-11 解决当lucene索引库不存在时，首页搜索报错的bug
			String indexPath = getIndex_path();
			if (!searchEngine.checkIndexPathExist(indexPath)) {
				return list;
			}
			// end
			
			if (!StringUtils.isNullOrEmpty(keyword) && keyword.indexOf("title:") < 0) {
				keyword = "(title:" + keyword + " OR content:" + keyword + " OR goods_brand: " + keyword + ")";
			}
			
			HighlighterParam highlighterParam = null;
			SortWrapper sortWrapper = null;
			IParamWrapper queryWrapper = WrapperFactory.newParamWrapper(keyword, LuceneVo.TITLE,true,highlighterParam,sortWrapper);
			isearcher = WrapperFactory.newSearchWrapper(indexPath,queryWrapper);
			
			QryResultWrapper topDocs = isearcher.searchLucene(LuceneVo.GOODS_CLASS,getGc_size());
			
			List<LuceneVo> luceneVoList = topDocs.getLuceneVoList();
			for (int i = 0; i < luceneVoList.size(); i++) {
				LuceneVo vo = luceneVoList.get(i);
				String gc = vo.getVo_goods_class();
				if(gc!=null&&gc.trim().length()>0){
					list.add(gc);
				}
			}
		}
		catch (Exception e) {
			searchEngine.throwException("对关键字命中的商品进行分类提取异常：", e);
		}
		finally {
			if(isearcher!=null){
				isearcher.close();
			}
		}
		return list;
	}

	/**
	 * 添加列表到索引库中
	 * 
	 * @erikzhang
	 * @param list
	 * @throws IOException
	 */
	public void writeIndex(List<LuceneVo> list) {
		logger.info("####批量添加索引:" + list==null?0:list.size() + "个####");
		if(list==null ||list.size()==0){return ;}
		IWriterWrapper writer = null;
		try {
			writer = WrapperFactory.newWriterWrapper(getIndex_path());
			writer.addDocument(list);
			writer.commit();
		}
		catch (Exception e) {
			String errInfo = "";
			try {
				errInfo = "size is" + list.size()+",dataList is"+JSON.toJSONString(list);
			} catch (Exception exp) {}
			writer.rollBack("批量添加索引:"+errInfo);
			searchEngine.throwException("批量添加索引异常："+errInfo, e);
		}
		finally {
			if(writer!=null){
				writer.iSNotUnlock();
			}
		}
	}

	/**
	 * 添加单个到索引库中
	 * 
	 * @erikzhang
	 * @param LuceneVo
	 * @throws IOException
	 */
	public void writeIndex(final LuceneVo vo) {
		logger.info("####添加索引:" + vo.getVo_id() + "####");
		if (sysConfig != null && sysConfig.getLucenen_queue() == 1) {
			LuceneThreadPool pool = LuceneThreadPool.instance();
			pool.addThread(new Runnable() {
				public void run() {
					IWriterWrapper writer = null;
					try {
						writer = WrapperFactory.newWriterWrapper(getIndex_path());
						writer.addDocument(vo);
						writer.commit();
					}
					catch (Exception e) {
						String errInfo = "";
						try {errInfo = "索引数据:"+JSON.toJSONString(vo);} catch (Exception exp) {}
						writer.rollBack("添加索引Runnable:"+errInfo);
						searchEngine.throwException("添加单个到索引库中异常Runnable："+errInfo, e);
					}
					finally {
						if(writer!=null){
							writer.iSNotUnlock();
						}
					}
				}
			});
		}
		else {
			IWriterWrapper writer = null;
			try {
				writer = WrapperFactory.newWriterWrapper(getIndex_path());
				writer.addDocument(vo);
				writer.commit();
			}
			catch (Exception e) {
				String errInfo = "";
				try {errInfo = "索引数据:"+JSON.toJSONString(vo);} catch (Exception exp) {}
				writer.rollBack("添加索引:"+errInfo);
				searchEngine.throwException("添加单个到索引库中异常："+errInfo, e);
			} finally {
				if(writer!=null){
					writer.iSNotUnlock();
				}
			}
		}

	}

	/**
	 * 更新索引
	 * 
	 * @throws Exception
	 */
	public void update(String id, LuceneVo vo) {

		logger.info("####更新索引:id=" + id + "####");
		IWriterWrapper writer = null;
		try {
			writer = WrapperFactory.newWriterWrapper(getIndex_path());
			writer.updateDocument(String.valueOf(id), vo, "id");
			writer.commit();
		}
		catch (Exception e) {
			writer.rollBack("更新索引:id=" + id);
			searchEngine.throwException("更新索引异常：", e);
		}
		finally {
			if(writer!=null){
				writer.iSNotUnlock();
			}
		}
	}

	/**
	 * 删除索引文件
	 * 
	 * @param id
	 */
	public void delete_index(String id) {
		logger.info("####删除索引:id=" + id + "####");
		IWriterWrapper writer = null;
		try {
			writer = WrapperFactory.newWriterWrapper(getIndex_path());
			writer.deleteDocuments(String.valueOf(id), "id");
			writer.commit();
		}
		catch (Exception e) {
			writer.rollBack("删除索引:id=" + id);
			searchEngine.throwException("删除索引文件异常：", e);
		}
		finally {
			if(writer!=null){
				writer.iSNotUnlock();
			}
		}
	}

	/**
	 * 删除所有索引文件
	 * 
	 * @erikzhang
	 */
//	private void deleteAllFile() {
//		logger.info("####删除所有索引文件####");
//		File index_file = new File(getIndex_path());
//		File[] files = index_file.listFiles();
//		for (int i = 0; i < files.length; i++) {
//			files[i].delete();
//		}
//	}

}
