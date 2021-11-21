package com.gs.pi4.api.app.service;

import java.util.List;
import java.util.stream.Collectors;

import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.ResourceNotFoundException;
import com.gs.pi4.api.app.vo.budget.BudgetResponseVO;
import com.gs.pi4.api.core.budget.request.BudgetRequest;
import com.gs.pi4.api.core.budget.request.BudgetRequestItemRepository;
import com.gs.pi4.api.core.budget.response.BudgetResponse;
import com.gs.pi4.api.core.budget.response.BudgetResponseItemRepository;
import com.gs.pi4.api.core.budget.response.BudgetResponseRepository;
import com.gs.pi4.api.core.company.Company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class BudgetResponseService {

    @Autowired
    BudgetResponseRepository repository;

    @Autowired
    CompanyPartnerService companyPartnerService;

    @Autowired
    BudgetRequestItemService budgetRequestItemService;

    @Autowired
    BudgetResponseItemService budgetResponseItemService;

    @Autowired
    BudgetRequestItemRepository budgetRequestItemRepository;

    @Autowired
    BudgetResponseItemRepository budgetResponseItemRepository;

    public List<BudgetResponseVO> findAllByCompany(@NonNull Long companyId) {
        return parse2BudgetResponseVO(repository.findAllByCompany(companyId));
    }
    
    public BudgetResponseVO findByBudgetWithCompany(Long companyId, Long budgetId) {
        return parse2BudgetResponseVO(repository.findByBudgetWithCompany(companyId, budgetId)
                .orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString())));
    }

    private BudgetResponseVO parse2BudgetResponseVO(@NonNull BudgetResponse entity) {
        return BudgetResponseVO.builder()
                .key(entity.getId())
                .description(entity.getDescription())
                .expiresOn(entity.getExpiresOn())
                .supplier(companyPartnerService.parse2CompanyPartnerVO(entity.getCompany()))
                .itens(budgetResponseItemService.parse2BudgetResponseItemVO(entity.getItens()))
                .buyer(companyPartnerService.parse2CompanyPartnerVO(entity.getBudgetRequest().getCompany()))
                .build();
    }

    private List<BudgetResponseVO> parse2BudgetResponseVO(@NonNull List<BudgetResponse> entities) {
        return entities.stream().map(this::parse2BudgetResponseVO).collect(Collectors.toList());

    }

    private BudgetResponse parse(@NonNull BudgetResponseVO vo) {
        return BudgetResponse.builder()
                .id(vo.getKey())
                .description(vo.getDescription())
                .expiresOn(vo.getExpiresOn())
                .company(Company.builder().id(vo.getSupplier().getId()).build())
                .itens(budgetResponseItemService.parse(vo.getItens()))
                .budgetRequest(new BudgetRequest(vo.getBudgetRequest().getKey()))
                .build();
    }

}
