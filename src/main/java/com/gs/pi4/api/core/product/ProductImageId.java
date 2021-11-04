package com.gs.pi4.api.core.product;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter @EqualsAndHashCode @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductImageId implements Serializable {
 
    @Column(name = "product_id")
    private Long productId;
 
    @Column(name = "image_id")
    private Long imageId;
 
}