package com.restbucks.bff.wechatstore.wechat;

import lombok.*;

import java.time.ZonedDateTime;

@ToString
@EqualsAndHashCode
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeChatOauthAccessToken {

    private OpenId openId;
    private String accessToken;
    private String refreshToken;
    private ZonedDateTime expires;
    private String scope;

    public WeChatOauthAccessToken(OpenId openId, String accessToken, String refreshToken, ZonedDateTime expires, String scope) {
        this.openId = openId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expires = expires;
        this.scope = scope;
    }
}
