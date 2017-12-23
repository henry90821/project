package com.smi.am.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.smi.am.service.vo.WraparoundResVo;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.valueobject.BaseValueObject;

public interface IReviewedMangeService {
	
	SmiResult<List<WraparoundResVo>> reviewMange(Map<String,Object> param,HttpServletRequest requset);
	

	BaseValueObject reviewed(Map<String,Object> param,HttpServletRequest requset);
}
 