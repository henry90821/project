package com.smi.mc.po;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SOO extends LinkedHashMap<Object, Object> {
	private static final long serialVersionUID = -8424177976155052216L;
	String headKey;

	private Map<Object, Object> getDefaultHead() {
		@SuppressWarnings("unchecked")
		Map<Object,Object> head = (Map<Object,Object>) get(this.headKey);
		if (head == null) {
			head = new HashMap<Object,Object>();
			put(this.headKey, head);
		}
		return head;
	}

	public void setPaging(int pageSize, int pageIndex) {
		Map<Object,Object> head = getDefaultHead();
		head.put("PAGE_SIZE", Integer.valueOf(pageSize));
		head.put("PAGE_INDEX", Integer.valueOf(pageIndex));
	}

	public int getPageSize() {
		@SuppressWarnings("unchecked")
		Map<Object,Object> head = (Map<Object,Object>) get(this.headKey);
		if (head == null)
			return -1;
		return ((Integer) head.get("PAGE_SIZE")).intValue();
	}

	public String getType() {
		@SuppressWarnings("unchecked")
		Map<Object,Object> head = (Map<Object,Object>) get(this.headKey);
		if (head == null)
			return null;
		return ((String) head.get("TYPE"));
	}

	public int getPageIndex() {
		@SuppressWarnings("unchecked")
		Map<Object,Object> head = (Map<Object,Object>) get(this.headKey);
		if (head == null)
			return -1;
		return ((Integer) head.get("PAGE_INDEX")).intValue();
	}

	public SOO(String sooName, String headKey) {
		if (sooName == null)
			return;
		this.headKey = headKey;
		Map<Object,Object> head = getDefaultHead();
		head.put("PAGE_SIZE", Integer.valueOf(-1));
		head.put("PAGE_INDEX", Integer.valueOf(-1));
		head.put("COUNT_TOTAL", "-1");
		head.put("TYPE", sooName);
	}

	public SOO() {
	}

	public String getSvcCountTotal() {
		@SuppressWarnings("unchecked")
		Map<Object,Object> head = (Map<Object,Object>) get(this.headKey);
		if (head == null)
			return null;
		return ((String) head.get("COUNT_TOTAL"));
	}

	public void setSvcCountTotal(String svcCountTotal) {
		Map<Object,Object> head = getDefaultHead();
		head.put("COUNT_TOTAL", svcCountTotal);
	}
}