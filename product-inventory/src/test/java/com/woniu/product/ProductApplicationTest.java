package com.woniu.product;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.junit4.SpringRunner;

import com.woniu.product.config.JedisClusterFactory;
import com.woniu.product.redis.dao.RedisDao;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductApplicationTest{
	@Autowired
	RedisDao redisDao;
	@Test
	public void test() {
	   redisDao.add("name:yejianhui", "25");
	   System.out.println(redisDao.query("name:yejianhui"));
	}
}
