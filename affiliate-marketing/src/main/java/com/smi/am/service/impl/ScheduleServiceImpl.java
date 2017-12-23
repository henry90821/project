package com.smi.am.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.am.dao.AmCouponsMapper;
import com.smi.am.dao.AmGiftPackageMapper;
import com.smi.am.service.IScheduleService;

@Service
public class ScheduleServiceImpl implements IScheduleService{

	@Autowired
	private AmCouponsMapper couponsMapper;
	
	@Autowired
	private AmGiftPackageMapper amGiftPackageMapper;
	
	@Override
	public void updateAllUnAuditCoupons() {
		couponsMapper.updateAllUnAuditCoupons();
	}

	@Override
	public void updateAllUnDeliveryCoupons() {
		couponsMapper.updateAllUnDeliveryCoupons();
	}

	@Override
	public void deliveryConfirmOnlineCoupons() {
		couponsMapper.deliveryConfirmOnlineCoupons();
	}

	@Override
	public void deliveryConfirmOnlineGitPackage() {
		amGiftPackageMapper.deliveryConfirmOnlineGitPackage();
	}

	@Override
	public void updateAllUnAuditGitPackage() {
		amGiftPackageMapper.updateAllUnAuditGitPackage();
	}

	@Override
	public void updateAllUnDeliveryGitPackage() {
		amGiftPackageMapper.updateAllUnDeliveryGitPackage();
	}

}
