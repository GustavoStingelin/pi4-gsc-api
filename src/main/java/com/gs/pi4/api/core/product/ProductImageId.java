package com.gs.pi4.api.core.product;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ProductImageId implements Serializable {
 
    @Column(name = "product_id")
    private Long productId;
 
    @Column(name = "image_id")
    private Long imageId;
 
}