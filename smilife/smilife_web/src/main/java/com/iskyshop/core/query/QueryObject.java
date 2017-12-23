package com.iskyshop.core.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;

/**
 * 
 * 基础查询对象，封装基础查询条件，包括页大小、当前页、排序信息等
 */
public class QueryObject implements IQueryObject {

	private String construct;// 查询构造器，为空时查询obj所有字段。若不需要查询所有字段，则此字段中要加上new，如"new Goods(id)"
	protected Integer pageSize = 12;// 默认分页数据，表示每页12条记录
	protected Integer currentPage = 1;// 当前页，默认为1。（注意：经查阅代码及查看前端最初始的翻页功能，发现，currentPage是从1开始的，而非0开始）
	protected String orderBy;// 排序字段，默认为addTime
	protected String orderType;// 排序类型，默认为倒叙
	protected Map params = new HashMap();
	protected String queryString = "1=1";

	public String getConstruct() {
		return construct;
	}

	public void setConstruct(String construct) {
		this.construct = construct;
	}

	public void setCurrentPage(Integer currentPage) {
		if(currentPage != null && currentPage > 0) {
			this.currentPage = currentPage;
		}
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public void setPageSize(Integer pageSize) {
		if(pageSize != null && pageSize > 0) {
			this.pageSize = pageSize;
		}
	}

	protected void setParams(Map params) {
		this.params = params;
	}

	public String getOrderType() {
		return orderType;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public String getOrder() {
		return orderType;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public QueryObject() {

	}

	/**
	 * 构造一个queryObject
	 * 
	 * @param construct
	 *            查询对象构造函数，如new Goods(id,goodsName)
	 * @param currentPage
	 *            当前页
	 * @param mv
	 *            需要封装的视图，可为null
	 * @param orderBy
	 *            排序字段
	 * @param orderType
	 *            排序类型
	 */
	public QueryObject(String construct, String currentPage, ModelAndView mv, String orderBy, String orderType) {
		if (!StringUtils.isNullOrEmpty(construct)) {
			this.setConstruct(construct);
		}
		if (currentPage != null) {
			this.setCurrentPage(CommUtil.null2Int(currentPage));
		}
		if (StringUtils.isNullOrEmpty(orderBy)) {
			this.setOrderBy("addTime");
			if(mv != null) {
				mv.addObject("orderBy", "addTime");
			}
		} else {
			this.setOrderBy(orderBy);
			if(mv != null) {
				mv.addObject("orderBy", orderBy);
			}
		}
		if (StringUtils.isNullOrEmpty(orderType)) {
			this.setOrderType("desc");
			if(mv != null) {
				mv.addObject("orderType", "desc");
			}
		} else {
			this.setOrderType(orderType);
			if(mv != null) {
				mv.addObject("orderType", orderType);
			}
		}
	}

	/**
	 * 构造一个查询对象queryObject
	 * 
	 * @param currentPage
	 *            当前页
	 * @param mv
	 *            返回的视图，可为null
	 * @param orderBy
	 *            排序字段
	 * @param orderType
	 *            排序类型
	 */
	public QueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType) {
		if (currentPage != null) {
			this.setCurrentPage(CommUtil.null2Int(currentPage));
		}
		if (StringUtils.isNullOrEmpty(orderBy)) {
			this.setOrderBy("addTime");
			if(mv != null) {
				mv.addObject("orderBy", "addTime");
			}
		} else {
			this.setOrderBy(orderBy);
			if(mv != null) {
				mv.addObject("orderBy", orderBy);
			}
		}
		if (StringUtils.isNullOrEmpty(orderType)) {
			this.setOrderType("desc");
			if(mv != null) {
				mv.addObject("orderType", "desc");
			}
		} else {
			this.setOrderType(orderType);
			if(mv != null) {
				mv.addObject("orderType", orderType);
			}
		}
	}

	/**
	 * 获取一个分页信息
	 * 
	 * @return 分页信息
	 */
	public PageObject getPageObj() {
		PageObject pageObj = new PageObject();
		pageObj.setCurrentPage(this.currentPage);
		pageObj.setPageSize(this.pageSize);
		return pageObj;
	}

	public String getQuery() {
		return queryString + orderString();
	}

	protected String orderString() {
		String orderString = " ";
		if (!StringUtils.isNullOrEmpty(getOrderBy())) {
			orderString += " order by obj." + this.getOrderBy();
		}
		if (!StringUtils.isNullOrEmpty(this.getOrderType())) {
			orderString = orderString + " " + getOrderType();
		}
		return orderString;
	}

	public Map getParameters() {
		return this.params;
	}

	@Override
	public IQueryObject addQuery(String field, SysMap para, String expression) {
		if (field != null && para != null) {
			queryString += " and " + field + " " + handleExpression(expression) + " :" + para.getKey().toString();
			params.put(para.getKey(), para.getValue());
		}
		return this;
	}

	public IQueryObject addQuery(String field, SysMap para, String expression, String logic) {
		if (field != null && para != null) {
			queryString += " " + logic + " " + field + " " + handleExpression(expression) + " :" + para.getKey().toString();
			params.put(para.getKey(), para.getValue());
		}
		return this;
	}

	@Override
	public IQueryObject addQuery(String scope, Map paras) {
		if (scope != null) {
			if (scope.trim().indexOf("and") == 0 || scope.trim().indexOf("or") == 0) {
				queryString += " " + scope;
			} else {
				queryString += " and " + scope;
			}
			if (!StringUtils.isNullOrEmpty(paras)) {
				for (Object key : paras.keySet()) {
					params.put(key, paras.get(key));
				}
			}
		}
		return this;
	}

	@Override
	public IQueryObject addQuery(String para, Object obj, String field, String expression) {
		if (field != null && para != null) {
			queryString += " and :" + para + " " + expression + " " + field;
			params.put(para, obj);
		}
		return this;
	}
	
	public IQueryObject addQuery(String para, Object obj, String field, String expression, String logic) {
		if (field != null && para != null) {
			queryString += " " + logic + " :" + para + " " + expression + " " + field;
			params.put(para, obj);
		}
		return this;
	}

	@Override
	public IQueryObject clearQuery() {
		queryString = "1=1 ";
		params.clear();
		return this;
	}

	/**
	 * 处理表达式 ，如果expression为null，则返回=，否则返回本身
	 * 
	 * @param expression
	 * @return 处理后的字符串
	 */
	private String handleExpression(String expression) {
		if (expression == null) {
			return "=";
		} else {
			return expression;
		}
	}

}
