package com.iskyshop.manage.ftp.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.FTPServer;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.WaterMark;
import com.iskyshop.foundation.service.IFTPServerService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: FTPServerTools.java
 * </p>
 * 
 * <p>
 * Description: 平台资源上传至FTP服务器工具类，上传时使用ip，页面显示时使用FTP地址
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
 * @date 2014年7月1日
 * 
 * @version iskyshop_b2b2c_colony v1.0集群版
 */
@Component
public class FTPServerTools {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IFTPServerService ftpserver;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;

	/**
	 * 将Web服务器上资源上传至Ftp服务器，返回Ftp存储路径
	 * 
	 * @param imgName
	 *            :上传到Ftp服务器中的资源文件名称
	 * @param SaveUrl
	 *            ：Ftp服务器保存资源的目录路径，可以随意设置保存路径，没有文件夹则会自动新建(必须以“/”开头)
	 */
	public String systemUpload(String imgName, String SaveUrl) {
		SaveUrl = this.configService.getSysConfig().getUploadFilePath() + SaveUrl;
		FTPServer ftp = this.getSystemFTP();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String WebPath = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache"
				+ File.separator + imgName;
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			ftpClient.connect(ftp.getFtp_ip());
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 设置上传目录及文件
			File srcFile = new File(WebPath);
			fis = new FileInputStream(srcFile);
			String dirs[] = SaveUrl.split("/");
			String d = "/";
			for (String dir : dirs) {
				if (!"".equals(dir)) {
					d = d + dir + "/";
					ftpClient.makeDirectory(d);
				}
			}
			ftpClient.changeWorkingDirectory(SaveUrl);
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("UTF-8");
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			ftpClient.storeFile(imgName, fis);
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error(e);
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
		File file = new File(WebPath);
		file.delete();
		return ftp.getFtp_addr() + "/" + SaveUrl;
	}

	/**
	 * dir为本地文件路径，该方法支持没有上传到cache文件夹中的文件上传
	 * 
	 * @param dir
	 * @param imgName
	 * @param SaveUrl
	 * @return
	 */
	public String systemUpload(String localDir, String imgName, String SaveUrl) {
		SaveUrl = this.configService.getSysConfig().getUploadFilePath() + SaveUrl;
		FTPServer ftp = this.getSystemFTP();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String WebPath = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + localDir
				+ File.separator + imgName;
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			ftpClient.connect(ftp.getFtp_ip());
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 设置上传目录及文件
			File srcFile = new File(WebPath);
			fis = new FileInputStream(srcFile);
			// 设置ftp文件存储目录
			String dirs[] = SaveUrl.split("/");
			String d = "/";
			for (String dir : dirs) {
				if (!"".equals(dir)) {
					d = d + dir + "/";
					ftpClient.makeDirectory(d);
				}
			}
			ftpClient.changeWorkingDirectory(SaveUrl);
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("UTF-8");
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			ftpClient.storeFile(imgName, fis);
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error(e);
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
		File file = new File(WebPath);
		file.delete();
		return ftp.getFtp_addr() + "/" + SaveUrl;
	}

