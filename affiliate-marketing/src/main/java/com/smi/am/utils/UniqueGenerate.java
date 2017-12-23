package com.smi.am.utils;

import java.util.HashSet;
import java.util.Set;

public class UniqueGenerate {

	public static synchronized String createUniqueId(){
		return System.nanoTime()+"";
	}
	
	public static void main(String[] args) {
		Set<String> ids=new HashSet<String>();
		for (int i = 0; i < 1000000; i++) {
			String createUniqueId = createUniqueId();
			ids.add(createUniqueId);
			System.out.println(createUniqueId);
		}
		System.out.println(ids.size());
	}
	
}
