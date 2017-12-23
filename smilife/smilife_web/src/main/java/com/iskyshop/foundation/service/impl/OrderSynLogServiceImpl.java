package com.iskyshop.foundation.service.impl;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.foundation.domain.OrderSynLog;
import com.iskyshop.foundation.service.IOrderSynLogService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by Andriy on 15/12/1.
 */
public class OrderSynLogServiceImpl implements IOrderSynLogService {

	@Resource(name = "orderSynLogDAO")
	private IGenericDAO<OrderSynLog> orderSynLogDAO;

	@Transactional(readOnly = false)
	@Override
	public boolean save(OrderSynLog instance) {
		try {
			this.orderSynLogDAO.save(instance);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
