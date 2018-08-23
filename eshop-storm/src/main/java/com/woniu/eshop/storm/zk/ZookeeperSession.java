package com.woniu.eshop.storm.zk;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;


public class ZookeeperSession {

	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	private ZooKeeper zooKeeper;

	public ZookeeperSession() {
		try {
			this.zooKeeper = new ZooKeeper("192.168.0.189:2181,192.168.0.190:2181,192.168.0.191:2181", 
					50000, new ZookeeperWatcher());
		    connectedSemaphore.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void accquireDistributeLock(String path) {
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
		} catch (Exception e) {
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

	public static ZookeeperSession getInstance() {
		return Singleton.getInstance();
	}
	
	public static void init() {
		getInstance();
	}
	
	private class ZookeeperWatcher implements Watcher{

		public void process(WatchedEvent event) {
			if(KeeperState.SyncConnected == event.getState()) {
				connectedSemaphore.countDown();
			}	
		}
		
	}
	
	private static class Singleton{
		
		private static ZookeeperSession zookeeperSession;
		
		static {
			zookeeperSession = new ZookeeperSession();
		}
		public static ZookeeperSession getInstance() {
			return zookeeperSession;
		}
		
	}
	
}
