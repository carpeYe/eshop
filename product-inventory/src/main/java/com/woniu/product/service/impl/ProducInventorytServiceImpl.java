package com.woniu.product.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.woniu.product.entity.ProductInventory;
import com.woniu.product.mapper.ProductInventoryMapper;
import com.woniu.product.redis.dao.RedisDao;
import com.woniu.product.service.ProductInventorytService;

@Service("producInventorytService")
public class ProducInventorytServiceImpl implements ProductInventorytService{
	
	@Resource
	private ProductInventoryMapper productInventoryMapper;
	@Resource
	private RedisDao redisDao;
	
	public ProductInventory queryProductInventoryByProductId(Integer productId) {
		return productInventoryMapper.findProductInventory(productId);
	}
	
	public void UpdateProductInventory(ProductInventory productInventory) {
		productInventoryMapper.updateProductInventory(productInventory);
	}

	public void removeProductInventoryCache(Integer productId) {
		String key = "product:inventory:" + productId;
		redisDao.delete(key);
		
	}

	public ProductInventory queryProductInventoryCacheByProductId(Integer productId) {
		String key = "product:inventory:" + productId;
		String result = redisDao.query(key);
		if(result != null && !"".equals(result)) {
			Long inventoryCnt = Long.valueOf(result);
			return new ProductInventory(productId, inventoryCnt);
		}
		return null;
	}

	public void addProductInventoryCache(ProductInventory productInventory) {
		String key = "product:inventory:" + productInventory.getProductId();
		String value = productInventory.getInventoryCnt()+"";
		redisDao.add(key, value);		
	}
	
	

}
