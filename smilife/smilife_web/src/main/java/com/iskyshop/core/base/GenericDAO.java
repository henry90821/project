package com.iskyshop.core.base;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.iskyshop.core.dao.IGenericDAO;

/**
 * 统所有数据库操作的基类，采用JDK5泛型接口完成POJO类型注入
 */
public abstract class GenericDAO<T> implements IGenericDAO<T> {
	protected Class<T> entityClass; // DAO所管理的Entity类型

	@Resource(name="genericEntityDao")
	private GenericEntityDao geDao;

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public GenericEntityDao getGeDao() {
		return geDao;
	}

	public void setGeDao(GenericEntityDao geDao) {
		this.geDao = geDao;
	}

	public GenericDAO() {
		this.entityClass = (Class<T>) ((ParameterizedType) (this.getClass().getGenericSuperclass()))
				.getActualTypeArguments()[0];
	}

	public GenericDAO(Class<T> type) {
		this.entityClass = type;
	}

	@Override
	public int batchUpdate(String jpql, Object[] params) {
		return this.geDao.batchUpdate(jpql, params);
	}
	
	@Override
	public int batchUpdate(String jpql, Map params) {
		return this.geDao.batchUpdate(jpql, params);
	}

	@Override
	public List executeNamedQuery(String queryName, Object[] params, int begin, int max) {
		return this.geDao.executeNamedQuery(queryName, params, begin, max);
	}

	@Override
	public List executeNativeNamedQuery(String nativeNamedQuery) {
		return this.geDao.executeNativeNamedQuery(nativeNamedQuery);
	}

	@Override
	public List executeNativeQuery(String sql, Object[] params, int begin, int max) {
		return this.geDao.executeNativeQuery(sql, params, begin, max);
	}

	@Override
	public int executeNativeSQL(String sql) {
		return this.geDao.executeNativeSQL(sql);
	}

	@Override
	public List find(String construct, String query, Map params, int begin, int max) {
		return this.getGeDao().find(this.entityClass, construct, query, params, begin, max);
	}

	@Override
	public void flush() {
		this.geDao.flush();
	}

	@Override
	public T get(Serializable id) {
		return (T)this.getGeDao().get(this.entityClass, id);
	}

	@Override
	public T getBy(String construct, String propertyName, Object value) {
		return (T) this.getGeDao().getBy(this.entityClass, construct, propertyName, value);
	}

	@Override
	public List query(String jpql, Map params, int begin, int max) {
		return this.getGeDao().query(jpql, params, begin, max);
	}

	@Override
	public void remove(Serializable id) {
		this.getGeDao().remove(this.entityClass, id);
	}
	
	@Override
	public void save(Object newInstance) {
		this.getGeDao().save(newInstance);
	}

	@Override
	public void update(Object transientObject) {
		this.getGeDao().update(transientObject);
	}

	@Override
	public void batchUpdate(List<T> list) {
		this.getGeDao().batchUpdate(list);
	}

	@Override
	public void batchInsert(List<T> list) {
		this.getGeDao().batchInsert(list);
	}

	@Override
	public void batchInsert(List<T> list, int num) {
		this.getGeDao().batchInsert(list,num);
	}

	@Override
	public int executeSql(String sql){
		return this.getGeDao().executeNativeSQL(sql);
	}
}
