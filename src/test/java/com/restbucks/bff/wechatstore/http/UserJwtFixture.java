package com.restbucks.bff.wechatstore.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbucks.bff.wechatstore.time.Clock;
import com.restbucks.bff.wechatstore.wechat.WeChatUser;
import com.restbucks.bff.wechatstore.wechat.WeChatUserFixture;

public class UserJwtFixture {
    private JwtIssuer issuer = new JwtIssuer();
    private ObjectMapper objectMapper = new ObjectMapper();
    private Clock clock = new Clock();
    private WeChatUserFixture user = new WeChatUserFixture();
    private String csrfToken = "csrfToken";

    public UserJwtFixture with(WeChatUserFixture userFixture) {
        this.user = userFixture;
        return this;
    }

    public String build() {
        issuer.setClock(clock);
        issuer.setObjectMapper(objectMapper);
        return issuer.buildUserJwt(user.build(), csrfToken);
    }

    public WeChatUser user() {
        return user.build();
    }

    public String csrfToken() {
        return csrfToken;
    }
}
