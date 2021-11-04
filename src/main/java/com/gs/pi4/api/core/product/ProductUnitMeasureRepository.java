package com.gs.pi4.api.core.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductUnitMeasureRepository extends JpaRepository<ProductUnitMeasure, Long> {

}
