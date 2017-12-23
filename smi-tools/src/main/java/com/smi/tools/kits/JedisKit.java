/**
 * 文件名称：JedisUtil.java
 * 版权：CopyRight 2014-2015 Digione Tech. Co. Ltd. All Rigths Reserved. 
 * 描述：Jedis 操作工具类
 * 修改人：caols
 * 修改时间：2014-10-27 下午4:22:18
 * BugId: <修改单号>
 * 修改内容：
 */
package com.smi.tools.kits;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 功能描述：Jedis操作工具类， 用于获取RedisPool. <br/>
 * 作者： carson <br/>
 * 时间： 2015年2月15日下午3:21:32 <br/>
 * BugID:  <br/>
 * 修改内容： <br/>
 */
public final class JedisKit {

	private static Logger logger = Logger.getLogger(JedisKit.class);
	/**
	 * redis配置参数
	 */
	private static Map<String, String> redisConfigParams = new HashMap<String, String>();

	private static JedisPool pool = null;

	/**
	 * 生成连接池实例
	 */
	static {
		redisConfigParams = IoKit.get("redies.properties");

		try {
			JedisPoolConfig config = new JedisPoolConfig();

			String ip = redisConfigParams.get("redis.ip");
			int port = Integer.parseInt(redisConfigParams.get("redis.port"));

			// maxActive
			config.setMaxTotal(Integer.parseInt(redisConfigParams.get("jedis.pool.maxActive")));
			config.setMaxIdle(Integer.parseInt(redisConfigParams.get("jedis.pool.maxIdle")));
			// maxWait
			config.setMaxWaitMillis(Integer.parseInt(redisConfigParams.get("jedis.pool.maxWait")));
			config.setTestOnBorrow(false);
			config.setTestOnReturn(false);
			config.setMinEvictableIdleTimeMillis(1000L * 60L * Integer.parseInt(redisConfigParams.get("jedis.pool.evict_time"))); //空闲对象,空闲多长时间会被驱逐出池里
			config.setTimeBetweenEvictionRunsMillis(1000L * Integer.parseInt(redisConfigParams.get("jedis.pool.evict_interval"))); //驱逐线程30秒执行一次
			config.setNumTestsPerEvictionRun(-1); //-1,表示在驱逐线程执行时,测试所有的空闲对象

			/**
			 * 如果你遇到 java.net.SocketTimeoutException: Read timed out exception的异常信息 请尝试在构造JedisPool的时候设置自己的超时值.
			 * JedisPool默认的超时时间是2秒(单位毫秒)
			 */
			pool = new JedisPool(config, ip, port, Integer.parseInt(redisConfigParams.get("jedis.pool.timeout")));
		} catch (Exception e) {
			logger.error("生成Jedis连接池实例异常，信息如下：\n");
			logger.error(e);
		}
	}

	/**
	 * 获取redis的实例
	 * 
	 * @return redis实例
	 */
	public synchronized static Jedis getJedis() {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			logger.error("获取redis实例异常，信息：", e);
			if(jedis != null) {
				jedis.close();
			}
			return null;
		}
		return jedis;
	}

	/**
	 * 释放redis实例到连接池.
	 * 
	 * @param jedis
	 *            redis实例
	 */
	public static void closeJedis(Jedis jedis) {
		
		if (jedis != null) {
			jedis.close();
		}
	}

}
