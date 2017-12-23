package com.iskyshop.oms.service.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.iskyshop.foundation.domain.WaitSynOrder;
import com.iskyshop.oms.service.AbstractOmsManagerService;

/**
 * 具体策略类-
 * @author dengyuqi
 *
 */
@Component
public class WaitSynOrderOmsServiceImpl extends AbstractOmsManagerService<WaitSynOrder> {

	private static Logger logger = Logger.getLogger(WaitSynOrderOmsServiceImpl.class);
	
	@Override
	public String getInsertData(WaitSynOrder waitSynOrder) {
		return waitSynOrder.getOrder_data();
	}

	@Override
	public String getUpdateData(WaitSynOrder waitSynOrder) {
		return waitSynOrder.getOrder_data();
	}

	@Override
	public boolean validateInsertOrder(WaitSynOrder waitSynOrder) {
		if(null == waitSynOrder || null == waitSynOrder.getOrder_id()
				|| null == waitSynOrder.getOrder_data()){
			return false;
		}
		return true;
	}

	@Override
	public boolean validateUpdateOrder(WaitSynOrder waitSynOrder) {
		if(null == waitSynOrder || null == waitSynOrder.getOrder_id()
				|| null == waitSynOrder.getOrder_data()){
			return false;
		}
		return true;
	}

}
