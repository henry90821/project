package com.iskyshop.foundation.service;

import com.iskyshop.core.beans.exception.CallException;
import com.iskyshop.foundation.domain.OrderForm;

/**
 * 订单分发接口
 * 
 * @ClassName: IOrderDistributeService
 * @Description: TODO(订单分发接口类,各种订单业务实现类可以实现,进行业务处理)
 * @author wangyun
 * @date 2015-11-16
 */
public interface IOrderDistributeService {
	
	/**
	 * 接口调用方法
	 * @param message
	 * @return
	 * @throws CallException
	 * 
	 */
	boolean distribte(String message,String url) throws CallException;
	
	/**
	 * 报文拼装方法
	 * @param orderform
	 * @param destination
	 * @return
	 */
	String createPushMessage(OrderForm orderform, String destination);
}
