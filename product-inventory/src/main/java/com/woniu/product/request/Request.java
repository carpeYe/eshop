package com.woniu.product.request;

public interface Request {

	void process();
	Integer getProductId();
	boolean isForceRefresh();
}
