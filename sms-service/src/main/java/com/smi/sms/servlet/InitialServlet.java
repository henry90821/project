package com.smi.sms.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smi.sms.service.ICodeDictService;

/**
 * InitialServlet
 * <p>
 * tomcat容器启动时，执行init方法，加载字典到内存
 * </p>
 * 
 * @author Liangshuai
 * @Date 2016年2月24日
 */
@Component("initialServlet")
public class InitialServlet extends HttpServlet {
	private static final long serialVersionUID = 7050385134374929833L;
	
	@Autowired
	ICodeDictService codeDictService;
	
	public InitialServlet() {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		codeDictService.setDictCache();
	}

}
