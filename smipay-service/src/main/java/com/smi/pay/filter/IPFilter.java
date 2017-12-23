/**
 * Description: Date:2012-5-1 Author:Along
 */
package com.smi.pay.filter;

import com.smi.pay.common.CommonUtil;

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

@WebFilter
public class IPFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String remoteHost = CommonUtil.getRemoteHost(req);

		String ips_flag = CommonUtil.getConfig().getIpsFlag();
		if ("true".equals(ips_flag)) {
			boolean flag = false;
			String ips[] = CommonUtil.getConfig().getIps().split(",");
			for (int i = 0; i < ips.length; i++) {
				String ip = ips[i];
				if (ip.equals(remoteHost)) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				res.sendRedirect("../no_role.jsp");
				return;
			}
		}
		filterchain.doFilter(request, response);

	}

	public void init(FilterConfig arg0) throws ServletException {

	}

}
