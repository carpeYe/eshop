package com.woniu.product.request;

import com.woniu.product.entity.ProductInventory;
import com.woniu.product.service.ProductInventorytService;

public class ProductCacheRefreshRequest implements Request{
	
	/*商品ID*/
	private Integer productId;
	
	/*商品库存service*/
	private ProductInventorytService productInventoryService;
	
	/*是否强制刷新内存队列*/
	private boolean forceRefresh;
	
	public ProductCacheRefreshRequest(Integer productId, ProductInventorytService productInventoryService, boolean forceRefresh) {
		this.productId = productId;
		this.productInventoryService = productInventoryService;
		this.forceRefresh = forceRefresh;
	}

	public void process() {
		//从数据库查询商品的库存数量
		ProductInventory productInventory = productInventoryService.queryProductInventoryByProductId(productId);
		//将商品的库存的数量添加到缓存当中去
		productInventoryService.addProductInventoryCache(productInventory);
	}

	public boolean isForceRefresh() {
		return forceRefresh;
	}

	public Integer getProductId() {
		return productId;
	}

}
