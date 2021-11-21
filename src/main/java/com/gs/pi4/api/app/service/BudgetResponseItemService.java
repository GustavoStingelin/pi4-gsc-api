package com.gs.pi4.api.app.service;

import java.util.List;
import java.util.stream.Collectors;

import com.gs.pi4.api.app.vo.budget.BudgetResponseItemVO;
import com.gs.pi4.api.core.budget.response.BudgetResponseItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class BudgetResponseItemService {

    @Autowired
    ProductService productService;

    @Autowired
    BudgetRequestItemService budgetRequestItemService;

    protected BudgetResponseItemVO parse2BudgetResponseItemVO(@NonNull BudgetResponseItem entity) {
        return BudgetResponseItemVO.builder()
            .key(entity.getId())
            .quantity(entity.getQuantity())
            .unitPrice(entity.getUnitPrice())
            .description(entity.getDescription())
            .product(productService.parse2ProductVO(entity.getProduct()))
            .reference(budgetRequestItemService.parse2BudgetRequestItemVO(entity.getReference()))
            .build();
    }

    protected List<BudgetResponseItemVO> parse2BudgetResponseItemVO(@NonNull List<BudgetResponseItem> entities) {
        return entities.stream().map(this::parse2BudgetResponseItemVO).collect(Collectors.toList());
    }

    protected BudgetResponseItem parse(@NonNull BudgetResponseItemVO vo) {
        return BudgetResponseItem.builder()
            .id(vo.getKey())
            .quantity(vo.getQuantity())
            .unitPrice(vo.getUnitPrice())
            .description(vo.getDescription())
            .product(productService.findEntityById(vo.getProduct().getKey()))
            .reference(budgetRequestItemService.findEntityById(vo.getReference().getKey()))
            .build();
    }

    protected List<BudgetResponseItem> parse(@NonNull List<BudgetResponseItemVO> itens) {
        return itens.stream().map(this::parse).collect(Collectors.toList());
    }

}
