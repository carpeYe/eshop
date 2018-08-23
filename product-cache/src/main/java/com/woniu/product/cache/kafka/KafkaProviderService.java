package com.woniu.product.cache.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class KafkaProviderService {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	/**
	 * kafka异步发送消息
	 * @param topic
	 * @param json
	 */
	public void sendMessage(String topic,JSONObject json) {
		kafkaTemplate.send(topic, json.toJSONString());
	}
}
