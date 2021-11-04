package com.gs.pi4.api.api.controller;

import java.util.List;

import com.gs.pi4.api.app.service.ProductUnitMeasureService;
import com.gs.pi4.api.app.vo.product.UnitMeasureVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "ProductUnitMeasureEndpoint")
@RestController
@RequestMapping("/api/product/unit_measure")
public class ProductUnitMeasureController {

    @Autowired
    ProductUnitMeasureService service;

    @Transactional
    @ApiOperation(value = "Returns all unit measures")
    @GetMapping(produces = { "application/json", "application/xml", "application/x-yaml" })
    public List<UnitMeasureVO> getAll(Authentication authentication) {
        return service.findAll();
    }

}