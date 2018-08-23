package com.woniu.product.cache.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaConsumer implements Runnable{
	
	private ConsumerConnector consumerConnector;
	
	private String topic;

	public KafkaConsumer(String topic) {
		this.consumerConnector = Consumer.createJavaConsumerConnector(createConsumerConfig());
		this.topic = topic;
	}
	@Override
	public void run() {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, 1);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = 
        		consumerConnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
        for (KafkaStream<byte[], byte[]> kafkaStream : streams) {
			new Thread(new KafkaMessageProcessor(kafkaStream)).start();
		}
	}

	
	/**
	 * 创建kafka cosumer config
	 * @return
	 */
	private static ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", "192.168.0.189:2181,192.168.0.190:2181,192.168.0.191:2181");
        props.put("group.id", "eshop-cache-group");
        props.put("zookeeper.session.timeout.ms", "40000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        return new ConsumerConfig(props);
    }
}
