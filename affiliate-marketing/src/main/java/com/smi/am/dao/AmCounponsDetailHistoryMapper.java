package com.smi.am.dao;

import com.smi.am.dao.model.AmCounponsDetailHistory;
import com.smi.am.dao.model.AmCounponsDetailHistoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AmCounponsDetailHistoryMapper {
    int countByExample(AmCounponsDetailHistoryExample example);

    int deleteByExample(AmCounponsDetailHistoryExample example);

    int deleteByPrimaryKey(Integer cdhDetailid);

    int insert(AmCounponsDetailHistory record);

    int insertSelective(AmCounponsDetailHistory record);

    List<AmCounponsDetailHistory> selectByExample(AmCounponsDetailHistoryExample example);

    AmCounponsDetailHistory selectByPrimaryKey(Integer cdhDetailid);

    int updateByExampleSelective(@Param("record") AmCounponsDetailHistory record, @Param("example") AmCounponsDetailHistoryExample example);

    int updateByExample(@Param("record") AmCounponsDetailHistory record, @Param("example") AmCounponsDetailHistoryExample example);

    int updateByPrimaryKeySelective(AmCounponsDetailHistory record);

    int updateByPrimaryKey(AmCounponsDetailHistory record);
}