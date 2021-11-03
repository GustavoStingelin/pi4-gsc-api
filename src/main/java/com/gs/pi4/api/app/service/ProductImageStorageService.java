
package com.gs.pi4.api.app.service;

import org.springframework.stereotype.Service;

@Service
public class ProductImageStorageService extends StorageService {
    
    private static final String PREFIX = "images/product";

    public ProductImageStorageService() {
        super(PREFIX);
    }

}
