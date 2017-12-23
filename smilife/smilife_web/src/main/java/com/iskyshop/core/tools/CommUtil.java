package com.iskyshop.core.tools;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.springframework.context.ApplicationContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.GoodsConfig;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.lucene.LuceneResult;

/**
 * 
 * <p>
 * Title: CommUtil.java
 * </p>
 * 
 * <p>
 * Description: 系统工具类，用来快速处理,系统默认将该工具类添加到ModelAndView中，前台可以使用$!CommUtil.xxx调用该工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>

 * @author carson.cao
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
public class CommUtil {

	private static Logger logger = Logger.getLogger(CommUtil.class);
	private static String serverAddress = null; //使用后台配置的商城地址（一般为域名）
	
	static {
		ApplicationContext ac = SpringUtils.getContext();
		ISysConfigService sc = (ISysConfigService)ac.getBean("sysConfigService");
		String addr = sc.getSysConfig().getAddress();
		if(!StringUtils.isNullOrEmpty(addr)) {
			serverAddress = addr.trim();
		}
	}
	/**
	 * 首字母小写
	 * @param str 待处理字符串
	 * @return 处理后字符串，如果字符串为null，则返回"", 否则返回首字母小写的字符串
	 */
	public static String first2low(String str) {
		if (StringUtils.isNullOrEmpty(str)) {
			return "";
		}
		String s = "";
		s = str.substring(0, 1).toLowerCase() + str.substring(1);
		return s;
	}

	/**
	 * 首字母大写
	 * @param str 待处理字符串
	 * @return 处理后字符串，如果字符串为null，则返回"", 否则返回首字母大写的字符串
	 */
	public static String first2upper(String str) {
		if (StringUtils.isNullOrEmpty(str)) {
			return "";
		}
		String s = "";
		s = str.substring(0, 1).toUpperCase() + str.substring(1);
		return s;
	}

	/**
	 * 将字符串转换为list，一行一条数据
	 * @param s 待处理的字符串
	 * @return 字符串列表, 如果字符串为null，则返回null
	 * @throws IOException io异常
	 */
	public static List<String> str2list(String s) throws IOException {
		List<String> list = new ArrayList<String>();
		if (null == s) {
			return null;
		}
		if (!StringUtils.isNullOrEmpty(s)) {
			StringReader fr = new StringReader(s);
			BufferedReader br = new BufferedReader(fr);
			String aline = "";
			while ((aline = br.readLine()) != null) {
				list.add(aline);
			}
		}
		return list;
	}

	/**
	 * 将格式为yyyy-MM-dd的时间字符串转换为Date对象
	 * @param s 时间字符串 ，格式为yyyy-MM-dd
	 * @return Date对象
	 */
	public static java.util.Date formatDate(String s) {
		java.util.Date d = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(Globals.DateFormat.DATE_FORMAT_SHORT);
			d = dateFormat.parse(s);
		} catch (Exception e) {
			logger.error("日期转换异常：" + e);
		}
		return d;
	}

	/**
	 * 将指定格式的时间字符串转换为Date对象
	 * @param s 时间字符串
	 * @param format 时间格式 
	 * @return Date对象
	 */
	public static java.util.Date formatDate(String s, String format) {
		java.util.Date d = null;
		try {
			SimpleDateFormat dFormat = new java.text.SimpleDateFormat(format);
			d = dFormat.parse(s);
		} catch (Exception e) {
			logger.error("日期转换异常：" + e);
		}
		return d;
	}

	/**
	 * 将时间对象转换为指定格式的时间字符串
	 * @param format 时间格式字符串
	 * @param v 时间对象
	 * @return 时间字符串
	 */
	public static String formatTime(String format, Object v) {
		if (v == null) {
			return null;
		}
		if ("".equals(v)) {
			return "";
		}
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(v);
	}

	/**
	 * 转换为长格式字符串 yyyy-MM-dd HH:mm:ss
	 * @param v 时间对象
	 * @return 时间字符串
	 */
	public static String formatLongDate(Object v) {
		if (v == null || "".equals(v)) {
			return "";
		}
		SimpleDateFormat df = new SimpleDateFormat(Globals.DateFormat.DATE_FORMAT_LONG);
		return df.format(v);
	}

	/**
	 * 转换为短格式字符串 yyyy-MM-dd
	 * @param v 时间对象
	 * @return 时间字符串
	 */
	public static String formatShortDate(Object v) {
		if (v == null) {
			return null;
		}
		SimpleDateFormat df = new SimpleDateFormat(Globals.DateFormat.DATE_FORMAT_SHORT);
		return df.format(v);
	}

	/**
	 * 将字符串进行UTF-8解码
	 * @param s 待解码字符串
	 * @return 解码后字符串，UTF-8格式编码
	 */
	public static String decode(String s) {
		String ret = s;
		try {
			ret = URLDecoder.decode(s.trim(), Globals.CharSet.UTF_8);
		} catch (Exception e) {
			logger.error("decode解码异常： " + e);
		}
		return ret;
	}
	/**
	 * 将字符串进行UTF-8编码
	 * @param s 待编码字符串
	 * @return 编码后字符串，UTF-8格式编码
	 */
	public static String encode(String s) {
		String ret = s;
		try {
			ret = URLEncoder.encode(s.trim(), "UTF-8");
		} catch (Exception e) {
		}
		return ret;
	}
	
	/**
	 * 对指定的字符串进行编码
	 * @param str 待编码字符串
	 * @param coding 编码集
	 * @return 编码后的字符串
	 */
	public static String convert(String str, String coding) {
		String newStr = "";
		if (str != null) {
			try {
				newStr = new String(str.getBytes("ISO-8859-1"), coding);
			} catch (Exception e) {
				return newStr;
			}
		}
		return newStr;
	}

	/**
	 * saveFileToServer 上传文件保存到服务器
	 * @param request http请求
	 * @param filePath filePath为上传文件的名称
	 * @param saveFilePathName 保存到服务器上的文件路径
	 * @param saveFileName 保存到服务器上的文件名称
	 * @param extendes 文件扩展名
	 * @return 保存后文件的信息
	 * @throws IOException io异常
	 */
	public static Map saveFileToServer(HttpServletRequest request, String filePath, String saveFilePathName,
					String saveFileName, String[] extendes) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile(filePath);
		Map map = new HashMap();
		
		if (file != null && !file.isEmpty()) {
			String extend = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
							.toLowerCase();
			if (StringUtils.isNullOrEmpty(saveFileName)) {
				saveFileName = UUID.randomUUID().toString() + "." + extend;
			}
			if (saveFileName.lastIndexOf(".") < 0) {
				saveFileName = saveFileName + "." + extend;
			}
			float fileSize = Float.valueOf(file.getSize()); // 返回文件大小，单位为k
			List<String> errors = new java.util.ArrayList<String>();
			boolean flag = true;
			if (extendes == null) {
				extendes = new String[] { "jpg", "jpeg", "gif", "bmp", "tbi", "png" };
			}
			for (String s : extendes) {
				if (extend.toLowerCase().equals(s)) {
					flag = true;
				}
			}
			if (flag) {
				File path = new File(saveFilePathName);
				if (!path.exists()) {
					path.mkdir();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				DataOutputStream out = new DataOutputStream(new FileOutputStream(saveFilePathName + File.separator
								+ saveFileName));
				InputStream is = null;
				try {
					is = file.getInputStream();
					int size = (int) fileSize;
					byte[] buffer = new byte[size];
					while (is.read(buffer) > 0) {
						out.write(buffer);
					}
				} catch (IOException exception) {
					exception.printStackTrace();
					logger.error(exception);
				} finally {
					if (is != null) {
						is.close();
					}
					if (out != null) {
						out.close();
					}
				}
				if (isImg(extend)) {
					File img = new File(saveFilePathName + File.separator + saveFileName);
					try {
						BufferedImage bis = ImageIO.read(img);
						int w = bis.getWidth();
						int h = bis.getHeight();
						map.put("width", w);
						map.put("height", h);
					} catch (Exception e) {
						logger.error(e);
					}
				}
				map.put("mime", extend);
				map.put("fileName", saveFileName);
				map.put("fileSize", fileSize);
				map.put("error", errors);
				map.put("oldName", file.getOriginalFilename());
			} else {
				errors.add("不允许的扩展名");
			}
		} else {
			map.put("width", 0);
			map.put("height", 0);
			map.put("mime", "");
			map.put("fileName", "");
			map.put("fileSize", 0.0f);
			map.put("oldName", "");
		}
		return map;
	}
	

	/**
	 * saveFileToServer 保存客户端post请求发送MultipartEntity的文件保存到服务器
	 * @param request http请求
	 * @param filePath filePath为上传文件的名称
	 * @param saveFilePathName 保存到服务器上的文件路径
	 * @param saveFileName 保存到服务器上的文件名称
	 * @param extendes 文件扩展名
	 * @return 保存后文件的信息
	 * @throws IOException io异常
	 */
	public static Map savePostFileToServer(HttpServletRequest request, String filePath, String saveFilePathName,
					String saveFileName, String[] extendes) throws IOException {
		DefaultMultipartHttpServletRequest multipartRequest = (DefaultMultipartHttpServletRequest) request;
		LinkedMultiValueMap map2= (LinkedMultiValueMap) multipartRequest.getMultiFileMap();
		CommonsMultipartFile file= (CommonsMultipartFile) map2.getFirst("file");
		
		Map map = new HashMap();
		
		if (file != null && !file.isEmpty()) {
			String extend = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
							.toLowerCase();
			if (StringUtils.isNullOrEmpty(saveFileName)) {
				saveFileName = UUID.randomUUID().toString() + "." + extend;
			}
			if (saveFileName.lastIndexOf(".") < 0) {
				saveFileName = saveFileName + "." + extend;
			}
			float fileSize = Float.valueOf(file.getSize()); // 返回文件大小，单位为k
			List<String> errors = new java.util.ArrayList<String>();
			boolean flag = true;
			if (extendes == null) {
				extendes = new String[] { "jpg", "jpeg", "gif", "bmp", "tbi", "png" };
			}
			for (String s : extendes) {
				if (extend.toLowerCase().equals(s)) {
					flag = true;
				}
			}
			if (flag) {
				File path = new File(saveFilePathName);
				if (!path.exists()) {
					path.mkdir();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				DataOutputStream out = new DataOutputStream(new FileOutputStream(saveFilePathName + File.separator
								+ saveFileName));
				InputStream is = null;
				try {
					is = file.getInputStream();
					int size = (int) fileSize;
					byte[] buffer = new byte[size];
					while (is.read(buffer) > 0) {
						out.write(buffer);
					}
				} catch (IOException exception) {
					exception.printStackTrace();
					logger.error(exception);
				} finally {
					if (is != null) {
						is.close();
					}
					if (out != null) {
						out.close();
					}
				}
				if (isImg(extend)) {
					File img = new File(saveFilePathName + File.separator + saveFileName);
					try {
						BufferedImage bis = ImageIO.read(img);
						int w = bis.getWidth();
						int h = bis.getHeight();
						map.put("width", w);
						map.put("height", h);
					} catch (Exception e) {
						logger.error(e);
					}
				}
				map.put("mime", extend);
				map.put("fileName", saveFileName);
				map.put("fileSize", fileSize);
				map.put("error", errors);
				map.put("oldName", file.getOriginalFilename());
			} else {
				errors.add("不允许的扩展名");
			}
		} else {
			map.put("width", 0);
			map.put("height", 0);
			map.put("mime", "");
			map.put("fileName", "");
			map.put("fileSize", 0.0f);
			map.put("oldName", "");
		}
		return map;
	}

	/**
	 * 判断是否为图片格式
	 * 
	 * @param extend
	 *            文件扩展名
	 * @return 如果是返回true，否则返回false
	 */
	public static boolean isImg(String extend) {
		boolean ret = false;
		List<String> list = new java.util.ArrayList<String>();
		list.add("jpg");
		list.add("jpeg");
		list.add("bmp");
		list.add("gif");
		list.add("png");
		list.add("tif");
		list.add("tbi");
		for (String s : list) {
			if (s.equals(extend)) {
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * 图片水印，一般使用gif png格式，其中png质量较好
	 * 
	 * @param pressImg
	 *            水印文件
	 * @param targetImg
	 *            目标文件
	 * @param pos
	 *            水印位置，使用九宫格控制
	 * @param alpha
	 *            水印图片透明度
	 */
	public static final void waterMarkWithImage(String pressImg, String targetImg, int pos, float alpha) {
		FileOutputStream out = null;
		try {
			// 目标文件
			Image theImg = Toolkit.getDefaultToolkit().getImage(targetImg);
			theImg.flush();
			BufferedImage bis = toBufferedImage(theImg);
			int width = theImg.getWidth(null);
			int height = theImg.getHeight(null);
			Graphics2D g = bis.createGraphics();
			g.drawImage(theImg, 0, 0, width, height, null);

			// 水印文件
			File fileBiao = new File(pressImg);
			Image srcBiao = ImageIO.read(fileBiao);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha / 100));
			int widthBiao = srcBiao.getWidth(null);
			int heightBiao = srcBiao.getHeight(null);
			int x = 0;
			int y = 0;
			if (pos == 1) {
			}
			if (pos == 2) {
				x = (width - widthBiao) / 2;
				y = 0;
			}
			if (pos == 3) {
				x = width - widthBiao;
				y = 0;
			}
			if (pos == 4) {
				x = width - widthBiao;
				y = (height - heightBiao) / 2;
			}
			if (pos == 5) {
				x = width - widthBiao;
				y = height - heightBiao;
			}
			if (pos == 6) {
				x = (width - widthBiao) / 2;
				y = height - heightBiao;
			}
			if (pos == 7) {
				x = 0;
				y = height - heightBiao;
			}
			if (pos == 8) {
				x = 0;
				y = (height - heightBiao) / 2;
			}
			if (pos == 9) {
				x = (width - widthBiao) / 2;
				y = (height - heightBiao) / 2;
			}
			g.drawImage(srcBiao, x, y, widthBiao, heightBiao, null);
			// 水印文件结束
			g.dispose();
			out = new FileOutputStream(targetImg);
			ImageIO.write(bis, "JPEG", out);
		} catch (Exception e) {
			logger.error(e);
		}finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 创建小图片 V1.3使用，改写算法，图片清晰度更好
	 * 
	 * @param source
	 *            原图片
	 * @param target
	 *            目标图片
	 * @param width
	 *            图片宽度，高度自动根据比例计算
	 * @param height
	 *            图片高度
	 * @return 创建图片是否成功
	 */
	public static boolean createSmall(String source, String target, int width, int height) {
		try {
			Image img = Toolkit.getDefaultToolkit().getImage(source);
			BufferedImage bis = toBufferedImage(img);
			int w = bis.getWidth();
			int h = bis.getHeight();
			int nw = width;
			int nh = (nw * h) / w;
			ImageCompress.ImageScale(source, target, width, height);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}

	}

	/**
	 * 读取图片为bufferedimage,修正图片读取ICC信息丢失而导致出现红色遮罩
	 * 
	 * @param image
	 *            图片
	 * @return 对象
	 */
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see e661 Determining If an Image Has Transparent
		// Pixels
		// boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			/*
			 * if (hasAlpha) { transparency = Transparency.BITMASK; }
			 */

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			logger.warn(e);
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			// int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
			/*
			 * if (hasAlpha) { type = BufferedImage.TYPE_INT_ARGB; }
			 */
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	/**
	 * 创建小图片 V 1.3版前使用
	 * 
	 * @param source
	 *            原图片
	 * @param target
	 *            目标图片
	 * @param width
	 *            图片宽度，高度自动根据比例计算
	 * @return 创建图片是否成功
	 */
	public static boolean createSmallOld(String source, String target, int width) {
		try {
			File sourceFile = new File(source);
			BufferedImage bis = ImageIO.read(sourceFile);
			int w = bis.getWidth();
			int h = bis.getHeight();
			int nw = width;
			int nh = (nw * h) / w;
			ImageScale is = new ImageScale();
			is.saveImageAsJpg(source, target, nw, nh);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}

	}

	/**
	 * 为图片添加水印
	 * @param filePath 文件路径
	 * @param outPath 输出文件路径
	 * @param text 水印文本
	 * @param markContentColor 水印颜色
	 * @param font 字体
	 * @param pos 位置
	 * @param qualNum 图片质量
	 * @return 添加成功返回true， 失败返回false
	 */
	public static boolean waterMarkWithText(String filePath, String outPath, String text, String markContentColor,
					Font font, int pos, float qualNum) {
		Image theImg = Toolkit.getDefaultToolkit().getImage(filePath);
		theImg.flush();
		BufferedImage bis = toBufferedImage(theImg);
		int width = bis.getWidth(null);
		int height = bis.getHeight(null);
		BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimage.createGraphics();
		if (font == null) {
			font = new Font("黑体", Font.BOLD, 30);
			g.setFont(font);
		} else {
			g.setFont(font);
		}
		if(!StringUtils.isNullOrEmpty(markContentColor)){
			g.setColor(getColor(markContentColor));
		}
		g.setBackground(Color.white);
		g.drawImage(theImg, 0, 0, null);
		FontMetrics metrics = new FontMetrics(font) {
		};
		Rectangle2D bounds = metrics.getStringBounds(text, null);
		int widthInPixels = (int) bounds.getWidth();
		int heightInPixels = (int) bounds.getHeight();
		int left = 0;
		int top = heightInPixels;
		
		if (pos == 2) {
			left = width / 2;
			top = heightInPixels;
		}
		if (pos == 3) {
			left = width - widthInPixels;
			top = heightInPixels;
		}
		if (pos == 4) {
			left = width - widthInPixels;
			top = height / 2;
		}
		if (pos == 5) {
			left = width - widthInPixels;
			top = height - heightInPixels;
		}
		if (pos == 6) {
			left = width / 2;
			top = height - heightInPixels;
		}
		if (pos == 7) {
			left = 0;
			top = height - heightInPixels;
		}
		if (pos == 8) {
			left = 0;
			top = height / 2;
		}
		if (pos == 9) {
			left = width / 2;
			top = height / 2;
		}
		g.drawString(text, left, top); // 添加水印的文字和设置水印文字出现的内容
		g.dispose();
		try {
			FileOutputStream out = new FileOutputStream(outPath);
			ImageIO.write(bimage, "JPEG", out);
			out.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	
	/**
	 * 创建文件夹
	 * @param folderPath 文件夹路径
	 * @return 成功true， 失败false
	 */
	public static boolean createFolder(String folderPath) {
		boolean ret = true;
		try {
			java.io.File myFilePath = new java.io.File(folderPath);
			if (!myFilePath.exists() && !myFilePath.isDirectory()) {
				ret = myFilePath.mkdirs();
				if (!ret) {
					logger.error("创建文件夹出错");
				}
			}
		} catch (Exception e) {
			logger.error("创建文件夹出错: " + e);
			ret = false;
		}
		return ret;
	}

	/**
	 * @param list
	 * @param perNum
	 * @return
	 */
	public static List toRowChildList(List list, int perNum) {
		List l = new java.util.ArrayList();
		if (list == null) {
			return l;
		}
		for (int i = 0; i < list.size(); i += perNum) {
			List cList = new ArrayList();
			for (int j = 0; j < perNum; j++) {
				if (i + j < list.size()) {
					cList.add(list.get(i + j));
				}
			}
			l.add(cList);
		}
		return l;
	}

	/**
	 * 复制列表
	 * @param list 待复制的列表
	 * @param begin 复制的开始位置
	 * @param end 复制的结束位置
	 * @return 复制后的列表
	 */
	public static List copyList(List list, int begin, int end) {
		List l = new ArrayList();
		if (list == null) {
			return l;
		}
		if (end > list.size()) {
			end = list.size();
		}
		for (int i = begin; i < end; i++) {
			l.add(list.get(i));
		}
		return l;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			logger.error("复制单个文件操作出错 : " + e);
		}
	}

	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 * 
	 * @param path
	 *            要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public static boolean deleteFolder(String path) {
		boolean flag = false;
		File file = new File(path);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(path);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(path);
			}
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param path
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String path) {
		boolean flag = false;
		File file = new File(path);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param path
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String path) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		File dirFile = new File(path);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			} else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}
		if (!flag) {
			return false;
		}
		return dirFile.delete();
	}

	/**
	 * 静态分页，结合urlwriter使用
	 * 
	 * @param url 页面url
	 * @param currentPage 当前页面页码
	 * @param pages 页面总数
	 * @return 分页后html代码
	 */
	public static String showPageStaticHtml(String url, int currentPage, int pages,String beanPath) {
		StringBuffer html=new StringBuffer();
		//分页组件样式
		html.append("<style>.pageInput{width: 50px;}</style>");
		html.append("<link href='"+ beanPath +"/resources/style/system/manage/blue/overlay.css' type='text/css' rel='stylesheet' />");
		//分页组件js,在此公共出来就不必在每个页面都写一个对应的方法
		html.append("<script>function confirmToPage(url){ var toPage=$('#to_page_id').val(),href=url+'_'+toPage+'.htm'; window.location.href= href; rule=/[0-9]+/; if(!rule.test(toPage)){ showDialog('msg_info','','请输入正整数!!!',2,'warning',3,'');return;} if(toPage > "+ pages +"){ showDialog('msg_info','','请输入整数不能大于分页总页值!!!',2,'warning',3,'');return;}}</script>");
		if (pages > 0) {
			html.append("共    "+pages+"  页");
			if (currentPage >= 1) {
				html.append("<a href='" + url + "_1.htm'>首页</a> ");
				if (currentPage > 1) {
					html.append("<a href='" + url + "_" + (currentPage - 1) + ".htm'>上一页</a> ");
				}
			}

			int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
			if (beginPage <= pages) {
				html.append("第　");
				for (int i = beginPage, j = 0; i <= pages && j < 6; i++, j++) {
					if (i == currentPage) {
						html.append("<a class='this' href='" + url + "_" + i + ".htm'>" + i + "</a> ");
					} else {
						html.append("<a href='" + url + "_" + i + ".htm'>" + i + "</a> ");
					}
				}
				html.append("页　");
			}
			if (currentPage <= pages) {
				if (currentPage < pages) {
					html.append("<a href='" + url + "_" + (currentPage + 1) + ".htm'>下一页</a> ");
				}
				html.append("<a href='" + url + "_" + pages + ".htm'>末页</a> ");
			}
		  html.append(" 转到     <input type='text' value='' class='pageInput' maxlength='3' id='to_page_id' >页"+ "<a href='javascript:void(0);' onclick='return confirmToPage(\"" + url + "\")'>  确定</a> ");
		}
		return html.toString();
	}

	/**
	 * 常规的分页信息，使用get传递参数
	 * 
	 * @param url 页面url
	 * @param params 参数
	 * @param currentPage 当前页码
	 * @param pages 页面总数
	 * @return 页面html
	 */
	public static String showPageHtml(String url, String params, int currentPage, int pages) {
		StringBuffer html=new StringBuffer();
		//分页组件样式
		html.append("<style>.pageInput{width: 50px;}</style>");
		//分页组件js,在此公共出来就不必在每个页面都写一个对应的方法
		html.append("<script>function confirmToPage(url,params){ var toPage=$('#to_page_id').val(),href=url+'?currentPage='+toPage+params; window.location.href= href; rule=/[0-9]+/; if(!rule.test(toPage)){ showDialog('msg_info','','请输入正整数!!!',2,'warning',3,'');return;} if(toPage > "+ pages +"){ showDialog('msg_info','','请输入整数不能大于分页总页值!!!',2,'warning',3,'');return;}}</script>");
		if (pages > 0) {
			html.append("共    "+pages+"  页");
			if (currentPage >= 1) {
				html.append("<a href='" + url + "?currentPage=1" + params + "'>首页</a> ");
				if (currentPage > 1) {
					html.append( "<a href='" + url + "?currentPage=" + (currentPage - 1) + params + "'>上一页</a> ");
				}
			}

			int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
			if (beginPage <= pages) {
				html.append("第　");
				for (int i = beginPage, j = 0; i <= pages && j < 6; i++, j++) {
					if (i == currentPage) {
						html.append("<a class='this' href='" + url + "?currentPage=" + i + params + "'>" + i + "</a> ");
					} else {
						html.append("<a href='" + url + "?currentPage=" + i + params + "'>" + i + "</a> ");
					}
				}
				html.append("页　");
			}
			if (currentPage <= pages) {
				if (currentPage < pages) {
					html.append("<a href='" + url + "?currentPage=" + (currentPage + 1) + params + "'>下一页</a> ");
				}
				html.append( "<a href='" + url + "?currentPage=" + pages + params + "'>末页</a> ");
			}
		   html.append(" 转到     <input type='text' value='' class='pageInput' maxlength='3' id='to_page_id' >页"+ "<a href='javascript:void(0);' onclick='return confirmToPage(\"" + url + "\"," + params+ ")'>  确定</a> ");
		}
		return html.toString();
	}

	/**
	 * 使用表单分页，前台需要给数据放到form里，适合多参数查询分页
	 * 
	 * @param currentPage 当前页面
	 * @param pages 页码
	 * @return 页面字符串
	 */
	public static String showPageFormHtml(int currentPage, int pages,String beanPath) {
		StringBuffer html=new StringBuffer();
		//分页组件样式 
		html.append("<style>.pageInput{width: 50px;}</style>");
		html.append("<link href='"+ beanPath +"/resources/style/system/manage/blue/overlay.css' type='text/css' rel='stylesheet' />");
		//分页组件js,在此公共出来就不必在每个页面都写一个对应的方法
		
		html.append("<script>function confirmToPage(){ var toPage=$('#to_page_id').val(),rule=/[0-9]+/; if(!rule.test(toPage)){ showDialog('msg_info','','请输入正整数!!!',2,'warning',3,'');return;} if(toPage > "+ pages +"){ showDialog('msg_info','','请输入整数不能大于分页总页值!!!',2,'warning',3,'');return;} return gotoPage(toPage); }</script>");
		if (pages > 0) {
			html.append("共   "+pages+" 页       ");
			if (currentPage >= 1) {
				html.append("<a href='javascript:void(0);' onclick='return gotoPage(1)'>首页</a> ");
				if (currentPage > 1) {
					html.append( "<a href='javascript:void(0);' onclick='return gotoPage(" + (currentPage - 1) + ")'>上一页</a> ");
				}
			}

			int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
			if (beginPage <= pages) {
				html.append( "第　");
				for (int i = beginPage, j = 0; i <= pages && j < 6; i++, j++) {
					if (i == currentPage) {
						html.append("<a class='this' href='javascript:void(0);' onclick='return gotoPage(" + i + ")'>" + i
								+ "</a> ");
					} else {
						html.append("<a href='javascript:void(0);' onclick='return gotoPage(" + i + ")'>" + i + "</a> ");
					}
				}
				html.append("页　");
			}
			if (currentPage <= pages) {
				if (currentPage < pages) {
					html.append("<a href='javascript:void(0);' onclick='return gotoPage(" + (currentPage + 1) + ")'>下一页</a> ");
				}
				html.append("<a href='javascript:void(0);' onclick='return gotoPage(" + pages + ")'>末页</a> ");
			}
			html.append(" 转到   <input type='text' class='pageInput' maxlength='3' value='' id='to_page_id' >页"+ "<a href='javascript:void(0);' onclick='return confirmToPage()'>  确定</a> ");
		}
		return html.toString();
	}

	/**
	 * ajax动态分页，使用json管理数据
	 * 
	 * @param url 页面url
	 * @param params 请求参数
	 * @param currentPage 当前页码
	 * @param pages 页码
	 * @return ajax请求页面
	 */
	public static String showPageAjaxHtml(String url, String params, int currentPage, int pages) {
		StringBuffer html=new StringBuffer();
		//分页组件样式
		html.append("<style>.pageInput{width: 50px;}</style>");
		//分页组件js,在此公共出来就不必在每个页面都写一个对应的方法
		html.append("<script>function confirmToPage(address){ var toPage=$('#to_page_id').val(); return ajaxPage(address,toPage);rule=/[0-9]+/; if(!rule.test(toPage)){ showDialog('msg_info','','请输入正整数!!!',2,'warning',3,'');return;} if(toPage > "+ pages +"){ showDialog('msg_info','','请输入整数不能大于分页总页值!!!',2,'warning',3,'');return;} }</script>");
		if (pages > 0) {
			String address = url + "?1=1" + params;
			html.append("共    "+pages+" 页      ");
			if (currentPage >= 1) {
				html.append( "<a href='javascript:void(0);' onclick='return ajaxPage(\"" + address + "\",1,this)'>首页</a> ");
				html.append("<a href='javascript:void(0);' onclick='return ajaxPage(\"" + address + "\"," + (currentPage - 1)
						+ ",this)'>上一页</a> ");
			}
			int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
			if (beginPage <= pages) {
				html.append( "第　");
				for (int i = beginPage, j = 0; i <= pages && j < 6; i++, j++) {
					if (i == currentPage) {
						html.append("<a class='this' href='javascript:void(0);' onclick='return ajaxPage(\"" + address + "\"," + i
								+ ",this)'>" + i + "</a> ");
					} else {
						html.append( "<a href='javascript:void(0);' onclick='return ajaxPage(\"" + address + "\"," + i + ",this)'>"
								+ i + "</a> ");
					}
				}
				html.append("页　");
			}
			if (currentPage <= pages) {
				html.append("<a href='javascript:void(0);' onclick='return ajaxPage(\"" + address + "\"," + (currentPage + 1)
						+ ",this)'>下一页</a> ");
				html.append("<a href='javascript:void(0);' onclick='return ajaxPage(\"" + address + "\"," + pages
						+ ",this)'>末页</a> ");
			}
			html.append(" 转到  <input type='text' class='pageInput' maxlength='3' value='' id='to_page_id' >页"+"<a href='javascript:void(0);' onclick='return confirmToPage(\"" + address + "\""
					+ ")'>  确定</a> ");
		}
		return html.toString();
	}

	/**
	 * 将分页信息封装到ModelAndView中
	 * 
	 * @param url
	 *            分页url
	 * @param staticURL
	 *            静态分页URL，使用urlrewrite实现伪静态
	 * @param params
	 *            非静态URL的参数
	 * @param pList
	 *            分页数据查询结果
	 * @param mv
	 *            输出的视图
	 */
	public static void saveIPageList2ModelAndView(String url, String staticURL, String params, IPageList pList,
					ModelAndView mv) {
		if (pList != null) {
			mv.addObject("objs", pList.getResult());
			mv.addObject("totalPage", new Integer(pList.getPages()));
			mv.addObject("pageSize", pList.getPageSize());
			mv.addObject("rows", new Integer(pList.getRowCount()));
			mv.addObject("currentPage", new Integer(pList.getCurrentPage()));
			mv.addObject("gotoPageHTML", CommUtil.showPageHtml(url, params, pList.getCurrentPage(), pList.getPages()));
			mv.addObject("gotoPageFormHTML", CommUtil.showPageFormHtml(pList.getCurrentPage(), pList.getPages(),(String)mv.getModel().get("webPath")));
			mv.addObject("gotoPageStaticHTML",
							CommUtil.showPageStaticHtml(staticURL, pList.getCurrentPage(), pList.getPages(),(String)mv.getModel().get("webPath")));
			mv.addObject("gotoPageAjaxHTML",
							CommUtil.showPageAjaxHtml(url, params, pList.getCurrentPage(), pList.getPages()));
		}
	}

	/**
	 * 将IPageList转换为ModelAndView的第二个方法，这里传递到前台的不一定是objs，可以自定义，用来同一个页面中多个分页查询
	 * 
	 * @param url
	 * @param staticURL
	 * @param params
	 * @param result_name
	 * @param pList
	 * @param mv
	 */
	public static void saveIPageList2ModelAndView2(String url, String staticURL, String params, String prefix,
					IPageList pList, ModelAndView mv) {
		if (pList != null) {
			mv.addObject(prefix + "_objs", pList.getResult());
			mv.addObject(prefix + "_totalPage", new Integer(pList.getPages()));
			mv.addObject(prefix + "_pageSize", pList.getPageSize());
			mv.addObject(prefix + "_rows", new Integer(pList.getRowCount()));
			mv.addObject(prefix + "_currentPage", new Integer(pList.getCurrentPage()));
			mv.addObject(prefix + "_gotoPageHTML",
							CommUtil.showPageHtml(url, params, pList.getCurrentPage(), pList.getPages()));
			mv.addObject(prefix + "_gotoPageFormHTML", CommUtil.showPageFormHtml(pList.getCurrentPage(), pList.getPages(),(String)mv.getModel().get("webPath")));
			mv.addObject(prefix + "_gotoPageStaticHTML",
							CommUtil.showPageStaticHtml(staticURL, pList.getCurrentPage(), pList.getPages(),(String)mv.getModel().get("webPath")));
			mv.addObject(prefix + "_gotoPageAjaxHTML",
							CommUtil.showPageAjaxHtml(url, params, pList.getCurrentPage(), pList.getPages()));
		}
	}

	/**
	 * 将lucene对象转换为分页对象
	 * 
	 * @param pList
	 * @param mv
	 */
	public static void saveLucene2ModelAndView(LuceneResult pList, ModelAndView mv) {
		if (pList != null) {
			mv.addObject("objs", pList.getVo_list());
			mv.addObject("totalPage", pList.getPages());
			mv.addObject("pageSize", pList.getPageSize());
			mv.addObject("rows", pList.getRows());
			mv.addObject("currentPage", new Integer(pList.getCurrentPage()));
			mv.addObject("gotoPageFormHTML", CommUtil.showPageFormHtml(pList.getCurrentPage(), pList.getPages(),(String)mv.getModel().get("webPath")));
		}
	}

	/**
	 * 生成随机字符
	 * @return 随机的字符
	 */
	public static char randomChar() {
		char[] chars = new char[] { 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G', 'h', 'H', 'i',
				'I', 'j', 'J', 'k', 'K', 'l', 'L', 'm', 'M', 'n', 'N', 'o', 'O', 'p', 'P', 'q', 'Q', 'r', 'R', 's', 'S',
				't', 'T', 'u', 'U', 'v', 'V', 'w', 'W', 'x', 'X', 'y', 'Y', 'z', 'Z' };
		int index = (int) (Math.random() * 52) - 1;
		if (index < 0) {
			index = 0;
		}
		return chars[index];
	}
	
	public static String[] splitByChar(String s, String c) {
		String[] list = s.split(c);
		return list;
	}
	
	/**
	 * 生成缩略字符串，超出部分使用...代替
	 * @param s 待处理的字符串
	 * @param maxLength 缩略字符串的长度
	 * @return 缩略后的字符串
	 */
	public static String substring(String s, int maxLength) {
		if (!StringUtils.hasLength(s)) {
			return s;
		}
		if (s.length() <= maxLength) {
			return s;
		} else {
			return s.substring(0, maxLength) + "...";
		}
	}

	/**
	 * 将字符串数字转换为整形数字
	 * @param s 字符串数字
	 * @return 整形数字，如果是null，返回0
	 */
	public static int null2Int(Object s) {
		int v = 0;
		
		if (!StringUtils.isNullOrEmpty(s)) {
			try {
				v = Integer.parseInt(s.toString().trim());
			} catch (Exception e) {
				logger.warn(e);
			}
		}
		return v;
	}

	public static float null2Float(Object s) {
		float v = 0.0f;
		if (s != null) {
			try {
				v = Float.parseFloat(s.toString());
			} catch (Exception e) {
				logger.warn(e);
			}
		}
		return v;
	}

	/**
	 * 将字符串数字转换为double数字
	 * @param s 字符串数字
	 * @return double数字，如果是null，返回0.0
	 */
	public static double null2Double(Object s) {
		double v = 0.0;
		if (s != null) {
			try {
				v = Double.parseDouble(null2String(s));
			} catch (Exception e) {
				logger.warn(e);
			}
		}
		return v;
	}
	/**
	 * 将字符串数字转换为boolean数字
	 * @param s 字符串数字
	 * @return boolean数字，如果是null，返回false
	 */
	public static boolean null2Boolean(Object s) {
		boolean v = false;
		if (s != null) {
			try {
				v = Boolean.parseBoolean(s.toString());
			} catch (Exception e) {
				logger.warn(e);
			}
		}
		return v;
	}
	
	public static int indexOf(String s, String sub) {
		return s.trim().indexOf(sub.trim());
	}

	/**
	 * 将null对象转换为空字符串""
	 * @param s 待判断的对象
	 * @return 转换后的字符串
	 */
	public static String null2String(Object s) {
		return s == null ? "" : s.toString().trim();
	}

	/**
	 * 将字符串数字转换为Long数字
	 * @param s 字符串数字
	 * @return Long数字，如果是null或转换失败，返回-1L
	 */
	public static Long null2Long(Object s) {
		Long v = -1L;
		if (s != null) {
			try {
				v = Long.parseLong(s.toString());
			} catch (Exception e) {
				if(StringUtils.hasText(s.toString())) {//攻击的代码会调用此函数，故对于可能的攻击代码将打印堆栈信息，以便分析攻击源
					logger.warn(e.getMessage(), e);
				} else {
					logger.warn(e);
				}				
			}
		}
		return v;
	}

	/**
	 * 将整形时间转换为字符串时间
	 * @param time 长整形时间值（毫秒）
	 * @return 时间字符串
	 */
	public static String getTimeInfo(long time) {
		int hour = (int) time / (1000 * 60 * 60);
		long balance = time - hour * 1000 * 60 * 60;
		int minute = (int) balance / (1000 * 60);
		balance = balance - minute * 1000 * 60;
		int seconds = (int) balance / 1000;
		String ret = "";
		if (hour > 0) {
			ret += hour + "小时";
		}
		if (minute > 0) {
			ret += minute + "分";
		} else if (minute <= 0 && seconds > 0) {
			ret += "零";
		}
		if (seconds > 0) {
			ret += seconds + "秒";
		}
		return ret;
	}

	/**
	 * 获取ip地址
	 * @param request httpservlet请求
	 * @return ip地址
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			java.net.InetAddress addr = null;
			try {
				addr = java.net.InetAddress.getLocalHost();
			} catch (java.net.UnknownHostException e) {
				e.printStackTrace();
			}
			ip = CommUtil.null2String(addr.getHostAddress()); // 获得本机IP
		}
		if (ip != null && !"unknown".equalsIgnoreCase(ip) && !isIp(ip)) {
			String[] ips = ip.split(",");
			if (ips.length > 0) {
				ip = ips[0];
			}
		}
		return ip;
	}

	/**
	 * 获取两个时间之间相隔时间信息map（key：day,hour,min,second）
	 * @param begin 开始时间
	 * @param end 结束时间
	 * @return map时间信息
	 */
	public static Map cal_time_space(Date begin, Date end) {
		long diff = end.getTime() - begin.getTime();
		long day = diff / (24 * 60 * 60 * 1000);
		long hour = diff / (60 * 60 * 1000) - day * 24;
		long min = (diff / (60 * 1000)) - day * 24 * 60 - hour * 60;
		long second = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60;
		Map<String, Long> map = new HashMap<String, Long>();
		map.put("day", day);
		map.put("hour", hour);
		map.put("min", min);
		map.put("second", second);
		return map;
	}

	/**
	 * 获取随机字符串
	 * @param length 字符串的长度
	 * @return 返回的随机字符串
	 */
	public static final String randomString(int length) {
		char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ")
				.toCharArray();
		if (length < 1) {
			return "";
		}
		Random randGen = new Random();
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	/**
	 * 获取随机的一个整数字符串
	 * @param length 整数字符串的长度
	 * @return 整数的字符串
	 */
	public static final String randomInt(int length) {
		if (length < 1) {
			return null;
		}
		Random randGen = new Random();
		char[] numbersAndLetters = "0123456789".toCharArray();

		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(10)];
		}
		return new String(randBuffer);
	}

	/**
	 * 计算两个时间间隔
	 * 
	 * @param time1 时间1
	 * @param time2 时间2
	 * @return 两个时间之间的相差的时间，单位毫秒
	 */
	public static long getDateDistance(String time1, String time2) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000 / 60 / 60 / 24;
		} catch (ParseException e) {
			logger.error(e);
		}
		return quot;
	}

	/**
	 * 浮点数除法运算，保证数据的精确度
	 * 
	 * @param a 除数
	 * @param b 被除数
	 * @return 除法结果
	 */
	public static double div(Object a, Object b) {
		double ret = 0.0;
		if (!"".equals(null2String(a)) && !"".equals(null2String(b))) {
			BigDecimal e = new BigDecimal(null2String(a));
			BigDecimal f = new BigDecimal(null2String(b));
			if (null2Double(f) > 0) {
				ret = e.divide(f, 3, BigDecimal.ROUND_DOWN).doubleValue();
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(ret));
	}
	
	/**
	 * 浮点数除法运算，保证数据的精确度
	 * 
	 * @param a 除数
	 * @param b 被除数
	 * @param mode（RoundingMode.FLOOR非四舍五入）
	 * @return 除法结果
	 */
	public static double div(Object a, Object b,RoundingMode mode) {
		double ret = 0.0;
		if (!"".equals(null2String(a)) && !"".equals(null2String(b))) {
			BigDecimal e = new BigDecimal(null2String(a));
			BigDecimal f = new BigDecimal(null2String(b));
			if (null2Double(f) > 0) {
				ret = e.divide(f, 3, BigDecimal.ROUND_DOWN).doubleValue();
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		df.setRoundingMode(mode);
		return Double.valueOf(df.format(ret));
	}
	/**
	 * 
	    * @Title: div
	    * @Description: TODO(浮点数除法运算，保证数据的精确度)
	    * @param @param a  除数
	    * @param @param b  被除数
	    * @param @param scale  精度
	    * @param @param pattern  格式化格式
	    * @param @return    参数  
	    * @return double    返回类型  double
	    * @throws
	 */
	public static double div(Object a, Object b,int scale,String pattern,RoundingMode mode) {
		double ret = 0.0;
		if (!"".equals(null2String(a)) && !"".equals(null2String(b))) {
			BigDecimal e = new BigDecimal(null2String(a));
			BigDecimal f = new BigDecimal(null2String(b));
			if (null2Double(f) > 0) {
				ret = e.divide(f,scale,BigDecimal.ROUND_DOWN).doubleValue();
			}
		}
		DecimalFormat df = new DecimalFormat(pattern);
		df.setRoundingMode(mode);
		return Double.valueOf(df.format(ret));
	}
	/**
	 * 浮点数据减法运算，保证数据的精确度
	 * 
	 * @param a 减数
	 * @param b 被减数
	 * @return 结果
	 */
	public static double subtract(Object a, Object b) {
		double ret = 0.0;
		BigDecimal e = new BigDecimal(CommUtil.null2Double(a));
		BigDecimal f = new BigDecimal(CommUtil.null2Double(b));
		ret = e.subtract(f).doubleValue();
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(ret));
	}

	/**
	 * 浮点数据加法
	 * 
	 * @param a 数据a
	 * @param b 数据b
	 * @return 求和结果
	 */
	public static double add(Object a, Object b) {
		double ret = 0.0;
		BigDecimal e = new BigDecimal(CommUtil.null2Double(a));
		BigDecimal f = new BigDecimal(CommUtil.null2Double(b));
		ret = e.add(f).doubleValue();
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(ret));
	}

	/**
	 * 浮点数乘法
	 * 
	 * @param a 乘数
	 * @param b 被乘数
	 * @return 乘积结果
	 */
	public static double mul(Object a, Object b) { // 乘法
		BigDecimal e = new BigDecimal(CommUtil.null2Double(a));
		BigDecimal f = new BigDecimal(CommUtil.null2Double(b));
		double ret = e.multiply(f).doubleValue();
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(ret));
	}

	/**
	 * 格式化钱，保留小数点后两位
	 * @param money 金额
	 * @return 处理结果，保留小数后两位
	 */
	public static String formatMoney(Object money) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(CommUtil.null2Double(money));
	}
	
	/**
	 * 格式化钱，保留小数点后两位
	 * @param money 金额
	 * @param mode（RoundingMode.FLOOR非四舍五入）
	 * @return 处理结果，保留小数后两位
	 */
	public static String formatMoney(Object money,RoundingMode mode) {
		DecimalFormat df = new DecimalFormat("0.00");
		df.setRoundingMode(mode);
		return df.format(money);
	}

	/**
	 * 兆转换为字节
	 * @param m 数据量，单位兆
	 * @return 字节数
	 */
	public static int m2byte(float m) {
		float a = m * 1024 * 1024;
		return (int) a;
	}

	/**
	 * 将整数转换为boolean值
	 * @param intValue 整形数值
	 * @return boolean值
	 */
	public static boolean convertIntToBoolean(int intValue) {
		return intValue != 0;
	}

	/**
	 * 获取请求的url(包括上下文部分，不以/结尾)
	 * @param request 请求
	 * @return url
	 */
	public static String getURL(HttpServletRequest request) {
		if(serverAddress == null) {
			String contextPath = "/".equals(request.getContextPath()) ? "" : request.getContextPath();
			
			String url = "http://" + request.getServerName();
			if (null2Int(request.getServerPort()) != 80) {
				url = url + ":" + null2Int(request.getServerPort()) + contextPath;
			} else {
				url = url + contextPath;
			}
			return url;
		} else {
			return serverAddress;
		}
	}
	
	/**
	 * 获取Web应用的本地访问地址(包括上下文部分，不以/结尾)
	 * @param request
	 * @return
	 */
	public static String getLocalURL(HttpServletRequest request) {
		String contextPath = "/".equals(request.getContextPath()) ? "" : request.getContextPath();
		String url = "http://127.0.0.1";
		if (null2Int(request.getServerPort()) != 80) {
			url = url + ":" + null2Int(request.getServerPort()) + contextPath;
		} else {
			url = url + contextPath;
		}
		return url;
	}

	/**
	 * 定义过滤信息 使用Jsoup过滤数据，保护网站安全
	 */
	private static final Whitelist USER_CONTENT_FILTER = Whitelist.relaxed();

	static {
		USER_CONTENT_FILTER.addTags("embed", "object", "param", "span", "div", "font");
		USER_CONTENT_FILTER.addAttributes("div", "width", "height", "style");
		USER_CONTENT_FILTER.addAttributes("span", "width", "height", "style");
		USER_CONTENT_FILTER.addAttributes("ul", "width", "height", "style");
		USER_CONTENT_FILTER.addAttributes("li", "width", "height", "style");
		USER_CONTENT_FILTER.addAttributes("table", "width", "height", "style");
		USER_CONTENT_FILTER.addAttributes("tr", "width", "height", "style");
		USER_CONTENT_FILTER.addAttributes("td", "width", "height", "style");
		USER_CONTENT_FILTER.addAttributes(":all", "style", "class", "id", "name");
		USER_CONTENT_FILTER.addAttributes("object", "classid", "codebase");
		USER_CONTENT_FILTER.addAttributes("param", "name", "value");
		USER_CONTENT_FILTER.addAttributes("embed", "src", "quality", "width", "height", "allowFullScreen",
				"allowScriptAccess", "flashvars", "name", "type", "pluginspage");
	}

	/**
	 * 过滤html代码
	 * @param content html代码
	 * @return 过滤后的代码
	 */
	public static String filterHTML(String content) {
		String s = Jsoup.clean(content, USER_CONTENT_FILTER);
		return s;
	}

	/**
	 * 解析时间 从给定的时间中提取制定的类型
	 * @param type y,M,d,H,m,s
	 * @param date 时间
	 * @return 时间
	 */
	public static int parseDate(String type, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if ("y".equals(type)) {
			return cal.get(Calendar.YEAR);
		}
		if ("M".equals(type)) {
			return cal.get(Calendar.MONTH) + 1;
		}
		if ("d".equals(type)) {
			return cal.get(Calendar.DAY_OF_MONTH);
		}
		if ("H".equals(type)) {
			return cal.get(Calendar.HOUR_OF_DAY);
		}
		if ("m".equals(type)) {
			return cal.get(Calendar.MINUTE);
		}
		if ("s".equals(type)) {
			return cal.get(Calendar.SECOND);
		}
		return 0;
	}

	

	/**
	 * 物理删除附件方法
	 * 
	 * @param request 请求
	 * @param acc acc
	 * @return 成功返回true，失败返回false
	 */
	public static boolean del_acc(HttpServletRequest request, Accessory acc) {
		boolean ret = true;
		boolean ret1 = true;
		if (acc != null) {
			String path = CommUtil.getServerRealPathFromRequest(request) + acc.getPath() + File.separator
							+ acc.getName();
			String small_path = CommUtil.getServerRealPathFromRequest(request) + acc.getPath() + File.separator
							+ acc.getName() + "_small." + acc.getExt();
			ret = deleteFile(path);
			ret1 = deleteFile(small_path);
		}
		return ret && ret1;
	}

	/**
	 * 前台判定是否存在文件
	 * 
	 * @param path 文件路径
	 * @return 存在返回true，失败返回false
	 */
	public static boolean fileExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	
	
	static int totalFolder = 0;
	static int totalFile = 0;
	/**
	 * 计算file文件大小，可以是单个文件也可以是文件夹
	 * 
	 * @param folder 文件夹
	 * @return 文件大小
	 */
	public static double fileSize(File folder) {
		if (folder.exists()) {
			totalFolder++;
			long foldersize = 0;
			File[] filelist = folder.listFiles();
			for (int i = 0; i < filelist.length; i++) {
				if (filelist[i].isDirectory()) {
					foldersize += fileSize(filelist[i]);
				} else {
					totalFile++;
					foldersize += filelist[i].length();
				}
			}
			return div(foldersize, 1024);
		} else {
			return 0;
		}
	}

	/**
	 * 计算文件夹下文件数量
	 * 
	 * @param file 文件
	 * @return 文件夹下的文件个数
	 */
	public static int fileCount(File file) {
		if (file == null) {
			return 0;
		}
		if (!file.isDirectory()) {
			return 1;
		}
		int fileCount = 0;
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isFile()) {
				fileCount++;
			} else if (f.isDirectory()) {
				fileCount++;
				fileCount += fileCount(file); // 如果遇到目录则通过递归调用继续统计
			}
		}
		return fileCount;
	}
	/**
	 * 获取当前请求完整的URL
	 * @param request 请求
	 * @return 请求的完整url
	 */
	public static String getAllUrl(HttpServletRequest request) {
		String queryUrl = request.getRequestURI();
		if (!StringUtils.isNullOrEmpty(request.getQueryString())) {
			queryUrl = queryUrl + "?" + request.getQueryString();
		}
		return queryUrl;
	}

	/**
	 * 根据html颜色代码返回java Color
	 * 
	 * @param color 指定的颜色
	 * @return 颜色对象
	 */
	public static Color getColor(String color) {
		if (color.charAt(0) == '#') {
			color = color.substring(1);
		}
		if (color.length() != 6) {
			return null;
		}
		try {
			int r = Integer.parseInt(color.substring(0, 2), 16);
			int g = Integer.parseInt(color.substring(2, 4), 16);
			int b = Integer.parseInt(color.substring(4), 16);
			return new Color(r, g, b);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	/**
	 * 根据种子a随机出一组长度为length不重复的整型数组
	 * 
	 * @param a 种子
	 * @param length 数组的长度
	 * @return 集合
	 */
	public static Set<Integer> randomInt(int a, int length) {
		Set<Integer> list = new TreeSet<Integer>();
		int size = length;
		if (length > a) {
			size = a;
		}
		while (list.size() < size) {
			Random random = new Random();
			int b = random.nextInt(a);
			list.add(b);
		}
		return list;
	}

	/**
	 * 格式化数字，保留对应的小数位
	 * 
	 * @param obj 对象
	 * @param len 小数点后的长度
	 * @return 浮点数
	 */
	public static Double formatDouble(Object obj, int len) {
		
		String format = "0.0";
		for (int i = 1; i < len; i++) {
			format = format + "0";
		}
		DecimalFormat df = new DecimalFormat(format);
		return Double.valueOf(df.format(obj));
	}

	/**
	 * 判断字符是否为中文
	 * 
	 * @param c 字符
	 * @return 是中文字符返回true，否则返回false
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
						|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
						|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
						|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
						|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串是否为乱码
	 * 
	 * @param strName 字符串
	 * @return 乱码返回true，否则返回false
	 */
	public static boolean isMessyCode(String strName) {
		Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
		Matcher m = p.matcher(strName);
		String after = m.replaceAll("");
		String temp = after.replaceAll("\\p{P}", "");
		char[] ch = temp.trim().toCharArray();
		float chLength = ch.length;
		float count = 0;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!Character.isLetterOrDigit(c)) {

				if (!isChinese(c)) {
					count = count + 1;
				}
			}
		}
		float result = count / chLength;
		return result > 0.4;

	}

	/**
	 * 去掉IP字符串前后所有的空格
	 * 
	 * @param ip ip地址 
	 * @return 处理后的ip地址
	 */
	public static String trimSpaces(String ip) {
		String ipAddress = ip;
		while (ipAddress.startsWith(" ")) {
			ipAddress = ipAddress.substring(1, ipAddress.length()).trim();
		}
		while (ipAddress.endsWith(" ")) {
			ipAddress = ipAddress.substring(0, ipAddress.length() - 1).trim();
		}
		return ipAddress;
	}

	/**
	 * 判断是否是一个ip
	 * 
	 * @param ip 待判读的ip地址
	 * @return 是ip地址就返回true，否则返回false
	 */
	public static boolean isIp(String ip) {
		String ipAddress = ip;
		boolean b = false;
		ipAddress = trimSpaces(ipAddress);
		if (ipAddress.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
			String[] s = ipAddress.split("\\.");
			if (Integer.parseInt(s[0]) < 255) {
				if (Integer.parseInt(s[1]) < 255) {
					if (Integer.parseInt(s[2]) < 255) {
						if (Integer.parseInt(s[3]) < 255) {
							b = true;
						}
					}
				}
			}
		}
		return b;
	}

	/**
	 * 计算当前域名，不含www的顶级域名
	 * 
	 * @param request
	 *            输入请求
	 * @return 顶级域名信息
	 */
	public static String generic_domain(HttpServletRequest request) {
		String systemDomain = "localhost";
		String serverName = request.getServerName();
		if (isIp(serverName)) {
			systemDomain = serverName;
		} else {
			if (serverName.indexOf(".") == serverName.lastIndexOf(".")) {
				systemDomain = serverName;
			} else {
				systemDomain = serverName.substring(serverName.indexOf(".") + 1);
			}
		}
		return systemDomain;
	}

	/**
	 * 判断是否是手机打开网页
	 * @param request http 请求
	 * @return 手机打开返回true， 否则返回false
	 */
	public boolean judgeIsMoblie(HttpServletRequest request) {
		boolean isMoblie = false;
		String[] mobileAgents = { "iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi",
				"opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod", "nokia",
				"samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma", "docomo",
				"up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos", "techfaith",
				"palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem", "wellcom", "bunjalloo",
				"maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos", "pantech", "gionee", "portalmmm",
				"jig browser", "hiptop", "benq", "haier", "^lct", "320x320", "240x320", "176x220", "w3c ", "acs-", "alav",
				"alca", "amoi", "audi", "avan", "benq", "bird", "blac", "blaz", "brew", "cell", "cldc", "cmd-", "dang",
				"doco", "eric", "hipt", "inno", "ipaq", "java", "jigs", "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g",
				"lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi", "mot-", "moto", "mwbp", "nec-", "newt", "noki",
				"oper", "palm", "pana", "pant", "phil", "play", "port", "prox", "qwap", "sage", "sams", "sany", "sch-",
				"sec-", "send", "seri", "sgh-", "shar", "sie-", "siem", "smal", "smar", "sony", "sph-", "symb", "t-mo",
				"teli", "tim-", "tosh", "tsm-", "upg1", "upsi", "vk-v", "voda", "wap-", "wapa", "wapi", "wapp", "wapr",
				"webc", "winw", "winw", "xda", "xda-", "Googlebot-Mobile" };
		if (request.getHeader("User-Agent") != null) {
			for (String mobileAgent : mobileAgents) {
				if (request.getHeader("User-Agent").toLowerCase().indexOf(mobileAgent) >= 0) {
					isMoblie = true;
					break;
				}
			}
		}
		return isMoblie;
	}

	/**
	 * 在字符串中隐藏重要的信息，以*号替换
	 * @param str 待隐藏字符串
	 * @param begin 开始位置
	 * @param end 结束位置
	 * @return 隐藏后的字符串
	 */
	public static String genericStar(String str, int begin, int end) {
		if (str.length() > begin && str.length() >= end) {
			return str.replaceAll(str.substring(begin, end), "********");
		} else {
			return str;
		}
	}

	/**
	 * 将json转为为List<Map>
	 * 
	 * @param json json串
	 * @return 转换后的list列表
	 */
	public static List<Map> json2List(String json) {
		List<Map> list = new ArrayList<Map>();
		if (!StringUtils.isNullOrEmpty(json)) {
			list = Json.fromJson(List.class, json);
		}
		return list;
	}
	
	/**
	 * 将json转为为List<java对象>
	 * @param T java对象类型
	 * @param json json串
	 * @return 转换后的list列表
	 */
	public static List<?> json2List(Class<?> T,String json) {
		List<Object> list = new ArrayList<Object>();
		List<Map> maps = json2List(json);
		for(Map map : maps){
			Object obj = Lang.map2Object(map, T);
			list.add(obj);
		}
		return list;
	}

	/**
	 * 检测字符串是否包含数字、字母、大写字母、特殊符号
	 * 
	 * @param str 待检查的字符串
	 * @return 返回1表示含有数字，返回2表示含有数字+小写字母，返回3表示含有数字+小写字母+大写字母，返回4表示含有数字+小写字母+大写字母+特殊符号
	 */
	public static int checkInput(String str) {
		int num = 0;
		num = Pattern.compile("\\d").matcher(str).find() ? num + 1 : num;
		num = Pattern.compile("[a-z]").matcher(str).find() ? num + 1 : num;
		num = Pattern.compile("[A-Z]").matcher(str).find() ? num + 1 : num;
		num = Pattern.compile("[-.!@#$%^&*()+?><]").matcher(str).find() ? num + 1 : num;
		return num;
	}
	
	/**
	 * 模糊用户名，保护隐私
	 * 
	 * @param userName 待检查的字符串
	 * @return 模糊好的字符串
	 */
	public static String fuzzy(String userName) {
		logger.debug("---------------- CommUtil->fuzzy: param: ----------- " + userName);
		String reg = "[\\w]+@[\\w]+.[\\w]+";
		if (!StringUtils.isNullOrEmpty(userName)) {
			if (userName.matches(reg)) {
				int end = userName.indexOf("@") - 1;
				int start = end / 2;
				StringBuffer sb = new StringBuffer(userName);
				sb.replace(start, end, "****");
				userName = sb.toString();
			} else {
				int len = userName.length();
				if(len >= 11) {
					userName = userName.substring(0, len - (userName.substring(3)).length()) + "****"
							+ userName.substring(7);
				} else if (len >= 6) {
					userName = userName.substring(0, len - 5) + "****" + userName.charAt(len - 1);
				} else {
					userName = userName.substring(0, len / 2) + "****" ;
				}
			}
		}
		return userName;
	}
	
	/**
	 * 从System的属性中获取当前应用的工作根目录，以File.separator结尾
	 * @return
	 */
	public static String  getServerRealPathFromSystemProp() {
		String rootPath = System.getProperty("iskyshopb2b2c.root");
		if(rootPath != null && !rootPath.endsWith(File.separator)) {
			rootPath += File.separator;
		}
		return rootPath;
	}
	
	/**
	 * 从参数Request中获取当前应用的工作根目录，以File.separator结尾
	 * @param request
	 * @return
	 */
	public static String  getServerRealPathFromRequest(HttpServletRequest request) {
		String rootPath = request.getSession().getServletContext().getRealPath("/");
		if(rootPath != null && !rootPath.endsWith(File.separator)) {
			rootPath += File.separator;
		}
		return rootPath;
	}

	/**
	 * html字符转义
	 * @param value
	 * @return
	 */
	public static String convertScriptTag(String value){
		if(value != null){
			value = value.replace("<script", "&lt;script")
					.replace("</script", "&lt;/script");
					//.replace(">", "&gt;")
					//.replace("\"", "&quot;");
		}
		return value;
	}
	
	/**
	 * 图片复制
	 * @param oldPath
	 * @param newPath
	 */
	public static void copyImage(String oldPath, String newPath){
		FileInputStream fi = null;
		BufferedInputStream in = null;
		FileOutputStream fo = null;
		BufferedOutputStream out = null;
		try{
			fi = new FileInputStream(oldPath);
			in = new BufferedInputStream(fi);
			fo = new FileOutputStream(newPath);
			out = new BufferedOutputStream(fo);
			byte[] buf = new byte[4096];
			int len = in.read(buf);
			while (len != -1){
				out.write(buf, 0, len);
				len = in.read(buf);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}finally{
			try{
				out.close();
				fo.close();
				in.close();
				fi.close();
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取webPath，返回值末尾不会包含字符"/"
	 * @param request
	 * @param config
	 * @return
	 */
	public static String getWebPath(HttpServletRequest request, SysConfig config) {
		String contextPath = "";
		if (!("/").equals(request.getContextPath())) {
			contextPath = request.getContextPath();
		}
		String webPath = CommUtil.getURL(request);
		String port = request.getServerPort() == 80 ? "" : ":" + CommUtil.null2Int(request.getServerPort());
		if (Globals.SSO_SIGN && config.isSecond_domain_open() && !"localhost".equals(CommUtil.generic_domain(request))
				&& !CommUtil.isIp(request.getServerName())) {
			webPath = "http://www." + CommUtil.generic_domain(request) + port + contextPath;
		}
		return webPath;
	}


	/**
	 * 将targetStr中与regex匹配的字符串替换为replacement，然后返回处理后的targetStrs
	 * @param targetStr 若为空串或null，则不对targetStr进行处理
	 * @param regex 不能为空串和null
	 * @param replacement 若为null，则对targetStr不进行处理
	 * @return
	 */
	public static String replaceStr(String targetStr, String regex, String replacement) {
		if(!StringUtils.isNullOrEmpty(targetStr) && !StringUtils.isNullOrEmpty(regex) && replacement != null) {
			return targetStr.replaceAll(regex, replacement);
		}
		
		return targetStr;
	}
	
	/**
	 * 用于在页面中
	 * @return
	 */
	public static String getHXCode() {
		return GoodsConfig.CODE_HX;
	}
	
	/**
	 * 截取小数
	 */
	public static double cutDecimals(Object obj, int count){
		 DecimalFormat formater = new DecimalFormat(); 
		 formater.setMaximumFractionDigits(count); 
		 formater.setGroupingSize(0); 
		 formater.setRoundingMode(RoundingMode.FLOOR); 
		 return Double.valueOf(formater.format(obj));
	}
	
	/**
	 * 过滤名称中的html代码
	 * 
	 * @param inputString
	 * @return
	 */
	public static String clearContent(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		java.util.regex.Pattern p_html1;
		java.util.regex.Matcher m_html1;

		try {
			String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
			String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			String regEx_html1 = "<[^>]+";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(""); // 过滤html标签

			textStr = htmlStr;
		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}
		return textStr;// 返回文本字符串
	}
	
	/**
	 * 将数组字符串转成字符串
	 * @param arr 
	 * @param separator 使用的分隔符
	 * @return
	 */
	public static String arrayToString(String[] arr,char separator)
	{
		if(arr==null || arr.length<=0)
			return "";
		
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<arr.length;i++){
			if(i>0)
				sb.append(separator);
			sb.append(arr[i]);
		}
		
		return sb.toString();
	}
	
	/**
	 * 将字符串转换成List数组
	 * @param str
	 * @param sep 
	 * @return
	 */
	public static List<String> string2List(String str,String sep)
	{
		List<String> result=new ArrayList<String>();
		if(str==null || str.length()<=0)
			return result;
		
		if(sep==null || "".equals(sep))
			sep=",";
		
		String[] arr=str.split(sep);
		for(String elm:arr)
			result.add(elm);
		
		return result;
	}
	
	/**
	 * 返回去除了查询字符串和session的url重写串的uri
	 * @param request
	 * @return
	 */
	public static String getPureUri(String requetUri) {
		if(requetUri != null && !"".equals(requetUri.trim())) {
			int idx = requetUri.indexOf(';');

	        if (idx > 0) {
	        	requetUri = requetUri.substring(0, idx);
	        }

	        idx = requetUri.indexOf('?');

	        if (idx > 0) {
	        	requetUri = requetUri.substring(0, idx);
	        }
		}
        
        return requetUri;
	}
	
}
