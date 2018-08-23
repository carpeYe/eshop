package com.woniu.product.cache.kafka;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.woniu.product.cache.model.ProductInfo;
import com.woniu.product.cache.service.CacheService;
import com.woniu.product.cache.spring.SpringContext;
import com.woniu.product.cache.zk.ZookeeperSession;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

public class KafkaMessageProcessor implements Runnable{
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final String PRODUCT_UPDATE_SERVICE_ID = "1";
	
	private KafkaStream<byte[], byte[]> kafkaStream;
	
	private CacheService cacheService;
	
	public KafkaMessageProcessor(KafkaStream<byte[], byte[]> kafkaStream) {
		this.kafkaStream = kafkaStream;
		this.cacheService = (CacheService) SpringContext.getApplicationContext().getBean("cacheService");
	}

	@Override
	public void run() {
		
		ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
		while(it.hasNext()) {
			String message = new String(it.next().message()); 
			JSONObject messageJson = JSONObject.parseObject(message);
			String serviceId = messageJson.getString("serviceId");
			if(PRODUCT_UPDATE_SERVICE_ID.equals(serviceId)) {
				ZookeeperSession zkSession = ZookeeperSession.getInstance();
				Integer id = messageJson.getInteger("id");
				zkSession.acquireDistributeLock(id+"");
				ProductInfo productInfo = new ProductInfo();
				productInfo.setId(id);
				productInfo.setName("mac pro");
				productInfo.setPrice("13999");
				productInfo.setModifyTime("2018-05-14 16:55:00");
				ProductInfo existProductInfo = cacheService.queryRedisProductInfo(id);
				if(existProductInfo != null ) {
					try {
						Date date = sdf.parse(productInfo.getModifyTime());
						Date existDate = sdf.parse(existProductInfo.getModifyTime());
						if(date.before(existDate)) {
							zkSession.releaseDistributeLock(id+"");
							return;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				cacheService.addEhcacheProductInfo(productInfo);
				cacheService.addRedisProductInfo(productInfo);
				zkSession.releaseDistributeLock(id+"");
			}
		}
	}

}
