package com.smi.pay.service;

import java.util.List;

import com.smi.pay.model.User;


public interface UserService {
	/**
	 * 查询所有用户信息
	 * @return List
	 * @throws Exception
	 * @since 2016-03-10
	 */
	public List<User> listUser();
	
	/**
	 * 根据ID修改对应用户信息
	 * @param appInfo
	 * @return 
	 * @throws Exception
	 * @since 2016-03-10
	 */
	public void updateUserById(User user);
	
	/**
	 * 根据ID查询对应用户信息
	 * @param id
	 * @return AppInfo
	 * @throws Exception
	 * @since 2016-03-10
	 */
	
	public User loadUser(int id);
	
	
	/**
	 * 新增用户
	 * @param appInfo
	 * @return
	 * @throws Exception
	 * @since 2016-03-10
	 */
	public void saveUser(User user);
	
	/**
	 * 根据ID删除应用信息
	 * @param id
	 * @return
	 * @throws Exception
	 * @since 2016-03-10
	 */
	public void deleteUser(int id);
	
	public User getByUserName(String username);
}
