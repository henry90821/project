package com.smi.pay.common;

import com.swetake.util.Qrcode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * TestQrcode.java 2013-01-15 下午14:06:23
 */

public class BarcodeUtil {

	public static String createBarcode(String filePath, String code) throws UnsupportedEncodingException {
		Qrcode rcode = new Qrcode();
		rcode.setQrcodeVersion(3); // 这个值最大40，值越大可以容纳的信息越多，够用就行了
		byte[] content = code.getBytes("utf-8");
		BufferedImage bufImg = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB); // 图片的大小
		Graphics2D gs = bufImg.createGraphics();
		gs.setBackground(Color.WHITE);
		gs.clearRect(0, 0, 300, 300);
		gs.setColor(Color.BLACK);

		// 输出内容> 二维码
		if (content.length > 0 && content.length < 120) {
			boolean[][] codeOut = rcode.calQrcode(content);
			for (int i = 0; i < codeOut.length; i++) {
				for (int j = 0; j < codeOut.length; j++) {
					if (codeOut[j][i]) {
						gs.fillRect(j * 10 + 5, i * 10 + 5, 10, 10);
					}
				}
			}
		} else {
			System.err.println("QRCode content bytes length = " + content.length + " not in [ 0,120 ]. ");
		}
		gs.dispose();
		bufImg.flush();
		String fileName = new Date().getTime() + ".png";
		File imgFile = new File(filePath + fileName);
		// 生成二维码QRCode图片
		try {
			// 图片格式
			ImageIO.write(bufImg, "png", imgFile);
			// Desktop.getDesktop().open(imgFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imgFile.getName();
	}

	/**
	 * 方法描述:
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		Qrcode rcode = new Qrcode();
		rcode.setQrcodeVersion(3); // 这个值最大40，值越大可以容纳的信息越多，够用就行了
		byte[] content = "http://weibo.com/u/1648732391".getBytes("utf-8");
		BufferedImage bufImg = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB); // 图片的大小
		Graphics2D gs = bufImg.createGraphics();
		gs.setBackground(Color.WHITE);
		gs.clearRect(0, 0, 300, 300);
		gs.setColor(Color.BLACK);

		// 输出内容> 二维码
		if (content.length > 0 && content.length < 120) {
			boolean[][] codeOut = rcode.calQrcode(content);
			for (int i = 0; i < codeOut.length; i++) {
				for (int j = 0; j < codeOut.length; j++) {
					if (codeOut[j][i]) {
						gs.fillRect(j * 10 + 5, i * 10 + 5, 10, 10);
					}
				}
			}
		} else {
			System.err.println("QRCode content bytes length = " + content.length + " not in [ 0,120 ]. ");
		}
		gs.dispose();
		bufImg.flush();
		File imgFile = new File("C:\\test.png");
		// 生成二维码QRCode图片

		try {
			// 图片格式
			ImageIO.write(bufImg, "png", imgFile);
			Desktop.getDesktop().open(imgFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
