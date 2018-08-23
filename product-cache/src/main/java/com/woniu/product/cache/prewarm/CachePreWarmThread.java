package com.woniu.product.cache.prewarm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.woniu.product.cache.model.ProductInfo;
import com.woniu.product.cache.service.CacheService;
import com.woniu.product.cache.spring.SpringContext;
import com.woniu.product.cache.zk.ZookeeperSession;

public class CachePreWarmThread implements Runnable{

	private ZookeeperSession zookeeperSession;
	
	private CacheService cacheService;
	
	private static final String ZOOKEEPER_SAVE_NODE_DATA_PATH = "/taskid-list";
	
	public CachePreWarmThread() {
		this.zookeeperSession = ZookeeperSession.getInstance();
		cacheService = (CacheService) SpringContext.getApplicationContext().getBean("cacheService");
	}

	@Override
	public void run() {

		String taskIdList = zookeeperSession.getNodeData(ZOOKEEPER_SAVE_NODE_DATA_PATH);
		String[] arrayTaskId = taskIdList.split(",");
		for (int i = 0; i < arrayTaskId.length; i++) {
			zookeeperSession.acquireDistributeLock("/taskId-status-lock"+arrayTaskId[i]);
			String status = zookeeperSession.getNodeData("/taskId-status"+arrayTaskId[i]);
			if("".equals(status)) {
				zookeeperSession.acquireDistributeLock("/taskId-lock"+arrayTaskId[i]);
				String hotPorductList = zookeeperSession.getNodeData("/taskid-hot-product-list"+arrayTaskId[i]);
				JSONArray productidJSONArray = JSONArray.parseArray(hotPorductList);
				for (int j = 0; j < productidJSONArray.size(); j++) {
					Long productId = productidJSONArray.getLong(j);
					JSONObject json = new JSONObject();
					json.put("id", productId);
					json.put("name", "mac pro");
					json.put("price", "10999");
					json.put("modifyTime", "2018-05-14 17:00:00");
					cacheService.addRedisProductInfo(JSONObject.toJavaObject(json, ProductInfo.class));
					cacheService.addEhcacheProductInfo(JSONObject.toJavaObject(json, ProductInfo.class));
				}
			 zookeeperSession.createNode("/taskId-status"+arrayTaskId[i]);
			 zookeeperSession.setNodeData("/taskId-status"+arrayTaskId[i], "success");
			 zookeeperSession.releaseDistributeLock("/taskId-lock"+arrayTaskId[i]);
			}
			zookeeperSession.releaseDistributeLock("/taskId-status-lock"+arrayTaskId[i]);
		}
	}

}
