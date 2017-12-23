package com.iskyshop.manage.admin.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;

/**
 * 
 * <p>
 * Title: StoreTools.java
 * </p>
 * 
 * <p>
 * Description: 后台管理店铺、商品工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-5-21
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component
public class StoreTools {
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ISysConfigService configService;

	/**
	 * 生成商品属性字符串
	 * 
	 * @param spec
	 * @return
	 */
	public String genericProperty(GoodsSpecification spec) {
		String val = "";
		for (GoodsSpecProperty gsp : spec.getProperties()) {
			val = val + "," + gsp.getValue();
		}
		if (!"".equals(val))
			return val.substring(1);
		else
			return "";
	}

	/**
	 * 根据系统规则建立图片存储文件夹
	 * 
	 * @param request
	 * @param config
	 * @param store
	 * @return
	 */
	public String createUserFolder(HttpServletRequest request, Store store) {
		SysConfig config = this.configService.getSysConfig();
		String uploadFilePath = config.getUploadFilePath();
		String path = CommUtil.getServerRealPathFromRequest(request)
				+ uploadFilePath + File.separator + "cache";
		CommUtil.createFolder(path);
		return path;
	}

	public String createUserFolderURL(Store store) {
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		if ("sidImg".equals(config.getImageSaveType())) { // 按照文件名存放(例:/店铺id/图片)
			path = "store/" + store.getId().toString();

		}
		if ("sidYearImg".equals(config.getImageSaveType())) { // 按照年份存放(例:/店铺id/年/图片)
			path = "store/" + store.getId() + "/"
					+ CommUtil.formatTime("yyyy", new Date());
		}
		if ("sidYearMonthImg".equals(config.getImageSaveType())) { // 按照年月存放(例:/店铺id/年/月/图片)
			path = "store/" + store.getId() + "/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date());
		}
		if ("sidYearMonthDayImg".equals(config.getImageSaveType())) { // 按照年月日存放(例:/店铺id/年/月/日/图片)
			path = "store/" + store.getId() + "/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date()) + "/"
					+ CommUtil.formatTime("dd", new Date());
		}
		return path;
	}

	/**
	 * 根据分类生成分类信息字符串
	 * 
	 * @param gc
	 * @return
	 */
	public String generic_goods_class_info(GoodsClass gc) {
		if (gc != null) {
			String goods_class_info = this.generic_the_goods_class_info(gc);
			return goods_class_info.substring(0, goods_class_info.length() - 1);
		} else
			return "";
	}

	private String generic_the_goods_class_info(GoodsClass gc) {
		if (gc != null) {
			String goods_class_info = gc.getClassName() + ">";
			if (gc.getParent() != null) {
				String class_info = generic_the_goods_class_info(gc.getParent());
				goods_class_info = class_info + goods_class_info;
			}
			return goods_class_info;
		} else
			return "";
	}

	public String query_user_store_url(String user_id, String url) {
		Store store = this.storeService.getObjByProperty(
				"new Store(id,addTime,store_second_domain,store_status)", "user.id",
				CommUtil.null2Long(user_id));
		if (store != null && store.getStore_status() >= 15) {
			String store_url = url + "/store_" + store.getId() + ".htm";
			if (this.configService.getSysConfig().isSecond_domain_open()
					&& !"".equals(CommUtil.null2String(store
							.getStore_second_domain()))) {
				String serverName = url.toLowerCase();
				String secondDomain = CommUtil.null2String(serverName
						.substring(0, serverName.indexOf(".")));
				if (serverName.indexOf(".") == serverName.lastIndexOf(".")) {
					secondDomain = "www";
				}
				store_url = "http://" + store.getStore_second_domain() + "."
						+ secondDomain;
			}
			return store_url;
		}
		return null;
	}

	/**
	 * 解析json数据，传入m_id,返回该m_id存在的map,如果对应的map不存在则返回null。 [{"gc_list":[2, 8,
	 * 35],"m_id":1},{"gc_list":[2, 8, 35],"m_id":5},{"gc_list":[2, 8,
	 * 35],"m_id":9}]
	 * 
	 * @param json
	 * @return
	 */
	public Map query_MainGc_Map(String m_id, String json) {
		Map map_temp = null;
		if (!StringUtils.isNullOrEmpty(json)) {
			List<Map> list_map = Json.fromJson(ArrayList.class, json);
			for (Map map : list_map) {
				if (m_id.equals(CommUtil.null2String(map.get("m_id")))) {
					map_temp = map;
					break;
				}
			}
		}
		return map_temp;
	}

	/**
	 * 解析店铺详细经营类目中的所有一级类目，该工具方法返回所有详细类目对应的父级类目集合 * [{"gc_list":[2, 8,
	 * 35],"m_id":1},{"gc_list":[2, 8, 35],"m_id":5},{"gc_list":[2, 8,
	 * 35],"m_id":9}]
	 * 
	 * @param json
	 * @return
	 */
	public List<GoodsClass> query_store_detail_MainGc(String json) {
		List<GoodsClass> gc_list = new ArrayList<GoodsClass>();
		if (!StringUtils.isNullOrEmpty(json)) {
			List<Map> list_map = Json.fromJson(ArrayList.class, json);
			for (Map map : list_map) {
				GoodsClass gc = this.goodsClassService.getObjById(CommUtil
						.null2Long(map.get("m_id")));
				if (gc != null) {
					gc_list.add(gc);
				}
			}
		}
		return gc_list;
	}

	/**
	 * 解析店铺详细经营类目中的所有二级详细类目，该工具方法返回所有详细类目集合 * [{"gc_list":[2, 8,
	 * 35],"m_id":1},{"gc_list":[2, 8, 35],"m_id":5},{"gc_list":[2, 8,
	 * 35],"m_id":9}]
	 * 
	 * @param json
	 * @return
	 */
	public Set<GoodsClass> query_store_DetailGc(String json) {
		Set<GoodsClass> gc_list = new TreeSet<GoodsClass>();
		if (!StringUtils.isNullOrEmpty(json)) {
			List<Map> all_list = Json.fromJson(ArrayList.class, json);
			for (Map map : all_list) {
				List<Integer> ls = (List) map.get("gc_list");
				if (!StringUtils.isNullOrEmpty(ls)) {
					for (Integer l : ls) {
						GoodsClass gc = this.goodsClassService
								.getObjById(CommUtil.null2Long(l));
						if(gc != null) {
							gc_list.add(gc);
						}
					}
				}
			}
		}
		return gc_list;
	}

	/**
	 * 本版本为集群版，所有资源文件上传到ftp之前需要上传到服务器缓存文件夹cache中，
	 * 
	 * @param request
	 * @param config
	 * @param store
	 * @return
	 */
	public String createAdminFolder(HttpServletRequest request) {
		SysConfig config = this.configService.getSysConfig();
		String uploadFilePath = config.getUploadFilePath();
		String path = CommUtil.getServerRealPathFromRequest(request)
				+ uploadFilePath + File.separator + "cache";
		CommUtil.createFolder(path);
		return path;
	}

	/**
	 * 自营商品图片保存路径
	 * 
	 * @return
	 */
	public String createAdminFolderURL() {
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		if ("sidImg".equals(config.getImageSaveType())) { // 按照文件名存放(例:/system/self_goods/图片)
			path = "system/self_goods";

		}
		if ("sidYearImg".equals(config.getImageSaveType())) { // 按照年份存放(例:/system/self_goods/年/图片)
			path = "system/self_goods/"
					+ CommUtil.formatTime("yyyy", new Date());
		}
		if ("sidYearMonthImg".equals(config.getImageSaveType())) { // 按照年月存放(例:/system/self_goods/年/月/图片)
			path = "system/self_goods/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date());
		}
		if ("sidYearMonthDayImg".equals(config.getImageSaveType())) { // 按照年月日存放(例:/system/self_goods/年/月/日/图片)
			path = "system/self_goods/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date()) + "/"
					+ CommUtil.formatTime("dd", new Date());
		}
		return path;
	}

	public Set<GoodsClass> query_m_id(String json){
		Set<GoodsClass> gc_list = new TreeSet<GoodsClass>();
		if (!StringUtils.isNullOrEmpty(json)) {
			List<Map> all_list = Json.fromJson(ArrayList.class, json);
			for (Map map : all_list) {
				int ls =  (int) map.get("m_id");
				if (!StringUtils.isNullOrEmpty(ls)) {
					GoodsClass gc = this.goodsClassService
							.getObjById(CommUtil.null2Long(ls));
					if(gc != null) {
						gc_list.add(gc);
					}
				}
			}
		}
		return gc_list;
	}
}
