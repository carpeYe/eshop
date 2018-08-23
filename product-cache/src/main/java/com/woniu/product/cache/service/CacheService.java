package com.woniu.product.cache.service;

import com.woniu.product.cache.model.ProductInfo;

/**
 * 本地缓存业务
 * @author Hesion
 *
 */
public interface CacheService {

	/**
	 * 根据商品Id获取商品信息
	 * @param id
	 * @return
	 */
	ProductInfo queryEhcacheProductInfo(Integer id);
	
	/**
	 * 新增商品信息
	 * @param productInfo
	 * @return
	 */
	ProductInfo addEhcacheProductInfo(ProductInfo productInfo);
	
	/**
	 * redis缓存新增商品信息
	 */
	void addRedisProductInfo(ProductInfo productInfo);
	
	/**
	 * redis缓存查询查询商品信息
	 */
	ProductInfo queryRedisProductInfo(Integer id);
	
	
}
