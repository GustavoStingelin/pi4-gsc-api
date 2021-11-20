package com.gs.pi4.api.app.service;

import java.util.List;
import java.util.stream.Collectors;

import com.gs.pi4.api.app.vo.budget.BudgetRequestItemVO;
import com.gs.pi4.api.core.budget.request.BudgetRequestItem;
import com.gs.pi4.api.core.product.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class BudgetRequestItemService {

    @Autowired
    ProductService productService;

    protected BudgetRequestItemVO parse2BudgetRequestItemVO(@NonNull BudgetRequestItem entity) {
        return BudgetRequestItemVO.builder()
            .key(entity.getId())
            .quantity(entity.getQuantity())
            .description(entity.getDescription())
            .product(productService.parse2ProductVO(entity.getProduct()))
            .build();
    }

    protected List<BudgetRequestItemVO> parse2BudgetRequestItemVO(@NonNull List<BudgetRequestItem> entities) {
        return entities.stream().map(this::parse2BudgetRequestItemVO).collect(Collectors.toList());
    }

    protected BudgetRequestItem parse(@NonNull BudgetRequestItemVO vo) {
        return BudgetRequestItem.builder()
            .id(vo.getKey())
            .quantity(vo.getQuantity())
            .description(vo.getDescription())
            .product(Product.builder().id(vo.getProduct().getKey()).build())
            .build();
    }

    protected List<BudgetRequestItem> parse(@NonNull List<BudgetRequestItemVO> itens) {
        return itens.stream().map(this::parse).collect(Collectors.toList());
    }

}
