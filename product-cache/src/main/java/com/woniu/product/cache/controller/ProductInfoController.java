package com.woniu.product.cache.controller;

import java.util.zip.CRC32;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.woniu.product.cache.kafka.KafkaProviderService;
import com.woniu.product.cache.model.ProductInfo;
import com.woniu.product.cache.rebuild.ReBuildQueue;
import com.woniu.product.cache.service.CacheService;

@RestController
public class ProductInfoController {

	@Resource
	private CacheService cacheService;
	@Resource
	private KafkaProviderService kafkaProviderService;
	
	@RequestMapping("/addehcache")
	public ProductInfo add(ProductInfo productInfo) {
		return cacheService.addEhcacheProductInfo(productInfo);
	}
	
	@RequestMapping("/getehcache")
	public ProductInfo get(Integer id) {
		return cacheService.queryEhcacheProductInfo(id);
	}
	
	@RequestMapping("/getredis")
	public ProductInfo getRedis(Integer id) {
		return cacheService.queryRedisProductInfo(id);
	}
	
	
	@RequestMapping("/product")
	public ProductInfo queryProductCache(Integer id) {
		ProductInfo productInfo = cacheService.queryRedisProductInfo(id);
		if(productInfo == null || "".equals(productInfo)) {
			productInfo = cacheService.queryEhcacheProductInfo(id);
		}
		if(productInfo == null || "".equals(productInfo)) {
			productInfo = cacheService.queryEhcacheProductInfo(id);
		}
		if(productInfo == null || "".equals(productInfo)) {
			JSONObject json = new JSONObject();
			json.put("id", id);
			json.put("name", "mac pro");
			json.put("price", "10999");
			json.put("modifyTime", "2018-05-14 17:00:00");
			productInfo = JSONObject.toJavaObject(json, ProductInfo.class);
			ReBuildQueue reBuildQueue = ReBuildQueue.getInstance();
			reBuildQueue.addProductInfo(productInfo);
		}	
		return productInfo;
	}
	
	@RequestMapping("/send")
	public ProductInfo sendKafka(ProductInfo productInfo) {
		JSONObject json = (JSONObject) JSONObject.toJSON(productInfo);
		json.put("serviceId", "1");
		String topic = "product";
		kafkaProviderService.sendMessage(topic, json);
		return productInfo;
	}
	
	
	public static void main(String...args) {
		CRC32 crc32 = new CRC32();
		crc32.update("6".getBytes());
		System.out.println(crc32.getValue()%2+1);
	}
	
}
