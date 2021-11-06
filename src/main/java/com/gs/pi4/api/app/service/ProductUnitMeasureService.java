package com.gs.pi4.api.app.service;

import java.util.List;
import java.util.stream.Collectors;

import com.gs.pi4.api.app.vo.product.UnitMeasureVO;
import com.gs.pi4.api.core.product.ProductUnitMeasure;
import com.gs.pi4.api.core.product.ProductUnitMeasureRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductUnitMeasureService {

    @Autowired
    ProductUnitMeasureRepository repository;

    public ProductUnitMeasure findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<UnitMeasureVO> findAll() {
        return parse2UnitMeasureVO(repository.findAll());
    }    

    public List<UnitMeasureVO> parse2UnitMeasureVO(List<ProductUnitMeasure> entities) {
        return entities.stream().map(this::parse2UnitMeasureVO).collect(Collectors.toList());
    }

    public UnitMeasureVO parse2UnitMeasureVO(ProductUnitMeasure entity) {
        return UnitMeasureVO.builder()
            .key(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .onlyInteger(entity.getOnlyInteger())
            .build();
    }
}


