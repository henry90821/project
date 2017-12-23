package com.iskyshop.foundation.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.foundation.domain.OrderGoodsStatistics;
import com.iskyshop.foundation.service.IOrderGoodsStatisticsService;

@Service
@Transactional
public class OrderGoodsStatisticsServiceImpl implements IOrderGoodsStatisticsService {
	
	private static Logger logger = Logger.getLogger(OrderGoodsStatisticsServiceImpl.class);
	
	@Resource(name = "orderGoodsStatisticsDAO")
	private IGenericDAO<OrderGoodsStatistics> orderGoodsStatisticsDao;

	@Override
	public boolean batchSave(List<OrderGoodsStatistics> instances) {
		logger.debug("------------------------");
//		orderGoodsStatisticsDao.batchInsert(instances);
		for(OrderGoodsStatistics goodsStatistics : instances){
			orderGoodsStatisticsDao.save(goodsStatistics);
		}
		return true;
	}

	@Override
	public void batchSave(List<OrderGoodsStatistics> instances, int num) {
		orderGoodsStatisticsDao.batchInsert(instances,num);
	}
	
}
