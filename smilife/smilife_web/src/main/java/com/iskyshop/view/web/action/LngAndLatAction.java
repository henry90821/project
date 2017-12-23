package com.iskyshop.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.AddressUtil;
import com.iskyshop.core.tools.StringUtils;

/**
 * @ClassName: LngAndLatAction
 * @Description: TODO(经纬度控制层)
 * @author wangyun
 * @date 2015-12-12
 * 
 */
@Controller
public class LngAndLatAction {

	/**
	 * @Title: getLngAndLat
	 * @Description: TODO(通过百度接口获取地址对应的经纬度)
	 * @param 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/lngAndLat/getLngAndLat.htm")
	public void getLngAndLat(HttpServletRequest request, HttpServletResponse response, String addrInfo) {
		try {
			Map map = AddressUtil.getLngAndLat(addrInfo);
			JSONObject json = JSONObject.fromObject(map);
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter print = response.getWriter();
			if (!StringUtils.isNullOrEmpty(json)) {
				print.write(json.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
