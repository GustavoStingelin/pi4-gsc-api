package com.gs.pi4.api.api.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(value = "Brand Endpoint", tags = { "BrandEndpoint" })
@RestController
@RequestMapping("/api/test")
public class Test {


    @ApiOperation(value = "Test")
    @GetMapping(value = "/a")
    public ResponseEntity<Map<Object, Object>> a(Authentication authentication) {

        Map<Object, Object> model = new HashMap<>();
        model.put("Hello", "World!");
        return ok(model);
    }

    @ApiOperation(value = "Test")
    @PostMapping(value = "/a")
    public ResponseEntity<Map<Object, Object>> aPost(Authentication authentication) {

        Map<Object, Object> model = new HashMap<>();
        model.put("Hello", "Postado!");
        return ok(model);
    }


}