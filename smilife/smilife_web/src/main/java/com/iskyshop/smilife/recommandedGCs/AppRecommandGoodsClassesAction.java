package com.iskyshop.smilife.recommandedGCs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iskyshop.smilife.common.Result;



/**
 * 获取APP2.0首页“分类”->“品类”页面中的所有数据
 * 
 * @author 吴荣龙
 * @date 2016年10月21日 下午10:47:40
 */
@Controller
@RequestMapping("/api/app")
public class AppRecommandGoodsClassesAction {
	
	@Autowired
	private IAppRecommandedGCsService appRecommandedGCsService;

	/**
     * 获取APP2.0首页“分类”->“品类”页面中的所有数据
     * @return
     */
	@RequestMapping(value = "/mall1100GetRecommandedGCsDetails.htm")
	@ResponseBody
	public Result getRecommandedGCsDetails() {
		Result result = new Result();
		result.setData(appRecommandedGCsService.getRecommandedGCsDetails());
		
		return result;
	}
}
