package com.woniu.product.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
/**
 *  单例
 * redis cluster 多节点连接服务加载
 * @author Hesion
 *
 */
@Configuration
@Component
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class JedisClusterFactory  {

	/*配置文件加载多节点ip:port数组*/
	private List<String> nodes;

	public List<String> getNodes() {
		return nodes;
	}
	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}
	
	@Bean
	public JedisCluster getConnection() {
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		for (String node : nodes) {
			String host = node.substring(0,node.indexOf(":"));
			Integer post =  Integer.valueOf(node.substring(node.indexOf(":")+1, node.length()));
			HostAndPort jedisClusterNode = new HostAndPort(host,post);
			jedisClusterNodes.add(jedisClusterNode);
		}
		JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
		return jedisCluster;
	}
}
