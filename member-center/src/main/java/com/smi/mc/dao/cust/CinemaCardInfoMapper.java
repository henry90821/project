package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface CinemaCardInfoMapper {
	
	int cinemaCardInfoMod(Map<String,Object> param);
	
	List<Map<String,Object>> qryCinemaCardInfo(Map<String,Object> param);

}
