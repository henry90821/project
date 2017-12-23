package com.iskyshop.manage.seller.action;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.domain.WaterMark;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsConfigService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IWaterMarkService;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * 
 * <p>
 * Title: TaobaoSellerAction.java
 * </p>
 * 
 * <p>
 * Description:淘宝数据控制器 1.0版本 功能包括：淘宝csv导入，目前支持淘宝助理5.7的数据导入，包括tbi图片的导入（自动生成大、中、小三种图片尺寸）
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
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class TaobaoSellerAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
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
	private IStoreService storeService;
	@Autowired
	private IGoodsConfigService goodsConfigService;


	@SecurityMapping(title = "导入淘宝CSV", value = "/seller/taobao.htm*", rtype = "seller", rname = "淘宝导入", rcode = "taobao_seller", rgroup = "商品管理")
	@RequestMapping("/seller/taobao.htm")
	public ModelAndView taobao(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/taobao.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String taobao_upload_status = CommUtil.null2String(request.getSession(true).getAttribute("taobao_upload_status"));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (StringUtils.isNullOrEmpty(taobao_upload_status)) {
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("display", true);
			List<UserGoodsClass> ugcs = this.userGoodsClassService.query(
					"select obj from UserGoodsClass obj where obj.user_id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
					params, -1, -1);
			mv.addObject("ugcs", ugcs);
			
			//经营的一级分类
			List<GoodsClass> gcs = this.storeTools.query_store_detail_MainGc(user.getStore().getGc_detail_info());
			if (gcs.size() == 0) { // 店铺未设置详细类目
				GoodsClass parent = this.goodsClassService.getObjById(user.getStore().getGc_main_id());
				gcs.add(parent);
			}
			mv.addObject("gcs", gcs);
		}
		if ("upload_img".equals(taobao_upload_status)) {
			mv = new JModelAndView("user/default/sellercenter/taobao_import_img.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			HashMap params = new HashMap();
			params.put("user_id", user.getId());
			List<Album> alubms = this.albumService.query("select obj from Album obj where obj.user.id=:user_id", params, -1,
					-1);
			mv.addObject("albums", alubms);
			mv.addObject("already_import_count", request.getSession(true).getAttribute("already_import_count"));
			mv.addObject("no_import_count", request.getSession(true).getAttribute("no_import_count"));
			mv.addObject("jsessionid", request.getSession().getId());
		}
		if ("upload_finish".equals(taobao_upload_status)) {
			mv = new JModelAndView("user/default/sellercenter/taobao_import_finish.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
		}
		return mv;
	}

	@SecurityMapping(title = "导入淘宝CSV", value = "/seller/taobao_import_csv.htm*", rtype = "seller", rname = "淘宝导入", rcode = "taobao_seller", rgroup = "商品管理")
	@RequestMapping("/seller/taobao_import_csv.htm")
	public ModelAndView taobao_import_csv(HttpServletRequest request, HttpServletResponse response, String gc_id3,
			String gc_id2, String gc_id1, String ugc_ids) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/taobao_import_img.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String taobao_upload_status = CommUtil.null2String(request.getSession(true).getAttribute("taobao_upload_status"));
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String path = CommUtil.getServerRealPathFromRequest(request) + "csv" + File.separator
				+ store.getId();
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
					goods.setGoods_recommend(CommUtil.null2Boolean(reader.get(goods_recommend_pos)));
					goods.setGoods_details(reader.get(goods_detail_pos));
					goods.setGoods_transfee(CommUtil.null2Int(reader.get(goods_transfee_pos)) - 1);
					goods.setGoods_current_price(goods.getStore_price());
					goods.setAddTime(new Date());
					goods.setGoods_seller_time(new Date());
					goods.setGoods_type(1);
					goods.setGoods_store(user.getStore());
					goods.setInventory_type("all");
					GoodsClass gc = null;
					if (gc_id3 != null) {
						gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id3));
					} else if (gc_id2 != null) {
						gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id2));
					} else if (gc_id1 != null) {
						gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id1));
					}
					goods.setGc(gc);
					String[] ugc_id_list = ugc_ids.split(",");
					for (String ugc_id : ugc_id_list) {
						UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil.null2Long(ugc_id));
						goods.getGoods_ugcs().add(ugc);
					}
					// 确定商品状态					
					goods.setPublish_goods_status(1);
					if (store.getGrade().getGoods_audit() == 1) {//不需要审核
						goods.setGoods_status(1);
					} else {
						goods.setGoods_status(-5);
					}
					
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
			logger.error(e);
		}
		if (already_import_count > 0) {
			HashMap params = new HashMap();
			params.put("user_id", user.getId());
			List<Album> alubms = this.albumService.query("select obj from Album obj where obj.user.id=:user_id", params, -1,
					-1);
			mv.addObject("albums", alubms);
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

	@SecurityMapping(title = "上传淘宝图片", value = "/seller/taobao_img_upload.htm*", rtype = "seller", rname = "淘宝导入", rcode = "taobao_seller", rgroup = "商品管理")
	@RequestMapping("/seller/taobao_img_upload.htm")
	public void taobao_img_upload(HttpServletRequest request, HttpServletResponse response, String album_id,
			String ajaxUploadMark) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String csv_path = CommUtil.getServerRealPathFromRequest(request) + "csv" + File.separator
				+ user.getStore().getId();
		Boolean html5Uploadret = false;
		Map ajaxUploadInfo = null;
		try {
			String csvFilePath = csv_path + File.separator + "taobao.cvs";
			CsvReader reader = new CsvReader(csvFilePath, '\t', Charset.forName("UTF-16LE")); // 一般用这编码读就可以了
			reader.readHeaders(); // 跳过表头 如果需要表头的话，不要写这句。
			reader.readHeaders();
			reader.readHeaders(); // 此处针对淘宝助理5.7版本特别处理。
			int goods_name_pos = 0; // 商品名称位置
			int goods_price_pos = 7; // 商品价格位置
			int goods_photo_pos = 28; // 商品图片位置

			String photo_url = this.storeTools.createUserFolderURL(user.getStore()) + "/taobao";

			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("imgFile");
			String upload_img_name = file.getOriginalFilename();
			upload_img_name = upload_img_name.substring(0, upload_img_name.indexOf("."));
			double fileSize = Double.valueOf(file.getSize()); // 返回文件大小，单位为byte
			fileSize = fileSize / 1048576;
			BigDecimal sizeusing = user.getStore().getSpacesize();
			double remainSpace = 0;
			if (user.getStore().getGrade().getSpaceSize() > 0) {
				remainSpace = CommUtil.subtract(user.getStore().getGrade().getSpaceSize(),
						BigDecimal.valueOf(CommUtil.null2Double(sizeusing)));

			} else {
				remainSpace = 10000000;
			}
			Map json_map = new HashMap();
			List<Goods> goods_list = (List<Goods>) request.getSession(true).getAttribute("taobao_goods_list");
			Goods goods = null;
			Boolean ret = false;
			while (reader.readRecord()) { // 逐行读入除表头的数据
				if (reader.get(goods_photo_pos).indexOf(upload_img_name) >= 0) {
					String goods_name = reader.get(goods_name_pos);
					double goods_price = CommUtil.null2Double(reader.get(goods_price_pos));
					for (Goods temp_goods : goods_list) {
						if (temp_goods.getGoods_name().equals(goods_name)
								&& CommUtil.null2Double(temp_goods.getGoods_price()) == goods_price) {
							goods = temp_goods;
							if (reader.get(goods_photo_pos).indexOf(upload_img_name) == 0) {
								ret = true;
							}
						}
					}
				}
			}
			reader.close();
			if (goods != null) {
				// 上传指定图片
				if (remainSpace > fileSize) {
					try {
						String saveFilePathName = this.storeTools.createUserFolder(request, user.getStore());//上传上来的文件所在的目录路径
						Map map = CommUtil.saveFileToServer(request, "imgFile", saveFilePathName, upload_img_name + ".tbi", null);
						String imgName = CommUtil.null2String(map.get("fileName"));
						String source = saveFilePathName + File.separator + imgName;//原图在web服务器上的路径
						String ext = (String) map.get("mime");
						ext = ext.indexOf(".") < 0 ? "." + ext : ext;
						String userId = user.getId().toString();
						
						Map params = new HashMap();
						params.put("store_id", user.getStore().getId());
						List<WaterMark> wms = this.waterMarkService
								.query("select obj from WaterMark obj where obj.store.id=:store_id", params, -1, -1);
						if (wms.size() > 0) {
							WaterMark mark = wms.get(0);
							if (mark.isWm_image_open() && mark.getWm_image() != null) {
								this.ftpTools.userDownloadWaterImg(mark, CommUtil.null2String(user.getId()));
								String pressImg = this.storeTools.createUserFolder(request, user.getStore()) + File.separator + mark.getWm_image().getName();
								int pos = mark.getWm_image_pos();
								float alpha = mark.getWm_image_alpha();
								CommUtil.waterMarkWithImage(pressImg, source, pos, alpha);//参数1水印图片，参数2原图
								this.ftpTools.DeleteWebImg(mark.getWm_image());
							}
							if (mark.isWm_text_open()) {
								int pos = mark.getWm_text_pos();
								String text = mark.getWm_text();
								String markContentColor = mark.getWm_text_color();
								CommUtil.waterMarkWithText(source, source, text, markContentColor,
										new Font(mark.getWm_text_font(), Font.BOLD, mark.getWm_text_font_size()), pos, 100f);
							}
						}
						
						//生成大图
						String bigTarget = source;
						CommUtil.createSmall(source, bigTarget, this.configService.getSysConfig().getBigWidth(),
								this.configService.getSysConfig().getBigHeight());
						
						//生成小图片
						String target = source + "_small" + ext;
						CommUtil.createSmall(source, target, this.configService.getSysConfig().getSmallWidth(),
								this.configService.getSysConfig().getSmallHeight());
						
						//生成中等图片
						String midtarget = source + "_middle" + ext;
						CommUtil.createSmall(source, midtarget, this.configService.getSysConfig().getMiddleWidth(),
								this.configService.getSysConfig().getMiddleHeight());
						
						// 上传大图
						String bigImgFTPPath = this.ftpTools.userUpload(imgName, "/" + photo_url, userId);
						
						// 上传小图
						this.ftpTools.userUpload(imgName + "_small" + ext, "/" + photo_url, userId);
						
						// 上传中图
						this.ftpTools.userUpload(imgName + "_middle" + ext, "/" + photo_url, userId);
						
						Accessory image = new Accessory();
						image.setPath(bigImgFTPPath);
						image.setAddTime(new Date());
						image.setExt((String) map.get("mime"));
						image.setWidth(this.configService.getSysConfig().getBigWidth());
						image.setHeight(this.configService.getSysConfig().getBigHeight());
						image.setName(imgName);
						image.setSize(BigDecimal.valueOf((new File(bigTarget)).length()));
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
								album.setAlbum_default(true);
								album.setUser(user);
								this.albumService.save(album);
							}
						}
						image.setAlbum(album);
						html5Uploadret = this.accessoryService.save(image);
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
						
						json_map.put("id", image.getId());
						json_map.put("remainSpace", remainSpace == 10000000 ? 0 : remainSpace);						
						if (html5Uploadret && ajaxUploadMark != null) {
							ajaxUploadInfo = new HashMap<String, String>();
							ajaxUploadInfo.put("url", image.getPath() + "/" + image.getName());
						}
						json_map.put("url", image.getPath() + "/" + image.getName());
						if (user.getStore().getGrade().getSpaceSize() > 0) {
							Double filesize = CommUtil.null2Double(map.get("fileSize"));
							BigDecimal storespace = user.getStore().getSpacesize();
							double allsize = CommUtil.add(storespace, BigDecimal.valueOf(filesize / 1048576));
							user.getStore().setSpacesize(BigDecimal.valueOf(allsize));
							this.storeService.update(user.getStore());
							remainSpace = CommUtil.subtract(user.getStore().getGrade().getSpaceSize(),
									BigDecimal.valueOf(CommUtil.null2Double(user.getStore().getSpacesize())));
						} else {
							remainSpace = 0;
						}
						json_map.put("remainSpace", remainSpace);
					} catch (IOException e) {
						logger.error(e);
					}
				} else {
					json_map.put("url", "");
					json_map.put("id", "");
					json_map.put("remainSpace", 0);

				}
				// 图片上传完毕
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
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
			logger.error(e.getMessage(), e);
		}
	}

	@SecurityMapping(title = "淘宝导入完成", value = "/seller/taobao_import_finish.htm*", rtype = "seller", rname = "淘宝导入", rcode = "taobao_seller", rgroup = "商品管理")
	@RequestMapping("/seller/taobao_import_finish.htm")
	public ModelAndView taobao_import_finish(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/taobao_import_finish.html",
				configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
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
	@RequestMapping("/seller/taobao_authorize.htm")
	public ModelAndView taobao_authorize(HttpServletRequest request, HttpServletResponse response, String code,
			String state) {
		ModelAndView mv = new JModelAndView("user/default/sellercenter/taobao_import_finish.html",
				configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}
}
