package com.smi.mc.api.utils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.smi.mc.api.common.MemberCenterConfiguration;
import com.smi.mc.api.valueobject.InfoCustVo;
import com.smi.mc.api.valueobject.MemberInfoVo;
import com.smi.mc.api.valueobject.enums.BlackListEnum;
import com.smi.mc.api.valueobject.enums.CertiTypeEnum;
import com.smi.mc.api.valueobject.enums.CustTypeEnum;
import com.smi.mc.api.valueobject.enums.CustVipLevelEnum;
import com.smi.mc.api.valueobject.enums.SexEnum;
import com.smi.tools.kits.StrKit;

/**
 * redis工具类
 * @author yanghailong
 *
 */
@SuppressWarnings("unchecked")
@Component
public class RedisUtil {
	@SuppressWarnings("rawtypes")
	@Autowired
	private  RedisTemplate redisTemplate;
	
	@Autowired
	private MemberCenterConfiguration memberCenterConfiguration;

	/**
	 * 批量删除对应的value
	 * 
	 * @param keys
	 */
	public  void remove(final String... keys) {
		for (String key : keys) {
			remove(key);
		}
	}

	/**
	 * 批量删除key
	 * 
	 * @param pattern
	 */
	public  void removePattern(final String pattern) {
		Set<String> keys = redisTemplate.keys(pattern);
		if (keys.size() > 0)
			redisTemplate.delete(keys);
	}

	/**
	 * 删除对应的value
	 * 
	 * @param key
	 */
	public  void remove(final String key) {
		if (exists(key)) {
			redisTemplate.delete(key);
		}
	}

	/**
	 * 判断缓存中是否有对应的value
	 * 
	 * @param key
	 * @return
	 */
	public  boolean exists(final String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 读取缓存
	 * 
	 * @param key
	 * @return
	 */
	public  Object get(final String key) {
		Object result = null;
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		result = operations.get(key);
		return result;
	}

	/**
	 * 写入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public  boolean set(final String key, Object value) {
		boolean result = false;
		try {
			ValueOperations<String, Object> operations = redisTemplate.opsForValue();
			operations.set(key, value);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 写入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public  boolean set(final String key, Object value, Long expireTime) {
		boolean result = false;
		try {
			ValueOperations<String, Object> operations = redisTemplate.opsForValue();
			operations.set(key, value);
			redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据token到redis缓存获取用户custId
	 * @param token
	 * @return
	 */
	public String getInfoCustIdByToken(String token){
		//base64解密token
		byte[] decodeToken = Base64Utils.decodeFromString(token);
		String tokenStr = StrKit.str(decodeToken, "UTF8");
		return (String)get(memberCenterConfiguration.getTokenPrefix() + tokenStr);
	}
	
	/**
	 * 更新Key的失效时间
	 * @param key 键值
	 * @param expireTime 时间
	 * @return
	 */
	public boolean expire(final String key,Long expireTime){
		return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
	}
	
	/**
	 * 会员中心返回实体转成MemberInfoVo
	 * @param infoCustVo
	 * @return
	 */
	public  MemberInfoVo assembleEntity(InfoCustVo infoCustVo){
		MemberInfoVo memberInfoVo = new MemberInfoVo();
		memberInfoVo.setCustVipLevel(CustVipLevelEnum.getEnumByCode(infoCustVo.getCustVipLevel()));
		memberInfoVo.setSexCode(SexEnum.getEnumByCode((infoCustVo.getSex())));
		memberInfoVo.setCustTypeCode(CustTypeEnum.getEnumByCode(infoCustVo.getCustType()));
		memberInfoVo.setCertiTypeCode(CertiTypeEnum.getEnumByCode(infoCustVo.getCertiType()));
		memberInfoVo.setBlackListCode(BlackListEnum.getEnumByCode(infoCustVo.getBlacklist()));
		memberInfoVo.setCustId(infoCustVo.getCustId());
		memberInfoVo.setCustName(infoCustVo.getCustName());
		memberInfoVo.setCustNbr(infoCustVo.getCustNbr());
		memberInfoVo.setNickName(infoCustVo.getNickName());
		memberInfoVo.setCertiAddr(infoCustVo.getCertiAddr());
		memberInfoVo.setMobile(infoCustVo.getContactMobile());
		memberInfoVo.setCertiNbr(infoCustVo.getCertiNbr());
		memberInfoVo.setEmail(infoCustVo.getEmail());
		memberInfoVo.setBirthdate(infoCustVo.getBirthdate());
		memberInfoVo.setJoinDate(infoCustVo.getCrtDate());
		memberInfoVo.setChannelCode(infoCustVo.getOrgId());
		memberInfoVo.setContactAddr(infoCustVo.getContactAddr());
		memberInfoVo.setIssync(infoCustVo.getIssync());
		memberInfoVo.setModDate(infoCustVo.getModDate());
		return memberInfoVo;
	}
}