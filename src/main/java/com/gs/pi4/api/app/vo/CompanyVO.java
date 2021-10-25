package com.gs.pi4.api.app.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.dozer.Mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @Builder @NoArgsConstructor @AllArgsConstructor @ToString
public class CompanyVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Mapping("id")
	@JsonProperty("id")
	private Long key;

    private Long logo;
    private String name;
    private String document;
    private Date foundedAt;
    private Date createdAt;
    private Date changedAt;
}

