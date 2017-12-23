package com.smi.am.dao;

import com.smi.am.dao.model.AmActivityShop;
import com.smi.am.dao.model.AmActivityShopExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AmActivityShopMapper {
    int countByExample(AmActivityShopExample example);

    int deleteByExample(AmActivityShopExample example);

    int deleteByPrimaryKey(Integer asId);

    int insert(AmActivityShop record);

    int insertSelective(AmActivityShop record);

    List<AmActivityShop> selectByExample(AmActivityShopExample example);

    AmActivityShop selectByPrimaryKey(Integer asId);

    int updateByExampleSelective(@Param("record") AmActivityShop record, @Param("example") AmActivityShopExample example);

    int updateByExample(@Param("record") AmActivityShop record, @Param("example") AmActivityShopExample example);

    int updateByPrimaryKeySelective(AmActivityShop record);

    int updateByPrimaryKey(AmActivityShop record);
}