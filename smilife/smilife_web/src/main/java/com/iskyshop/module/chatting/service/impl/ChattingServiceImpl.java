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
import com.iskyshop.module.chatting.domain.Chatting;
import com.iskyshop.module.chatting.service.IChattingService;

@Service
@Transactional
public class ChattingServiceImpl implements IChattingService{
	@Resource(name = "chattingDAO")
	private IGenericDAO<Chatting> chattingDao;
	@Transactional(readOnly = false)
	public boolean save(Chatting chatting) {
		/**
		 * init other field here
		 */
		try {
			this.chattingDao.save(chatting);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public Chatting getObjById(Long id) {
		Chatting chatting = this.chattingDao.get(id);
		if (chatting != null) {
			return chatting;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.chattingDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> chattingIds) {
		// TODO Auto-generated method stub
		for (Serializable id : chattingIds) {
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
		GenericPageList pList = new GenericPageList(Chatting.class,construct, query,
				params, this.chattingDao);
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
	public boolean update(Chatting chatting) {
		try {
			this.chattingDao.update( chatting);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<Chatting> query(String query, Map params, int begin, int max){
		return this.chattingDao.query(query, params, begin, max);
		
	}
}
