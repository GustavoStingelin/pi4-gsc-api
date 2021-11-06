package com.gs.pi4.api.app.vo.product;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gs.pi4.api.app.vo.ImageVO;

import org.dozer.Mapping;
import org.springframework.hateoas.RepresentationModel;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode(callSuper=false) @Builder
public class ProductVO extends RepresentationModel<ProductVO> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Mapping("id")
	@JsonProperty("id")
	private Long key;

    private String name;
    private String description;
    private Float price;

    private UnitMeasureVO unitMeasure;
    private List<ImageVO> images;

}
