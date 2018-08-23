package com.woniu.product.request;

import com.woniu.product.entity.ProductInventory;
import com.woniu.product.service.ProductInventorytService;
/**
 *  商品库存更新操作
 * @author Hesion
 *
 */
public class ProductInventoryDBUpdateRequest implements Request{
	
	private ProductInventory productInventory; 
	
	private ProductInventorytService productInventorytService;
	

	public ProductInventoryDBUpdateRequest(ProductInventory productInventory,
			ProductInventorytService productInventorytService) {
		this.productInventory = productInventory;
		this.productInventorytService = productInventorytService;
	}

	public void process() {
		productInventorytService.removeProductInventoryCache(productInventory.getProductId());
		productInventorytService.UpdateProductInventory(productInventory);
	}

	public Integer getProductId() {
		return productInventory.getProductId();
	}

	public boolean isForceRefresh() {
		return false;
	}

}
