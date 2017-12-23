package com.smi.pay.dao;

import com.smi.pay.model.AppInfo;

import java.util.List;

public interface AppInfoDao {

	int deleteByPrimaryKey(Integer id);

	int insert(AppInfo appInfo);

	AppInfo load(Integer id);

	int update(AppInfo appInfo);

	AppInfo getByCodePay(String appcode);

	AppInfo getByCodeRefund(String appcode);

	List getAll();

}