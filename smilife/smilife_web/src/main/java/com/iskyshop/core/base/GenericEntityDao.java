package com.iskyshop.core.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.iskyshop.core.exception.CanotRemoveObjectException;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;

/**
 * 
 * 数据库操作基础DAO，系统使用JPA完成所有数据库操作，默认JPA的实现为Hibernate
 */
@Repository
@Scope
@Lazy(value=false)
public class GenericEntityDao {
	@PersistenceContext
	private EntityManager em;

	public GenericEntityDao() {
	}

	public Object get(Class clazz, Serializable id) {
		if (id == null)
			return null;
		return em.find(clazz, id);
	}

	public List find(Class clazz, String construct,
			String queryStr, Map params, int begin,int max) {
		StringBuffer jpql = null;
		if (construct != null && !"".equals(construct)) {
			jpql = new StringBuffer("select " + construct + " from ");
		} else {
			jpql = new StringBuffer("select obj from ");
		}
		jpql.append(clazz.getName()).append(" obj").append(" where ").append(queryStr);
		Query query = em.createQuery(jpql.toString());
		if (params != null && params.size() > 0) {
			for (Object key : params.keySet()) {
				query.setParameter(key.toString(), params.get(key));
			}
		}
		if (begin >= 0 && max > 0) {
			query.setFirstResult(begin);
			query.setMaxResults(max);
		}
		query.setHint("org.hibernate.cacheable", true);
//		Session session = (Session) em.getDelegate();
//		Statistics stat = session.getSessionFactory().getStatistics();
		List ret = query.getResultList();
		if (ret != null && ret.size() >= 0) {
			return ret;
		} else {
			return new ArrayList();
		}
	}

	public List query(String jpql, Map params, int begin,int max) {
		Query query = em.createQuery(jpql);
		if (params != null && params.size() > 0) {
			for (Object key : params.keySet()) {
				query.setParameter(key.toString(), params.get(key));
			}
		}
		if (begin >= 0 && max > 0) {
			query.setFirstResult(begin);
			query.setMaxResults(max);
		}
		query.setHint("org.hibernate.cacheable", true);
		List list = query.getResultList();
		if (list != null && list.size() > 0) {
			return list;
		} else
			return new ArrayList();
	}

	public void remove(Class clazz, Serializable id)
			throws CanotRemoveObjectException {
		Object object = this.get(clazz, id);
		if (object != null) {
			try {
				em.remove(object);
			} catch (Exception e) {
				throw new CanotRemoveObjectException();
			}
		}
	}

	public void save(Object instance) {
		em.persist(instance);
	}

	public Object getBy(Class clazz, String construct,
			String propertyName, Object value) {
		StringBuffer jpql = null;
		if (construct != null && !"".equals(construct)) {
			jpql = new StringBuffer("select " + construct + " from ");
		} else {
			jpql = new StringBuffer("select obj from ");
		}
		jpql.append(clazz.getName()).append(" obj");
		Query query = null;
		if (!StringUtils.isNullOrEmpty(propertyName) && value != null) {
			jpql.append(" where obj.").append(propertyName).append(" = :value");
			query = em.createQuery(jpql.toString()).setParameter("value", value);
		} else {
			throw new IllegalArgumentException("propertyName and value should not be null. clazz:" + clazz.getName() + ", construct:" + construct + ", propertyName:" + propertyName + ", value:" + value);
		} 
		query.setHint("org.hibernate.cacheable", true);
		List ret = query.getResultList();
		if (ret != null && ret.size() == 1) {
			return ret.get(0);
		} else if (ret != null && ret.size() > 1) {
			throw new IllegalStateException("warning  --more than one object find!!");
		} else {
			return null;
		}
	}

	public List executeNamedQuery(String queryName,
			Object[] params, int begin, int max) {
		Query query = em.createNamedQuery(queryName);
		int parameterIndex = 1;
		if (params != null && params.length > 0) {
			for (Object obj : params) {
				query.setParameter(parameterIndex++, obj);
			}
		}
		if (begin >= 0 && max > 0) {
			query.setFirstResult(begin);
			query.setMaxResults(max);
		}
		query.setHint("org.hibernate.cacheable", true);
		List ret = query.getResultList();
		if (ret != null && ret.size() >= 0) {
			return ret;
		} else {
			return new ArrayList();
		}
	}

