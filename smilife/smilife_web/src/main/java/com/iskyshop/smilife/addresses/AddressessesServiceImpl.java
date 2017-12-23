package com.iskyshop.smilife.addresses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.query.AddressQueryObject;
import com.iskyshop.foundation.service.IAddressService;

@Service
public class AddressessesServiceImpl implements AddressessesService {


	@Autowired
	private IAddressService addressService;
	
	/**
	 * 收货地址
	 *
	 */
	@Override
	public IPageList getAddressesList(String custId,int targetPage,int pageSize) {
		AddressQueryObject qo = new AddressQueryObject();
		qo.addQuery("obj.user.id", new SysMap("user_id",CommUtil.null2Long(custId)), "=");
		qo.setCurrentPage(targetPage);
		qo.setPageSize(pageSize);
		qo.setOrderBy("addTime");
		qo.addQuery("area_id is not null",	null);
		IPageList pList = this.addressService.list(qo);
		return pList;
	}

}
