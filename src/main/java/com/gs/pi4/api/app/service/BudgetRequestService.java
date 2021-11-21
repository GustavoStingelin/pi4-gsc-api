package com.gs.pi4.api.app.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.gs.pi4.api.api.exception.BusinessException;
import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.ResourceNotFoundException;
import com.gs.pi4.api.app.vo.budget.BudgetRequestVO;
import com.gs.pi4.api.core.budget.request.BudgetRequest;
import com.gs.pi4.api.core.budget.request.BudgetRequestItem;
import com.gs.pi4.api.core.budget.request.BudgetRequestItemRepository;
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

    @Autowired
    BudgetRequestItemRepository budgetRequestItemRepository;

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
        return entities.stream().map(this::parse2BudgetRequestVO).collect(Collectors.toList());

    }

    private BudgetRequest parse(@NonNull BudgetRequestVO vo) {
        return BudgetRequest.builder().id(vo.getKey()).description(vo.getDescription()).expiresOn(vo.getExpiresOn())
                .company(Company.builder().id(vo.getBuyer().getId()).build())
                .itens(budgetRequestItemService.parse(vo.getItens())).build();
    }

    @Transactional
    public BudgetRequestVO create(@NonNull User user, @NonNull BudgetRequestVO vo) {
        BudgetRequest entity = parse(vo);
        entity.setId(0L);
        entity.setCreatedAt(new Date());
        entity.setCreatedBy(user);
        List<BudgetRequestItem> itens = entity.getItens().stream().map(item -> {
            item.setId(0L);
            item.setCreatedAt(new Date());
            item.setCreatedBy(user);

            if (item.getProduct().getUnitMeasure().isOnlyInteger()) {
                Float diff = item.getQuantity().intValue() - item.getQuantity();
                if (diff < 0) {
                    throw new BusinessException(CodeExceptionEnum.BUDGET_REQUEST_ITEM_QUANTITY_INVALID);
                }
            }

            return item;
        }).collect(Collectors.toList());

        entity.setItens(null);

        Long budgetId = repository.save(entity).getId();

        itens = budgetRequestItemRepository.saveAll(itens.stream().map(item -> {
            item.setBudgetRequest(BudgetRequest.builder().id(budgetId).build());
            return item;
        }).collect(Collectors.toList()));

        entity.setId(budgetId);
        entity.setItens(itens);

        return parse2BudgetRequestVO(entity);
    }

    public void deleteByIdWithCompany(Long companyId, Long budgetId) {
        BudgetRequest entity = repository.findByBudgetWithCompany(companyId, budgetId)
                .orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString()));

        if (!entity.getResponses().isEmpty()) {
            throw new BusinessException(CodeExceptionEnum.BUDGET_REQUEST_HAS_RESPONSES);
        }

        repository.delete(entity);
    }

}
