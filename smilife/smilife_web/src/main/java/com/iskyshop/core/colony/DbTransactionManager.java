package com.iskyshop.core.colony;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * 
 * <p>
 * Title: DbTransactionManager.java
 * </p>
 * 
 * <p>
 * Description:集群版事务管理器，重写Spring默认的事务管理器，在事务开启前根据事务级别完成数据源切换，只读事务读取从数据源数据，非只读事务向主数据源写数据
 * ，mysql配置住从数据源完成数据同步，做到数据库读写分离.MySQL5.0-5.5支持数据异步复制，MySQL5.5支持主从数据源半同步复制
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
public class DbTransactionManager extends JpaTransactionManager {
	/**
	 * 判断事务类型，非只读事务切换数据源为主数据源
	 */
	@Override
	protected void doBegin(Object object, TransactionDefinition definition) {
		if (!definition.isReadOnly()) { // 如果事务不为只读，则为写数据，自动切换到master数据源
			DbContextHolder.setDbType("master");
		}
		super.doBegin(object, definition);
	}

	/**
	 * 事务执行完毕后，清除前面设置的数据源信息，回复默认读数据源
	 */
	@Override
	protected void doCleanupAfterCompletion(Object object) {
		// TODO Auto-generated method stub
		DbContextHolder.clearDbType(); // 清除切换数据源信息，默认为slave数据源
		super.doCleanupAfterCompletion(object);
	}

}
