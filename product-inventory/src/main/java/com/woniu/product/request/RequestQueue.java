package com.woniu.product.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求内存队列
 * 
 * @author Administrator
 *
 */
public class RequestQueue {

	/*阻塞队列数组*/
	private List<ArrayBlockingQueue<Request>> queues = new ArrayList<ArrayBlockingQueue<Request>>();
	/*标识符map*/
	private Map<Integer,Boolean> flagMap = new ConcurrentHashMap<Integer, Boolean>();

	/**
	 * 单例有很多种方式去实现：我采取绝对线程安全的一种方式
	 * 
	 * 静态内部类的方式，去初始化单例
	 * 
	 * @author Administrator
	 *
	 */
	private static class Singleton {

		private static RequestQueue instance;
		static {
			instance = new RequestQueue();
		}

		public static RequestQueue getInstance() {
			return instance;
		}
	}
	/**
	 * 获取请求队列实例
	 * @return
	 */
	public static RequestQueue getInstance() {
		return Singleton.getInstance();
	}
	
	/**
	 * 阻塞队列数组中新增 阻塞队列
	 * @param queue
	 */
	public void addQueue(ArrayBlockingQueue<Request> queue) {
		queues.add(queue);
	}
	
	/**
	 * 获取阻塞队列数组的长度
	 * @return
	 */
	public Integer queryQueuesSize() {
		return queues.size();
	}
	
	/**
	 * 获取阻塞队列
	 */
	public ArrayBlockingQueue<Request> getQueue(Integer index){
		return queues.get(index);
	}

	/**
	 * 获取标识符map
	 * @return
	 */
	public Map<Integer, Boolean> getFlagMap() {
		return flagMap;
	}
	
	
}
