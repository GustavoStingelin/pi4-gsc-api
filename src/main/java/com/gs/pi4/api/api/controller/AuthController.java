package com.gs.pi4.api.api.controller;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.gs.pi4.api.api.exception.BusinessException;
import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.security.jwt.JwtTokenProvider;
import com.gs.pi4.api.app.service.UserService;
import com.gs.pi4.api.app.vo.user.UserAccountCredentialsVO;
import com.gs.pi4.api.app.vo.user.UserRegistrationVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "AuthenticationEndpoint")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    UserService service;

    @Transactional
    //@CrossOrigin(origins = "*")
    @ApiOperation(value = "Creates a user and returns a token")
    @PostMapping(value = "/signup", produces = { "application/json", "application/xml",
            "application/x-yaml" }, consumes = { "application/json", "application/xml", "application/x-yaml" })
    public ResponseEntity<String> signup(@RequestBody UserRegistrationVO data, HttpServletResponse res) {

        Calendar requiredBirthday = Calendar.getInstance();
        requiredBirthday.setTime(new Date());
        requiredBirthday.add(Calendar.YEAR, -18);

        var email = data.getEmail();
        var password = data.getPassword();

        if (data.getBirthday().toInstant().isAfter(requiredBirthday.toInstant())) {
            throw new BusinessException(CodeExceptionEnum.NON_HANDLED_ERROR);
        } else if (password.length() < 8) {
            throw new BusinessException(CodeExceptionEnum.NON_HANDLED_ERROR);
        } else if (!email.matches("^.+@.+\\..+$")) {
            throw new BusinessException(CodeExceptionEnum.NON_HANDLED_ERROR);
        } else if (service.hasEmail(email)) {
            throw new BusinessException(CodeExceptionEnum.REGISTER_DUPLICATED_EMAIL);
        } else if (!(data.getGender() == 'M' || data.getGender() == 'F')) {
            throw new BusinessException(CodeExceptionEnum.NON_HANDLED_ERROR);
        }

        service.createUser(data);
        return signin(UserAccountCredentialsVO.builder().email(email).password(password).build(), res);
    }

    @CrossOrigin(origins = "*")
    @ApiOperation(value = "Authenticates a user and returns a token")
    @PostMapping(value = "/signin", produces = { "application/json", "application/xml",
            "application/x-yaml" }, consumes = { "application/json", "application/xml", "application/x-yaml" })
    public ResponseEntity<String> signin(@RequestBody UserAccountCredentialsVO data, HttpServletResponse res) {
        try {
            var email = data.getEmail();
            var password = data.getPassword();
            var user = service.findByEmail(email);
            var token = "";
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            if (user != null) {
                token = tokenProvider.createToken(email, user.getRoles());
            } else {
                throw new UsernameNotFoundException("Email " + email + " not found!");
            }

            Cookie cookie = new Cookie("Auth-Token", token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(7200);
        
            res.addCookie(cookie);

            return ResponseEntity.ok().build();
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email/password supplied!");
        }
    }
}
