package com.woniu.product.cache.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
/**
 * Ehcache本地缓存配置管理类
 * @author carpeYe
 *
 */
@Configuration
@EnableCaching
public class EhcaheConfig {

	@Bean
	public EhCacheManagerFactoryBean chCacheManagerFactoryBean() {
		EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
		ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
		ehCacheManagerFactoryBean.setShared(true);
		return ehCacheManagerFactoryBean;
	}
	
	@Bean
	public EhCacheCacheManager ehCacheCacheManager(EhCacheManagerFactoryBean ehCacheManagerFactoryBean) {
		return new EhCacheCacheManager(ehCacheManagerFactoryBean.getObject());
	}
}
