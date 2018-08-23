package com.woniu.product.thread;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.woniu.product.request.Request;
import com.woniu.product.request.RequestQueue;

public class RequestProcessThreadPool {

	private ExecutorService threadPool = Executors.newFixedThreadPool(10);

	private RequestProcessThreadPool() {	
		RequestQueue requestQueue = RequestQueue.getInstance();
		for(int i = 0 ; i < 10 ; i++) {
			ArrayBlockingQueue<Request> abq = new ArrayBlockingQueue<Request>(100);
			requestQueue.addQueue(abq);
			threadPool.submit(new RequestProcessThread(abq));
		}
	}
	
	private static class Singleton{
		
		private static RequestProcessThreadPool instance;
		
		static {
			instance = new RequestProcessThreadPool();
		}
		
		public static RequestProcessThreadPool getInstance() {
			return instance;
		}
	}
	
	public static RequestProcessThreadPool getInstance() {
		return Singleton.getInstance();
	}
	
	
}
