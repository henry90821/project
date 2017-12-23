package com.iskyshop.lsolr.util;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import com.iskyshop.core.tools.CommUtil;
import com.tydic.framework.util.PropertyUtil;

public class SolrServerFactory {
	private static Logger logger = Logger.getLogger(SolrServerFactory.class);
	
	public static final String	GOODS	= CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator + "goods";
	public static final String	GROUP	= CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator + "groupgoods";
	public static final String	LIFE	= CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator + "lifegoods";
	
	
	/** 商品服务器地址 */
	public static final String solr_baseUrl_goods = "solr.baseUrl.goods";
	/** groupgoods服务器地址 */
	public static final String solr_baseUrl_groupgoods = "solr.baseUrl.groupgoods";
	/** lifegoods服务器地址 */
	public static final String solr_baseUrl_lifegoods = "solr.baseUrl.lifegoods";
	/** 连接超时时间,毫秒 */
	public static final String solr_connectionTimeout = "solr.connectionTimeout";
	/** 默认每个host最大连接数 */
	public static final String solr_defaultMaxConnectionsPerHost = "solr.defaultMaxConnectionsPerHost";
	/** 最大连接数 */
	public static final String solr_maxTotalConnections = "solr.maxTotalConnections";
	/** 最大重试次数,默认0 */
	public static final String solr_maxRetries = "solr.maxRetries";

	private static HttpSolrServer goodsServer;
	private static HttpSolrServer groupgoodsServer;
	private static HttpSolrServer lifegoodsServer;

	private static HttpSolrServer newInstance(String baseUrl, Integer connectionTimeout,
			Integer defaultMaxConnectionsPerHost, Integer maxRetries, Integer maxTotalConnections) {
		logger.info("SolrServerFactory newInstance HttpSolrServer start..");
		logger.info("baseUrl is:"+baseUrl+",connectionTimeout is:"+connectionTimeout+",defaultMaxConnectionsPerHost is:"+defaultMaxConnectionsPerHost
				+",maxRetries is:"+maxRetries+",maxTotalConnections is:"+maxTotalConnections);
		HttpSolrServer server = null;
		try {
			server = new HttpSolrServer(baseUrl);
			server.setConnectionTimeout(connectionTimeout);
			server.setDefaultMaxConnectionsPerHost(defaultMaxConnectionsPerHost);
			server.setMaxRetries(maxRetries);
			server.setMaxTotalConnections(maxTotalConnections);
			logger.info("SolrServerFactory newInstance HttpSolrServer succ..");
		} catch (Exception e) {
			logger.info("SolrServerFactory newInstance HttpSolrServer failed..",e);
		}finally {
			logger.info("SolrServerFactory newInstance HttpSolrServer end..");
		}
		
		return server;
	}

	private static Integer pInt(String key) {
		return Integer.valueOf(PropertyUtil.getProperty(key));
	}

	private static String p(String key) {
		return PropertyUtil.getProperty(key);
	}
	
	
	public static Logger getLogger() {
		return logger;
	}

	public static HttpSolrServer getGoodsServer() {
		if(goodsServer==null){
			synchronized (SolrServerFactory.class) {
				if(goodsServer==null){
					goodsServer = newInstance(p(solr_baseUrl_lifegoods), pInt(solr_connectionTimeout),
							pInt(solr_defaultMaxConnectionsPerHost), pInt(solr_maxRetries), pInt(solr_maxTotalConnections));
				}
			}
		}
		return goodsServer;
	}

	public static HttpSolrServer getGroupgoodsServer() {
		if(groupgoodsServer==null){
			synchronized (SolrServerFactory.class) {
				if(groupgoodsServer==null){
					groupgoodsServer = newInstance(p(solr_baseUrl_groupgoods), pInt(solr_connectionTimeout),
							pInt(solr_defaultMaxConnectionsPerHost), pInt(solr_maxRetries), pInt(solr_maxTotalConnections));
				}
			}
		}
		return groupgoodsServer;
	}

	public static HttpSolrServer getLifegoodsServer() {
		if(lifegoodsServer==null){
			synchronized (SolrServerFactory.class) {
				if(lifegoodsServer==null){
					lifegoodsServer = newInstance(p(solr_baseUrl_lifegoods), pInt(solr_connectionTimeout),
							pInt(solr_defaultMaxConnectionsPerHost), pInt(solr_maxRetries), pInt(solr_maxTotalConnections));
				}
			}
		}
		return lifegoodsServer;
	}


//	static {
//		goodsServer = newInstance(p(solr_baseUrl_lifegoods), pInt(solr_connectionTimeout),
//				pInt(solr_defaultMaxConnectionsPerHost), pInt(solr_maxRetries), pInt(solr_maxTotalConnections));
//		groupgoodsServer = newInstance(p(solr_baseUrl_groupgoods), pInt(solr_connectionTimeout),
//				pInt(solr_defaultMaxConnectionsPerHost), pInt(solr_maxRetries), pInt(solr_maxTotalConnections));
//		lifegoodsServer = newInstance(p(solr_baseUrl_lifegoods), pInt(solr_connectionTimeout),
//				pInt(solr_defaultMaxConnectionsPerHost), pInt(solr_maxRetries), pInt(solr_maxTotalConnections));
//	}

//	public static HttpSolrServer getGoodsServer() {
//		return goodsServer;
//	}
//
//	public static HttpSolrServer getGroupgoodsServer() {
//		return groupgoodsServer;
//	}
//
//	public static HttpSolrServer getLifegoodsServer() {
//		return lifegoodsServer;
//	}
	
	public static HttpSolrServer getServer(String path){
		if (path.equals(GOODS)) {
			return getGoodsServer();
		}
		else if (path.equals(GROUP)) {
			return getGroupgoodsServer();
		}
		else if (path.equals(LIFE)) {
			return getLifegoodsServer();
		}
		else {
			return getGoodsServer();
		}
	}
	
}
