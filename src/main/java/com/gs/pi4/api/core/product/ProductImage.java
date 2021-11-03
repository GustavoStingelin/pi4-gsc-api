package com.gs.pi4.api.core.product;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.gs.pi4.api.core.image.Image;

import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "public", name = "product_image")
@Where(clause = "deleted_at is null")
@Getter @Setter @EqualsAndHashCode @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductImage implements Serializable  {
 
    @EmbeddedId
    private ProductImageId id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    private Product product;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("imageId")
    private Image image;
 
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "deleted_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

}