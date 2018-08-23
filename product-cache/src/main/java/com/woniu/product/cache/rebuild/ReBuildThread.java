package com.woniu.product.cache.rebuild;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.woniu.product.cache.model.ProductInfo;
import com.woniu.product.cache.service.CacheService;
import com.woniu.product.cache.spring.SpringContext;
import com.woniu.product.cache.zk.ZookeeperSession;
/**
 *   商品缓存重建线程
 * @author Hesion
 *
 */
public class ReBuildThread implements Runnable{
	
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	public void run() {
		
		ReBuildQueue reBuildQueue = ReBuildQueue.getInstance();
		CacheService cacheService = 
				(CacheService) SpringContext.getApplicationContext().getBean("cacheService");
		ZookeeperSession zookeeperSession = ZookeeperSession.getInstance();
		while(true) {
			
			ProductInfo productInfo = reBuildQueue.takeProductInfo();
			zookeeperSession.acquireDistributeLock(productInfo.getId()+"");
			ProductInfo exsitProductInfo = cacheService.queryRedisProductInfo(productInfo.getId());
			if(exsitProductInfo != null) {
				Date existTime;
				try {
					existTime = sdf.parse(exsitProductInfo.getModifyTime());
					Date nowTime = sdf.parse(productInfo.getModifyTime());
					if(existTime.after(nowTime)) {
						continue;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}	
			}
			cacheService.addEhcacheProductInfo(exsitProductInfo);
			cacheService.addEhcacheProductInfo(exsitProductInfo);
			zookeeperSession.releaseDistributeLock(productInfo.getId()+"");
		}
		
	}

}
