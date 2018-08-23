package com.woniu.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.woniu.product.listener.InitListener;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@MapperScan("com.woniu.product.mapper")
public class ProductApplication {

	public static void main(String... args) {
		SpringApplication.run(ProductApplication.class, args);
	}
	
	/**
	 * 注册监听器
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public ServletListenerRegistrationBean<InitListener> servletListenerRegistrationBean() {
		ServletListenerRegistrationBean<InitListener> servletListenerRegistrationBean =
				new ServletListenerRegistrationBean();
		servletListenerRegistrationBean.setListener(new InitListener());
		return servletListenerRegistrationBean;
	}
}
