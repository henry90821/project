package com.iskyshop.lsolr.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;

import com.iskyshop.lsolr.exception.SolrRuntimeException;

/**
 * 基于solrJ操作的工具类
 * TODO 并发处理时,连接数的管理
 * 连接的关闭
 * 连接超时重试的问题
 * response status 的判断
 * 
 * 查询穿透的处理?
 * @author shiyl
 *
 */
public class SolrUtil {
	private static Logger logger = Logger.getLogger(SolrUtil.class);
	
	/**
	 * 查询数据
	 * @param server
	 * @param data
	 */
	public static QueryResponse query(SolrServer server, SolrParams params) {
		QueryResponse res = null;
		try {
			res = server.query(params);
		} catch (SolrServerException e) {
			throw new SolrRuntimeException("query-SolrServerException", e);
		}
		return res;
	}
	
	/**
	 * 新增单个数据
	 * @param server
	 * @param data
	 */
	public static UpdateResponse add(SolrServer server, SolrInputDocument data) {
		UpdateResponse res = null;
		try {
			res = server.add(data);
		} catch (SolrServerException e) {
			throw new SolrRuntimeException("add-signal-SolrServerException", e);
		} catch (IOException e) {
			throw new SolrRuntimeException("add-signal-IOException", e);
		}
		return res;
	}
	
	/**
	 * 新增列表数据
	 * @param server
	 * @param data
	 */
	public static UpdateResponse add(SolrServer server, Collection<SolrInputDocument> dataList) {
		UpdateResponse res = null;
		try {
			res = server.add(dataList);
		} catch (SolrServerException e) {
			throw new SolrRuntimeException("add-list-SolrServerException", e);
		} catch (IOException e) {
			throw new SolrRuntimeException("add-list-IOException", e);
		}
		return res;
	}
	
	/**
	 * 更新单个数据
	 * @param server
	 * @param data
	 */
	public static UpdateResponse update(SolrServer server, SolrInputDocument data) {
		return add(server, data);
	}
	
	/**
	 * 删除单个数据
	 * @param server
	 * @param data
	 */
	public static UpdateResponse delete(SolrServer server, String id) {
		UpdateResponse res = null;
		try {
			res = server.deleteById(id);
		} catch (SolrServerException e) {
			throw new SolrRuntimeException("delete-deleteById-SolrServerException", e);
		} catch (IOException e) {
			throw new SolrRuntimeException("delete-deleteById-IOException", e);
		}
		return res;
	}

//	/**
//	 * 删除所有文档，为安全起见，使用时再解注函数体 。
//	 */
//	private static UpdateResponse deleteAll(SolrServer server) {
//		UpdateResponse res = null;
//		try {
//			res = server.deleteByQuery("*:*");
//		} catch (SolrServerException e) {
//			throw new SolrRuntimeException("deleteAll-SolrServerException", e);
//		} catch (IOException e) {
//			throw new SolrRuntimeException("deleteAll-IOException", e);
//		}
//		return res;
//	}
	
	/**
	 * 提交更改
	 * @param server
	 */
	public static void commit(SolrServer server){
		try {
			server.commit();
		} catch (SolrServerException e) {
			throw new SolrRuntimeException("commit-SolrServerException",e);
		} catch (IOException e) {
			throw new SolrRuntimeException("commit-IOException",e);
		}
	}
	
	/**
	 * 更新并提交
	 * @param server
	 * @param data
	 */
	public static void updateAndCommit(SolrServer server, SolrInputDocument data){
		update(server, data);
		commit(server);
	}
	
	/**
	 * 新增并提交
	 * @param server
	 * @param data
	 */
	public static void addAndCommit(SolrServer server, SolrInputDocument data){
		add(server, data);
		commit(server);
	}
	
	/**
	 * 新增并提交
	 * @param server
	 * @param data
	 */
	public static void addAndCommit(SolrServer server, Collection<SolrInputDocument> data){
		add(server, data);
		commit(server);
	}
	
	/**
	 * 删除并提交
	 * @param server
	 * @param data
	 */
	public static void deleteAndCommit(SolrServer server, String id){
		delete(server, id);
		commit(server);
	}
	
}
