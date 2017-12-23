package com.iskyshop.smilife.addresses;

import com.iskyshop.core.query.support.IPageList;

/**
 * 收货地址
 *
 */
public interface AddressessesService {

	IPageList getAddressesList(String custId,int targetPage,int pageSize);

}
