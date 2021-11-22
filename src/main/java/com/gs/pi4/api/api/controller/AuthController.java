package com.gs.pi4.api.api.controller;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.gs.pi4.api.api.exception.BusinessException;
import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.security.jwt.JwtTokenProvider;
import com.gs.pi4.api.app.service.UserService;
import com.gs.pi4.api.app.vo.user.UserAccountCredentialsVO;
import com.gs.pi4.api.app.vo.user.UserBasicVO;
import com.gs.pi4.api.app.vo.user.UserRegistrationVO;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Value("${security.jwt.token.expire-lenght:86400000}") // 24h
    private long validityInMilliseconds;

    @Transactional
    @ApiOperation(value = "Creates a user and returns a token")
    @PostMapping(value = "/signup", produces = { "application/json", "application/xml",
            "application/x-yaml" }, consumes = { "application/json", "application/xml", "application/x-yaml" })
    public UserBasicVO signup(@RequestBody UserRegistrationVO data, HttpServletResponse res) {

        Calendar requiredBirthday = Calendar.getInstance();
        requiredBirthday.setTime(new Date());
        requiredBirthday.add(Calendar.YEAR, -2);

        var email = data.getEmail();
        var password = data.getPassword();

        if (data.getBirthday().toInstant().isAfter(requiredBirthday.toInstant())) {
            throw new BusinessException(CodeExceptionEnum.AUTH_REGISTER_INVALID_BIRTHDAY);
        } else if (password.length() < 8) {
            throw new BusinessException(CodeExceptionEnum.AUTH_REGISTER_PASSWORD_SMALL);
        } else if (!email.matches("^.+@.+\\..+$")) {
            throw new BusinessException(CodeExceptionEnum.AUTH_REGISTER_INVALID_EMAIL);
        } else if (service.hasEmail(email)) {
            throw new BusinessException(CodeExceptionEnum.AUTH_REGISTER_DUPLICATED_EMAIL);
        } else if (!(data.getGender() == 'M' || data.getGender() == 'F')) {
            throw new BusinessException(CodeExceptionEnum.AUTH_REGISTER_INVALID_GENDER);
        }

        service.createUser(data);
        return signin(UserAccountCredentialsVO.builder().email(email).password(password).build(), res);
    }


    @ApiOperation(value = "Authenticates a user and returns a token")
    @PostMapping(value = "/signin", produces = { "application/json", "application/xml",
            "application/x-yaml" }, consumes = { "application/json", "application/xml", "application/x-yaml" })
    public UserBasicVO signin(@RequestBody UserAccountCredentialsVO data, HttpServletResponse res) {
        try {
            String token;
            String email = data.getEmail();
            String password = data.getPassword();
            User user = service.findByEmail(email);
            
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            if (user != null) {
                token = tokenProvider.createToken(email, user.getRoles());
            } else {
                throw new UsernameNotFoundException(CodeExceptionEnum.AUTH_SIGNIN_USER_NOT_FOUND.toString());
            }

            ResponseCookie cookie = ResponseCookie.from("Auth-Token", "Bearer-" + token)
                .httpOnly(true)
                .maxAge(validityInMilliseconds - 3600000)
                .sameSite("None")
                .secure(true)
                .path("/")
                .build();
                
            res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
            return service.parseUser2UserBasicVO(user);

        } catch (AuthenticationException e) {
            throw new BadCredentialsException(CodeExceptionEnum.AUTH_BAD_CREDENTIALS.toString());
        }
    }

    @ApiOperation(value = "Remove the token from cookie")
    @GetMapping(value = "/logout")
    public void logout(HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from("Auth-Token", "Removed...")
            .httpOnly(true)
            .maxAge(1)
            .sameSite("None")
            .secure(true)
            .path("/")
            .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
