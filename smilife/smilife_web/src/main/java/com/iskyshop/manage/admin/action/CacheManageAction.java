package com.iskyshop.manage.admin.action;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.BlockingCache;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 系统缓存管理控制器
 * 
 * @author erikchang
 * 
 */
@Controller
public class CacheManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;

	@SecurityMapping(title = "缓存列表", value = "/admin/cache_list.htm*", rtype = "admin", rname = "更新缓存", rcode = "cache_manage", rgroup = "工具")
	@RequestMapping("/admin/cache_list.htm")
	public ModelAndView cache_list(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/cache_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CacheManager manager = CacheManager.create();
		BlockingCache cache = new BlockingCache(manager.getEhcache("SimplePageFragmentCachingFilter"));
		int data_cache_size = 0;
		long cache_memory_size = 0;
		for (String name : manager.getCacheNames()) {
			data_cache_size = data_cache_size + (manager.getCache(name) != null ? manager.getCache(name).getSize() : 0);
			cache_memory_size = cache_memory_size
					+ (manager.getCache(name) != null ? manager.getCache(name).getMemoryStoreSize() : 0);
		}
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String path = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache" + File.separator;
		File Directory = new File(path);
		double folder_bate = this.Getfoldersize(Directory);
		double folder_m = folder_bate / 1024 / 1024;
		if (folder_m != 0) {
			mv.addObject("folder_cache_size", CommUtil.null2String(folder_m).substring(0, 4));
		}
		mv.addObject("cache_memory_size", cache_memory_size);
		mv.addObject("data_cache_size", data_cache_size);
		mv.addObject("page_cache_size", cache.getSize());
		return mv;
	}

	@SecurityMapping(title = "更新缓存", value = "/admin/update_cache.htm*", rtype = "admin", rname = "更新缓存", rcode = "cache_manage", rgroup = "工具")
	@RequestMapping("/admin/update_cache.htm")
	public ModelAndView update_cache(HttpServletRequest request, HttpServletResponse response, String data_cache,
			String page_cache, String folder_cache) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		CacheManager manager = CacheManager.create();
		String[] names = manager.getCacheNames();
		if (CommUtil.null2Boolean(data_cache)) {
			for (String name : names) {
				if (!"SimplePageCachingFilter".equalsIgnoreCase(name)) {
					manager.clearAllStartingWith(name);
				}
			}
		}
		// manager.clearAll();
		if (CommUtil.null2Boolean(page_cache)) {
			Ehcache cache = manager.getEhcache("SimplePageCachingFilter");
			manager.clearAllStartingWith("SimplePageCachingFilter");
		}
		if (CommUtil.null2Boolean(folder_cache)) {
			String path = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache"
					+ File.separator;
			this.delAllFile(path);
		}
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/cache_list.htm");
		mv.addObject("op_title", "更新缓存成功");
		return mv;
	}

	// 获取文件夹内所有文件大小
	private double Getfoldersize(File f) {
		double size = 0;
		File flist[] = f.listFiles();
		if (!StringUtils.isNullOrEmpty(flist)) {
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					size = size + Getfoldersize(flist[i]);
				} else {
					size = size + flist[i].length();
				}
			}
		}
		return size;
	}

	// 删除文件夹内所有文件， 文件夹完整绝对路径
	private void delAllFile(String path) {
		File file = new File(path);
		String[] tempList = file.list();
		File temp = null;
		if (!StringUtils.isNullOrEmpty(tempList)) {
			for (int i = 0; i < tempList.length; i++) {
				if (path.endsWith(File.separator)) {
					temp = new File(path + tempList[i]);
				} else {
					temp = new File(path + File.separator + tempList[i]);
				}
				if (temp.isFile()) {
					temp.delete();
				}
				if (temp.isDirectory()) {
					delAllFile(path + File.separator + tempList[i]); // 先删除文件夹里面的文件
					delFolder(path + File.separator + tempList[i]); // 再删除空文件夹
				}
			}
		}
	}

	// 删除文件夹， 文件夹完整绝对路径
	private void delFolder(String folderPath) {
		try {
			this.delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
