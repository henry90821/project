package com.iskyshop.view.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.database.DatabaseTools;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 程序安装控制器
 * 
 * @author erikzhang
 * 
 */
@Controller
public class InstallViewAction implements ServletContextAware {
	private Logger logger = Logger.getLogger(this.getClass());
	private ServletContext servletContext;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private DatabaseTools databaseTools;
	
	private final String lockFile = "/install.lock";
	private final String testSqlFile = "/resources/data/test.sql";
	private final String baseSqlFile = "/resources/data/base.sql";

	@RequestMapping("/install.htm")
	public ModelAndView install(HttpServletRequest request, HttpServletResponse response, String install_status,
			String title, String pws, String test_data) {
		ModelAndView mv = new JModelAndView("WEB-INF/templates/install/install1.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);
		if ("".equals(CommUtil.null2String(install_status))) {
			mv.addObject("install_status", "install1");
		}
		if ("install1".equals(CommUtil.null2String(install_status))) {
			mv = new JModelAndView("WEB-INF/templates/install/install2.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 2, request, response);
			mv.addObject("install_status", "install2");
		}
		if ("install2".equals(CommUtil.null2String(install_status))) {
			mv = new JModelAndView("WEB-INF/templates/install/install3.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 2, request, response);
			mv.addObject("install_status", "install3");
			mv.addObject("test_data", CommUtil.null2String(test_data));
			String shop_url = CommUtil.getURL(request);
			mv.addObject("shop_url", shop_url);
			mv.addObject("title", CommUtil.null2String(title));
			mv.addObject("pws", CommUtil.null2String(pws));
			String shop_manage_url = CommUtil.getURL(request) + "/admin/index.htm";
			mv.addObject("shop_manage_url", shop_manage_url);
		}
		mv.addObject("version", Globals.DEFAULT_SHOP_OUT_VERSION);
		return mv;
	}

	@RequestMapping("/install_over.htm")
	public ModelAndView install_over(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "您已经安装ISkyShop商城系统，重新安装请删除install.lock文件");
		return mv;
	}

	@RequestMapping("/install_view.htm")
	public ModelAndView install_view(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("WEB-INF/templates/install/install_view.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);
		return mv;
	}

	@RequestMapping("/install_init_test.htm")
	public void install_init_test(HttpServletRequest request, HttpServletResponse response, String title, String pws)
			throws Exception {
		String path = CommUtil.getServerRealPathFromRequest(request) + lockFile;
		File file = new File(path);
		if (!file.exists()) {
			String filePath = CommUtil.getServerRealPathFromRequest(request) + testSqlFile;
			File sql_file = new File(filePath);
			boolean ret = true;
			if (sql_file.exists()) {
				this.databaseTools.executSqlScript(filePath);
			}
			CacheManager manager = CacheManager.create();
			manager.clearAll();
			SysConfig config = this.configService.getSysConfig();
			config.setAddTime(new Date());
			config.setTitle(title);
			config.setWebsiteState(true);
			this.configService.update(config);
			User admin = this.userService.getObjByProperty(null, "userName", "admin");
			admin.setPassword(Md5Encrypt.md5(pws).toLowerCase());
			this.userService.update(admin);
			manager = CacheManager.create();
			manager.clearAll();
			// 创建资源文件夹,默认upload
			String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + "upload" + File.separator
					+ "cache";
			CommUtil.createFolder(saveFilePathName);
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				file.createNewFile();
				writer = response.getWriter();
				writer.print(true);
			} catch (IOException e) {
				logger.error(e);
			}
		} else {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print(false);
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	@RequestMapping("/install_init_base.htm")
	public void install_init_base(HttpServletRequest request, HttpServletResponse response, String title, String pws)
			throws Exception {
		String docPath = CommUtil.getServerRealPathFromRequest(request);
		logger.info("项目的跟路径为： " + docPath);
		String path = docPath  + lockFile;
		
		logger.info("安装控制文件路径为： " + path);
		File file = new File(path);
		if (!file.exists()) {
			// 导入基础SQL数据
			String filePath = docPath + baseSqlFile;
			logger.info("数据库初始化脚本文件： " + filePath);
			File sql_file = new File(filePath);
			if (sql_file.exists()) {
				this.databaseTools.executSqlScript(filePath);
			}
			CacheManager manager = CacheManager.create();
			manager.clearAll();
			SysConfig config = this.configService.getSysConfig();
			config.setAddTime(new Date());
			config.setTitle(title);
			config.setWebsiteState(true);
			this.configService.update(config);
			User admin = this.userService.getObjByProperty(null, "userName", "admin");
			admin.setPassword(Md5Encrypt.md5(pws).toLowerCase());
			this.userService.update(admin);
			manager = CacheManager.create();
			manager.clearAll();
			// 创建资源文件夹,默认upload
			String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + "upload" + File.separator
					+ "cache";
			CommUtil.createFolder(saveFilePathName);
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				file.createNewFile();
				writer.print(true);
			} catch (IOException e) {
				logger.error(e);
			}
		} else {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print(false);
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
