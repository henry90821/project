package com.iskyshop.smilife.recommandedGCs;

import java.util.List;
import java.util.Map;

public interface IAppRecommandedGCsService {
	/**
	 * 获取APP2.0首页“分类”->“品类”页面中的所有数据
	 * @return
	 */
	public List<Map> getRecommandedGCsDetails();
}
