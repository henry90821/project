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
import com.iskyshop.module.chatting.domain.ChattingConfig;
import com.iskyshop.module.chatting.service.IChattingConfigService;

@Service
@Transactional
public class ChattingConfigServiceImpl implements IChattingConfigService{
	@Resource(name = "chattingConfigDAO")
	private IGenericDAO<ChattingConfig> chattingConfigDao;
	@Transactional(readOnly = false)
	public boolean save(ChattingConfig chattingConfig) {
		/**
		 * init other field here
		 */
		try {
			this.chattingConfigDao.save(chattingConfig);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public ChattingConfig getObjById(Long id) {
		ChattingConfig chattingConfig = this.chattingConfigDao.get(id);
		if (chattingConfig != null) {
			return chattingConfig;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.chattingConfigDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> chattingConfigIds) {
		// TODO Auto-generated method stub
		for (Serializable id : chattingConfigIds) {
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
		GenericPageList pList = new GenericPageList(ChattingConfig.class,construct, query,
				params, this.chattingConfigDao);
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
	public boolean update(ChattingConfig chattingConfig) {
		try {
			this.chattingConfigDao.update( chattingConfig);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	@Transactional(readOnly = true)
	public List<ChattingConfig> query(String query, Map params, int begin, int max){
		return this.chattingConfigDao.query(query, params, begin, max);
		
	}
}
