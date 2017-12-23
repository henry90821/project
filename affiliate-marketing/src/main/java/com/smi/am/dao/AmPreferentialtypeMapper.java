package com.smi.am.dao;

import com.smi.am.dao.model.AmPreferentialtype;
import com.smi.am.dao.model.AmPreferentialtypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AmPreferentialtypeMapper {
    int countByExample(AmPreferentialtypeExample example);

    int deleteByExample(AmPreferentialtypeExample example);

    int deleteByPrimaryKey(Integer ptId);

    int insert(AmPreferentialtype record);

    int insertSelective(AmPreferentialtype record);

    List<AmPreferentialtype> selectByExample(AmPreferentialtypeExample example);

    AmPreferentialtype selectByPrimaryKey(Integer ptId);

    int updateByExampleSelective(@Param("record") AmPreferentialtype record, @Param("example") AmPreferentialtypeExample example);

    int updateByExample(@Param("record") AmPreferentialtype record, @Param("example") AmPreferentialtypeExample example);

    int updateByPrimaryKeySelective(AmPreferentialtype record);

    int updateByPrimaryKey(AmPreferentialtype record);
}