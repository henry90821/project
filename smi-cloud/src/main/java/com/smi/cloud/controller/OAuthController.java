package com.smi.cloud.controller;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.smilife.core.common.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Andriy on 2016/10/25.
 */
@RestController
@RequestMapping(value = "/oauth")
public class OAuthController extends BaseController {

	private static final String OAUTH_DOC_PATH = "/oauth/docs.html";

	@ApiIgnore
	@RequestMapping(value = "/wiki", method = { RequestMethod.GET })
	public ModelAndView oauthDocs(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView(OAUTH_DOC_PATH);
	}
}
