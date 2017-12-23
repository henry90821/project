package com.iskyshop.module.app.service;

import java.util.ArrayList;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.module.app.domain.AppNewlyVersionInfo;


public interface IAppNewlyVerionService {

	/**
	 * 请求APP的最新版本(是否要强制更新，则按以下算法来确定：检查手机中当前安装的APP版本与服务器端的最新版本之间（包括最新版本）是否有要强制更新的版本，若有，则会强制更新手机中的当前版本（即使最新版本可能不要求强制更新）)
	 * @param platFormFlag ：请求的安装包适用的平台。1：android   2：ios
	 * @param channel: 安装包所对应的分发渠道，如对应豌豆荚的channel为wandoujia。当platformFlag为2时，此参数不起作用
	 * @param verNumber: 手机中当前已安装的APP版本号
	 */
	public AppNewlyVersionInfo queryAppNewlyVer(String platFormFlag, String channel, int verNumber);
	
	public boolean batchSave(ArrayList<AppNewlyVersionInfo>  AppVersion);
	
	public boolean save(AppNewlyVersionInfo instance); 
	
	public IPageList list(IQueryObject properties);
	
	/**
	 * 返回所有渠道中的最新版本
	 * @return
	 */
	public ArrayList<AppNewlyVersionInfo> queryAllLatestVers();
}
