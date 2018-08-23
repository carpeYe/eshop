package com.woniu.product.cache.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

@Configuration
@Component
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class JedisClusterFactory {

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
