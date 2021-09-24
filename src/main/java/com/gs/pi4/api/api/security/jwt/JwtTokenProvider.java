package com.gs.pi4.api.api.security.jwt;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.gs.pi4.api.api.exception.InvalidJwtAuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey;

    @Value("${security.jwt.token.expire-lenght:86400000}") // 24h
    private long validityInMilliseconds;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostConstruct
    public void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String email, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        var cookies = req.getCookies();
        if (cookies != null) {
            String bearerToken = Arrays.stream(cookies).filter(c -> c.getName().equals("Auth-Token")).findFirst()
                    .map(Cookie::getValue).orElse(null);
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7, bearerToken.length());
            }
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return (claims.getBody().getExpiration().after(new Date())); // true or false

        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or Invalid token");
        }
    }
}
