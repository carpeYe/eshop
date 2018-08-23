package com.woniu.product.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.woniu.product.cache.listener.InitListener;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class ProductCacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductCacheApplication.class, args);
	}
	
	
	/**
	 * 注册监听器
	 * @return
	 */
	@Bean
	public ServletListenerRegistrationBean<InitListener> servletListenerRegistrationBean() {
		ServletListenerRegistrationBean<InitListener> servletListenerRegistrationBean = 
				new ServletListenerRegistrationBean<InitListener>(new InitListener());
		return servletListenerRegistrationBean;
	}

}
