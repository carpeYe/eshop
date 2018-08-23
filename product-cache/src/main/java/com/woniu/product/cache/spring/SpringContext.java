package com.woniu.product.cache.spring;

import org.springframework.context.ApplicationContext;
/**
 * spring 上下文
 * @author Hesion
 *
 */
public class SpringContext {

	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		SpringContext.applicationContext = applicationContext;
	}
	
	
	
	
}
