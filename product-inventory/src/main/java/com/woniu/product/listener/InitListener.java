package com.woniu.product.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.woniu.product.thread.RequestProcessThreadPool;

public class InitListener implements ServletContextListener{

	public void contextInitialized(ServletContextEvent sce) {
		// 初始化工作线程池和内存队列
		RequestProcessThreadPool.getInstance();
	}

	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
	}

}
