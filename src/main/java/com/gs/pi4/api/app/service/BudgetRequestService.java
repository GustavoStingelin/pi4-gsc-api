package com.gs.pi4.api.app.service;

import java.util.List;

import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.ResourceNotFoundException;
import com.gs.pi4.api.app.vo.budget.BudgetRequestVO;
import com.gs.pi4.api.app.vo.product.ProductVO;
import com.gs.pi4.api.core.budget.request.BudgetRequest;
import com.gs.pi4.api.core.budget.request.BudgetRequestRepository;
import com.gs.pi4.api.core.company.Company;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class BudgetRequestService {

    @Autowired
    BudgetRequestRepository repository;

    @Autowired
    CompanyPartnerService companyPartnerService;

    @Autowired
    BudgetRequestItemService budgetRequestItemService;

    public List<BudgetRequestVO> findAllByCompany(@NonNull Long companyId) {
        return parse2BudgetRequestVO(repository.findAllByCompany(companyId));
    }

    public BudgetRequestVO findByBudgetWithCompany(@NonNull Long companyId, @NonNull Long budgetId) {
        return parse2BudgetRequestVO(repository.findByBudgetWithCompany(companyId, budgetId)
                .orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString())));
    }

    private BudgetRequestVO parse2BudgetRequestVO(@NonNull BudgetRequest entity) {
        return BudgetRequestVO.builder().key(entity.getId()).description(entity.getDescription())
                .expiresOn(entity.getExpiresOn())
                .buyer(companyPartnerService.parse2CompanyPartnerVO(entity.getCompany()))
                .itens(budgetRequestItemService.parse2BudgetRequestItemVO(entity.getItens())).build();
    }
    
    private List<BudgetRequestVO> parse2BudgetRequestVO(@NonNull List<BudgetRequest> entities) {
        return entities.stream().map(this::parse2BudgetRequestVO).collect(java.util.stream.Collectors.toList());

    }
    private BudgetRequest parse(@NonNull BudgetRequestVO vo) {
        return BudgetRequest.builder()
            .id(vo.getKey())
            .description(vo.getDescription())
            .expiresOn(vo.getExpiresOn())
            .company(Company.builder().id(vo.getBuyer().getId()).build())
            .itens(budgetRequestItemService.parse(vo.getItens()))
            .build();
    }

    public ProductVO create(@NonNull User user, @NonNull BudgetRequestVO vo) {
        return null;
    }

}
