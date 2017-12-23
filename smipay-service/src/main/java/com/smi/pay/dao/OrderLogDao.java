package com.smi.pay.dao;

import java.util.List;
import java.util.Map;

import com.smi.pay.model.OrderLog;


public interface OrderLogDao {
    
    int deleteByPrimaryKey(Integer id);

    
    int insert(OrderLog order);
    
    int update(OrderLog order);

    OrderLog load(Integer id);
    
    List getAll(Map filter);
    
}