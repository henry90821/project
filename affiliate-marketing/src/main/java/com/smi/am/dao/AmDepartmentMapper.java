package com.smi.am.dao;

import com.smi.am.dao.model.AmDepartment;
import com.smi.am.dao.model.AmDepartmentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AmDepartmentMapper {
    int countByExample(AmDepartmentExample example);

    int deleteByExample(AmDepartmentExample example);

    int deleteByPrimaryKey(Integer dDepartmentid);

    int insert(AmDepartment record);

    int insertSelective(AmDepartment record);

    List<AmDepartment> selectByExample(AmDepartmentExample example);

    AmDepartment selectByPrimaryKey(Integer dDepartmentid);

    int updateByExampleSelective(@Param("record") AmDepartment record, @Param("example") AmDepartmentExample example);

    int updateByExample(@Param("record") AmDepartment record, @Param("example") AmDepartmentExample example);

    int updateByPrimaryKeySelective(AmDepartment record);

    int updateByPrimaryKey(AmDepartment record);
}