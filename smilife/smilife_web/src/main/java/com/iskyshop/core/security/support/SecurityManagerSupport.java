package com.iskyshop.core.security.support;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.UserTools;


public class SecurityManagerSupport implements UserDetailsService, AuthenticationUserDetailsService {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private UserTools userTools;
	

	private static final String ADMIN = "ADMIN";
	private static final String BUYER = "BUYER";
	private static final String SELLER = "SELLER";

	/**
	 * BUYER登录时：data为买家的custId
	 * ADMIN或SELLER登录时，data为登录账号和登录角色的组合（以英文逗号分隔，如：admin,ADMIN、xmh,SELLER）
	 */
	public UserDetails loadUserByUsername(String data) throws UsernameNotFoundException, DataAccessException {
		String[] tokens = data.split(",");
		String loginRole = BUYER;
		String userName = tokens[0].trim();
		if(tokens.length == 2) {
			if(ADMIN.equalsIgnoreCase(tokens[1].trim())) {
				loginRole = ADMIN;
			} else {
				loginRole = SELLER;
			}
		}
		
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		User user = null;
		if(BUYER.equals(loginRole)) {
			user = this.userTools.syncBuyerFromCRM(userName, true, true);
			
			if(user == null) {
				logger.error("未找到custId为" + userName + "的用户。");
				throw new UsernameNotFoundException("用户不存在");
			}
			
			for(Role role: user.getRoles()) {
				if(BUYER.equals(role.getType())) {
					GrantedAuthority grantedAuthority = new GrantedAuthorityImpl(role.getRoleCode().toUpperCase());
					authorities.add(grantedAuthority);
				}
			}
		} else if(SELLER.equals(loginRole)){
			user = this.userService.getSellerByLoginAccount(userName);
			
			if(user == null) {
				logger.error("未找到loginAccount为" + userName + "的商家用户。");
				throw new UsernameNotFoundException("用户不存在");
			}
			
			for(Role role: user.getRoles()) {
				if(SELLER.equals(role.getType())) {
					GrantedAuthority grantedAuthority = new GrantedAuthorityImpl(role.getRoleCode().toUpperCase());
					authorities.add(grantedAuthority);
				}
			}
		} else {
			user = this.userService.getAdminByUsername(userName);
			
			if(user == null) {
				logger.error("未找到userName为" + userName + "的管理员用户。");
				throw new UsernameNotFoundException("用户不存在");
			}
			
			for(Role role: user.getRoles()) {
				if(ADMIN.equals(role.getType())) {
					GrantedAuthority grantedAuthority = new GrantedAuthorityImpl(role.getRoleCode().toUpperCase());
					authorities.add(grantedAuthority);
				}
			}
		}
		
		user.setAuthorities(authorities);
		user.setCurrentLoginRole(loginRole);//不管后续用户认证是否通过，在此处都保存当前用户登录的角色。只有被认证通过的UserDetails才会被保存到AuthenticationToken中去
		
		return user;
	}

	
	/**
	 * 此方法只适合在Cas登录认证时被调用
	 */
	@Override
	public UserDetails loadUserDetails(Authentication token) throws UsernameNotFoundException {
		Assert.isAssignable(CasAssertionAuthenticationToken.class, token.getClass());
		CasAssertionAuthenticationToken auth = (CasAssertionAuthenticationToken)token;
		//因为星美CAS Server端返回买家的custId是被保存在Assertion的attribute中的而非name字段中，故在此处要转换下
		String custId = (String)auth.getAssertion().getPrincipal().getAttributes().get("custId");
				 
		if(logger.isInfoEnabled()) {
			logger.info("用户[" + auth.getAssertion().getPrincipal().getName() + "]（custId=" + custId + "）单点登录成功！");
		}
		
		return this.loadUserByUsername(custId);
	}
	
}
