package com.iskyshop.module.chatting.service.impl;
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
import com.iskyshop.module.chatting.domain.ChattingLog;
import com.iskyshop.module.chatting.service.IChattingLogService;

@Service
@Transactional
public class ChattingLogServiceImpl implements IChattingLogService{
	@Resource(name = "chattingLogDAO")
	private IGenericDAO<ChattingLog> chattingLogDao;
	@Transactional(readOnly = false)
	public boolean save(ChattingLog chattingLog) {
		/**
		 * init other field here
		 */
		try {
			this.chattingLogDao.save(chattingLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public ChattingLog getObjById(Long id) {
		ChattingLog chattingLog = this.chattingLogDao.get(id);
		if (chattingLog != null) {
			return chattingLog;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.chattingLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> chattingLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : chattingLogIds) {
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
		Map params = properties.getParameters();
		String construct = properties.getConstruct();
		GenericPageList pList = new GenericPageList(ChattingLog.class,construct, query,
				params, this.chattingLogDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	@Transactional(readOnly = false)
	public boolean update(ChattingLog chattingLog) {
		try {
			this.chattingLogDao.update( chattingLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<ChattingLog> query(String query, Map params, int begin, int max){
		return this.chattingLogDao.query(query, params, begin, max);
		
	}
}
