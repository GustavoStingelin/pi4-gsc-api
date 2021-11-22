package com.gs.pi4.api.api.controller;

import java.util.List;

import com.gs.pi4.api.app.service.CompanyService;
import com.gs.pi4.api.app.service.security.AuthorizationService;
import com.gs.pi4.api.app.vo.company.CompanyVO;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "CompanyEndpoint")
@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    CompanyService service;

    @Autowired
    AuthorizationService authorization;

    @Transactional
    @ApiOperation(value = "Returns a list of my companies")
    @GetMapping(value = "my", produces = { "application/json", "application/xml", "application/x-yaml" })
    public List<CompanyVO> getMyCompanies(Authentication authentication) {
        User user = authorization.getUser(authentication);
        return service.findAllByUserId(user.getId());
    }

    @Transactional
    @ApiOperation(value = "Create a company")
    @PostMapping(value = "my", produces = { "application/json", "application/xml", "application/x-yaml" }, consumes = {
            "application/json", "application/xml", "application/x-yaml" })
    public CompanyVO createMyCompany(Authentication authentication, @RequestBody CompanyVO vo) {
        User user = authorization.getUser(authentication);
        vo.setKey(null);
        vo.setLogo(1L);
        return service.createCompany(vo, user);
    }

}
