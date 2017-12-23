package com.iskyshop.manage.ftp.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.FTPServer;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.FTPServerQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IFTPServerService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.impl.UserServiceImpl;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * 
 * <p>
 * Title: FTPServerManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统FTP服务器管理类，添加、保存、修改、删除FTP服务器信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author hezeng
 * 
 * @version iskyshop_b2b2c_colony v1.0集群版
 */
@Controller
public class FTPServerManageAction {
	private static Logger logger = Logger.getLogger(FTPServerManageAction.class);
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IFTPServerService ftpserverService;
	@Autowired
	private IAccessoryService accessoryService;

	/**
	 * FTP服务器列表
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "FTP服务器列表", value = "/admin/ftpserver_list.htm*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/admin/ftpserver_list.htm")
	public ModelAndView ftpserver_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String type) {
		ModelAndView mv = new JModelAndView("admin/blue/ftpserver_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		FTPServerQueryObject qo = new FTPServerQueryObject(currentPage, mv, orderBy, orderType);
		if (!StringUtils.isNullOrEmpty(type)) {
			qo.addQuery("obj.ftp_type", new SysMap("ftp_type", CommUtil.null2Int(type)), "=");
			mv.addObject("type", type);
		} else {
			qo.addQuery("obj.ftp_type", new SysMap("ftp_type", 0), "=");
		}
		qo.setOrderBy("ftp_sequence");
		qo.setOrderType("asc");
		IPageList pList = this.ftpserverService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		
		List arrList = new ArrayList();
		List list = pList.getResult();
		for (Object object : list) {
			FTPServer ftpServer = (FTPServer) object;
  			String strSql = "SELECT COUNT(*) FROM ISKYSHOP_USER T WHERE T.`FTP_ID`=" + ftpServer.getId();
			int count = this.userService.getCount(strSql, null);
			ftpServer.setUser_Count(count);
			arrList.add(ftpServer);
		}
		mv.addObject("arrList", arrList);
		return mv;
	}

	/**
	 * FTP服务器添加
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "FTP服务器添加", value = "/admin/ftpserver_add.htm*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/admin/ftpserver_add.htm")
	public ModelAndView ftpserver_add(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/ftpserver_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * FTP服务器编辑
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "FTP服务器编辑", value = "/admin/ftpserver_edit.htm*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/admin/ftpserver_edit.htm")
	public ModelAndView ftpserver_edit(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/ftpserver_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!StringUtils.isNullOrEmpty(id)) {
			FTPServer ftpserver = this.ftpserverService.getObjById(Long.parseLong(id));
			mv.addObject("obj", ftpserver);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * FTP服务器保存
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "FTP服务器保存", value = "/admin/ftpserver_save.htm*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/admin/ftpserver_save.htm")
	public ModelAndView ftpserver_save(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String ftp_amount) {
		WebForm wf = new WebForm();
		String url = CommUtil.getURL(request);

		FTPServer ftpserver = null;
		if ("".equals(id)) {
			ftpserver = wf.toPo(request, FTPServer.class);
			ftpserver.setAddTime(new Date());
			String addr = ftpserver.getFtp_addr();
			if (addr != null && addr.endsWith("/")) {
				ftpserver.setFtp_addr(addr.substring(0, addr.length() - 1));
			}
		} else {
			FTPServer obj = this.ftpserverService.getObjById(Long.parseLong(id));
			ftpserver = (FTPServer) wf.toPo(request, obj);
		}
		if (StringUtils.isNullOrEmpty(ftp_amount)) {
			ftpserver.setFtp_amount(100);
		}
		if ("".equals(id)) {
			this.ftpserverService.save(ftpserver);
		} else {
			this.ftpserverService.update(ftpserver);
		}
		// 新添加的系统服务器若设置为当前使用，将其他系统服务器设置为非当前使用
		if (ftpserver.getFtp_type() == 1 && ftpserver.getFtp_system() == 1) {
			Map<String, Number> map = new HashMap<String, Number>();
			map.put("ftp_system", 1);
			map.put("ftp_id", ftpserver.getId());
			map.put("ftp_type", 1);
			List<FTPServer> objs = this.ftpserverService
					.query("select obj from FTPServer obj where obj.ftp_system=:ftp_system and obj.ftp_type=:ftp_type and obj.id!=:ftp_id",
							map, -1, -1);
			for (FTPServer obj : objs) {
				obj.setFtp_system(0);
				this.ftpserverService.update(obj);
			}
		}
		SysConfig config = this.configService.getSysConfig();
		String uploadFilePath = config.getUploadFilePath();
		String path = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "cache";
		CommUtil.createFolder(path); // 创建缓存目录
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", url + "/admin/ftpserver_list.htm?currentPage=" + currentPage);
		mv.addObject("op_title", "保存服务器成功");
		mv.addObject("add_url", url + "/admin/ftpserver_add.htm");
		return mv;
	}

	/**
	 * FTP服务器删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "FTP服务器删除", value = "/admin/ftpserver_del.htm*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/admin/ftpserver_del.htm")
	public String ftpserver_del(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage, String type) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!"".equals(id)) {
				FTPServer ftpserver = this.ftpserverService.getObjById(Long.parseLong(id));
				this.ftpserverService.delete(Long.parseLong(id));
			}
		}
		return "redirect:ftpserver_list.htm?currentPage=" + currentPage + "&type=" + type;
	}

	@SecurityMapping(title = "FTP服务器用户转移", value = "/admin/ftpserver_transfer.htm*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/admin/ftpserver_transfer.htm")
	public ModelAndView ftpserver_transfer(HttpServletRequest request, HttpServletResponse response, String fid,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/ftpserver_transfer.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		FTPServer ftp_from = this.ftpserverService.getObjById(CommUtil.null2Long(fid));
		Map params = new HashMap();
		params.put("ftp_type", 0);
		params.put("ftp_id", CommUtil.null2Long(fid));
		List<FTPServer> objs = this.ftpserverService
				.query("select obj from FTPServer obj where obj.ftp_type=:ftp_type and obj.id!=:ftp_id and obj.ftp_users.size<obj.ftp_amount",
						params, -1, -1);
		mv.addObject("objs", objs);
		mv.addObject("ftp_from", ftp_from);
		return mv;
	}

	@SecurityMapping(title = "FTP服务器用户转移保存", value = "/admin/ftpserver_transfer_save.htm*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/admin/ftpserver_transfer_save.htm")
	public void ftpserver_transfer_save(HttpServletRequest request, HttpServletResponse response, String from_id,
			String to_id) {
		FTPServer from_ftp = this.ftpserverService.getObjById(CommUtil.null2Long(from_id));
		FTPServer to_ftp = this.ftpserverService.getObjById(CommUtil.null2Long(to_id));
		int count = 0;
		int last = to_ftp.getFtp_amount() - to_ftp.getFtp_users().size();
		for (User user : from_ftp.getFtp_users()) {
			if (count < last) {
				count++;
				user.setFtp(to_ftp);
				boolean flag = this.userService.update(user);
				String newChar = to_ftp.getFtp_addr();
				String oldChar = from_ftp.getFtp_addr();
				if (flag) {
					for (Accessory acc : user.getFiles()) { // 更新用户上传所有附件的FTP路径
						String new_path = acc.getPath().replace(oldChar, newChar);
						acc.setPath(new_path);
						this.accessoryService.update(acc);
					}
				}
			} else {
				break;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(count);
		} catch (IOException e) {
			logger.error("FTP服务器用户转移保存异常: " + e);
		}
	}

	@SecurityMapping(title = "FTP服务器测试", value = "/admin/ftpserver_test.htm*", rtype = "admin", rname = "FTP设置", rcode = "admin_set_ftp", rgroup = "设置")
	@RequestMapping("/admin/ftpserver_test.htm")
	public void ftpserver_test(HttpServletRequest request, HttpServletResponse response, String id) throws IOException {
		int uploadCode = -100; // 上传code，100上传成功，-100上传失败
		int downloadCode = -100; // 下载code，100下载成功，-100下载失败
		int resultCode = -100; // 100FTP测试成功，-100FTP测试失败
		String fileName = "";
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "cache";
		Map map = new HashMap();
		try {
			map = CommUtil.saveFileToServer(request, "acc_test", saveFilePathName, "", null);
			if (map.get("fileName") != "") {
				fileName = CommUtil.null2String(map.get("fileName"));
				uploadCode = this.ImageUpload(id, fileName, "/ftpTest");
			}
		} catch (IOException e) {
			logger.error("保存文件到ftp服务器异常：" + e);
		}
		if (uploadCode == 100) { // 上传成功后进行下载
			if (!StringUtils.isNullOrEmpty(fileName)) {
				uploadCode = this.ImageDownload(id, "/ftpTest", fileName);
				if (uploadCode == 100) { // 比较上传与下载的文件是否一致
					String fileUrl = saveFilePathName + File.separator + fileName;
					if (CommUtil.fileExist(fileUrl)) {
						FileInputStream fis = new FileInputStream(fileUrl);
						if (fis.available() > 0) {
							downloadCode = 100; // 下载成功
						}
					}
				}
			}
			if (downloadCode == 100) { // 上传成功的同时，下载也同样成功，证明FTP调试成功
				resultCode = 100;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(resultCode);
		} catch (IOException e) {
			logger.error("FTP服务器测试异常:" + e);
		}
	}

	public int ImageUpload(String ftp_id, String imgName, String SaveUrl) {
		int uploadCode = 100; // 100上传成功，-100上传失败！
		SaveUrl = this.configService.getSysConfig().getUploadFilePath() + SaveUrl;
		FTPServer ftp = this.ftpserverService.getObjById(CommUtil.null2Long(ftp_id));
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String WebPath = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache"
				+ File.separator + imgName;
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			if (ftp != null) {
				ftpClient.connect(ftp.getFtp_ip());
				ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
				// 设置上传目录及文件
				File srcFile = new File(WebPath);
				fis = new FileInputStream(srcFile);
				// 设置ftp文件存储目录
				String[] dirs = SaveUrl.split("/");
				String d = "/";
				for (String dir : dirs) {
					if (!StringUtils.isNullOrEmpty(dir)) {
						d = d + dir + "/";
						ftpClient.makeDirectory(d);
					}
				}

				ftpClient.enterLocalPassiveMode();
				ftpClient.changeWorkingDirectory(SaveUrl);
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				// 设置文件类型（二进制）
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

				ftpClient.storeFile(imgName, fis);
			} else {
				uploadCode = -100;
			}
		} catch (IOException e) {
			logger.error("ftp服务器上传图片异常： " + e);
			uploadCode = -100;
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error("ftp服务器关闭连接异常： " + e);
				uploadCode = -100;
			}
		}
		File file = new File(WebPath);
		file.delete();
		return uploadCode;
	}

	public int ImageDownload(String ftp_id, String download_url, String acc_name) {
		download_url = this.configService.getSysConfig().getUploadFilePath() + download_url;
		FTPClient ftpClient = new FTPClient();
		FileOutputStream fos = null;
		FileOutputStream foswm = null;
		int downCode = 100; // 100下载成功，-100下载失败
		FTPServer ftp = this.ftpserverService.getObjById(CommUtil.null2Long(ftp_id));
		String url = ftp.getFtp_ip();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String path = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache"
				+ File.separator;
		try {
			ftpClient.connect(url);
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			ftpClient.enterLocalPassiveMode();
			// 下载水印图片
			String remoteFileNamewm = download_url + "/" + acc_name;
			foswm = new FileOutputStream(path + File.separator + acc_name);
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.retrieveFile(remoteFileNamewm, foswm);
			foswm.flush();
			foswm.close();
		} catch (IOException e) {
			logger.error("ftp服务器图片下载异常： " + e);
			downCode = -100;
		} finally {
			IOUtils.closeQuietly(fos);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error("ftp服务器关闭连接异常： " + e);
				downCode = -100;
			}
		}
		return downCode;
	}
}