package com.smi.pay.dao;

import java.util.List;
import java.util.Map;

import com.smi.pay.model.Refund;
import com.smi.pay.model.RefundLog;


public interface RefundLogDao {
    
    int deleteByPrimaryKey(Integer id);

    
    int insert(RefundLog refundLog);
    
    int update(RefundLog refundLog);

    RefundLog load(Integer id);
    
    List getAll(Map filter);
    
    
}