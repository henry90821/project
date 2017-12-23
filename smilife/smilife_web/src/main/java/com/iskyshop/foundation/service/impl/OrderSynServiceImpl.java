package com.iskyshop.foundation.service.impl;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.GoodsConfig;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderSyn;
import com.iskyshop.foundation.domain.OrderSynLog;
import com.iskyshop.foundation.service.IOrderSynService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrderSynServiceImpl implements IOrderSynService {
	private final Logger LOGGER = Logger.getLogger(this.getClass());

	@Resource(name = "orderSynDAO")
	private IGenericDAO<OrderSyn> orderSynDAO;
	@Resource(name = "orderSynLogDAO")
	private IGenericDAO<OrderSynLog> orderSynLogDAO;

	@Transactional(readOnly = false)
	public boolean save(OrderSyn instance) {
		try {
			orderSynDAO.save(instance);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean update(OrderSyn instance) {
		try {
			orderSynDAO.update(instance);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = true)
	public OrderSyn getObjById(Long id) {
		OrderSyn orderSyn = orderSynDAO.get(id);
		if (orderSyn != null) {
			return orderSyn;
		}
		return null;
	}

	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.orderSynDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = true)
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(OrderSyn.class, construct, query, params, this.orderSynDAO);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj.getPageSize(), params);
		} else
			pList.doList(0, -1);
		return pList;
	}

	@Transactional(readOnly = true)
	public List<OrderSyn> query(String query, Map params, int begin, int max) {
		return this.orderSynDAO.query(query, params, begin, max);
	}

	@Transactional(readOnly = false)
	@Override
	public boolean batchSave(List<OrderSyn> orderSynList) {
		boolean result = true;
		for (OrderSyn orderSyn : orderSynList) {
			if (!this.save(orderSyn)) {
				result = false;
				break;
			}

		}
		return result;
	}

	@Transactional(readOnly = false)
	@Override
	public boolean updateAndLog(OrderSyn instance) {
		Boolean result = false;
		OrderSynLog orderSynLog = null;

		try {
			// // 如果待同步订单的状态不是成功状态则需要进行同步日志的处理
			// if (instance.getSynStatus() != 1) {
			//
			// }

			// 创建同步日志对象
			orderSynLog = new OrderSynLog();
			// 设置订单同步报文
			orderSynLog.setOrderInfo(instance.getOrderInfo());
			// 设置订单同步时间
			orderSynLog.setSynTime(instance.getSynTime());
			// 设置订单同步备注
			orderSynLog.setFailInfo(instance.getSynDesc());
			// 关联订单信息
			orderSynLog.setOrderForm(instance.getOrderForm());
			// 设置订单同步URL
			orderSynLog.setSynIUrl(instance.getGoodsConfig().getSynIUrl());
			// 设置订单同步状态
			orderSynLog.setSynStatus(instance.getSynStatus());
			// 判断是否需用将订单同步用户设置为默认值
			instance.setSynUser(StringUtils.isNullOrEmpty(instance.getSynUser()) ? OrderSyn.SYN_USER_DEFAULT_VALUE
					: instance.getSynUser());
			// 设置订单同步用户
			orderSynLog.setSynUser(instance.getSynUser());
			// 设置订单同步日子数据添加时间
			orderSynLog.setAddTime(new Date());
			// 保存订单同步日志对象
			this.orderSynLogDAO.save(orderSynLog);
			// 更新待同步订单对象
			this.orderSynDAO.update(instance);
			result = true;
		} catch (Exception e) {
			LOGGER.error("保存OrderSyn和OrderSynLog对象失败！", e);
			result = false;
		}
		return result;
	}

	@Transactional(readOnly = true)
	@Override
	public OrderSyn queryHisenseByApp(OrderForm orderId) {
		OrderSyn result = null;
		Map<String, Object> params = null;
		final String hql = "FROM OrderSyn WHERE orderForm.id=:orderId and goodsConfig.configCode=:configCode and synStatus=:synStatus and deleteStatus=:deleteStatus";

		try {
			params = new HashMap<>();
			params.put("orderId", orderId.getId()); // 需要查询的订单ID值
			params.put("configCode", GoodsConfig.CODE_HX); // 待同步订单类型为海信商品
			params.put("synStatus", 1); // 订单状态为成功
			params.put("deleteStatus", 0); // 数据状态为有效状态
			List<OrderSyn> hisenseOrderSynList = this.orderSynDAO.query(hql, params, -1, -1);
			if (!StringUtils.isNullOrEmpty(hisenseOrderSynList)) {
				result = hisenseOrderSynList.get(0);
			}
		} catch (Exception e) {
			LOGGER.error("根据订单OrderForm查询海信待同步订单出错！", e);
		}
		return result;
	}

}
