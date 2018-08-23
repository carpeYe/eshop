package com.woniu.product.redis.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Repository;
import com.woniu.product.redis.dao.RedisDao;

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
