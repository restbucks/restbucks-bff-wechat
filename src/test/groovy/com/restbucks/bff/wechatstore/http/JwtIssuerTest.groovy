package com.restbucks.bff.wechatstore.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.restbucks.bff.wechatstore.time.Clock
import com.restbucks.bff.wechatstore.wechat.WeChatUser
import com.restbucks.bff.wechatstore.wechat.WeChatUserFixture
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import spock.lang.Specification

import java.time.ZonedDateTime

class JwtIssuerTest extends Specification {

    JwtIssuer subject = new JwtIssuer()
    ObjectMapper objectMapper = new ObjectMapper()
    Clock clock = Mock()

    def setup() {
        subject.clock = clock
        subject.objectMapper = objectMapper
    }

    def "it generate jwt with wechat user profile and csrf token"() {

        given:
        WeChatUser user = new WeChatUserFixture().build()
        ZonedDateTime now = ZonedDateTime.now()
        String csrfToken = "csrfToken"

        clock.now() >> now

        when:
        def userJwt = subject.buildUserJwt(user, csrfToken)

        then:
        Jws<Claims> claims = Jwts.parser().setSigningKey(subject.getSigningKey())
                .parseClaimsJws(userJwt)

        String subject = claims.getBody().getSubject();
        assert JsonPath.read(subject, "openId") == user.getOpenId().getValue()
        assert claims.getBody().getIssuedAt() == Date.from(now.withNano(0).toInstant()) //Jwts wll remove the nano seconds when generating the jwt
        assert claims.getBody().getExpiration() == Date.from(now.plusSeconds(this.subject.getExpiresInSeconds()).withNano(0).toInstant())
        assert claims.getBody().get("csrfToken") == csrfToken

    }
}