package com.woniu.product.service.impl;

import java.util.concurrent.ArrayBlockingQueue;

import org.springframework.stereotype.Service;

import com.woniu.product.request.Request;
import com.woniu.product.request.RequestQueue;
import com.woniu.product.service.RequestAsyncProcessService;

@Service("requestAsyncProcessService")
public class RequestAsyncProcessServiceImpl implements RequestAsyncProcessService{

	public void process(Request request) {
		try {
			ArrayBlockingQueue<Request> queue = getRoutingQueue(request.getProductId());
			queue.put(request);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayBlockingQueue<Request> getRoutingQueue(Integer productId){
		
		RequestQueue requestQueue = RequestQueue.getInstance();
		
		String key = String.valueOf(productId);
		
		int h;
		/*
		 * int 最大位数是32位
		  ^是异或运算符（把数据转换成二进制，然后按位进行运算）。
		  运算规则：0^0 = 0， 1^0 = 1，  0^1 = 1，  1^1 = 0，运算对象相同为0，不同为1.
		*/
		int hash = key == null ? 0 : (h = key.hashCode()) ^ (h >>> 16);
		
		// 对hash值取模，将hash值路由到指定的内存队列中，比如内存队列大小8
		// 用内存队列的数量对hash值取模之后，结果一定是在0~7之间
		// 所以任何一个商品id都会被固定路由到同样的一个内存队列中去的
		// 二进制取模
		int index = (requestQueue.queryQueuesSize() - 1) & hash;
		
		return requestQueue.getQueue(index);
		
	}
	
}
