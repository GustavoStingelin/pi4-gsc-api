package com.gs.pi4.api.app.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gs.pi4.api.app.vo.product.ProductVO;

import org.dozer.Mapping;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode @Builder
public class SupplierVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Mapping("id")
	@JsonProperty("id")
	private Long key;

    private ImageVO logo;
    private String name;
    private String document;
    private List<ProductVO> products;

    private TimestampLogVO timestampLog;
}
