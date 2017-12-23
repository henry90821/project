package com.smilife.core.service;

import net.bull.javamelody.MonitoredWithSpring;

/**
 * 用来约定Service层在值对象与数据实体对象之间转换的方法签名<br/>
 * Created by Andriy on 16/4/3.
 * 
 * @param <V>
 *            值对象
 * @param <E>
 *            数据实体对象
 */
@MonitoredWithSpring
public interface TransferObjectService<V, E> {

	/**
	 * 将值对象转换成数据实体
	 *
	 * @param vo
	 *            需要转换的值对象
	 * @return 返回对应的数据实体对象
	 */
	E transferToEntity(V vo);

	/**
	 * 将数据实体转换成值对象
	 *
	 * @param entity
	 *            需要转换的数据实体对象
	 * @return 返回对应的值对象
	 */
	V transferToValueObject(E entity);
}
