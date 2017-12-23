package com.iskyshop.view.web.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.service.IAdvertPositionService;
import com.iskyshop.foundation.service.IAdvertService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: AdvertViewAction.java
 * </p>
 * 
 * <p>
 * Description:广告调用控制器,系统采用广告位形式管理广告信息，前端使用js完成调用，js调用的是该控制器中的invoke方法，
 * redirect方法用来控制并记录广告点击信息
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
 * @date 2014-9-16
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class AdvertViewAction {
	
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAdvertPositionService advertPositionService;
	@Autowired
	private IAdvertService advertService;

	/**
	 * 广告调用方法
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/advert_invoke.htm")
	public ModelAndView advert_invoke(HttpServletRequest request,
			HttpServletResponse response, String id,String index) {
		ModelAndView mv = new JModelAndView("advert_invoke.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (id != null && !"".equals(id)) {
			AdvertPosition ap = this.advertPositionService.getObjById(CommUtil
					.null2Long(id));
			if (ap != null) {
				AdvertPosition obj = new AdvertPosition();
				obj.setId(CommUtil.null2Long(ap.getId()));
				obj.setAp_type(ap.getAp_type());
				obj.setAp_status(ap.getAp_status());
				obj.setAp_show_type(ap.getAp_show_type());
				obj.setAp_width(ap.getAp_width());
				obj.setAp_height(ap.getAp_height());
				obj.setAp_location(ap.getAp_location());
				List<Advert> advs = new ArrayList<Advert>();
				for (Advert temp_adv : ap.getAdvs()) {
					if (temp_adv.getAd_status() == 1
							&& temp_adv.getAd_begin_time().before(new Date())
							&& temp_adv.getAd_end_time().after(new Date())) {
						advs.add(temp_adv);
					}
				}
				if (advs.size() > 0) {
					if ("text".equals(obj.getAp_type())) {// 文字广告
						if (obj.getAp_show_type() == 0) {// 固定广告
							obj.setAp_text(advs.get(0).getAd_text());
							obj.setAp_acc_url(advs.get(0).getAd_url());
							obj.setAdv_id(CommUtil.null2String(advs.get(0)
									.getId()));
						}
						if (obj.getAp_show_type() == 1) {// 随机广告
							Random random = new Random();
							int i = random.nextInt(advs.size());
							obj.setAp_text(advs.get(i).getAd_text());
							obj.setAp_acc_url(advs.get(i).getAd_url());
							obj.setAdv_id(CommUtil.null2String(advs.get(i)
									.getId()));
						}
					}
					if ("img".equals(obj.getAp_type())) {// 图片广告
						if (obj.getAp_show_type() == 0) {// 固定广告
							obj.setAp_marked(ap.getAp_marked());
							obj.setAp_acc(advs.get(0).getAd_acc());
							obj.setAp_acc_url(advs.get(0).getAd_url());
							obj.setAdv_id(CommUtil.null2String(advs.get(0).getId()));
							List<Advert> advertList=new ArrayList<Advert>();
							Date date=new Date();
							for(int i=0;i<ap.getAdvs().size();i++){
								Advert adv=ap.getAdvs().get(i);
								if(adv.getAd_begin_time().compareTo(date)<0 && adv.getAd_end_time().compareTo(date)>0){
									advertList.add(adv);
								}
								obj.setAdvs(advertList);
							}
							mv.addObject("index", index);
						}
						if (obj.getAp_show_type() == 1) {// 随机广告
							Random random = new Random();
							int i = random.nextInt(advs.size());
							obj.setAp_acc(advs.get(i).getAd_acc());
							obj.setAp_acc_url(advs.get(i).getAd_url());
							obj.setAdv_id(CommUtil.null2String(advs.get(i)
									.getId()));
						}
					}
					if ("slide".equals(obj.getAp_type())) {// 幻灯广告
						if (obj.getAp_show_type() == 0) {// 固定广告
							obj.setAdvs(advs);
						}
						if (obj.getAp_show_type() == 1) {// 随机广告
							Random random = new Random();
							Set<Integer> list = CommUtil.randomInt(advs.size(),
									8);
							for (int i : list) {
								obj.getAdvs().add(advs.get(i));
							}
						}
					}
					if ("scroll".equals(obj.getAp_type())) {// 滚动广告
						if (obj.getAp_show_type() == 0) {// 固定广告
							obj.setAdvs(advs);
						}
						if (obj.getAp_show_type() == 1) {// 随机广告
							Random random = new Random();
							Set<Integer> list = CommUtil.randomInt(advs.size(),
									12);
							for (int i : list) {
								obj.getAdvs().add(advs.get(i));
							}
						}
					}
					if ("bg_slide".equals(obj.getAp_type())) {
						if (obj.getAp_show_type() == 0) {// 固定广告
							obj.setAdvs(advs);
						}
						if (obj.getAp_show_type() == 1) {// 随机广告
							Random random = new Random();					
							Set<Integer> list = CommUtil.randomInt(advs.size(),
									8);
							for (int i : list) {
								obj.getAdvs().add(advs.get(i));
							}
						}
					}
				} else {
					obj.setAp_acc(ap.getAp_acc());
					obj.setAp_text(ap.getAp_text());
					obj.setAp_acc_url(ap.getAp_acc_url());
					Advert adv = new Advert();
					adv.setAd_url(obj.getAp_acc_url());
					adv.setAd_acc(ap.getAp_acc());
					obj.getAdvs().add(adv);
					obj.setAp_location(ap.getAp_location());
				}
				if (obj.getAp_status() == 1) {
					mv.addObject("obj", obj);
				} else {
					mv.addObject("obj", new AdvertPosition());
				}
			}
		}
		return mv;
	}

	/**
	 * 广告URL跳转方法
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @param id
	 */
	@RequestMapping("/advert_redirect.htm")
	public void advert_redirect(HttpServletRequest request,
			HttpServletResponse response, String id,String url) {
		try {
			Advert adv = this.advertService.getObjById(CommUtil.null2Long(id));
			if (adv != null) {
				adv.setAd_click_num(adv.getAd_click_num() + 1);
				this.advertService.update(adv);
			}
			if (adv != null) {
				url = adv.getAd_url();
				response.sendRedirect(url);
			} else {
				response.sendRedirect(CommUtil.getURL(request)+"/"+url);
			}
		} catch (IOException e) {
			logger.warn(e);
		}
	}
}
