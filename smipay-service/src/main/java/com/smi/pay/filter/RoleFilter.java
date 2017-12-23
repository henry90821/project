/**
 * Description: Date:2012-5-1 Author:Along
 */
package com.smi.pay.filter;

import com.smi.pay.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*"})
public class RoleFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		if (req.getSession().getAttribute("user") == null) {
			res.sendRedirect("/login.jsp");
		} else {
			User user = (User) req.getSession().getAttribute("user");
			String role = user.getRole();

			// 如果session过期，请重新登录
			if (user == null) {
				res.sendRedirect("../login.jsp");
				return;
			}

			String currenturi = req.getRequestURI();// 得到当前的URI
			// //角色的添加编辑权限
			if (currenturi.contains("/admin/user") || currenturi.contains("/admin/call")) {
				if (role.equals("1")) {
					res.sendRedirect("../no_role.jsp");
					return;
				}
			}
			req.getSession().setAttribute("role", role);
			filterchain.doFilter(request, response);
		}

	}

	public void init(FilterConfig arg0) throws ServletException {

	}

}
