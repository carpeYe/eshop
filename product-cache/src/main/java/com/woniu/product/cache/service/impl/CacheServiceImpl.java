package com.woniu.product.cache.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.woniu.product.cache.dao.RedisDao;
import com.woniu.product.cache.model.ProductInfo;
import com.woniu.product.cache.service.CacheService;

@Service("cacheService")
public class CacheServiceImpl implements CacheService{
	
	private static final String CACHE_NAME = "local"; 
	
	@Autowired
	private RedisDao redisDao;
	
	
	/**
	 * 根据商品Id获取商品信息
	 * @param id
	 * @return
	 */
	@Cacheable(value = CACHE_NAME , key = "'key_'+#id")
	public ProductInfo queryEhcacheProductInfo(Integer id) {
		return null;
	}
	
	/**
	 * 新增商品信息
	 * @param productInfo
	 * @return
	 */
	@CachePut(value = CACHE_NAME , key = "'key_'+#productInfo.getId()")
	public ProductInfo addEhcacheProductInfo(ProductInfo productInfo) {
		return productInfo;
	}

	/**
	 * redis缓存查询查询商品信息
	 */
	@Override
	public void addRedisProductInfo(ProductInfo productInfo) {
		redisDao.add("product:productId:"+productInfo.getId(), JSONObject.toJSONString(productInfo));
	}
	/**
	 * redis缓存新增商品信息
	 */
	@Override
	public ProductInfo queryRedisProductInfo(Integer id) {
		String productStr = redisDao.query("product:productId:"+id);
		return JSONObject.parseObject(productStr, ProductInfo.class);
	}
	
}
