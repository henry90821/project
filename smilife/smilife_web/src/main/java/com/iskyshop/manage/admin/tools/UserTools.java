package com.iskyshop.manage.admin.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.exception.SmiBusinessException;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.FTPServer;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IFTPServerService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.IUserService;
import com.smilife.bcp.dto.common.EQryType;
import com.smilife.bcp.dto.response.MemberInfoResp;
import com.smilife.bcp.service.UserManageConnector;

@Component
public class UserTools {
	private Logger logger = Logger.getLogger(this.getClass());

	private SimpleDateFormat shortFormat = new SimpleDateFormat(Globals.DateFormat.DATE_FORMAT_SHORT);
	private SimpleDateFormat longFormat = new SimpleDateFormat(Globals.DateFormat.DATE_FORMAT_LONG);

	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IFTPServerService ftpService;
	@Autowired
	private UserManageConnector userManageConnector;

	
	/**
	 * 根据用户名判断用户是否在线(先去除同一账号只能有一个Session的限制（因为好像原来的代码也没起作用）：added By wuronglong)
	 * 
	 * @param userName
	 * @return
	 */
	public boolean userOnLine(String userName) {
		boolean ret = false;
		if (!StringUtils.isNullOrEmpty(userName)) {
			List<Object> objs = this.sessionRegistry.getAllPrincipals();
			for (Object un : objs) {
				if (userName.equalsIgnoreCase(un.toString())) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	
	/**
	 * 判断是否有管理员在线
	 * 
	 * @return
	 */
	public boolean adminOnLine() {
		boolean ret = false;
		List<User> users = this.userService.query("select new User(userName) from User obj where obj.userRole='ADMIN'",
				null, -1, -1);
		List<Object> objs = this.sessionRegistry.getAllPrincipals();
		for (Object un : objs) {
			for (User user : users) {
				if (user.getUserName().equalsIgnoreCase(un.toString())) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	

	/**
	 * 将CRM端的会员信息memberInfo更新到商城的User对象中去。此函数不会为此User设置角色字段和权限
	 * 
	 * @param mi
	 *            CRM端的会员信息，不能为null
	 * @param user
	 *            被赋值的user对象。若为null，则此函数内部会自动创建一个User对象
	 * @return 最终的User对象。但若参数mi相对于参数user没有发生变化，则返回null。
	 */
	private User convertMember2User(MemberInfoResp mi, User user) {
		if (user == null) {
			user = new User();
		} else if (!userInfoChanged(mi, user)) {
			return null;
		}

		// 下面是将mi中的用户信息更新到user中去
		if (StringUtils.isNullOrEmpty(mi.getCustId())) {
			throw new SmiBusinessException("CRM端的用户信息中custId字段不能为空。custName=" + mi.getCustName() + ",custMobile=" + mi.getMobile());
		}
		
		try {
			user.setBirthday(shortFormat.parse(mi.getBirthDate()));
		} catch (Exception e) {
			user.setBirthday(null);
		}

		user.setCard(mi.getCertiNbr());
		user.setAddress(mi.getContactAddr());
		user.setCustId(mi.getCustId());
		user.setTrueName(mi.getCustName());
		user.setEmail(mi.getEmail());
		user.setMobile(mi.getMobile());
		user.setNickName(mi.getNickName());

		// 商城后台会员管理页面中会显示用户的注册时间，那里使用的就是User的addTime字段。
		try {
			user.setAddTime(longFormat.parse(mi.getJoinDate()));
		} catch (ParseException e) {
			throw new SmiBusinessException("CRM端的用户信息中joindate字段错误：joindate=" + mi.getJoinDate() + ", custId=" + mi.getCustId(), e);
		}

		if ("1".equals(mi.getSex())) {
			user.setSex(1); // 男
		} else if ("2".equals(mi.getSex())) {
			user.setSex(0); // 女
		} else {
			user.setSex(-1); // 保密
		}

		user.setUserName(mi.getCustName());//保持与星美生活APP上显示的一致
		
		if("admin".equalsIgnoreCase(user.getUsername())) {//因为有些代码中写死了username为"admin"的用户即为管理员用户，故不允许其它用户使用此名字
			user.setUserName("admin_");
		}

		return user;
	}
	

	/**
	 * 检查CRM端返回的会员信息相对于商城端对应的User对象是否发生了变化。若有变化，则返回true，没变化则返回false
	 * 
	 * @param mi
	 *            CRM端会员信息，不能为null
	 * @param user
	 *            商城端用户信息，不能为null
	 * @return
	 */
	private boolean userInfoChanged(MemberInfoResp mi, User user) {
		if (user.getAddress() == null && !StringUtils.isNullOrEmpty(mi.getContactAddr()) || user.getAddress() != null
				&& !user.getAddress().equals(mi.getContactAddr())) {
			return true;
		}

		if (user.getEmail() == null && !StringUtils.isNullOrEmpty(mi.getEmail()) || user.getEmail() != null
				&& !user.getEmail().equals(mi.getEmail())) {
			return true;
		}

		if (user.getMobile() == null && !StringUtils.isNullOrEmpty(mi.getMobile()) || user.getMobile() != null
				&& !user.getMobile().equals(mi.getMobile())) {
			return true;
		}

		if (user.getNickName() == null && !StringUtils.isNullOrEmpty(mi.getNickName()) || user.getNickName() != null
				&& !user.getNickName().equals(mi.getNickName())) {
			return true;
		}

		if (user.getSex() == 0 && !"2".equals(mi.getSex()) || user.getSex() == 1 && !"1".equals(mi.getSex())
				|| user.getSex() == -1 && ("2".equals(mi.getSex()) || "1".equals(mi.getSex()))) {
			return true;
		}

		if (user.getCard() == null && !StringUtils.isNullOrEmpty(mi.getCertiNbr()) || user.getCard() != null
				&& !user.getCard().equals(mi.getCertiNbr())) {
			return true;
		}

		boolean tmp = !StringUtils.isNullOrEmpty(mi.getCustName());

		if (user.getTrueName() == null && tmp || user.getTrueName() != null && !user.getTrueName().equals(mi.getCustName())) {
			return true;
		}

		if (user.getUserName() == null && tmp || user.getUserName() != null && !user.getUserName().equals(mi.getCustName())) {
			return true;
		}

		if (user.getBirthday() == null && !StringUtils.isNullOrEmpty(mi.getBirthDate()) || user.getBirthday() != null
				&& !shortFormat.format(user.getBirthday()).equals(mi.getBirthDate())) {
			return true;
		}

		if (user.getCustId() == null && !StringUtils.isNullOrEmpty(mi.getCustId()) || user.getCustId() != null
				&& !user.getCustId().equals(mi.getCustId())) {
			return true;
		}

		return false;
	}
	

	/**
	 * 保存或更新商城端User表中的对应记录
	 * 
	 * @param custId
	 *            CRM端的会员的custId，商城端的User表中的custId字段与之对应
	 * @param toUpdateUser
	 *            若对应User存在，则是否需要更新此对应的User：true：更新User，false：不更新User
	 * @param needReturnUser
	 *            是否要返回对应的User对象：true：则会返回对应的User对象(若toUpdateUser为true,则返回的此User为update后的User)；false：返回null
	 * @throws SmiBusinessException
	 *             若保存或更新User失败或custId对应的会员不存在，则会抛出此异常
	 * @return User表中对应的用户对象，若needReturnUser为false，则返回null
	 */
	@Transactional(readOnly = false)
	public User syncBuyerFromCRM(String custId, boolean toUpdateUser, boolean needReturnUser) throws SmiBusinessException {
		if (StringUtils.isNullOrEmpty(custId)) {
			throw new SmiBusinessException("同步CRM端的会员到商城User表中失败：custId为空或null");
		}

		User retUser = null;
		MemberInfoResp mi = null;
		User user = this.userService.getObjByProperty(null, "custId", custId);

		if (user != null) {
			if (toUpdateUser) {
				mi = this.userManageConnector.queryMemberInfo(custId);
				if (mi == null) {
					throw new SmiBusinessException("同步CRM端的会员到商城User表中失败：custId=" + custId + " 在CRM端不存在");
				}
				User tmpUser = this.convertMember2User(mi, user);
				if (tmpUser != null) {
					this.userService.update(tmpUser);
				}
			}
			if (needReturnUser) {
				retUser = user;
			}

			return retUser;
		}

		// 商城中不存在对应CRM端的用户，则同步此用户到商城
		if (mi == null) {
			mi = this.userManageConnector.queryMemberInfo(custId);
			if (mi == null) {
				throw new SmiBusinessException("同步CRM端的会员到商城User表中失败：custId=" + custId + " 在CRM端不存在");
			}
		}

		// 对应的User不存在，则进行同步
		user = this.convertMember2User(mi, null);

		// 为用户配置在用户数量限制内的FTP，如果没有则选择用户数量最少的FTP
		Map params = new HashMap();
		params.put("ftp_type", 0);

		List<FTPServer> FtpServers = this.ftpService.query(
				"select obj from FTPServer obj where obj.ftp_users.size<obj.ftp_amount and obj.ftp_type=:ftp_type", params,
				0, 1);
		if (FtpServers.size() > 0) {
			user.setFtp(FtpServers.get(0));
		} else {
			FtpServers = this.ftpService.query(
					"select obj from FTPServer obj where obj.ftp_type=:ftp_type order by obj.ftp_users.size asc", params, 0,
					1);
			if (FtpServers.size() > 0) {
				user.setFtp(FtpServers.get(0));
			} else {
				throw new SmiBusinessException("未找到可用的用户FTP服务器!");
			}
		}

		// 设置用户权限
		params.clear();
		params.put("type", "BUYER");
		List<Role> roles = this.roleService.query("select obj from Role obj where obj.type=:type", params, -1, -1);
		user.getRoles().addAll(roles);
		user.setUserRole("BUYER");

		// 保存用户由于可能的并发问题，所以可能导致在前面根据custId在商城端没有找到对应的用户，但在下面保存用户时商城端已存在了对应custId的用户
		try {
			boolean flag = this.userService.save(user);
			//以下不需要考虑toUpdateUser。因为同步过来的User或并发同步过来的User都是最新的
			if(needReturnUser) {
				if(flag) {
					retUser = user;
				} else {
					retUser = this.userService.getObjByProperty(null, "custId", user.getCustId());// 重新查出User
				}
			}
			
			logger.info("成功将CRM端的会员同步到商城User表中。custId = " + user.getCustId());
		} catch (Exception e) {
			throw new SmiBusinessException("将CRM端的用户(custId=" + user.getCustId() + ")保存到商城端失败。", e);
		}

		return retUser;
	}
	

	/**
	 * 按用户角色查找用户，即获取角色为role，查找值为userMark，且id不等于uId的用户(对于“BUYER”角色会先到CRM端去查找用户并同步此用户到商城User表中去)
	 * 
	 * @param role
	 *            要查找的用户的角色。必须是以下三个值之一："BUYER", "ADMIN", "SELLER"
	 * @param userMark
	 *            查找值。不能为null。若role为“BUYER”，则userMark为用户的mobile字段的值；若role为“ADMIN”，则userMark为用户的userName字段的值；若role为“SELLER”，
	 *            则userMark为用户的sellerLoginAccount字段的值
	 * @param uId
	 *            若为null，则此uId不起作用；否则，则此参数也会做为查询参数，表示除此uId之外的用户
	 * @return 若不存在满足条件的用户，则返回null
	 */
	public User getUser(String role, String userMark, Long uId) {
		User retUser = null;
		if ("BUYER".equals(role)) {
			MemberInfoResp mi = userManageConnector.queryMemberInfo(EQryType.ByMobileNumber, userMark);
			if (mi != null) {
				User tmpUser = this.syncBuyerFromCRM(mi.getCustId(), false, true);
				if("BUYER".equals(tmpUser.getUserRole())) {
					retUser = tmpUser;
				}
			}
		} else {
			String field = "userName";
			if ("SELLER".equals(role)) {
				field = "sellerLoginAccount";
			}
			User tmpUser = userService.getObjByProperty(null, field, userMark);
			if(tmpUser != null) {
				if("SELLER".equals(role) && "SELLER".equals(tmpUser.getUserRole()) || "ADMIN".equals(role) && "ADMIN".equals(tmpUser.getUserRole())) {
					retUser = tmpUser;
				}
			}
		}

		if (uId != null && retUser != null && retUser.getId() == uId) {
			retUser = null;
		}

		return retUser;
	}
	
}
