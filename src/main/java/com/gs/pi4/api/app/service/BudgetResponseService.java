package com.gs.pi4.api.app.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.gs.pi4.api.api.exception.BusinessException;
import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.ResourceNotFoundException;
import com.gs.pi4.api.app.vo.budget.BudgetResponseVO;
import com.gs.pi4.api.core.budget.request.BudgetRequest;
import com.gs.pi4.api.core.budget.request.BudgetRequestItemRepository;
import com.gs.pi4.api.core.budget.response.BudgetResponse;
import com.gs.pi4.api.core.budget.response.BudgetResponseItem;
import com.gs.pi4.api.core.budget.response.BudgetResponseItemRepository;
import com.gs.pi4.api.core.budget.response.BudgetResponseRepository;
import com.gs.pi4.api.core.company.Company;
import com.gs.pi4.api.core.user.User;

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

    @Autowired
    BudgetRequestService budgetRequestService;

    public List<BudgetResponseVO> findAllByCompany(@NonNull Long companyId) {
        return parse2BudgetResponseVO(repository.findAllByCompany(companyId), companyId);
    }

    public List<BudgetResponseVO> findAllByCompanyToBuyer(@NonNull Long companyId) {
        return parse2BudgetResponseVO(repository.findAllByCompanyToBuyer(companyId));
    }

    public BudgetResponseVO findByIdWithCompanyToBuyer(@NonNull Long budgetId, @NonNull Long companyId) {
        return parse2BudgetResponseVO(repository.findByIdWithCompanyToBuyer(budgetId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString())));
    }
    
    public BudgetResponseVO findByBudgetWithCompany(Long budgetId, Long companyId) {
        return parse2BudgetResponseVO(repository.findByBudgetWithCompany(budgetId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString())), companyId);
    }

    private BudgetResponseVO parse2BudgetResponseVO(@NonNull BudgetResponse entity) {
        return BudgetResponseVO.builder()
                .key(entity.getId())
                .description(entity.getDescription())
                .expiresOn(entity.getExpiresOn())
                .supplier(companyPartnerService.parse2CompanyPartnerVO(entity.getCompany()))
                .itens(budgetResponseItemService.parse2BudgetResponseItemVO(entity.getItens()))
                .buyedAt(entity.getBuyedAt())
                .budgetRequest(budgetRequestService
                        .parse2BudgetRequestVO(budgetRequestService.findEntityById(entity.getBudgetRequest().getId())))
                .build();
    }

    private BudgetResponseVO parse2BudgetResponseVO(@NonNull BudgetResponse entity, Long supplierId) {
        return BudgetResponseVO.builder()
            .key(entity.getId())
            .description(entity.getDescription())
            .expiresOn(entity.getExpiresOn())
            .supplier(companyPartnerService.parse2CompanyPartnerVO(entity.getCompany()))
            .itens(budgetResponseItemService.parse2BudgetResponseItemVO(entity.getItens()))
            .buyedAt(entity.getBuyedAt())
            .budgetRequest(budgetRequestService.parse2BudgetRequestVO(entity.getBudgetRequest(), supplierId))
            .build();
    }

    protected List<BudgetResponseVO> parse2BudgetResponseVO(@NonNull List<BudgetResponse> entities) {
        return entities.stream().map(this::parse2BudgetResponseVO).collect(Collectors.toList());
    }

    private List<BudgetResponseVO> parse2BudgetResponseVO(@NonNull List<BudgetResponse> entities, Long supplierId) {
        return entities.stream().map(el -> parse2BudgetResponseVO(el, supplierId)).collect(Collectors.toList());
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

    @Transactional
    public BudgetResponseVO create(@NonNull User user, @NonNull BudgetResponseVO vo) {
        BudgetResponse entity = parse(vo);
        entity.setId(0L);
        entity.setCreatedAt(new Date());
        entity.setCreatedBy(user);

        List<BudgetResponseItem> itens = entity.getItens().stream().map(item -> {
            item.setId(0L);
            item.setCreatedAt(new Date());
            item.setCreatedBy(user);

            if (item.getProduct().getUnitMeasure().isOnlyInteger() || item.getQuantity() <= 0) {
                Float diff = item.getQuantity().intValue() - item.getQuantity();
                if (diff < 0) throw new BusinessException(CodeExceptionEnum.BUDGET_REQUEST_ITEM_QUANTITY_INVALID);
            }
            if (item.getUnitPrice() < 0) throw new BusinessException(CodeExceptionEnum.BUDGET_REQUEST_ITEM_PRICE_INVALID);

            return item;
        }).collect(Collectors.toList());

        entity.setItens(null);

        Long budgetId = repository.save(entity).getId();

        itens = budgetResponseItemRepository.saveAll(itens.stream().map(item -> {
            item.setBudgetResponse(BudgetResponse.builder().id(budgetId).build());
            return item;
        }).collect(Collectors.toList()));

        entity.setId(budgetId);
        entity.setItens(itens);

        return parse2BudgetResponseVO(entity);
    }


    public void deleteByIdWithCompany(Long companyId, Long budgetId, User user) {
        BudgetResponse entity = repository.findByBudgetWithCompany(budgetId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString()));

        if (entity.getBuyedAt() != null) {
            throw new BusinessException(CodeExceptionEnum.BUDGET_RESPONSE_IS_BUYED);
        }

        entity.setDeletedAt(new Date());
        entity.setDeletedBy(user);
        repository.save(entity);
    }

    public BudgetResponseVO acceptBudget(Long budgetId, Long companyId, User user) {
        BudgetResponse entity = repository.findByBudgetWithCompany(budgetId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString()));

        if (entity.getBuyedAt() != null) {
            throw new BusinessException(CodeExceptionEnum.BUDGET_RESPONSE_IS_BUYED);
        }

        entity.setBuyedAt(new Date());
        entity.setChangedAt(new Date());
        entity.setChangedBy(user);
        return parse2BudgetResponseVO(repository.save(entity));
    }

}
