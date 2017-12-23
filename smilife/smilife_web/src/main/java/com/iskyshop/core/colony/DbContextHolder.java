package com.iskyshop.core.colony;

/**
 * 
 * <p>
 * Title: DbContextHolder.java
 * </p>
 * 
 * <p>
 * Description: 集群版数据源rute类，该类提供数据源切换及数据源信息清除，达到数据库读写分离的目的
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
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
public class DbContextHolder {
	private static final ThreadLocal<Object> CONTEXTHOLDER = new ThreadLocal<Object>();

	/**
	 * 设置数据源信息，和配置文件中的数据源名称对应，默认为主从数据源各一个，可以支持任意多个数据源
	 * 
	 * @param dbType
	 */
	public static void setDbType(Object dbType) {
		CONTEXTHOLDER.set(dbType);
	}

	/**
	 * 获取的数据源信息
	 * 
	 * @return 当前使用的数据源
	 */
	public static String getDbType() {
		return (String) CONTEXTHOLDER.get();
	}

	/**
	 * 清除数据源信息
	 */
	public static void clearDbType() {
		CONTEXTHOLDER.remove();
	}

}
