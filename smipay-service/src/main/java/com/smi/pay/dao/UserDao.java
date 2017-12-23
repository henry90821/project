package com.smi.pay.dao;

import java.util.List;
import java.util.Map;

import com.smi.pay.model.AppInfo;
import com.smi.pay.model.Order;
import com.smi.pay.model.User;


public interface UserDao {
    
	    int deleteByPrimaryKey(Integer id);

	    int insert(User user);

	    User load(Integer id);
	    
	    int update(User user);
	    
	    List getAll();
	    
	    User getByUserName(String username);
    
    
}