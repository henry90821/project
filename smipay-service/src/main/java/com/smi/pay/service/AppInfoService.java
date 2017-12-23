package com.smi.pay.service;

import java.util.List;

import com.smi.pay.model.AppInfo;


public interface AppInfoService {
	/**
	 * 查询所有应用信息
	 * @return List
	 * @throws Exception
	 * @since 2016-03-10
	 */
	public List<AppInfo> listAppInfo();
	
	/**
	 * 根据ID修改对应应用信息
	 * @param appInfo
	 * @return 
	 * @throws Exception
	 * @since 2016-03-10
	 */
	public void updateAppInfoById(AppInfo appInfo);
	
	/**
	 * 根据小区ID查询对应应用信息
	 * @param id
	 * @return AppInfo
	 * @throws Exception
	 * @since 2016-03-10
	 */
	
	public AppInfo loadAppInfo(int id);
	
	
	/**
	 * 新增应用信息
	 * @param appInfo
	 * @return
	 * @throws Exception
	 * @since 2016-03-10
	 */
	public void saveAppInfo(AppInfo appInfo);
	
	/**
	 * 根据ID删除应用信息
	 * @param id
	 * @return
	 * @throws Exception
	 * @since 2016-03-10
	 */
	public void deleteAppInfo(int id);
}
