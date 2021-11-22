package com.gs.pi4.api.api.controller;

import java.util.List;

import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.UnauthorizedActionException;
import com.gs.pi4.api.app.service.BudgetRequestService;
import com.gs.pi4.api.app.service.CompanyPartnerService;
import com.gs.pi4.api.app.service.CompanyService;
import com.gs.pi4.api.app.vo.budget.BudgetRequestVO;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "BudgetRequestEndpoint")
@RestController
@RequestMapping("/api/budget_request")
public class BudgetRequestController {

    @Autowired
    BudgetRequestService service;

    @Autowired
    CompanyService companyService;

    @Autowired
    CompanyPartnerService companyPartnerService;

    @ApiOperation(value = "Returns a list of my Budget Requests")
    @GetMapping(value = "my/company/{companyId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public List<BudgetRequestVO> getMyBudgetRequests(Authentication authentication,
            @PathVariable("companyId") Long companyId) {
        User user = (User) authentication.getPrincipal();

        if (!companyService.userHasCompany(user, companyId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        return service.findAllByCompany(companyId);
    }

    @ApiOperation(value = "Returns a list of Provider Budget Requests")
    @GetMapping(value = "provider/{companyId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public List<BudgetRequestVO> getProviderBudgetRequests(Authentication authentication,
            @PathVariable("companyId") Long companyId) {
        User user = (User) authentication.getPrincipal();

        if (!companyService.userHasCompany(user, companyId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        return service.findAllByCompanyToPartner(companyId);
    }

    @ApiOperation(value = "Returns a Provider Budget Request")
    @GetMapping(value = "provider/{companyId}/budget/{budgetId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public BudgetRequestVO getProviderBudgetRequest(Authentication authentication,
            @PathVariable("companyId") Long companyId, @PathVariable("budgetId") Long budgetId) {
        User user = (User) authentication.getPrincipal();

        if (!companyService.userHasCompany(user, companyId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        return service.findByIdWithCompanyToPartner(budgetId, companyId);
    }

    @ApiOperation(value = "Returns a Budget Request")
    @GetMapping(value = "my/company/{companyId}/budget/{budgetId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public BudgetRequestVO getMyBudgetRequest(Authentication authentication, @PathVariable("companyId") Long companyId,
            @PathVariable("budgetId") Long budgetId) {
        User user = (User) authentication.getPrincipal();

        if (!companyService.userHasCompany(user, companyId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        return service.findByBudgetWithCompany(companyId, budgetId);
    }

    @ApiOperation(value = "Returns a Budget Request")
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

    @Transactional
    @ApiOperation(value = "Create a budget")
    @PostMapping(value = "my", produces = { "application/json", "application/xml", "application/x-yaml" }, consumes = {
            "application/json", "application/xml", "application/x-yaml" })
    public BudgetRequestVO create(Authentication authentication, @RequestBody BudgetRequestVO vo) {
        User user = (User) authentication.getPrincipal();

        // TODO: Abstract this to a AuthorizationService
        if (!companyService.userHasCompany(user, vo.getBuyer().getId())) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        vo.getItens().stream().forEach(el -> {
            if (!companyPartnerService.isPartner(vo.getBuyer(), el.getProduct())) {
                throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString(),
                        "You don't have access on " + el.getProduct().getKey() + " product");
            }
        });

        return service.create(user, vo);
    }

}
