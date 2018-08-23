package com.woniu.product.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woniu.product.entity.ProductInventory;
import com.woniu.product.request.ProductCacheRefreshRequest;
import com.woniu.product.request.ProductInventoryDBUpdateRequest;
import com.woniu.product.request.Request;
import com.woniu.product.service.ProductInventorytService;
import com.woniu.product.service.RequestAsyncProcessService;
import com.woniu.product.vo.Response;

@RestController
public class ProductController {

	@Resource
	private ProductInventorytService productInventorytService;
	
	@Resource
	private RequestAsyncProcessService requestAsyncProcessService;
	
	
	@RequestMapping("/update")
	public Response updateProductInventory(ProductInventory productInventory) {
		try {
			Request request = new ProductInventoryDBUpdateRequest(productInventory, productInventorytService);
			requestAsyncProcessService.process(request);
			return new Response(Response.SUCCESS, "商品库存更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(Response.FAILURE, "商品库存更新失败");
		}
		
	}
	
	
	@RequestMapping("/query")
	public Response queryProductInventory(Integer productId) {
		try {
			Request request = new ProductCacheRefreshRequest(productId, productInventorytService, false);
			requestAsyncProcessService.process(request);
			ProductInventory productInventory = null;
			Long startTime = System.currentTimeMillis();
			while(true) {				
				Long endTime = System.currentTimeMillis();
				if(endTime - startTime > 200) {
					break;
				}
				productInventory = productInventorytService.queryProductInventoryCacheByProductId(productId);	
				if(productInventory != null) {
					System.out.println("缓存数据库中读取到库存数据 :" +productInventory.getProductId() +" "+"productCnt: "+productInventory.getInventoryCnt());
					return new Response(Response.SUCCESS, "productId: " +productInventory.getProductId() +" "+"productCnt: "+productInventory.getInventoryCnt());
				}else {
					System.out.println("缓存数据库商品读取都塞中  :" + (endTime - startTime)+"request:"+request+" flag: "+ request.isForceRefresh());
					Thread.sleep(20);
				}
			}
			productInventory = productInventorytService.queryProductInventoryByProductId(productId);
			if(productInventory != null) {
				System.out.println("Mysql数据库中读取到库存数据 :" +productInventory.getProductId() +" "+"productCnt: "+productInventory.getInventoryCnt());
				 request = new ProductCacheRefreshRequest(productId, productInventorytService, true);
				 requestAsyncProcessService.process(request);
			}
			return new Response(Response.SUCCESS, "productId: " +productInventory.getProductId() +" "+"productCnt: "+productInventory.getInventoryCnt());
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(Response.FAILURE, "商品库存查询失败");
		}
	}
}
