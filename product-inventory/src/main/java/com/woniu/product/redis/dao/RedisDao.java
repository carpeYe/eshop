package com.woniu.product.redis.dao;

public interface RedisDao {

	void add(String key,String value);
	
	String query(String key);
	
	void delete(String key);
}
