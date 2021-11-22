package com.gs.pi4.api.api.controller;

import java.util.List;
import java.util.Objects;

import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.UnauthorizedActionException;
import com.gs.pi4.api.app.service.CompanyPartnerService;
import com.gs.pi4.api.app.service.CompanyService;
import com.gs.pi4.api.app.service.security.AuthorizationService;
import com.gs.pi4.api.app.vo.company.CompanyPartnerVO;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "CompanyPartnerEndpoint")
@RestController
@RequestMapping("/api/company_partner")
public class CompanyPartnerController {

    @Autowired
    CompanyPartnerService service;

    @Autowired
    CompanyService companyService;

    @Autowired
    AuthorizationService authorization;

    @ApiOperation(value = "Returns a list of CompanyPartner")
    @GetMapping(value = "company/{companyId}", produces = { "application/json", "application/xml", "application/x-yaml" })
    public List<CompanyPartnerVO> getPartners(Authentication authentication,
            @PathVariable("companyId") Long companyId) {
        authorization.userHasCompany(authentication, companyId);
        return service.getPartners(companyId);
    }

    @Transactional
    @ApiOperation(value = "Returns a list of Pending CompanyPartner")
    @GetMapping(value = "company/{companyId}/pending", produces = { "application/json", "application/xml", "application/x-yaml" })
    public List<CompanyPartnerVO> getPendingPartners(Authentication authentication,
            @PathVariable("companyId") Long companyId) {
        authorization.userHasCompany(authentication, companyId);
        return service.findAllPendingPartners(companyId);
    }

    @ApiOperation(value = "Returns a list of Pending CompanyPartner")
    @GetMapping(value = "company/{companyId}/pending_for_me", produces = { "application/json", "application/xml", "application/x-yaml" })
    public List<CompanyPartnerVO> getPendingPartnersForMe(Authentication authentication,
            @PathVariable("companyId") Long companyId) {
        authorization.userHasCompany(authentication, companyId);
        return service.findAllPendingRequestsForMe(companyId);
    }

    @Transactional
    @ApiOperation(value = "Returns a list of no CompanyPartner")
    @GetMapping(value = "company/{companyId}/excepts_my", produces = { "application/json", "application/xml", "application/x-yaml" })
    public List<CompanyPartnerVO> findAllExceptsMy(Authentication authentication,
            @PathVariable("companyId") Long companyId) {
        authorization.userHasCompany(authentication, companyId);
        return service.findAllExceptsCompanyId(companyId);
    }

    @Transactional
    @ApiOperation(value = "Send request to a company")
    @PostMapping(value = "company/{fromId}/to/{toId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public CompanyPartnerVO sendPartnerRequest(Authentication authentication, @PathVariable("fromId") Long fromId,
            @PathVariable("toId") Long toId) {
        authorization.userHasCompany(authentication, fromId);
        
        if ( Objects.equals(fromId, toId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }
                
        User user = authorization.getUser(authentication);
        return service.sendPartnerRequest(companyService.findById(fromId), companyService.findById(toId), user);
    }

    @Transactional
    @ApiOperation(value = "Delete request to a company")
    @DeleteMapping(value = "company/{fromId}/to/{toId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public void declinePartnerRequest(Authentication authentication, @PathVariable("fromId") Long fromId,
            @PathVariable("toId") Long toId) {
        authorization.userHasAnyCompany(authentication, fromId, toId);

        if (Objects.equals(fromId, toId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        service.declinePartnerRequest(fromId, toId);
    }

    @Transactional
    @ApiOperation(value = "Accept request to a company")
    @PostMapping(value = "company/{fromId}/to/{toId}/accept", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public void acceptPartnerRequest(Authentication authentication, @PathVariable("fromId") Long fromId,
            @PathVariable("toId") Long toId) {
        authorization.userHasCompany(authentication, fromId);

        if (Objects.equals(fromId, toId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        service.acceptPartnerRequest(fromId, toId);
    }

}