	public void update(Object instance) {
		em.merge(instance);
	}

	public List executeNativeNamedQuery(String nativeNamedQuery) {
		Query query = em.createNativeQuery(nativeNamedQuery);
		List ret = query.getResultList();
		return ret;
	}

	public List executeNativeQuery(String sql, Map params,
			int begin, int max) {
		Query query = em.createNativeQuery(sql);
		if (params != null) {
			Iterator its = params.keySet().iterator();
			while (its.hasNext()) {
				query.setParameter(CommUtil.null2String(its.next()),
						params.get(its.next()));
			}
		}
		if (begin >= 0 && max > 0) {
			query.setFirstResult(begin);
			query.setMaxResults(max);
		}
		List ret = query.getResultList();
		if (ret != null && ret.size() >= 0) {
			return ret;
		} else {
			return new ArrayList();
		}
	}

	public List executeNativeQuery(String sql, Object[] params,
			int begin, int max) {
		Query query = em.createNativeQuery(sql);
		int parameterIndex = 1;
		if (params != null && params.length > 0) {
			for (Object obj : params) {
				query.setParameter(parameterIndex++, obj);
			}
		}
		if (begin >= 0 && max > 0) {
			query.setFirstResult(begin);
			query.setMaxResults(max);
		}
		List ret = query.getResultList();
		if (ret != null && ret.size() >= 0) {
			return ret;
		} else {
			return new ArrayList();
		}
	}

	public int executeNativeSQL(String sql) {
		Query query = em.createNativeQuery(sql);
		query.setHint("org.hibernate.cacheable", true);
		int ret = query.executeUpdate();
		return ret;
	}

	public int batchUpdate(String jpql, Object[] params) {
		Query query = em.createQuery(jpql);
		int parameterIndex = 1;
		if (params != null && params.length > 0) {
			for (Object obj : params) {
				query.setParameter(parameterIndex++, obj);
			}
		}
		query.setHint("org.hibernate.cacheable", true);
		int ret = query.executeUpdate();
		return ret;
	}
	
	public int batchUpdate(String jpql, Map<String,Object> params) {
		Query query = em.createQuery(jpql);
		if (params != null && params.size() > 0) {
			for (Map.Entry<String, Object> e : params.entrySet()) {
				query.setParameter(e.getKey(), e.getValue());
			}
		}
		query.setHint("org.hibernate.cacheable", true);
		int ret = query.executeUpdate();
		return ret;
	}

	public void flush() {
		em.getTransaction().commit();
	}

	
	/**
	 * 批量更新对象。默认每次更新200个对象
	 * @param list
	 */
	public void batchUpdate(List list) {
		for (int i = 0; i < list.size(); i++) {
			em.merge(list.get(i));
			if (i % 200 == 0) {
				em.flush();
				em.clear();
			}
		}
		em.flush();
		em.clear();
	}
	
	
	/**
	 * 批量更新对象
	 * @param list  待更新的对象列表
	 * @param num 批量更新时会分多次提交对象到数据库，此参数num表示每次提交到数据库的对象个数
	 */
	public void batchUpdate(List list, int num) {
		for (int i = 0; i < list.size(); i++) {
			em.merge(list.get(i));
			if (i % num == 0) {
				em.flush();
				em.clear();
			}
		}
		em.flush();
		em.clear();
	}
	

	/**
	 * 批量插入对象。默认每次插入200个对象
	 * @param list
	 */
	public void batchInsert(List list) {
		this.batchInsert(list, 200);
	}
	
	
	/**
	 * 批量插入对象
	 * @param list 待插入的对象列表
	 * @param num  批量插入时会分多次进行flush，此参数num表示每次插入的对象个数
	 */
	public void batchInsert(List list, int num) {
		for (int i = 0; i < list.size(); i++) {
			em.persist(list.get(i));
			if (i % num == 0) {
				em.flush();
				em.clear();
			}
		}
		em.flush();
		em.clear();
	}
}
