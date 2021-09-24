package com.gs.pi4.api.app.vo.product;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gs.pi4.api.app.vo.TimestampLogVO;

import org.dozer.Mapping;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode @Builder
public class UnitMeasureVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Mapping("id")
	@JsonProperty("id")
	private Long key;

    private String name;
    private String description;
    private boolean onlyInteger;

    private TimestampLogVO timestampLog;
}
