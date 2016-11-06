package com.restbucks.bff.wechatstore.wechat;

import java.time.ZonedDateTime;
import java.util.UUID;

public class WeChatOauthAccessTokenFixture {
    private WeChatOauthAccessToken target = new WeChatOauthAccessToken();

    public WeChatOauthAccessTokenFixture() {
        target.setOpenId(OpenId.valueOf(UUID.randomUUID().toString()));
        target.setAccessToken("accessToken");
        target.setExpires(ZonedDateTime.now().plusSeconds(7200));
        target.setRefreshToken("refreshToken");
        target.setScope("scope");
    }

    public WeChatOauthAccessToken build() {
        return target;
    }
}
