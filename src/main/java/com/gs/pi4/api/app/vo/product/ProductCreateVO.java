package com.gs.pi4.api.app.vo.product;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.dozer.Mapping;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode(callSuper=false) @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductCreateVO extends RepresentationModel<ProductCreateVO> {
    
    @Mapping("id")
	@JsonProperty("id")
	private Long key;

    private String name;
    private String description;

    private Long price;

    private Long companyId;
    private Long unitMeasureId;
    private List<MultipartFile> images;

}
