package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Predeposit;

public interface IPredepositService {
	/**
	 * 保存一个Predeposit，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Predeposit instance);
	
	/**
	 * 根据一个ID得到Predeposit
	 * 
	 * @param id
	 * @return
	 */
	Predeposit getObjById(Long id);
	
	/**
	 * 根据
	 * 
	 * @Title: getObjByProperty
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param construct
	 * @param @param propertyName
	 * @param @param value
	 * @param @return 参数
	 * @return User 返回类型	
	 * @throws
	 */
	Predeposit getObjByProperty(String construct, String propertyName, String value);
	
	/**
	 * 删除一个Predeposit
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Predeposit
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Predeposit
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Predeposit
	 * 
	 * @param id
	 *            需要更新的Predeposit的id
	 * @param dir
	 *            需要更新的Predeposit
	 */
	boolean update(Predeposit instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Predeposit> query(String query, Map params, int begin, int max);
	
	
	/**
	 * 在商城下单然后再到支付中心下单，最后进入支付中心支付页面(不会判断商城当前是否开启了预存款功能)
	 * @param request
	 * @param response
	 * @param mobile 被充值账号所绑定的手机号
	 * @param amount 充值金额
	 * @param channel 支付渠道ChannelEnum
	 * @param orderType OrderTypeEnum
	 * @param returnUrl 回调业务系统地址
	 */
	public Object predepositPay(HttpServletRequest request, HttpServletResponse response, String account, String amount, String channel, String orderType, String returnUrl);
}
