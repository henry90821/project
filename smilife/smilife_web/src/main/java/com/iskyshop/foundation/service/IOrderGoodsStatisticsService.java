package com.iskyshop.foundation.service;


import com.iskyshop.foundation.domain.OrderGoodsStatistics;
import java.util.List;

/**
 * 
 * @author dengyuqi
 *
 */
public interface IOrderGoodsStatisticsService {
	
	/**
	 * 
	 * @param instances
	 * @return
	 */
	boolean batchSave(List<OrderGoodsStatistics> instances);
	
	void batchSave(List<OrderGoodsStatistics> instances,int num);

}
