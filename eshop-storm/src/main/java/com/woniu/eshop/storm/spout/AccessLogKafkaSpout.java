package com.woniu.eshop.storm.spout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
/**
 * AccessLog kafka接受spout
 * @author Hesion
 *
 */
public class AccessLogKafkaSpout extends BaseRichSpout{

	private static final long serialVersionUID = 3465199868902943698L;
	
	private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(1000);
	
	private SpoutOutputCollector collector;
	
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		startKafkaComsumer("access-log");
	}

	public void nextTuple() {
		if(queue.size() > 0) {
			try {
				String message = queue.take();
				collector.emit(new Values(message));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else {
			Utils.sleep(200);
		}
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("message"));
		
	}
	
	@SuppressWarnings("unused")
	private void startKafkaComsumer(String topic){
		 Properties props = new Properties();
        props.put("zookeeper.connect", "192.168.0.189:2181,192.168.0.190:2181,192.168.0.191:2181");
        props.put("group.id", "eshop-cache-group");
        props.put("zookeeper.session.timeout.ms", "40000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        ConsumerConfig config = new  ConsumerConfig(props);
        ConsumerConnector consumerConnector = Consumer.createJavaConsumerConnector(config);
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, 1);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = 
        		consumerConnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
        for (KafkaStream<byte[], byte[]> kafkaStream : streams) {
			new Thread(new KafkaMessageProcessor(kafkaStream)).start();
		}
	}
	
	private class KafkaMessageProcessor implements Runnable{
		
		private KafkaStream<byte[], byte[]> kafkaStream;

		public KafkaMessageProcessor(KafkaStream<byte[], byte[]> kafkaStream) {
			this.kafkaStream = kafkaStream;
		}

		public void run() {
			ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
			while(it.hasNext()) {
				String message = new String(it.next().message());
				try {
					queue.put(message);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
