package com.iskyshop.view.web.tools;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.FTPServer;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

@Component
public class SnapshotTools {
	private static Logger logger = Logger.getLogger(SnapshotTools.class);
	@Autowired
	private  FTPServerTools ftpTools;
	@Autowired
	private  ISysConfigService configService;
	/**
	 * 创建商品的快照并将快照保存到用户的ftp服务器上，返回快照的访问链接地址
	 * @param goodsUrl 要创建快照的商品的链接地址
	 * @param user_id 用户实例的id
	 */
	public String createGoodsSnapshot(final String goodsUrl, String user_id)
	{
		final FTPServer ftp = this.ftpTools.getUserFTP(user_id);
		final String dstFileName = UUID.randomUUID().toString() + ".html";
		String uploadDir = this.configService.getSysConfig().getUploadFilePath();
		final String srcFilePath = CommUtil.getServerRealPathFromSystemProp() + uploadDir + File.separator + "cache" + File.separator + dstFileName;
		final String dstFileDir = "/snapshot/" + (new SimpleDateFormat("yyyyMM")).format(new Date());
		Thread t = new Thread(new Runnable() {
			public void run() {
				BufferedWriter writer = null;
				try {
					HttpClient client = new HttpClient();
					HttpMethod method = new GetMethod(goodsUrl);					
					client.executeMethod(method);
					String tempString = method.getResponseBodyAsString();
					logger.info("Snapshot for goods(" + goodsUrl + ") is " + (StringUtils.isNullOrEmpty(tempString)? "null" : "not null"));
					method.releaseConnection();
					writer = new BufferedWriter(new FileWriter(srcFilePath));
					writer.append(tempString);
					writer.flush(); // 需要及时清掉流的缓冲区，万一文件过大就有可能无法写入了
				} catch (HttpException e2) {
					logger.error(e2);
				} catch (IOException e2) {
					logger.error(e2);
				} catch (Exception e3) {
					logger.error(e3);
				} finally {
					if(null != writer){
						try {
							writer.close();
						} catch (IOException e) {
							logger.error(e);
						}
					}
				}
				//将快照文件上传到ftp上
				ftpTools.userUpload(srcFilePath, ftp, dstFileDir, dstFileName);
			}
		});
		t.start();
		return ftp.getFtp_addr() + "/" + uploadDir + dstFileDir + "/" + dstFileName;
	}

}
