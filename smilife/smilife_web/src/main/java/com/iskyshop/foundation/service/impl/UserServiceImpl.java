package com.iskyshop.foundation.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.exception.SmiBusinessException;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.UserTools;
import com.smilife.bcp.dto.common.EQryType;
import com.smilife.bcp.dto.response.MemberBalanceResp;
import com.smilife.bcp.dto.response.MemberInfoResp;
import com.smilife.bcp.service.FeeManageConnector;
import com.smilife.bcp.service.UserManageConnector;

@Service("userServiceImpl")
public class UserServiceImpl implements IUserService {
	private static Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	@Resource(name = "userDAO")
	private IGenericDAO<User> userDAO;

	@Autowired
	private FeeManageConnector feeManageConnector;
	
	@Autowired
	private UserManageConnector userManageConnector;
	
	@Autowired
	private UserTools userTools;

	
	@Override
	@Transactional
	public boolean delete(Long id) {
		try {
			this.userDAO.remove(id);
			return true;
		} catch (Exception e) {
			String errMsg = "删除用户(id=" + id + ")失败";
			logger.error(errMsg, e);
			throw new SmiBusinessException(errMsg, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public User getObjById(Long id) {
		return this.userDAO.get(id);
	}
	
	@Override
	@Transactional
	public boolean save(User user) {
		try {
			this.userDAO.save(user);//保存成功后，user中的id会自动被赋值
		} catch(ConstraintViolationException e) {//数据库中custId被设为unique，所以由于并发，可能会同时插入同一个custId值的User，这时会抛出ConstraintViolationException异常，而对于这个异常，不应向外抛出
			logger.info(e.getMessage() + "：数据库中已有对应custId(custId=" + user.getCustId() + ")的User记录，故不再保存当前User对象！");//user参数的id不会被赋值
			return false;
		} catch (Exception e) {
			throw new SmiBusinessException("保存用户失败：custId=" + user.getCustId() + "， userName=" + user.getUserName() + "， sellerLoginAccount=" + user.getSellerLoginAccount(), e);
		}
		return true;
	}

	@Override
	@Transactional
	public boolean update(User user) {
		try {
			this.userDAO.update(user);
			return true;
		} catch (Exception e) {
			throw new SmiBusinessException("更新用户对象失败：uId=" + user.getId(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> query(String query, Map params, int begin, int max) {
		return this.userDAO.query(query, params, begin, max);

	}

	@Override
	@Transactional(readOnly = true)
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(User.class, construct, query, params, this.userDAO);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null) {
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj.getPageSize());
			}
		} else {
			pList.doList(0, -1);
		}
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public User getObjByProperty(String construct, String propertyName, String value) {
		return this.userDAO.getBy(construct, propertyName, value);
	}

	@Override
	@Transactional(readOnly = true)
	public int getCount(String nativeSql, Object[] params) {
		List list = this.userDAO.executeNativeQuery(nativeSql, params, -1, -1);
		if (!StringUtils.isNullOrEmpty(list)) {
			return Integer.parseInt(list.get(0).toString());
		} else {
			return 0;
		}
	}

	@Override
	@Transactional
	public void batchUpdate(List<User> list) {
		this.userDAO.batchUpdate(list);
	}
	
	@Override
	@Transactional
	public BigDecimal getUserAvailableBalance(String custId) {
		MemberBalanceResp memberBalanceResp = feeManageConnector.queryEnergy(custId);
		return memberBalanceResp.getCash();
	}

	@Override
	@Transactional
	public User getAdminByUsername(String userName) {
		return userTools.getUser("ADMIN", userName, null);
	}

	@Override
	@Transactional
	public User getSellerByLoginAccount(String sellerLoginAccount) {
		return userTools.getUser("SELLER", sellerLoginAccount, null);
	}

	@Override
	@Transactional
	public boolean isSellerAccountExisted(String sellerLoginAccount) {
		Map  params = new HashMap();
		params.put("sellerLoginAccount", sellerLoginAccount);
		List<User> users = this.query("select new User(id) from User obj where obj.sellerLoginAccount=:sellerLoginAccount and obj.userRole='SELLER'", params, -1, -1);
		
		if(users.size() > 1) {
			throw new SmiBusinessException("查找卖家(sellerLoginAccount=" + sellerLoginAccount + ")时失败：查找到多个User实例，数据有误，请修复后重试！");
		}
		
		return !users.isEmpty();
	}

	@Override
	@Transactional
	public User getBuyerByMobile(String mobile) {
		return userTools.getUser("BUYER", mobile, null);
	}

	@Override
	@Transactional
	public User getUserByCustId(String custId) {
		return userTools.syncBuyerFromCRM(custId, false, true);
	}
	
	@Override
	public User getBuyerOrMainSellerByMobile(String mobile) {
		User retUser = null;
		
		if(StringUtils.isNullOrEmpty(mobile)) {
			return null;
		}
		
		MemberInfoResp mi = userManageConnector.queryMemberInfo(EQryType.ByMobileNumber, mobile.trim());
		if (mi != null) {
			retUser = userTools.syncBuyerFromCRM(mi.getCustId(), false, true);
		}
		
		return retUser;
	}

}
