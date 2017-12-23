package com.iskyshop.foundation.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.FTPServer;
import com.iskyshop.foundation.service.IFTPServerService;

@Service
@Transactional
public class FTPServerServiceImpl implements IFTPServerService {
	@Resource(name = "fTPServerDAO")
	private IGenericDAO<FTPServer> fTPServerDao;

	@Transactional(readOnly = false)
	public boolean save(FTPServer fTPServer) {
		/**
		 * init other field here
		 */
		try {
			this.fTPServerDao.save(fTPServer);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = true)
	public FTPServer getObjById(Long id) {
		FTPServer fTPServer = this.fTPServerDao.get(id);
		if (fTPServer != null) {
			return fTPServer;
		}
		return null;
	}

	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.fTPServerDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> fTPServerIds) {
		// TODO Auto-generated method stub
		for (Serializable id : fTPServerIds) {
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
		GenericPageList pList = new GenericPageList(FTPServer.class, construct,
				query, params, this.fTPServerDao);
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
	public boolean update(FTPServer fTPServer) {
		try {
			this.fTPServerDao.update(fTPServer);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = true)
	public List<FTPServer> query(String query, Map params, int begin, int max) {
		return this.fTPServerDao.query(query, params, begin, max);

	}
}
