package com.iskyshop.module.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.module.app.domain.AppNewlyVersionInfo;
import com.iskyshop.module.app.service.IAppNewlyVerionService;


@Service("appNewlyVerionService")
@Transactional
public class AppNewlyVerionServiceImpl implements IAppNewlyVerionService {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Resource(name = "appnewlyversioninfoDAO")
	private IGenericDAO<AppNewlyVersionInfo> appnewlyversioninfoDAO;
	
	private String findMostNewlyVerSql_android = "select a from AppNewlyVersionInfo a where a.platformFlag=1 and a.channelId=:channel and not exists(select b from AppNewlyVersionInfo b where b.platformFlag=1 and b.channelId=a.channelId and b.verNumber>a.verNumber)";
	private String findMostNewlyVerSql_ios = "select a from AppNewlyVersionInfo a where a.platformFlag=2 and not exists(select b from AppNewlyVersionInfo b where b.platformFlag=2 and b.verNumber>a.verNumber)";
	private String findNewlyVersSql_android = "select a from AppNewlyVersionInfo a where a.platformFlag=1 and a.channelId=:channel and a.verNumber >:verNumber";
	private String findNewlyVersSql_ios = "select a from AppNewlyVersionInfo a where a.platformFlag=2 and a.verNumber >:verNumber";
	private String findAllMostNewlyVersSql = "select a from AppNewlyVersionInfo a  where a.verNumber >=all(select b.verNumber from AppNewlyVersionInfo b  where b.channelId=a.channelId and b.platformFlag=a.platformFlag)";
	
	@Override
	public AppNewlyVersionInfo queryAppNewlyVer(String platformFlag, String channel, int verNumber) {
		HashMap<String,Object> params = new HashMap<String,Object>();
		String sql = this.findMostNewlyVerSql_ios;		
		
		if("1".equals(platformFlag)) {//android
			params.put("channel", channel);
			sql = this.findMostNewlyVerSql_android;			
		} else if(!"2".equals(platformFlag)) {
			return null;
		} 		
		
		List<AppNewlyVersionInfo> appVersions = (List<AppNewlyVersionInfo>)this.appnewlyversioninfoDAO.query(sql, params, -1, -1);
		if(appVersions == null || appVersions.size() == 0) {
			if("2".equals(platformFlag)) {
				return null;
			} else if(!"smi".equals(channel)) {
				params.put("channel", "smi");
				appVersions = this.appnewlyversioninfoDAO.query(sql, params, -1, -1);
			}			
		}
		if(appVersions != null && appVersions.size() > 0) {
			AppNewlyVersionInfo tmp = appVersions.get(appVersions.size() - 1);
			AppNewlyVersionInfo latestVer = new AppNewlyVersionInfo();
			latestVer.setAddTime(tmp.getAddTime());
			latestVer.setChannelId(tmp.getChannelId());
			latestVer.setId(tmp.getId());
			latestVer.setInstallUrl(tmp.getInstallUrl());
			latestVer.setPlatformFlag(tmp.getPlatformFlag());
			latestVer.setUpdateFlag(tmp.getUpdateFlag());
			latestVer.setVerDesc(tmp.getVerDesc());
			latestVer.setVerName(tmp.getVerName());
			latestVer.setVerNumber(tmp.getVerNumber());
			
			if(latestVer.getUpdateFlag() == 1 && latestVer.getVerNumber() > verNumber) {//防止跳版本时跳过强制更新
				sql = this.findNewlyVersSql_ios;
				if("1".equals(platformFlag)) {
					sql = this.findNewlyVersSql_android;
				}
				params.put("verNumber", verNumber);
				appVersions = (List<AppNewlyVersionInfo>)this.appnewlyversioninfoDAO.query(sql, params, -1, -1);
				if(appVersions != null && appVersions.size() > 0) {
					//检查是否有要强制更新的版本，若有，则要强制更新，否则不强制更新
					for(AppNewlyVersionInfo appVer: appVersions) {
						if(appVer.getUpdateFlag() == 0) {
							latestVer.setUpdateFlag(0);
							break;
						}
					}					
				}				
			}
			
			return latestVer;
		}
		
		return null;
		
	}
	

	@Override
	@Transactional(readOnly = false)
	public boolean batchSave(ArrayList<AppNewlyVersionInfo> AppVersions) {		
		try {
			if(AppVersions != null && AppVersions.size() > 0) {
				for(AppNewlyVersionInfo appVer : AppVersions) {
					this.appnewlyversioninfoDAO.save(appVer);
				}			
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean save(AppNewlyVersionInfo instance) {
		try {
			this.appnewlyversioninfoDAO.save(instance);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
		GenericPageList pList = new GenericPageList(Template.class,construct, query, params, this.appnewlyversioninfoDAO);
		PageObject pageObj = properties.getPageObj();
		if (pageObj != null)
			pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage(),
							pageObj.getPageSize() == null ? 0 : pageObj.getPageSize());
		return pList;
	}

	@Override
	public ArrayList<AppNewlyVersionInfo> queryAllLatestVers() {
		
		ArrayList<AppNewlyVersionInfo> vers = null; 
		
		try {
			vers = (ArrayList<AppNewlyVersionInfo>)this.appnewlyversioninfoDAO.query(this.findAllMostNewlyVersSql, null, -1, -1);
		} catch (Exception e) {
			logger.error(e);
		}
		if(vers != null && vers.size() > 0) {
			//对于有多条最大版本的渠道，根据发布时间addTime来选择最新提交的一个版本
			ArrayList<AppNewlyVersionInfo> mostNewlyVers = new ArrayList<AppNewlyVersionInfo>();
			ArrayList<AppNewlyVersionInfo> toRemove = new ArrayList<AppNewlyVersionInfo>();
			
			while(vers.size() > 0) {
				AppNewlyVersionInfo ver = vers.get(0);
				for(int i = 1; i < vers.size(); i++) {
					AppNewlyVersionInfo tmp = vers.get(i);
					if(tmp.getChannelId().equals(ver.getChannelId()) && tmp.getPlatformFlag() == ver.getPlatformFlag()) {
						if(tmp.getAddTime().after(ver.getAddTime())) {
							toRemove.add(ver);
							ver = tmp;	
						} else {
							toRemove.add(tmp);
						}
											
					} 
				}
				mostNewlyVers.add(ver);
				vers.remove(ver);
				for(AppNewlyVersionInfo t: toRemove) {
					vers.remove(t);
				}
				toRemove.clear();
			}			
			
			return mostNewlyVers;
		}
		
		return null;
	}	

}
