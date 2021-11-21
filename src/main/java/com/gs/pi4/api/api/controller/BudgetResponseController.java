package com.gs.pi4.api.api.controller;

import java.util.List;

import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.UnauthorizedActionException;
import com.gs.pi4.api.app.service.BudgetResponseService;
import com.gs.pi4.api.app.service.CompanyPartnerService;
import com.gs.pi4.api.app.service.CompanyService;
import com.gs.pi4.api.app.vo.budget.BudgetResponseVO;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "BudgetResponseEndpoint")
@RestController
@RequestMapping("/api/budget_response")
public class BudgetResponseController {

    @Autowired
    BudgetResponseService service;

    @Autowired
    CompanyService companyService;

    @Autowired
    CompanyPartnerService companyPartnerService;

    @ApiOperation(value = "Returns a list of my Budget Response")
    @GetMapping(value = "my/company/{companyId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public List<BudgetResponseVO> getMyBudgetResponse(Authentication authentication,
            @PathVariable("companyId") Long companyId) {
        User user = (User) authentication.getPrincipal();

        if (!companyService.userHasCompany(user, companyId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        return service.findAllByCompany(companyId);
    }

    
    @ApiOperation(value = "Returns a Budget Response")
    @GetMapping(value = "my/company/{companyId}/budget/{budgetId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public BudgetResponseVO getMyBudgetResponse(Authentication authentication, @PathVariable("companyId") Long companyId,
            @PathVariable("budgetId") Long budgetId) {
        User user = (User) authentication.getPrincipal();

        if (!companyService.userHasCompany(user, companyId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        return service.findByBudgetWithCompany(companyId, budgetId);
    }

}
