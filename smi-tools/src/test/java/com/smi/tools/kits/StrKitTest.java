package com.smi.tools.kits;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

public class StrKitTest {

	public static LinkedBlockingQueue linkQueue = new LinkedBlockingQueue(200);
	@Test
	public void splitTest() {
		String codes = "3243,,34334, 53434, 3233";
		
		List<String> list = Arrays.asList(StrKit.split(codes, ","));
		
		for(String code : list) {
			System.out.println("[" + code + "]");
		}
		System.out.println("-----------------------");
	}
	
	
	@Test
	public void testQuene() {
		
		Thread t1 = new Thread(new AddThread());
		t1.start();
		Thread t2 = new Thread(new GetThread());
		t2.start();
		
		ThreadKit.sleep((long) (350));
	}
	
	
	
}

class AddThread implements Runnable {
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		
		for(int i = 0; i < 10000; i ++) {
			// 添加一个元素并返回true       如果队列已满，则返回false
			System.out.println("offer -> " + i + " " +  StrKitTest.linkQueue.offer(i));
		}
	}
}

class GetThread implements Runnable {
	@Override
	public void run() {
		while(true) {
			System.out.println("POLL -> " + StrKitTest.linkQueue.poll());
		}
	}
}
