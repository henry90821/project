package com.iskyshop.lsolr.entity;


public interface ISearchWrapper {
	/**获取查询参数*/
	IParamWrapper getQueryWrapper();
	
	/**关闭连接,释放资源*/
	void close();
	
	/**
	 * 查询数据列表
	 * @param filterFieldName 过滤器字段名
	 * @param n 数量
	 * @return
	 * @throws Exception
	 */
	QryResultWrapper searchLucene(String filterFieldName, int n) throws Exception;

	QryResultWrapper searchLucene(int n) throws Exception;

//	QryResultWrapper searchLucene(int n, SortWrapper sort) throws Exception;
//	QryResultWrapper searchLucene(String filterFieldName, int n, SortWrapper sort) throws Exception;
	
	/**
	 * 分页查询
	 * @param queryWrapper 查询条件
	 * @param currentPage 当前页
	 * @param pageSize 每页数量
	 * @return
	 * @throws Exception
	 */
	QryResultWrapper searchByPage(int currentPage, int pageSize) throws Exception;

}
