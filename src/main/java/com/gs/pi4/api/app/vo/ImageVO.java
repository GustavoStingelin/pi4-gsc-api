package com.gs.pi4.api.app.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.dozer.Mapping;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode @Builder
public class ImageVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Mapping("id")
	@JsonProperty("id")
	private String externalId;

}
