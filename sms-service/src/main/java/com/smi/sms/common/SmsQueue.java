package com.smi.sms.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.smi.sms.model.SmsHistory;
import com.smi.tools.kits.JsonKit;

public class SmsQueue {

	private static Logger logger = Logger.getLogger(SmsQueue.class);
	private static LinkedBlockingQueue<SmsHistory> smsQueue = new LinkedBlockingQueue<SmsHistory>(FinalValue.QUEUE_SIZE);

	/**
	 * 向队列中增加一些元素， 如果添加成功（队列不满） 返回true， 如果队列已经满了，则返回false
	 * 
	 * @param obj
	 *            添加的对象
	 * @return 添加结果
	 */
	public static boolean offerElement(SmsHistory obj) {
		return smsQueue.offer(obj);
	}

	/**
	 * 获取元素， 并从队列头部删除元素， 如果队列为空，则返回null
	 * 
	 * @return 队列中的元素
	 */
	public static SmsHistory pollElement() {
		return smsQueue.poll();
	}

	/**
	 * 打印队列大小及元素
	 */
	public static void printQueue() {
		int size = smsQueue.size();
		logger.info("队列最大长度为： " + FinalValue.QUEUE_SIZE);
		logger.info("队列实际大小为: " + size);
		logger.info("队列元素如下： ");

		Iterator<SmsHistory> it = smsQueue.iterator();

		while (it.hasNext()) {
			logger.info(JsonKit.toJsonString(it.next()));
		}
	}

	/**
	 * 将队列元素转换为列表输出
	 */
	public static List<SmsHistory> getQueueAsList() {
		List<SmsHistory> list = new ArrayList<SmsHistory>();
		Iterator<SmsHistory> it = smsQueue.iterator();

		while (it.hasNext()) {
			list.add(it.next());
		}

		return list;
	}
}
