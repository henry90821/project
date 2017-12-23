package com.iskyshop.manage.seller.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.StringUtils;

/**
 * 菜单管理工具，V1.3版开始使用，卖家中心快捷菜单采用json管理，该工具使用解析json数据
 * 
 * @author erikzhang
 * 
 */
@Component
public class MenuTools {
	public List<Map> generic_seller_quick_menu(String menu_json) {
		List<Map> list = new ArrayList<Map>();
		if (!StringUtils.isNullOrEmpty(menu_json)) {
			list = Json.fromJson(List.class, menu_json);
		}
		return list;
	}
}
