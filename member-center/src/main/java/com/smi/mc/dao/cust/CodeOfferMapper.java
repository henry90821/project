package com.smi.mc.dao.cust;

import java.util.List;
import java.util.Map;

public interface CodeOfferMapper {

	List<Map<String, Object>> qryCodeOffer(Map<String, Object> param);

	List<Map<String, Object>> qryCodeOfferByOffername(Map<String, Object> param);

}