	public void systemDeleteFtpImg(Accessory acc) throws IOException {
		FTPServer ftp = this.getSystemFTP();
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			ftpClient.connect(ftp.getFtp_ip());
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			String regex = ftp.getFtp_addr();
			String paths[] = acc.getPath().split(regex);
			String delePath = "";
			for (String path : paths) {
				if (!"".equals(path)) {
					delePath = path;
				}
			}
			ftpClient.deleteFile(delePath + "/" + acc.getName());
			ftpClient.deleteFile(delePath + "/" + acc.getName() + "_small." + acc.getExt());
			ftpClient.deleteFile(delePath + "/" + acc.getName() + "_middle." + acc.getExt());
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error(e);
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 从系统ftp服务器上下载水印图片
	 * 
	 * @param acc
	 * @param watermark
	 * @param user_id
	 */
	public void systemDownloadWaterImg(WaterMark watermark) {
		// TODO Auto-generated method stub
		FTPClient ftpClient = new FTPClient();
		FileOutputStream foswm = null;
		FTPServer ftp = this.getSystemFTP();
		String url = ftp.getFtp_ip();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String path = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache" + File.separator;
		try {
			ftpClient.connect(url);
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			
			if(watermark.getWm_image() == null){
				throw new RuntimeException("请先设置水印图片！");
			}
			// 下载水印图片
			String remoteFileNamewm = uploadFilePath + "/wm/" + watermark.getWm_image().getName();
			foswm = new FileOutputStream(path + watermark.getWm_image().getName());
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			ftpClient.retrieveFile(remoteFileNamewm, foswm);
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(foswm);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error(e);
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 从系统ftp服务器上下载原图片
	 * 
	 * @param acc
	 * @param watermark
	 * @param user_id
	 */
	public void systemDownloadImg(Accessory acc) {
		// TODO Auto-generated method stub
		FTPClient ftpClient = new FTPClient();
		FileOutputStream foswm = null;
		FTPServer ftp = this.getSystemFTP();
		String url = ftp.getFtp_ip();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String path = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache" + File.separator;
		try {
			ftpClient.connect(url);
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 下载水印图片
			String relative_path=null;//取出图片的相对路径
			if(acc.getPath().indexOf(ftp.getFtp_addr())!=-1){
				String[] acc_path = acc.getPath().split(ftp.getFtp_addr());
				relative_path=acc_path[1];
			}else{
				relative_path=acc.getPath().replaceAll("http://[a-z]*\\.[a-z]+\\.([a-z]{2,3}){1,2}", "");
			}
			logger.info("addr:" + ftp.getFtp_addr());
			String remoteFileNamewm = relative_path + "/" + acc.getName();
			foswm = new FileOutputStream(path + acc.getName());
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			ftpClient.retrieveFile(remoteFileNamewm, foswm);
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(foswm);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error(e);
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 将Web服务器缓存目录中的资源上传至指定用户在Ftp服务器上的指定目录中（ftp上按user_id进行分组保存，即参数SaveUrl对应的目录会被放到user_id这个目录下）（保存的文件名与原文件相同，Web服务器上的资源会被删除）。
	 * 返回文件在Ftp上的最终存储目录的访问路径（返回目录是否以分隔符结尾由参数SaveUrl决定，与SaveUrl保持一致。）
	 * 
	 * @param imgName
	 *            :Web服务器缓存目录中的资源文件的文件名
	 * @param SaveUrl
	 *            ：Ftp服务器保存资源的目录路径，可以随意设置保存路径，没有文件夹则自动新建（必须以“/”开头）
	 */
	public String userUpload(String imgName, String SaveUrl, String user_id) {
		SaveUrl = this.configService.getSysConfig().getUploadFilePath() + "/" + user_id + SaveUrl;
		FTPServer ftp = this.getUserFTP(user_id);
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String WebPath = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache" + File.separator + imgName;
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		File srcFile = new File(WebPath);
		
		try {
			ftpClient.connect(ftp.getFtp_ip());
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
	
			//检查FTP上是否存在目标目录，若没有，则创建目录
			Boolean doesExist = ftpClient.changeWorkingDirectory(SaveUrl);//通过此方法来判断目标目录是否存在，若存在，则会返回true，其它情况返回false
			if(!doesExist) {
				String parts[] = SaveUrl.split("/");
				String dirTmp = ""; 
				int i = 0;
				while(i < parts.length) {
					dirTmp += "/" +parts[i++];
					ftpClient.makeDirectory(dirTmp);				
				}
			}			
			
			ftpClient.changeWorkingDirectory(SaveUrl);
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("UTF-8");
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			
			fis = new FileInputStream(srcFile);
			ftpClient.storeFile(imgName, fis);
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error(e);
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
		srcFile.delete();
		return ftp.getFtp_addr() + "/" + SaveUrl;
	}

	
	/**
	 * 将Web服务器上由srcFilePath指定的资源文件上传至指定Ftp服务器上的指定目录中（保存的文件名由dstFileName指定，Web服务器上的资源会被删除）
	 * 
	 * 
	 * @param srcFilePath
	 *            :Web服务器中将要上传到Ftp服务器上的资源文件的完整路径            
	 * @param ftp
	 *            ：ftp服务器对象
	 * @param dstFileDir
	 *            :资源文件将要上传到Ftp服务器上的目录路径（必须以“/”开头）
	 * @param dstFileName
	 *            ：资源文件在FTP服务器上的文件名	 
	 */
	public void userUpload(String srcFilePath, FTPServer ftp, String dstFileDir, String dstFileName) {
		dstFileDir = this.configService.getSysConfig().getUploadFilePath() + dstFileDir;
		
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;	
		File file = new File(srcFilePath);
		try {
			ftpClient.connect(ftp.getFtp_ip());
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());			
			
			//检查FTP上是否存在目标目录，若没有，则创建目录
			Boolean doesExist = ftpClient.changeWorkingDirectory(dstFileDir);//通过此方法来判断目标目录是否存在，若存在，则会返回true，其它情况返回false
			if(!doesExist) {
				String parts[] = dstFileDir.split("/");
				String dirTmp = ""; 
				int i = 0;
				while(i < parts.length) {
					dirTmp += "/" +parts[i++];
					ftpClient.makeDirectory(dirTmp);				
				}
			}			

			ftpClient.makeDirectory(dstFileDir);			
			ftpClient.changeWorkingDirectory(dstFileDir);
			
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("UTF-8");
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			
			fis = new FileInputStream(file);
			ftpClient.storeFile(dstFileName, fis);
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error(e);
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}		
		file.delete();
	}
	
	/**
	 * 商家删除位于ftp上的图片
	 * 
	 * @param acc
	 * @param user_id
	 * @throws IOException
	 */
	public void userDeleteFtpImg(Accessory acc, String user_id) throws IOException {
		FTPServer ftp = this.getUserFTP(user_id);
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			ftpClient.connect(ftp.getFtp_ip());
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			String regex = ftp.getFtp_addr();
			String paths[] = acc.getPath().split(regex);
			String delePath = "";
			for (String path : paths) {
				if (!"".equals(path)) {
					delePath = path;
				}
			}
			ftpClient.deleteFile(delePath + "/" + acc.getName());
			ftpClient.deleteFile(delePath + "/" + acc.getName() + "_small." + acc.getExt());
			ftpClient.deleteFile(delePath + "/" + acc.getName() + "_middle." + acc.getExt());
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error(e);
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 从用户ftp服务器上下载水印图片
	 * 
	 * @param acc
	 * @param watermark
	 * @param user_id
	 */
	public void userDownloadWaterImg(WaterMark watermark, String user_id) {
		// TODO Auto-generated method stub
		FTPClient ftpClient = new FTPClient();
		FileOutputStream foswm = null;
		FTPServer ftp = this.getUserFTP(user_id);
		String url = ftp.getFtp_ip();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String path = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache" + File.separator;
		try {
			ftpClient.connect(url);
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 下载水印图片
			String remoteFileNamewm = null;
			remoteFileNamewm = uploadFilePath + "/" + user_id + "/wm/"  + watermark.getWm_image().getName();
			foswm = new FileOutputStream(path + watermark.getWm_image().getName());
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			ftpClient.retrieveFile(remoteFileNamewm, foswm);
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(foswm);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error(e);
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 从用户ftp服务器上下载原图片
	 * 
	 * @param acc
	 * @param watermark
	 * @param user_id
	 */
	public void userDownloadImg(Accessory acc, String user_id) {
		// TODO Auto-generated method stub
		FTPClient ftpClient = new FTPClient();
		FileOutputStream foswm = null;
		FTPServer ftp = this.getUserFTP(user_id);
		String url = ftp.getFtp_ip();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String path = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache" + File.separator;
		try {
			ftpClient.connect(url);
			ftpClient.login(ftp.getFtp_username(), ftp.getFtp_password());
			// 下载水印图片
			String acc_path[] = acc.getPath().split(ftp.getFtp_addr());
			String remoteFileNamewm = acc_path[1] + "/" + acc.getName();
			foswm = new FileOutputStream(path + acc.getName());
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			ftpClient.retrieveFile(remoteFileNamewm, foswm);
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			IOUtils.closeQuietly(foswm);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error(e);
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 删除web服务器中的图片
	 * 
	 * @return
	 */
	public void DeleteWebImg(Accessory acc) {
		if(acc == null) return;
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String WebPath = CommUtil.getServerRealPathFromSystemProp() + uploadFilePath + File.separator + "cache"
				+ File.separator + acc.getName();
		File file = new File(WebPath);
		file.delete();
	}

	private FTPServer getSystemFTP() {
		FTPServer ftp = null;
		Map ftp_map = new HashMap();
		ftp_map.put("ftp_type", 1);
		ftp_map.put("ftp_system", 1);// 正使用的系统ftp
		List<FTPServer> ftps = this.ftpserver.query(
				"select obj from FTPServer obj where obj.ftp_system=:ftp_system and obj.ftp_type=:ftp_type", ftp_map, -1,
				-1);
		if (ftps.size() > 0) {
			ftp = ftps.get(0);
		}
		return ftp;
	}

	public FTPServer getUserFTP(String user_id) {
		User user = null;
		if (!StringUtils.isNullOrEmpty(user_id)) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
		} else {
			user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		}
		FTPServer ftp = user.getFtp();
		return ftp;
	}

}
