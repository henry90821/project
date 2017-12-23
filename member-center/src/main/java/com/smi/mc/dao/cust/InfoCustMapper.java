package com.smi.mc.dao.cust;

import com.smi.mc.model.cust.InfoCust;
import com.smi.mc.model.cust.InfoCustExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface InfoCustMapper {
    int countByExample(InfoCustExample example);

    int deleteByExample(InfoCustExample example);

    int deleteByPrimaryKey(String custId);

    int insert(InfoCust record);

    int insertSelective(InfoCust record);

    List<InfoCust> selectByExample(InfoCustExample example);

    InfoCust selectByPrimaryKey(String custId);

    int updateByExampleSelective(@Param("record") InfoCust record, @Param("example") InfoCustExample example);

    int updateByExample(@Param("record") InfoCust record, @Param("example") InfoCustExample example);

    int updateByPrimaryKeySelective(InfoCust record);

    int updateByPrimaryKey(InfoCust record);
    
    /**
     * <p>Description: 根据custId获取随机数</p>
     * @param custId
     * @return String
     * @author yanghailong	
     * @date 2016年11月1日
     */
    String getRndNumByCustId(String custId);
}