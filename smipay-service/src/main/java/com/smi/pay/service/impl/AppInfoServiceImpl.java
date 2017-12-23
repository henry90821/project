package com.smi.pay.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smi.pay.dao.AppInfoDao;
import com.smi.pay.model.AppInfo;
import com.smi.pay.service.AppInfoService;

@Service("AppInfoService")
@Transactional(readOnly = false)
public class AppInfoServiceImpl implements AppInfoService {

	@Autowired
	private AppInfoDao	appInfoDao;

	@Override
	public List<AppInfo> listAppInfo() {
		// TODO Auto-generated method stub
		return appInfoDao.getAll();
	}

	@Override
	public void updateAppInfoById(AppInfo appInfo) {
		appInfoDao.update(appInfo);
	}

	@Override
	public AppInfo loadAppInfo(int id) {
		// TODO Auto-generated method stub
		return appInfoDao.load(id);
	}

	@Override
	public void saveAppInfo(AppInfo appInfo) {
		appInfoDao.insert(appInfo);

	}

	@Override
	public void deleteAppInfo(int id) {
		appInfoDao.deleteByPrimaryKey(id);

	}

}
