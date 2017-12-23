package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.domain.ArticleClass;
import com.iskyshop.foundation.domain.query.ArticleQueryObject;
import com.iskyshop.foundation.service.IArticleClassService;
import com.iskyshop.foundation.service.IArticleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.mysql.jdbc.StringUtils;

/**
 * 
 * <p>
 * Title: ArticleManageAction.java
 * </p>
 * 
 * <p>
 * Description:系统文章管理控制器，用来发布、修改系统文章信息
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
 * @author erikzhang
 * 
 * @date 2014-5-21
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class ArticleManageAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IArticleClassService articleClassService;

	/**
	 * Article列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "文章列表", value = "/admin/article_list.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping("/admin/article_list.htm")
	public ModelAndView article_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/article_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String params = "";
		ArticleQueryObject qo = new ArticleQueryObject(currentPage, mv, orderBy, orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Article.class, mv);
		IPageList pList = this.articleService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/admin/article_list.htm", "", params, pList, mv);
		return mv;
	}

	/**
	 * article添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "文章添加", value = "/admin/article_add.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping("/admin/article_add.htm")
	public ModelAndView article_add(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String class_id) {
		ModelAndView mv = new JModelAndView("admin/blue/article_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<ArticleClass> acs = this.articleClassService.query(
				"select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
		Article obj = new Article();
		obj.setDisplay(true);
		if (!StringUtils.isNullOrEmpty(class_id)) {
			obj.setArticleClass(this.articleClassService.getObjById(Long.parseLong(class_id)));
		}
		mv.addObject("obj", obj);
		mv.addObject("acs", acs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * article编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "文章编辑", value = "/admin/article_edit.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping("/admin/article_edit.htm")
	public ModelAndView article_edit(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/article_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!StringUtils.isNullOrEmpty(id)) {
			Article article = this.articleService.getObjById(Long.parseLong(id));
			List<ArticleClass> acs = this.articleClassService.query(
					"select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
			mv.addObject("acs", acs);
			mv.addObject("obj", article);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * article保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "文章保存", value = "/admin/article_save.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping("/admin/article_save.htm")
	public ModelAndView article_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String class_id, String content) {
		WebForm wf = new WebForm();
		Article article = null;
		if ("".equals(id)) {
			article = wf.toPo(request, Article.class);
			article.setAddTime(new Date());
		} else {
			Article obj = this.articleService.getObjById(Long.parseLong(id));
			article = (Article) wf.toPo(request, obj);
		}
		article.setArticleClass(this.articleClassService.getObjById(Long.parseLong(class_id)));
		logger.debug(content);
		if ("".equals(id)) {
			this.articleService.save(article);
		} else
			this.articleService.update(article);
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存文章成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage + "&class_id=" + class_id);
		}
		return mv;
	}

	@SecurityMapping(title = "文章删除", value = "/admin/article_del.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping("/admin/article_del.htm")
	public String article_del(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!"".equals(id)) {
				Article article = this.articleService.getObjById(Long.parseLong(id));
				this.articleService.delete(Long.parseLong(id));
			}
		}
		return "redirect:article_list.htm";
	}

	@SecurityMapping(title = "文章AJAX更新", value = "/admin/article_ajax.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping("/admin/article_ajax.htm")
	public void article_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Article obj = this.articleService.getObjById(Long.parseLong(id));
		Field[] fields = Article.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if ("int".equals(field.getType().getName())) {
					clz = Class.forName("java.lang.Integer");
				}
				if ("boolean".equals(field.getType().getName())) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!"".equals(value)) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.articleService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			logger.error(e);
		}

	}

	@RequestMapping("/admin/article_mark.htm")
	public void article_mark(HttpServletRequest request, HttpServletResponse response, String mark, String id) {
		Map params = new HashMap();
		params.put("mark", mark.trim());
		params.put("id", CommUtil.null2Long(id));
		List<Article> arts = this.articleService.query("select obj from Article obj where obj.mark=:mark and obj.id!=:id",
				params, -1, -1);
		boolean ret = true;
		if (arts.size() > 0) {
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
}