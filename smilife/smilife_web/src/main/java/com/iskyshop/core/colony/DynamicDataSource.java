package com.iskyshop.core.colony;

import java.util.logging.Logger;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 
 * <p>
 * Title: DynamicDataSource.java
 * </p>
 * 
 * <p>
 * Description: 根据Spring特性，设立动态数据源信息，在配置文件中配置多个数据源，制定默认数据源，通过该类完成数据源的设定切换，默认数据源为一主一从 ,主数据源用来写数据，从数据源用来读数据
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2015-3-26
 * 
 * @version iskyshop_b2b2c 2015 集群版
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	/**
	 * 调用工具类完成数据源读取
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		// TODO Auto-generated method stub
		return DbContextHolder.getDbType();
	}

	@Override
	public Logger getParentLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
