package com.gs.pi4.api.app.vo.budget;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gs.pi4.api.app.vo.company.CompanyPartnerVO;

import org.dozer.Mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode @Builder @NoArgsConstructor @AllArgsConstructor
public class BudgetResponseVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Mapping("id")
	@JsonProperty("id")
	private Long key;

    private CompanyPartnerVO supplier;
    private BudgetRequestVO budgetRequest;
    private List<BudgetResponseItemVO> itens;
    private String description;
    private Date expiresOn;
    private Date buyedAt;
}
