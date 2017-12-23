package com.smi.am.dao;

import com.smi.am.dao.model.AmSyslog;
import com.smi.am.dao.model.AmSyslogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AmSyslogMapper {
    int countByExample(AmSyslogExample example);

    int deleteByExample(AmSyslogExample example);

    int deleteByPrimaryKey(Integer slId);

    int insert(AmSyslog record);

    int insertSelective(AmSyslog record);

    List<AmSyslog> selectByExample(AmSyslogExample example);

    AmSyslog selectByPrimaryKey(Integer slId);

    int updateByExampleSelective(@Param("record") AmSyslog record, @Param("example") AmSyslogExample example);

    int updateByExample(@Param("record") AmSyslog record, @Param("example") AmSyslogExample example);

    int updateByPrimaryKeySelective(AmSyslog record);

    int updateByPrimaryKey(AmSyslog record);
}