package com.hari.ProductService.service;

import com.hari.ProductService.model.ProductRequest;
import com.hari.ProductService.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
