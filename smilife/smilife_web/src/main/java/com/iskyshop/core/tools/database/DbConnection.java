package com.iskyshop.core.tools.database;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 
 * <p>
 * Title: DbConnection.java
 * </p>
 * 
 * <p>
 * Description: 数据库的连接,使用线程安全管理，确保数据库链接只存在一个，维护系统性能正常
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-24
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Repository
@SuppressWarnings("serial")
public class DbConnection {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private javax.sql.DataSource dataSource;
	// 线程安全
	public static final ThreadLocal<Connection> THREAD = new ThreadLocal<Connection>();

	public Connection getConnection() {
		Connection conn = THREAD.get();
		if (conn == null) {
			try {
				conn = this.dataSource.getConnection();
				THREAD.set(conn);
			} catch (Exception e) {
				logger.error("获取数据库连接异常： " + e);
			}
		}
		return conn;
	}

	/**
	 * 关闭链接
	 * 
	 * @throws Exception
	 */
	public void closeAll() {
		try {
			Connection conn = THREAD.get();
			if (conn != null) {
				conn.close();
				THREAD.set(null);
			}
		} catch (Exception e) {
			try {
				logger.error("关闭数据库连接异常： " + e);
				throw e;
			} catch (Exception e1) {
				logger.error("关闭数据库连接异常： " + e1);
			}
		}
	}
}
