package com.smi.pay.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smi.pay.dao.UserDao;
import com.smi.pay.model.User;
import com.smi.pay.service.UserService;

@Service("UserService")
@Transactional(readOnly = false)
public class UserServiceImpl implements UserService{

	@Autowired
	private UserDao userDao;

	@Override
	public List<User> listUser() {
		return userDao.getAll();
	}

	@Override
	public void updateUserById(User user) {
		 userDao.update(user);
	}

	@Override
	public User loadUser(int id) {
		return userDao.load(id);
	}

	@Override
	public void saveUser(User user) {
		userDao.insert(user);
	}

	@Override
	public void deleteUser(int id) {
        userDao.deleteByPrimaryKey(id);		
	}
	@Override
	public User getByUserName(String username)
	 {
		 return userDao.getByUserName(username);
	 }

}
