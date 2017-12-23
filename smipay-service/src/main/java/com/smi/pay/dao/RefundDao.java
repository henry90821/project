package com.smi.pay.dao;

import java.util.List;
import java.util.Map;

import com.smi.pay.model.Refund;


public interface RefundDao {
    
    int deleteByPrimaryKey(Integer id);

    int insert(Refund refund);
    
    int update(Refund refund);

    Refund load(Integer id);
    
    Refund getByRefundNo(String refundNo);
    
    int refundCount(String billNo);
    
    List getAll(Map filter);
    
}