package com.gs.pi4.api.app.service.security;

import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.UnauthorizedActionException;
import com.gs.pi4.api.app.service.CompanyService;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    @Autowired
    CompanyService companyService;

    public User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    public void userHasCompany(Authentication authentication, Long companyId) throws UnauthorizedActionException {
        if (!companyService.userHasCompany(getUser(authentication), companyId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }
    }

    public void userHasAnyCompany(Authentication authentication, Long companyId, Long companyId2)
            throws UnauthorizedActionException {
        if (!companyService.userHasCompany(getUser(authentication), companyId)
                && !companyService.userHasCompany(getUser(authentication), companyId2)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }
    }

    public void userHasCompany(User user, Long companyId) throws UnauthorizedActionException {
        if (!companyService.userHasCompany(user, companyId)) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }
    }

}
