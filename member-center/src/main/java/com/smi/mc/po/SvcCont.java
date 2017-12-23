package com.smi.mc.po;

import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SvcCont implements Serializable {
	private static final long serialVersionUID = -5925311472231927807L;
	private List<SOO> soos = new ArrayList<SOO>();

	@JSONField(name = "SOO")
	public List<SOO> getSoos() {
		return this.soos;
	}

	@Deprecated
	@JSONField(name = "SOO")
	public void setSoos(List<SOO> soos) {
		this.soos = soos;
	}
}