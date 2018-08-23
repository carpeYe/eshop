package com.woniu.product.cache.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.woniu.product.cache.dao.RedisDao;

import redis.clients.jedis.JedisCluster;

@Repository("redisDao")
public class RedisDaoImpl implements RedisDao{

	@Autowired
	private JedisCluster jedisCluster;
	
	public void add(String key, String value) {
		jedisCluster.set(key, value);
	}

	public String query(String key) {
		return jedisCluster.get(key);
	}

	public void delete(String key) {
		jedisCluster.del(key);
		
	}

}
