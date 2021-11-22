package com.gs.pi4.api.api.controller;

import java.util.List;

import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.UnauthorizedActionException;
import com.gs.pi4.api.app.service.BudgetRequestService;
import com.gs.pi4.api.app.service.CompanyPartnerService;
import com.gs.pi4.api.app.service.CompanyService;
import com.gs.pi4.api.app.service.security.AuthorizationService;
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

    @Autowired
    AuthorizationService authorization;

    @ApiOperation(value = "Returns a list of my Budget Requests")
    @GetMapping(value = "my/company/{companyId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public List<BudgetRequestVO> getMyBudgetRequests(Authentication authentication,
            @PathVariable("companyId") Long companyId) {
        authorization.userHasCompany(authentication, companyId);
        return service.findAllByCompany(companyId);
    }

    @ApiOperation(value = "Returns a list of Provider Budget Requests")
    @GetMapping(value = "provider/{companyId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public List<BudgetRequestVO> getProviderBudgetRequests(Authentication authentication,
            @PathVariable("companyId") Long companyId) {
        authorization.userHasCompany(authentication, companyId);
        return service.findAllByCompanyToPartner(companyId);
    }

    @ApiOperation(value = "Returns a Provider Budget Request")
    @GetMapping(value = "provider/{companyId}/budget/{budgetId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public BudgetRequestVO getProviderBudgetRequest(Authentication authentication,
            @PathVariable("companyId") Long companyId, @PathVariable("budgetId") Long budgetId) {
        authorization.userHasCompany(authentication, companyId);
        return service.findByIdWithCompanyToPartner(budgetId, companyId);
    }

    @ApiOperation(value = "Returns a Budget Request")
    @GetMapping(value = "my/company/{companyId}/budget/{budgetId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public BudgetRequestVO getMyBudgetRequest(Authentication authentication, @PathVariable("companyId") Long companyId,
            @PathVariable("budgetId") Long budgetId) {
        authorization.userHasCompany(authentication, companyId);
        return service.findByBudgetWithCompany(companyId, budgetId);
    }

    @ApiOperation(value = "Returns a Budget Request")
    @DeleteMapping(value = "my/company/{companyId}/budget/{budgetId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public void deleteMyBudgetRequest(Authentication authentication, @PathVariable("companyId") Long companyId,
            @PathVariable("budgetId") Long budgetId) {
        authorization.userHasCompany(authentication, companyId);

        User user = authorization.getUser(authentication);
        service.deleteByIdWithCompany(companyId, budgetId, user);
    }

    @Transactional
    @ApiOperation(value = "Create a budget")
    @PostMapping(value = "my", produces = { "application/json", "application/xml", "application/x-yaml" }, consumes = {
            "application/json", "application/xml", "application/x-yaml" })
    public BudgetRequestVO create(Authentication authentication, @RequestBody BudgetRequestVO vo) {
        authorization.userHasCompany(authentication, vo.getBuyer().getId());

        vo.getItens().stream().forEach(el -> {
            if (!companyPartnerService.isPartner(vo.getBuyer(), el.getProduct())) {
                throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString(),
                        "You don't have access on " + el.getProduct().getKey() + " product");
            }
        });

        User user = authorization.getUser(authentication);
        return service.create(user, vo);
    }

}
