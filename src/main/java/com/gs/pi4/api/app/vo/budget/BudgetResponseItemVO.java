package com.gs.pi4.api.app.vo.budget;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gs.pi4.api.app.vo.TimestampLogVO;
import com.gs.pi4.api.app.vo.product.ProductVO;

import org.dozer.Mapping;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode @Builder
public class BudgetResponseItemVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Mapping("id")
	@JsonProperty("id")
	private Long key;

    private ProductVO product;
    private Float quantity;
    private Float unitPrice;
    private String description;

    private TimestampLogVO timestampLog;
}
