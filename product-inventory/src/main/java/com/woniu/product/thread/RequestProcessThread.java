package com.woniu.product.thread;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import com.woniu.product.request.ProductCacheRefreshRequest;
import com.woniu.product.request.ProductInventoryDBUpdateRequest;
import com.woniu.product.request.Request;
import com.woniu.product.request.RequestQueue;

/**
 * 执行请求的工作线程
 * 
 * @author Hesion
 *
 */
public class RequestProcessThread implements Callable<Boolean> {

	/* https://blog.csdn.net/qq_23359777/article/details/70146778
	 * ArrayBlockingQueue是一个阻塞式的队列，继承自AbstractBlockingQueue,
	 * 间接的实现了Queue接口和Collection接口。底层以数组的形式保存数据(实际上可看作一个循环数组)。常用的操作包括 add
	 * ,offer,put，remove,poll,take,peek。 前三者add offer put
	 * 是插入的操作。后面四个方法是取出的操作。他们之间的区别和关联： add: 内部实际上获取的offer方法，当Queue已经满了时，抛出一个异常。不会阻塞。
	 * offer:当Queue已经满了时，返回false。不会阻塞。
	 * put:当Queue已经满了时，会进入等待，只要不被中断，就会插入数据到队列中。会阻塞，可以响应中断。 取出方法中
	 * remove和add相互对应。也就是说，调用remove方法时，假如对列为空，则抛出一场。另外的，poll与offer相互对应。take和put相互对应。
	 * peek方法比较特殊，前三个取出的方法，都会将元素从Queue的头部溢出，但是peek不会，实际上只是，获取队列头的元素。peek方法也不会阻塞。
	 * 当队列为空时，直接返回Null。 2.对比LinkedBlockingQueue
	 * LinkedBlockingQueue也是一个阻塞式的队列，与ArrayBlockingQueue的区别是什么呢？
	 * LinkedBlockingQueue保存元素的是一个链表。其内部有一个Node的内部类，其中有一个成员变量 Node
	 * next。就这样形成了一个链表的结构，要获取下一个元素，只要调用next就可以了。而ArrayBlockingQueue则是一个数组。
	 * LinkedBlockingQueue内部读写(插入获取)各有一个锁，而ArrayBlockingQueue则读写共享一个锁。
	 * 3.选择LinkedBlockingQueue还是ArrayBlockingQueue
	 * 个人感觉大多数场景适合使用LinkedBlockingQueue。在JDK源码当中有说明，
	 * LinkedBlockingQueue比ArrayBlockingQueue有更高的吞吐量，但是性能表现更难预测（
	 * 也就是说相比ArrayBlockingQueue性能表现不稳定，但是也很稳定了）。
	 * 为什么会有吞吐量的区别，个人以为可能是ArrayBlockingQueue两个锁的缘故，在大量并发的情况下，插入和读取都很多时，就会造成一点的时间浪费。
	 * 还有一点，应为LinkedBlockingQueue创建时，默认会直接创建一个Integer.MAX_VALUE的数组，当插入少，读取多时，
	 * 就会造成很大的空间浪费。而LinkedBlockingQueue实际上实在等需要的时候才会创建一个Node节点。
	 */
	private ArrayBlockingQueue<Request> queue;

	public RequestProcessThread(ArrayBlockingQueue<Request> queue) {
		this.queue = queue;
	}

	public Boolean call() throws Exception {
		try {
			while(true) {
				// ArrayBlockingQueue
				// Blocking就是说明，如果队列满了，或者是空的，那么都会在执行操作的时候，阻塞住
				Request request = queue.take();
				boolean forceRfresh = request.isForceRefresh();
				// 先做读请求的去重
				if(!forceRfresh) {
					RequestQueue requestQueue = RequestQueue.getInstance();
					Map<Integer,Boolean> flagMap = requestQueue.getFlagMap();
				    if(request instanceof ProductCacheRefreshRequest) {
				    	System.out.println("进入商品查询队列" + request.getProductId()+"request:"+request+" flag: "+ request.isForceRefresh());
				    	Boolean flag = flagMap.get(request.getProductId());
				    	// 如果flag是null
				    	if(flag == null) {
				    		flagMap.put(request.getProductId(), false);
				    	}
				    	// 如果是缓存刷新的请求，那么就判断，如果标识不为空，而且是true，就说明之前有一个这个商品的数据库更新请求
				    	if(flag != null && flag) {
				    		System.out.println("商品进入查询 ");
				    		flagMap.put(request.getProductId(), false);
				    	}
				    	// 如果是缓存刷新的请求，而且发现标识不为空，但是标识是false
						// 说明前面已经有一个数据库更新请求+一个缓存刷新请求了，大家想一想
				    	if(flag != null && !flag) {
				    		// 对于这种读请求，直接就过滤掉，不要放到后面的内存队列里面去了
				    		System.out.println("商品查询被过滤 ");
				    		return true;
				    	}
				    }else if(request instanceof ProductInventoryDBUpdateRequest) {
				    	// 如果是一个更新数据库的请求，那么就将那个productId对应的标识设置为true
				    	System.out.println("进入商品更新队列" + request.getProductId());
				    	flagMap.put(request.getProductId(), true);
				    }
				}
				request.process();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
