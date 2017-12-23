package com.smi.am.service;

import com.smi.am.service.vo.HomePageVo;
import com.smi.am.service.vo.UserVo;
import com.smi.am.utils.SmiResult;

import net.bull.javamelody.MonitoredWithSpring;

/**
 * 首页接口
 * @author yanghailong
 *
 */
@MonitoredWithSpring
public interface IHomePageService {

	
	public SmiResult<HomePageVo> showHomePageInfo(UserVo userVo) throws Exception ;
	
}
