package com.gs.pi4.api.api.controller;

import java.util.List;

import javax.transaction.Transactional;

import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.UnauthorizedActionException;
import com.gs.pi4.api.app.service.BudgetResponseService;
import com.gs.pi4.api.app.service.CompanyPartnerService;
import com.gs.pi4.api.app.service.CompanyService;
import com.gs.pi4.api.app.service.ProductService;
import com.gs.pi4.api.app.vo.budget.BudgetResponseVO;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    ProductService productService;

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


    @Transactional
    @ApiOperation(value = "Create a budget")
    @PostMapping(value = "my", produces = { "application/json", "application/xml", "application/x-yaml" }, consumes = {
            "application/json", "application/xml", "application/x-yaml" })
    public BudgetResponseVO create(Authentication authentication, @RequestBody BudgetResponseVO vo) {
        User user = (User) authentication.getPrincipal();

        if (!companyService.userHasCompany(user, vo.getSupplier().getId())) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        vo.getItens().stream().forEach(el -> {
            if (!productService.isCompanyOfProductId(el.getProduct().getKey(), vo.getSupplier().getId())) {
                throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString(),
                        "You don't have access on " + el.getProduct().getKey() + " product");
            }
        });

        return service.create(user, vo);
    }

    @ApiOperation(value = "Delete a Budget Response")
    @DeleteMapping(value = "my/company/{companyId}/budget/{budgetId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public void deleteMyBudgetRequest(Authentication authentication, @PathVariable("companyId") Long companyId,
            @PathVariable("budgetId") Long budgetId) {
        User user = (User) authentication.getPrincipal();

        if (!companyService.userHasCompany(user, companyId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        service.deleteByIdWithCompany(companyId, budgetId, user);
    }

    @ApiOperation(value = "Accepet a Budget Response")
    @PatchMapping(value = "my/company/{companyId}/budget/{budgetId}/buy", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public void buyBudgetRequest(Authentication authentication, @PathVariable("companyId") Long companyId,
            @PathVariable("budgetId") Long budgetId) {
        User user = (User) authentication.getPrincipal();

        if (!companyService.userHasCompany(user, companyId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        service.acceptBudget(companyId, budgetId, user);
    }

}
