package com.iskyshop.core.security.support;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.iskyshop.core.tools.Md5Encrypt;

public class CustomedPasswordEncoder implements PasswordEncoder {

	@Override
	public String encodePassword(String rawPass, Object salt) throws DataAccessException {
		return Md5Encrypt.md5(rawPass).toLowerCase();
	}

	@Override
	public boolean isPasswordValid(String encPass, String rawPass, Object salt) throws DataAccessException {
		if(encPass != null && rawPass != null && encPass.equals(Md5Encrypt.md5(rawPass).toLowerCase())) {
			return true;
		}
		return false;
	}

}
