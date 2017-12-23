package com.iskyshop.foundation.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.User;


public interface IUserService {
	
	/**
	 * 
	 * @param user
	 * @return 若User表中不存在与user.getCustId()相同的记录，且保存成功，返回true；若存在custId相同的记录，则不会保存且返回false,此时，参数user的id不会被赋值，应用应根据custId字段重新获取对应的User实例。若操作数据库错误，则会抛出相关异常 
	 */
	boolean save(User user);

	/**
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 
	 * @param user
	 * @return
	 */
	boolean update(User user);

	/**
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 
	 * @param id
	 * @return
	 */
	User getObjById(Long id);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	User getObjByProperty(String construct, String propertyName, String value);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<User> query(String query, Map params, int begin, int max);

	/**
	 * 查询user用户数据量。nativeSql语句必须是select count的语句，如"select count(*) from xxxxx"
	 * @param nativeSql
	 * @param params
	 * @return
	 */
	public int getCount(String nativeSql, Object[] params);

	/**
	 * 用户信息批量修改
	 * @param list
	 */
	public void batchUpdate(List<User> list);
	
	
	/**
	 * 查询用户在CRM中记录的余额
	 * @param custId
	 * @return
	 */
	public BigDecimal getUserAvailableBalance(String custId);
	
	
	/**
	 * 根据买家用户绑定的手机号来查找此买家
	 * @param mobile
	 * @return 若未找到，则返回null
	 */
	public User getBuyerByMobile(String mobile);
	
	
	/**
	 * 根据会员编号CustId来查找此用户（包括所有买家账号和店铺主账号）。若商城端已存在此用户，则不会去CRM端同步对应的会员信息。若要同步会员信息，请调用UserTools.syncBuyerFromCRM
	 * @param custId 买家账号和店铺主账号的custId
	 * @return
	 */
	public User getUserByCustId(String custId);
	
	
	/**
	 * 根据卖家的登录账号来查找对应的卖家。若查找到多个，则会抛出异常。
	 * @param sellerLoginAccount  卖家(包括主账号和子账号)的登录账号
	 * @return
	 */
	public User getSellerByLoginAccount(String sellerLoginAccount);
	
	/**
	 * 根据userName字段的值来查找后台管理员User对象。若查找到多个，则会抛出异常。
	 * @param userName
	 * @return 若未找到用户，则返回null
	 */
	public User getAdminByUsername(String userName);
	
	/**
	 * 检查商家账号是否已存在
	 * @param sellerLoginAccount 经过clearContent函数处理后的商家账号名称
	 * @return  true:商家账号存在，false：商家账号不存在
	 */
	public boolean isSellerAccountExisted(String sellerLoginAccount);

	
	/**
	 * 根据账号绑定的手机号查找对应的买家账户或主账号卖家账户
	 * @param mobile
	 * 
	 * @return 未找到则返回null
	 */
	public User getBuyerOrMainSellerByMobile(String mobile);
}
