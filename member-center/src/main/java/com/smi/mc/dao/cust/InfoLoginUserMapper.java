package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.smi.mc.model.cust.InfoLoginUser;
import com.smi.mc.model.cust.InfoLoginUserExample;

public interface InfoLoginUserMapper {
    int countByExample(InfoLoginUserExample example);

    int deleteByExample(InfoLoginUserExample example);

    int deleteByPrimaryKey(String loginUserId);

    int insert(InfoLoginUser record);

    int insertSelective(InfoLoginUser record);

    List<InfoLoginUser> selectByExample(InfoLoginUserExample example);

    InfoLoginUser selectByPrimaryKey(String loginUserId);

    int updateByExampleSelective(@Param("record") InfoLoginUser record, @Param("example") InfoLoginUserExample example);

    int updateByExample(@Param("record") InfoLoginUser record, @Param("example") InfoLoginUserExample example);

    int updateByPrimaryKeySelective(InfoLoginUser record);

    int updateByPrimaryKey(InfoLoginUser record);
	
	List<Map<String, Object>> qryInfoLoginUser(Map<String, Object> param);

	List<Map<String, Object>> qryInfoLoginUserForCheck(Map<String, Object> param);

	int addInfoLoginUser(Map<String, Object> param);

	int updateInfoLoginUser(Map<String, Object> param);
	
	/**
	 * <p>Description: 根据账号到info_login_user表获取custId</p>
	 * @param loginUser
	 * @return List<String>
	 * @author yanghailong	
	 * @date 2016年11月1日
	 */
	List<String> getCustIdByLoginUser(String loginUser);
	
	
}