package com.iskyshop.manage.admin.action;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.csvreader.CsvReader;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.WaterMark;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsConfigService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IWaterMarkService;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * @info 淘宝数据控制器 1.0版本 功能包括：淘宝csv导入
 * @since V1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com hezeng
 */
@Controller
public class SelfTaobaoManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IWaterMarkService waterMarkService;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private FTPServerTools ftpTools;
	
	@Autowired
	private IGoodsConfigService goodsConfigService;

	@SecurityMapping(title = "导入淘宝CSV", value = "/admin/taobao.htm*", rtype = "admin", rname = "淘宝导入", rcode = "taobao_self", rgroup = "自营")
	@RequestMapping("/admin/taobao.htm")
	public ModelAndView taobao(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/taobao.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String taobao_upload_status = CommUtil.null2String(request.getSession(true).getAttribute("taobao_upload_status"));
		if (StringUtils.isNullOrEmpty(taobao_upload_status)) {
			Map params = new HashMap();
			params.clear();
			params.put("display", true);
			List<GoodsClass> gcs = this.goodsClassService.query(
					"select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
					params, -1, -1);
			mv.addObject("gcs", gcs);
		}
		if ("upload_img".equals(taobao_upload_status)) {
			mv = new JModelAndView("admin/blue/taobao_import_img.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			HashMap params = new HashMap();
			params.put("userRole", "ADMIN");
			params.put("userRole2", "ADMIN_SELLER");
			List<Album> albums = this.albumService.query(
					"select obj from Album obj where (obj.user.userRole=:userRole or obj.user.userRole=:userRole2) order by obj.album_sequence asc",
					params, -1, -1);
			mv.addObject("albums", albums);
			mv.addObject("already_import_count", request.getSession(true).getAttribute("already_import_count"));
			mv.addObject("no_import_count", request.getSession(true).getAttribute("no_import_count"));
			mv.addObject("jsessionid", request.getSession().getId());
		}
		if ("upload_finish".equals(taobao_upload_status)) {
			mv = new JModelAndView("admin/blue/taobao_import_finish.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
		}
		return mv;
	}

	@SecurityMapping(title = "导入淘宝CSV", value = "/admin/taobao_import_csv.htm*", rtype = "admin", rname = "淘宝导入", rcode = "taobao_self", rgroup = "自营")
	@RequestMapping("/admin/taobao_import_csv.htm")
	public ModelAndView taobao_import_csv(HttpServletRequest request, HttpServletResponse response, String gc_id3,
			String gc_id2) {
		ModelAndView mv = new JModelAndView("admin/blue/taobao_import_img.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String taobao_upload_status = CommUtil.null2String(request.getSession(true).getAttribute("taobao_upload_status"));
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String path = CommUtil.getServerRealPathFromRequest(request) + "csv" + File.separator
				+ "self";
		CommUtil.createFolder(path);
		int already_import_count = 0;
		int no_import_count = 0;
		List<Goods> taobao_goods_list = new ArrayList<Goods>();
		try {
			Map map = CommUtil.saveFileToServer(request, "taobao_cvs", path, "taobao.cvs", null);
			if (!StringUtils.isNullOrEmpty(map.get("fileName"))) {
				String csvFilePath = path + File.separator + "taobao.cvs";
				CsvReader reader = new CsvReader(csvFilePath, '\t', Charset.forName("UTF-16LE")); // 一般用这编码读就可以了
				reader.readHeaders(); // 跳过表头 如果需要表头的话，不要写这句。
				reader.readHeaders();
				reader.readHeaders(); // 此处针对淘宝助理5.7版本特别处理。
				int goods_name_pos = 0; // 商品名称位置
				int goods_price_pos = 7; // 商品价格位置
				int goods_count_pos = 9; // 商品数量位置
				int goods_detail_pos = 20; // 商品描述位置
				int goods_transfee_pos = 11; // 商品运费承担
				int goods_recommend_pos = 18; // 商品推荐位置
				User user = SecurityUserHolder.getCurrentUser();
				Album album = this.albumService.getDefaultAlbum(user.getId());
				if (album == null) {
					album = new Album();
					album.setAddTime(new Date());
					album.setAlbum_name("默认相册");
					album.setAlbum_sequence(-10000);
					album.setAlbum_default(true);
					album.setUser(user);
					this.albumService.save(album);
				}
				
				Map<String,String> goodsConfigMap = new HashMap<String,String>();
				goodsConfigMap.put("configCode", "ptsp");
				List<GoodsConfig> goodsConfigList=this.goodsConfigService.query("select obj from GoodsConfig obj where obj.configCode=:configCode", goodsConfigMap, -1, -1);
				GoodsConfig goodsConfig=goodsConfigList.get(0);
				
				while (reader.readRecord()) { // 逐行读入除表头的数据
					Goods goods = new Goods();
					goods.setGoods_name(reader.get(goods_name_pos));
					goods.setStore_price(BigDecimal.valueOf(CommUtil.null2Double(reader.get(goods_price_pos))));
					goods.setGoods_price(goods.getStore_price());
					goods.setGoods_inventory(CommUtil.null2Int(reader.get(goods_count_pos)));
					goods.setGoods_status(1); // 平台默认状态为在仓库中
					goods.setGoods_recommend(CommUtil.null2Boolean(reader.get(goods_recommend_pos)));
					goods.setGoods_details(reader.get(goods_detail_pos));
					goods.setUser_admin(user);
					goods.setGoods_transfee(CommUtil.null2Int(reader.get(goods_transfee_pos)) - 1);
					goods.setGoods_current_price(goods.getStore_price());
					goods.setAddTime(new Date());
					goods.setGoods_type(0);
					goods.setGoods_seller_time(new Date());
					goods.setInventory_type("all");
					GoodsClass gc = null;
					if (gc_id2 != null) {
						gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id2));
					}
					if (gc_id3 != null) {
						gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id3));
					}
					goods.setGc(gc);
					
					goods.setDescription_evaluate(new BigDecimal(0));
					goods.setWell_evaluate(0);
					goods.setMiddle_evaluate(0);
					goods.setBad_evaluate(0);
					
					goods.setGoodsConfig(goodsConfig);
					
					this.goodsService.save(goods);
					taobao_goods_list.add(goods);
					already_import_count++;
				}
				reader.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		if (already_import_count > 0) {
			HashMap params = new HashMap();
			params.put("userRole", "ADMIN");
			params.put("userRole2", "ADMIN_SELLER");
			List<Album> albums = this.albumService.query(
					"select obj from Album obj where (obj.user.userRole=:userRole or obj.user.userRole=:userRole2) order by obj.album_sequence asc",
					params, -1, -1);
			mv.addObject("albums", albums);
			mv.addObject("jsessionid", request.getSession().getId());
			request.getSession(true).setAttribute("taobao_goods_list", taobao_goods_list);
			request.getSession(true).setAttribute("taobao_upload_status", "upload_img");
			request.getSession(true).setAttribute("already_import_count", already_import_count);
			request.getSession(true).setAttribute("no_import_count", no_import_count);
		}
		mv.addObject("already_import_count", already_import_count);
		mv.addObject("no_import_count", no_import_count);
		return mv;
	}

	@SecurityMapping(title = "上传淘宝图片", value = "/admin/taobao_img_upload.htm*", rtype = "admin", rname = "淘宝导入", rcode = "taobao_self", rgroup = "自营")
	@RequestMapping("/admin/taobao_img_upload.htm")
	public void taobao_img_upload(HttpServletRequest request, HttpServletResponse response, String album_id,
			String ajaxUploadMark) {
		logger.debug("-----------SelfTaobaoManageAction taobao_img_upload begin-----------");
		logger.debug("-----------ajaxUploadMark:" + ajaxUploadMark);
		Boolean html5Uploadret = false;
		Map ajaxUploadInfo = null;
		CsvReader reader = null;
		try {
			//1.获取当前上传图片的文件名
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("imgFile");
			String upload_img_name = file.getOriginalFilename();
			upload_img_name = upload_img_name.substring(0, upload_img_name.indexOf("."));
			logger.debug("-----------upload_img_name:" + upload_img_name);
			
			//2.获取上传的csv文件
			String csv_path = CommUtil.getServerRealPathFromRequest(request) + "csv" + File.separator
					+ "self";
			String csvFilePath = csv_path + File.separator + "taobao.cvs";
			reader = new CsvReader(csvFilePath, '\t', Charset.forName("UTF-16LE")); // 一般用这编码读就可以了
			reader.readHeaders(); // 跳过表头 如果需要表头的话，不要写这句。
			reader.readHeaders();
			reader.readHeaders(); // 此处针对淘宝助理5.7版本特别处理。
			int goods_name_pos = 0; // 商品名称位置
			int goods_price_pos = 7; // 商品价格位置
			int goods_photo_pos = 28; // 商品图片位置

			//3.获取csv文件对应的商品
			List<Goods> goods_list = (List<Goods>) request.getSession(true).getAttribute("taobao_goods_list");
			logger.debug("-----------goods_list size:" + goods_list==null ? 0 : goods_list.size());
			
			//4.获取当前图片所属的商品及该图片是否为该商品的主图片
			Goods goods = null;
			Boolean ret = false; //是否为主图片
			//4.1 遍历csv文件，找到当前图片对应的cvs文件记录行 
			while (reader.readRecord()) {
				if (reader.get(goods_photo_pos).indexOf(upload_img_name) >= 0) {
					//4.2依据商品名称、商品价格匹配到该记录行对应的商品
					String goods_name = reader.get(goods_name_pos);
					double goods_price = CommUtil.null2Double(reader.get(goods_price_pos));
					for (Goods temp_goods : goods_list) {
						if (temp_goods.getGoods_name().equals(goods_name)
								&& CommUtil.null2Double(temp_goods.getGoods_price()) == goods_price) {
							goods = temp_goods;
							if (reader.get(goods_photo_pos).indexOf(upload_img_name) == 0) {
								ret = true;
							}
							break;
						}
					}
				}
			}
			logger.debug("-----------goods_id:" + goods==null ? -1 : goods.getId());
			logger.debug("-----------is main image:" + ret);
			
//			if (goods != null) {
			// 5.上传图片
			Map map = CommUtil.saveFileToServer(request, "imgFile",
					CommUtil.getServerRealPathFromRequest(request)
							+ this.configService.getSysConfig().getUploadFilePath() + File.separator + "cache",
					upload_img_name + ".tbi", null);
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			WaterMark mark = this.waterMarkService.getObjByProperty(null, "user.id", user.getId());
			if (mark != null) {
				String upload_image_dir=CommUtil.getServerRealPathFromRequest(request)+this.configService.getSysConfig().getUploadFilePath() + File.separator + "cache";
				if (mark.isWm_image_open() && mark.getWm_image() != null) {
					this.ftpTools.systemDownloadWaterImg(mark);
					String pressImg = csv_path + File.separator + mark.getWm_image().getName();
					String targetImg = upload_image_dir + File.separator + map.get("fileName");
					int pos = mark.getWm_image_pos();
					float alpha = mark.getWm_image_alpha();
					CommUtil.waterMarkWithImage(pressImg, targetImg, pos, alpha);
					this.ftpTools.DeleteWebImg(mark.getWm_image());
				}
				if (mark.isWm_text_open()) {
					String targetImg = upload_image_dir + File.separator + map.get("fileName");
					int pos = mark.getWm_text_pos();
					String text = mark.getWm_text();
					String markContentColor = mark.getWm_text_color();
					this.waterMarkWithText(targetImg, targetImg, text, markContentColor,
							new Font(mark.getWm_text_font(), Font.BOLD, mark.getWm_text_font_size()), pos, 100f);
				}
			}
			
			//6.保存图片附件表
			Accessory image = new Accessory();
			image.setAddTime(new Date());
			image.setExt((String) map.get("mime"));
			image.setPath(CommUtil.getServerRealPathFromRequest(request) + this.configService.getSysConfig().getUploadFilePath() + File.separator + "cache");
			image.setWidth(CommUtil.null2Int(map.get("width")));
			image.setHeight(CommUtil.null2Int(map.get("height")));
			image.setName(CommUtil.null2String(map.get("fileName")));
			image.setUser(user);
			Album album = null;
			if (!StringUtils.isNullOrEmpty(album_id)) {
				album = this.albumService.getObjById(CommUtil.null2Long(album_id));
			} else {
				album = this.albumService.getDefaultAlbum(CommUtil.null2Long(user.getId()));
				if (album == null) {
					album = new Album();
					album.setAddTime(new Date());
					album.setAlbum_name("默认相册");
					album.setAlbum_sequence(-10000);
					album.setUser(user);
					album.setAlbum_default(true);
					this.albumService.save(album);
				}
			}
			image.setAlbum(album);
			html5Uploadret = this.accessoryService.save(image);
			logger.debug("-----------save accessory end,accessory_id:" + image==null ? -1 : image.getId());
			
			//7.更新商品表与图片附件表的关联关系
			if (ret) {
				goods.setGoods_main_photo(image);
			} else {
				goods.getGoods_photos().add(image);
			}
			
			try{
				this.goodsService.update(goods);
			}catch(Exception e){
				logger.info(e.getMessage());
			}
			
			logger.debug("-----------update goods end,goods_id:" + goods.getId());
			
			//8.更新附件表
			// 同步生成小图片
			String ext = image.getExt().indexOf(".") < 0 ? "." + image.getExt() : image.getExt();
			String source = image.getPath() + File.separator + image.getName();
			String target = source + "_small" + ext;
			CommUtil.createSmall(source, target, this.configService.getSysConfig().getSmallWidth(),
					this.configService.getSysConfig().getSmallHeight());
			// 同步生成中等图片
			String midext = image.getExt().indexOf(".") < 0 ? "." + image.getExt() : image.getExt();
			String midtarget = source + "_middle" + ext;
			CommUtil.createSmall(source, midtarget, this.configService.getSysConfig().getMiddleWidth(),
					this.configService.getSysConfig().getMiddleHeight());
			String photo_url = this.storeTools.createAdminFolderURL() + "/taobao";
			// 上传小图
			this.ftpTools.systemUpload(image.getName() + "_small" + ext, "/" + photo_url);
			// 上传中图
			this.ftpTools.systemUpload(image.getName() + "_middle" + ext, "/" + photo_url);
			// 上传原图并更新路径
			String new_url = this.ftpTools.systemUpload(image.getName(), "/" + photo_url);
			image.setPath(new_url);
			this.accessoryService.update(image);
			logger.debug("-----------update accessory end,accessory_id:" + image.getId());
			
			if (html5Uploadret && ajaxUploadMark != null) {
				ajaxUploadInfo = new HashMap<String, String>();
				ajaxUploadInfo.put("url", image.getPath() + "/" + image.getName());
			}
//			} else {
//				Map json_map = new HashMap();
//				json_map.put("url", "");
//				json_map.put("id", "");
//				json_map.put("remainSpace", 0);
//			}
		} catch (IOException e) {
			//e.printStackTrace();
			logger.error(e);
		} finally{
			if(null != reader){
				reader.close();
			}
		}
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			if (ajaxUploadMark != null) {
				writer.print(Json.toJson(ajaxUploadInfo, JsonFormat.compact()));
			} else {
				writer.print(html5Uploadret);
			}
		} catch (IOException e) {
			logger.error(e);
		}
		logger.debug("-----------SelfTaobaoManageAction taobao_img_upload end-----------");
	}

	@SecurityMapping(title = "淘宝导入完成", value = "/admin/taobao_import_finish.htm*", rtype = "admin", rname = "淘宝导入", rcode = "taobao_self", rgroup = "自营")
	@RequestMapping("/admin/taobao_import_finish.htm")
	public ModelAndView taobao_import_finish(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/taobao_import_finish.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		request.getSession(true).removeAttribute("taobao_upload_status");
		request.getSession(true).removeAttribute("taobao_goods_list");
		request.getSession(true).removeAttribute("already_import_count");
		request.getSession(true).removeAttribute("no_import_count");
		return mv;
	}

	/**
	 * 淘宝授权返回处理
	 * 
	 * @param request
	 * @param response
	 * @param code
	 * @param state
	 * @return
	 */
	@RequestMapping("/admin/taobao_authorize.htm")
	public ModelAndView taobao_authorize(HttpServletRequest request, HttpServletResponse response, String code,
			String state) {
		ModelAndView mv = new JModelAndView("admin/blue/taobao_import_finish.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}

	private static boolean waterMarkWithText(String filePath, String outPath, String text, String markContentColor,
			Font font, int pos, float qualNum) {
		ImageIcon imgIcon = new ImageIcon(filePath);
		Image theImg = imgIcon.getImage();
		int width = theImg.getWidth(null);
		int height = theImg.getHeight(null);
		BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimage.createGraphics();
		if (font == null) {
			font = new Font("宋体", Font.BOLD, 20);
			g.setFont(font);
		} else {
			g.setFont(font);
		}
		g.setColor(CommUtil.getColor(markContentColor));
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
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
		try (FileOutputStream out = new FileOutputStream(outPath);) {
			ImageIO.write(bimage, "JPEG", out);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
