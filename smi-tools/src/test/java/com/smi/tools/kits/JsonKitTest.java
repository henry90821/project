package com.smi.tools.kits;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class JsonKitTest {

	@Test
	public void testJsontoString() {
		String username = "1709832239";
		String sex = "1";
		
		Address add = new Address();
		add.setId(123223);
		add.setLocation("福田体育公园");
		
		Map<String, Object> map = new HashMap<String,Object>();
		
		map.put("username", username);
		map.put("sex", sex);
		map.put("entity", null);
		
		System.out.println(JsonKit.toJsonString(map));
		System.out.println(JsonKit.toJsonString(add));
	}

	
}

class Address {
	private int id;
	private String location;
	
	public Address() {
	}
	
	public Address(int id, String location) {
		this.id = id;
		this.location = location;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
