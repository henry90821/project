package com.smi.mc.dao.cust;

import com.smi.mc.model.cust.InfoCerti;
import com.smi.mc.model.cust.InfoCertiExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface InfoCertiMapper {
    int countByExample(InfoCertiExample example);

    int deleteByExample(InfoCertiExample example);

    int deleteByPrimaryKey(String certiId);

    int insert(InfoCerti record);

    int insertSelective(InfoCerti record);

    List<InfoCerti> selectByExample(InfoCertiExample example);

    InfoCerti selectByPrimaryKey(String certiId);

    int updateByExampleSelective(@Param("record") InfoCerti record, @Param("example") InfoCertiExample example);

    int updateByExample(@Param("record") InfoCerti record, @Param("example") InfoCertiExample example);

    int updateByPrimaryKeySelective(InfoCerti record);

    int updateByPrimaryKey(InfoCerti record);
    
    /**
     * <p>Description:根据custId获取证件信息 </p>
     * @param custId
     * @return InfoCerti
     * @author yanghailong	
     * @date 2016年11月1日
     */
    InfoCerti getInfoCertiByCustId(String custId);
    
}