package com.woniu.product.cache.zk;
/**
 *  单例ZookeeperSession
 * @author Hesion
 *
 */
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperSession {
  
	/**
	 * CountDownLatch是一个同步工具类，用来协调多个线程之间的同步，或者说起到线程之间的通信（而不是用作互斥的作用）。
		CountDownLatch能够使一个线程在等待另外一些线程完成各自工作之后，再继续执行。
		使用一个计数器进行实现。计数器初始值为线程的数量。当每一个线程完成自己任务后，计数器的值就会减一。
		当计数器的值为0时，表示所有的线程都已经完成了任务，然后在CountDownLatch上等待的线程就可以恢复执行任务。
	 */
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	
	private ZooKeeper zooKeeper;
	
	public ZookeeperSession() {
		try {
			this.zooKeeper = 
					new ZooKeeper("192.168.0.189:2181,192.168.0.190:2181,192.168.0.191:2181", 50000, new ZookeeperWatcher());
			connectedSemaphore.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取分布式锁
	 * @param productId
	 */
	public void acquireDistributeLock(String path) {
		try {
			zooKeeper.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		} catch (KeeperException | InterruptedException exception) {
			if(exception instanceof NodeExistsException) {
				while(true) {
					try {
						Thread.sleep(20);
						zooKeeper.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
					} catch (KeeperException | InterruptedException e) {
						if(exception instanceof NodeExistsException) {
							continue;
						}
					}
					break;
				}
			}
		}
	}
	
	/**
	 * 释放分布式锁
	 * @param productId
	 */
	public void releaseDistributeLock(String path) {
		try {
			zooKeeper.delete(path, -1);
		} catch (InterruptedException | KeeperException e) {
			e.printStackTrace();
		}
	}
	
	public void createNode(String path) {
		try {
			zooKeeper.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public String getNodeData(String path) {
		try {
		 return	new String(zooKeeper.getData(path, false, new Stat()));
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void setNodeData(String path,String data) {
		try {
			zooKeeper.setData(path, data.getBytes(), -1);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private class ZookeeperWatcher implements Watcher{

		@Override
		public void process(WatchedEvent event) {
			
			if(KeeperState.SyncConnected == event.getState()) {
				 connectedSemaphore.countDown();
			}
			
		}
		
	}
	/**
	 *  静态类
	 * @author Hesion
	 *
	 */
	private static class Singleton{
		
		private static ZookeeperSession zookeeperSession;
		
		static {
			zookeeperSession = new ZookeeperSession();
		}
		
		public static ZookeeperSession getInstance() {
			return zookeeperSession;
		}
	}
	
	
	public static ZookeeperSession getInstance() {
		return Singleton.getInstance();
	}
	
	public static void init() {
		getInstance();
	}
}
