package com.smi.mc.dao.cust;

import java.util.Map;

/**
 * 会员卡定义表
 * @author yanghailong
 *
 */
public interface CodeCardMapper {

	Map<String,Object> getOfferId(Map<String,Object> param) ;
	
	String getCardName() ;
	
}
