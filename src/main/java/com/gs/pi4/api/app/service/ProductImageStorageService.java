
package com.gs.pi4.api.app.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.gs.pi4.api.core.image.Image;
import com.gs.pi4.api.core.product.Product;
import com.gs.pi4.api.core.product.ProductImage;
import com.gs.pi4.api.core.product.ProductImageId;

import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class ProductImageStorageService extends StorageService {
    
    private static final String PREFIX = "images/product";

    public ProductImageStorageService() {
        super(PREFIX);
    }

    protected ProductImage parse2ProductImage(@NonNull Image image, @NonNull Product product) {
        return ProductImage.builder()
            .createdAt(new Date())
            .id(new ProductImageId(product.getId(), image.getId()))
            .image(image)
            .product(product)
            .build();
    }

    protected List<ProductImage> parseImage(@NonNull List<Image> images, @NonNull Product product) {
        return images.stream().map(el -> parse2ProductImage(el, product)).collect(Collectors.toList());
    }
}
