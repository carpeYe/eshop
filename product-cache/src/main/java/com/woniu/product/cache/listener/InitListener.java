package com.woniu.product.cache.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.woniu.product.cache.kafka.KafkaConsumer;
import com.woniu.product.cache.spring.SpringContext;

public class InitListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		/*ServletContext sc = sce.getServletContext();
		ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(sc);
		SpringContext.setApplicationContext(ac);
		new Thread(new KafkaConsumer("product")).start();*/
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
