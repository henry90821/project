package com.smi.am.dao;

import com.smi.am.dao.model.AmRole;
import com.smi.am.dao.model.AmRoleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AmRoleMapper {
    int countByExample(AmRoleExample example);

    int deleteByExample(AmRoleExample example);

    int deleteByPrimaryKey(Integer rRoleid);

    int insert(AmRole record);

    int insertSelective(AmRole record);

    List<AmRole> selectByExample(AmRoleExample example);

    AmRole selectByPrimaryKey(Integer rRoleid);

    int updateByExampleSelective(@Param("record") AmRole record, @Param("example") AmRoleExample example);

    int updateByExample(@Param("record") AmRole record, @Param("example") AmRoleExample example);

    int updateByPrimaryKeySelective(AmRole record);

    int updateByPrimaryKey(AmRole record);
}