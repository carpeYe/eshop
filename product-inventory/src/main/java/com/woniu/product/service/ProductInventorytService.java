package com.woniu.product.service;

import com.woniu.product.entity.ProductInventory;

/**
 * 商品库存业务层
 * @author Hesion
 *
 */
public interface ProductInventorytService {

	/**
	 * 根据商品Id查询商品库存信息
	 * @param productId
	 * @return
	 */
	ProductInventory queryProductInventoryByProductId(Integer productId);
	
	/**
	 * 更新商品库存信息
	 * @param productInventory
	 */
	void UpdateProductInventory(ProductInventory productInventory);
	
	/**
	 * 删除缓存中的商品库存
	 * @param productId
	 */
	void removeProductInventoryCache(Integer productId);
	
	/**
	 * 查询缓存中商品库存
	 * @param productId
	 * @return
	 */
	ProductInventory queryProductInventoryCacheByProductId(Integer productId);
	
	/**
	 * 新增缓存中商品库存
	 * @param productInventory
	 */
	void addProductInventoryCache(ProductInventory productInventory);
}
