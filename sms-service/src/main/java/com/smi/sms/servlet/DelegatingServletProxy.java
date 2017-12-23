package com.smi.sms.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DelegatingServletProxy extends GenericServlet {
	private static final long serialVersionUID = -7739740420405594661L;
	
	private String targetBean;
	private Servlet proxy;

	@Override
	public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
		proxy.service(req, resp);
	}

	@Override
	public void init() throws ServletException {
		this.targetBean = getServletName();
		getServletBean();
		proxy.init(getServletConfig());
	}

	private void getServletBean() {
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		this.proxy = (Servlet) wac.getBean(this.targetBean);
	}

}
