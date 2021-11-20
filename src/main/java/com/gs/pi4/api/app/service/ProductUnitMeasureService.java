package com.gs.pi4.api.app.service;

import java.util.List;
import java.util.stream.Collectors;

import com.gs.pi4.api.app.vo.product.UnitMeasureVO;
import com.gs.pi4.api.core.product.ProductUnitMeasure;
import com.gs.pi4.api.core.product.ProductUnitMeasureRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class ProductUnitMeasureService {

    @Autowired
    ProductUnitMeasureRepository repository;

    public ProductUnitMeasure findById(@NonNull Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<UnitMeasureVO> findAll() {
        return parse2UnitMeasureVO(repository.findAll());
    }    

    protected List<UnitMeasureVO> parse2UnitMeasureVO(@NonNull List<ProductUnitMeasure> entities) {
        return entities.stream().map(this::parse2UnitMeasureVO).collect(Collectors.toList());
    }

    protected UnitMeasureVO parse2UnitMeasureVO(@NonNull ProductUnitMeasure entity) {
        return UnitMeasureVO.builder()
            .key(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .onlyInteger(entity.isOnlyInteger())
            .build();
    }
}


