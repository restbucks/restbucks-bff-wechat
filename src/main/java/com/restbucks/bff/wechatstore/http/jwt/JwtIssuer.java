package com.restbucks.bff.wechatstore.http.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbucks.bff.wechatstore.time.Clock;
import com.restbucks.bff.wechatstore.wechat.WeChatUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static java.lang.String.format;

@Component
@ConfigurationProperties("wechatstore.jwt")
@Getter
@Setter
@ToString(exclude = "signingKey")//to exclude credentials from logs
@EqualsAndHashCode
public class JwtIssuer {

    @Autowired
    private Clock clock;

    @Autowired
    private ObjectMapper objectMapper;

    private String signingKey = "shouldBeARandomString";

    private int expiresInSeconds = 3600;


    public String buildUserJwt(WeChatUser weChatUser, String csrfToken) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("csrfToken", csrfToken);
        return buildUserJwt(weChatUser, claims);
    }

    public String buildUserJwt(WeChatUser weChatUser, Map<String, Object> claims) {
        try {
            ZonedDateTime now = clock.now();
            JwtBuilder builder = Jwts.builder()
                    .setSubject(objectMapper.writeValueAsString(weChatUser))
                    .setIssuer("Restbucks")
                    .setIssuedAt(Date.from(now.toInstant()))
                    .setExpiration(Date.from(now.plusSeconds(getExpiresInSeconds()).toInstant()))
                    .signWith(HS512, getSigningKey());
            claims.entrySet().forEach(claim -> builder.claim(claim.getKey(), claim.getValue()));
            return builder.compact();
        } catch (Exception e) {
            throw new CannotIssueJwtException(format("Cannot issue jwt with %s and %s due to %s",
                    weChatUser, claims, e.getMessage()), e);
        }
    }

    public WeChatUser verified(String userJwt, String csrfToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(userJwt);
            String expectCsrfToken = (String) claims.getBody().get("csrfToken");
            if (expectCsrfToken.equals(csrfToken)) {
                return objectMapper.readValue(claims.getBody().getSubject(), WeChatUser.class);
            } else {
                throw new CannotVerifyJwtException(format("Cannot verify csrf token, expect [%s], got [%s]", expectCsrfToken, csrfToken));// should I return expect and actual?
            }
            //OK, we can trust this JWT
        } catch (Exception e) {
            throw new CannotVerifyJwtException(format("Cannot verify jwt token due to %s", e.getMessage()), e);
        }
    }
}
