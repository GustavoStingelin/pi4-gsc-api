package com.gs.pi4.api.app.vo.company;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @Builder @NoArgsConstructor @AllArgsConstructor @ToString
public class CompanyPartnerVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private boolean isAccepted;
}

