package com.iskyshop.view.web.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.ProductMapping;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IProductMappingService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.UserTools;


/**
 * 系统验证控制器，用来管理系统验证码生成、用户名验证、邮箱验证等各类验证请求
 */
@Controller
public class VerifyAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IUserService userService;
	@Autowired
	private UserTools userTools;
	@Autowired
	private IStoreService storeService;

	@Autowired
	private IProductMappingService productMappingService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IShipAddressService shipAddressService;

	/**
	 * 验证码ajax验证方法
	 * 
	 * @param response
	 */
	@RequestMapping("/verify_code.htm")
	public void validate_code(HttpServletRequest request, HttpServletResponse response, String code, String code_name) {
		HttpSession session = request.getSession(true);
		String verify_code = "";
		if ("".equals(CommUtil.null2String(code_name))) {
			verify_code = (String) session.getAttribute("verify_code");
		} else {
			verify_code = (String) session.getAttribute(code_name);
		}
		boolean ret = true;
		if (verify_code != null && !"".equals(verify_code)) {
			if (!CommUtil.null2String(code.toUpperCase()).equals(verify_code)) {
				ret = false;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 门店ID
	 * ajax验证方法
	 * @param response
	 */
	@RequestMapping("/verify_sa_code.htm")
	public void verify_sa_code(HttpServletRequest request, HttpServletResponse response, String sa_code,String shipId){
		String codeFlag="no";
		if(shipId != null && !"".equals(shipId)){
			Map params = new HashMap();
			params.put("shipId", Long.parseLong(shipId));
			params.put("sa_code", sa_code);
			List<ShipAddress> shipAddressList=this.shipAddressService.
					query("select obj from ShipAddress obj where obj.id!=:shipId and obj.sa_code=:sa_code", params, -1,-1);
		    if(shipAddressList != null && shipAddressList.size()>0){
		    	codeFlag="yes";
		    }
		}else{
			Map params = new HashMap();
			params.put("sa_code", sa_code);
			List<ShipAddress> shipAddressList=this.shipAddressService.
					query("select obj from ShipAddress obj where obj.sa_code=:sa_code", params, -1,-1);
			if(shipAddressList != null && shipAddressList.size()>0){
				codeFlag="yes";
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.print(codeFlag);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			writer.close();
	    }
	}

	/**
	 * 用户名ajax验证方法(返回false:表示用户存在，true：表示用户不存在)
	 * 
	 * @param response
	 * @param userMark 若role为“BUYER”，则userMark为用户的mobile字段的值；若role为“ADMIN”，则userMark为用户的userName字段的值；若role为“SELLER”，
	 *            则userMark为用户的sellerLoginAccount字段的值
	 * @param role
	 */
	@RequestMapping("/verify_username.htm")
	public void doesUserNotExist(HttpServletResponse response, String userMark, String role) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(userTools.getUser(role, userMark, null) == null ? true: false);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 用户名在crm和商城是否存在ajax验证方法(返回false:表示用户不存在，true：表示用户存在)。此方法只能验证Buyer和主账号的Seller用户名，不能验证管理员和卖家子账号。
	 * 
	 * @param response
	 */
	@RequestMapping("/verify_username2.htm")
	public void verify_username2(HttpServletResponse response, String mobile) {
		boolean ret = false;//用户不存在
		if(userService.getBuyerOrMainSellerByMobile(mobile) != null) {
			ret = true;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			logger.error(e);
		}
	}
	

	/**
	 * 验证Email
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 */
	@RequestMapping("/verify_email.htm")
	public void verify_email(HttpServletRequest request, HttpServletResponse response, String email, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("email", email);
		params.put("id", CommUtil.null2Long(id));
		List<User> users = this.userService.query("select obj.id from User obj where obj.email=:email and obj.id!=:id",
				params, -1, -1);
		if (users != null && users.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 验证店铺名称是否重复
	 * 
	 * @param request
	 * @param response
	 * @param email
	 * @param id
	 */
	@RequestMapping("/verify_storename.htm")
	public void verify_storename(HttpServletRequest request, HttpServletResponse response, String store_name, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("store_name", store_name);
		params.put("id", CommUtil.null2Long(id));
		List<Store> users = this.storeService
				.query("select obj.id from Store obj where obj.store_name=:store_name and obj.id!=:id", params, -1, -1);
		if (users != null && users.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 验证手机号是否重复
	 * 
	 * @param request
	 * @param response
	 * @param mobile
	 * @param id
	 */
	@RequestMapping("/verify_mobile.htm")
	public void verify_mobile(HttpServletRequest request, HttpServletResponse response, String mobile, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("mobile", mobile);
		params.put("id", CommUtil.null2Long(id));
		List<User> users = this.userService.query("select obj.id from User obj where obj.mobile=:mobile and obj.id!=:id",
				params, -1, -1);
		if (users != null && users.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 验证码生成
	 * 
	 * @param response
	 * @param mobile
	 * @throws IOException
	 */
	@RequestMapping("/verify.htm")
	public void verify(HttpServletRequest request, HttpServletResponse response, String name, String w, String h)
			throws IOException {
		response.setContentType("image/jpeg");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		HttpSession session = request.getSession();
		// 在内存中创建图象
		int width = 73;
		int height = 27;
		if (!"".equals(CommUtil.null2String(w))) {
			width = CommUtil.null2Int(w);
		}
		if (!"".equals(CommUtil.null2String(h))) {
			height = CommUtil.null2Int(h);
		}
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// 获取图形上下文
		Graphics g = image.getGraphics();

		// 生成随机类
		Random random = new Random();

		// 设定背景色
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);

		// 设定字体
		g.setFont(new Font("Times New Roman", Font.PLAIN, 24));

		// 画边框
		g.setColor(new Color(80, 80, 80));
		g.drawRect(0, 0, width - 1, height - 1);

		// 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 255; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}

		// 取随机产生的认证码(4位数字)
		String sRand = "";
		for (int i = 0; i < 4; i++) {
			String rand = CommUtil.randomInt(1).toUpperCase();
			sRand += rand;
			// 将认证码显示到图象中
			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
			g.drawString(rand, 14 * i + 6, 24);
		}

		// 将认证码存入SESSION
		if ("".equals(CommUtil.null2String(name))) {
			session.setAttribute("verify_code", sRand);
		} else {
			session.setAttribute(name, sRand);
		}
		// 图象生效
		g.dispose();
		ServletOutputStream responseOutputStream = response.getOutputStream();
		// 输出图象到页面
		ImageIO.write(image, "JPEG", responseOutputStream);

		// 以下关闭输入流！
		responseOutputStream.flush();
		responseOutputStream.close();
	}

	private Color getRandColor(int fc, int bc) {// 给定范围获得随机颜色
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	/***
	 * 是否存在商品编码
	 * @param request
	 * @param response
	 */
	@RequestMapping("/validator_goods_code.htm")
	public void goodsCode(HttpServletRequest request, HttpServletResponse response,String goodsCode,String goodsId) {
		String codeFlag="no";
		if(goodsId != null && !"".equals(goodsId)){
			Map goodsParams = new HashMap();
			goodsParams.put("goodsId", Long.parseLong(goodsId));
			goodsParams.put("goodsCode", goodsCode);
			List<Goods> goodsList=this.goodsService.query("select obj from Goods obj where obj.id!=:goodsId and obj.productMapping.goodsCode=:goodsCode", goodsParams, -1,-1);
		    if(goodsList != null && goodsList.size()>0){
		    	codeFlag="yes";
		    }
		}else{
			Map params = new HashMap();
			params.put("goodsCode", goodsCode);
			List<ProductMapping> productMappingList=this.productMappingService.query("select obj from ProductMapping obj where obj.goodsCode=:goodsCode", params, -1,-1);
			if(productMappingList != null && productMappingList.size()>0){
				codeFlag="yes";
			}
		}

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.print(codeFlag);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			writer.close();
	    }
	}
	
	@RequestMapping("/verify_cinemaId.htm")
	public void verify_cinemaId(HttpServletRequest request, HttpServletResponse response, String cinemaId, String sa_code) {
		String codeFlag="no";
		if(!StringUtils.isNullOrEmpty(cinemaId) && !StringUtils.isNullOrEmpty(sa_code)){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("cinemaId", cinemaId);
			params.put("sa_code", sa_code);
			List<ShipAddress> shipAddressList = this.shipAddressService.query("select obj from ShipAddress obj where obj.cinema_id = :cinemaId and obj.sa_code = :sa_code", params, -1, -1);
			if(shipAddressList != null && !shipAddressList.isEmpty()){
				codeFlag = "yes";
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.print(codeFlag);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			writer.close();
	    }
	}
}
