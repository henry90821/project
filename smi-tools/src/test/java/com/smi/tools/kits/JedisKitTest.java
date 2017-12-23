/**
 * 文件名称：RedisUtilTest.java
 * 版权：CopyRight 2014-2015 Digione Tech. Co. Ltd. All Rigths Reserved. 
 * 描述：
 * 修改人：Administrator
 * 修改时间：2014-10-27 下午6:42:59
 * BugId: <修改单号>
 * 修改内容：
 */
package com.smi.tools.kits;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import redis.clients.jedis.Jedis;

/**
 * 功能描述：
 * @author : Administrator
 * @version ：v1.0
 * @time： 2014-10-27 下午6:42:59
 */
public class JedisKitTest {
	
	Logger logger = Logger.getLogger(this.getClass());
	
	Jedis jedis = null;
	
	@Before
	public void before() throws Exception {
		jedis = JedisKit.getJedis();
	}

	@Test
	public void testSet() {
		
		for (int i = 0; i < 10; i++) {
			//jedis.set("test" + i, "from java client "  + i);
			logger.info(jedis.get("test" + i));
			
		}
	}
	
	@Test
	public void testGet() {
		//jedis.set("test", "from java client");
		
		//String use = jedis.hget("userLoginFail", "use");
		
		//logger.info("use =====> " + use);
		
		 //jedis.keys
		Set<String> keys = jedis.keys("*");
		for (String key : keys) {
			logger.info(key);
			logger.info(jedis.hgetAll(key));
		}
		
	}
	
	@Test
	public void testClear() {
		
		Set<String> keys = jedis.keys("*");
		for (String key : keys) {
			logger.info(key);
			jedis.del(key);
		}
	}
	
	/**
	 * 通过hash存储数据
	 */
	@Test
	public void testHashSet() {
		
		Address add1 = new Address(1, "深南大道128号 三楼");
		Address add2 = new Address(1, "梅坂大道 滢水 三楼");
		
		jedis.hset("address", "home", JsonKit.toJsonString(add2));
		jedis.hset("address", "office", JsonKit.toJsonString(add1));
		
		jedis.expire("address", 10);
		
		long seconds = jedis.ttl("address");
		
		//logger.info(jedis.hlen("address"));
		
		logger.info(seconds);
		
		
	}
	
	/**
	 * 从hash中获取数据
	 */
	@Test
	public void testHashGet() {
		
		//jedis.hdel("address", "home");
		
		String homeJson = jedis.hget("address", "home");
		logger.info("homeJson =====> " + homeJson);
		
	}
	
	@Test
	public void testHashGet2() {
		
		String sql = jedis.hget("user", "sql1");
		logger.info("sql =====> " + sql);
		
	}
	
	@Test
	public void testHashGet3() {
		
		Map<String, String> appReqo = jedis.hgetAll("1002");
		Set<String > keySet = appReqo.keySet();
		for(String key : keySet) {
			System.out.println(key + " => " + appReqo.get(key));
		}
		
	}
	
	@Test
	public void testHashExists() {
		
		boolean exist = jedis.hexists("1002", "11233266514442");
		logger.info("exist =====> " + exist);
		
	}
	
	/**
	 * redis 模拟 栈
	 */
	@Test
	public void testRedisStackPush() {
		
		jedis.lpush("color", "红色");
		jedis.lpush("color", "蓝色");
		jedis.lpush("color", "绿色");
		
		Assert.assertEquals("绿色", jedis.lpop("color"));
		Assert.assertEquals("蓝色", jedis.lpop("color"));
		Assert.assertEquals("红色", jedis.lpop("color"));
	}
	
	/**
	 * redis 模拟 先进先出队列 
	 */
	@Test
	public void testRedisFIFO() {
		jedis.lpush("color", "红色");
		jedis.lpush("color", "蓝色");
		jedis.lpush("color", "绿色");
		
		
		
		Assert.assertEquals("红色", jedis.rpop("color"));
		Assert.assertEquals("蓝色", jedis.rpop("color"));
		Assert.assertEquals("绿色", jedis.rpop("color"));
	}
	
	
	@After
	public void after() {
		JedisKit.closeJedis(jedis);
	}
	
	public class Address {
		private int id;
		private String location;
		
		public Address(int id, String location) {
			this.id = id;
			this.location = location;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}
	}
	
}
