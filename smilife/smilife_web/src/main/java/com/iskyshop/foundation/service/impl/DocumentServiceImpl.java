package com.iskyshop.foundation.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.foundation.domain.Document;
import com.iskyshop.foundation.service.IDocumentService;

@Service
@Transactional
public class DocumentServiceImpl implements IDocumentService {
	@Resource(name = "documentDAO")
	private IGenericDAO<Document> documentDao;
	@Transactional(readOnly = false)
	public boolean save(Document document) {
		/**
		 * init other field here
		 */
		try {
			this.documentDao.save(document);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public Document getObjById(Long id) {
		Document document = this.documentDao.get(id);
		if (document != null) {
			return document;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.documentDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> documentIds) {
		// TODO Auto-generated method stub
		for (Serializable id : documentIds) {
			delete((Long) id);
		}
		return true;
	}
	@Transactional(readOnly = true)
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Document.class, construct,
				query, params, this.documentDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(
						pageObj.getCurrentPage() == null ? 0 : pageObj
								.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj
								.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	@Transactional(readOnly = false)
	public boolean update(Document document) {
		try {
			this.documentDao.update(document);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public List<Document> query(String query, Map params, int begin, int max) {
		return this.documentDao.query(query, params, begin, max);

	}

	@Override
	@Transactional(readOnly = true)
	public Document getObjByProperty(String construct, String propertyName,
			Object value) {
		// TODO Auto-generated method stub
		return this.documentDao.getBy(construct, propertyName, value);
	}
}
