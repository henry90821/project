package com.iskyshop.manage.ftp.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.iskyshop.core.tools.StringUtils;

public class FTPServerManageTest {

	private Logger	logger	= Logger.getLogger(this.getClass());

	@Test
	public void imageUpload() {
		int upload_code = 100; // 100上传成功，-100上传失败！
		String saveUrl = "upload/ftpTest";

		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			ftpClient.connect("192.168.68.23");
			ftpClient.login("mallftp", "mall@T4Img");
			// 设置上传目录及文件
			File srcFile = new File("C://Users//Administrator//Desktop//大梅沙//IMG20150404151935.jpg");
			fis = new FileInputStream(srcFile);
			System.out.println(fis.available());
			// 设置ftp文件存储目录
			String[] dirs = saveUrl.split("/");
			String d = "/";
			for (String dir : dirs) {
				if (!StringUtils.isNullOrEmpty(dir)) {
					d = d + dir + "/";
					ftpClient.makeDirectory(d);
				}
			}
			ftpClient.changeWorkingDirectory(saveUrl);
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("UTF-8");
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			ftpClient.storeFile("232.jpg", fis);

		}
		catch (IOException e) {
			logger.error("ftp服务器上传图片异常： " + e);
			upload_code = -100;
		}
		finally {
			IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			}
			catch (IOException e) {
				logger.error("ftp服务器关闭连接异常： " + e);
				upload_code = -100;
			}
		}
	}

}
