package com.smi.am.service.vo;

import java.util.List;

import com.smi.am.dao.model.AmCounponsDetails;
import com.smi.am.dao.model.AmCoupons;
import com.smi.am.dao.model.AmGiftPackage;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class CustPackVo extends BaseVo{

	@ApiModelProperty(value = "礼包列表")
	private List<GiftPackageResVo> AmGiftPackageList;

	@ApiModelProperty(value = "优惠券详情列表")
	private List<AmCounponsDetails> counponsList;

	@ApiModelProperty(value = "优惠券列表")
	private List<CouponsVo> CouponsVo;
	
	@ApiModelProperty(value = "用户角色列表")
	private List<UserVo> userList;

	public List<GiftPackageResVo> getAmGiftPackageList() {
		return AmGiftPackageList;
	}

	public void setAmGiftPackageList(List<GiftPackageResVo> amGiftPackageList) {
		AmGiftPackageList = amGiftPackageList;
	}

	public List<AmCounponsDetails> getCounponsList() {
		return counponsList;
	}

	public void setCounponsList(List<AmCounponsDetails> counponsList) {
		this.counponsList = counponsList;
	}

	public List<CouponsVo> getCouponsVo() {
		return CouponsVo;
	}

	public void setCouponsVo(List<CouponsVo> couponsVo) {
		CouponsVo = couponsVo;
	}

	public List<UserVo> getUserList() {
		return userList;
	}

	public void setUserList(List<UserVo> userList) {
		this.userList = userList;
	}

}
