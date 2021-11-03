package com.gs.pi4.api.app.service;

import com.gs.pi4.api.app.vo.product.UnitMeasureVO;
import com.gs.pi4.api.core.product.ProductUnitMeasure;

import org.springframework.stereotype.Service;

@Service
public class ProductUnitMeasureService {

    public UnitMeasureVO parse2UnitMeasureVO(ProductUnitMeasure entity) {
        return UnitMeasureVO.builder()
            .key(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .onlyInteger(entity.getOnlyInteger())
            .build();
    }
}


