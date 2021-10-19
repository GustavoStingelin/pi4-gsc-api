package com.gs.pi4.api.api.controller;

import com.gs.pi4.api.api.security.jwt.JwtTokenProvider;
import com.gs.pi4.api.app.service.UserService;
import com.gs.pi4.api.app.vo.user.UserBasicVO;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "UserEndpoint")
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    UserService service;

    @Transactional
    @ApiOperation(value = "Returns a simple User")
    @GetMapping(produces = { "application/json", "application/xml", "application/x-yaml" })
    public UserBasicVO getMyBasicProfile(Authentication authentication) {
        return service.parseUser2UserBasicVO((User) authentication.getPrincipal());
    }

}
