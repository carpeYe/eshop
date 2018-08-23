package com.woniu.product.cache.rebuild;

import java.util.concurrent.ArrayBlockingQueue;

import com.woniu.product.cache.model.ProductInfo;

public class ReBuildQueue {

	private  ArrayBlockingQueue<ProductInfo> queue = new ArrayBlockingQueue<ProductInfo>(100);
	
	
	private static class Singleton{
		
		private static ReBuildQueue reBuildQueue;
		
		static {
			reBuildQueue = new ReBuildQueue();
		}
		public static ReBuildQueue getInstance() {
			return reBuildQueue;
		}
	}
	
	public void addProductInfo(ProductInfo productInfo) {
		try {
			queue.put(productInfo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public ProductInfo takeProductInfo() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ReBuildQueue getInstance() {
		return Singleton.getInstance();
	}
	
	public static void init() {
		getInstance();
	}
}
