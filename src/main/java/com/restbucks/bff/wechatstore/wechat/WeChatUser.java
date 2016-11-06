package com.restbucks.bff.wechatstore.wechat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter(AccessLevel.PROTECTED)
public class WeChatUser {

    @JsonSerialize(using = OpenIdSerializer.class)
    private OpenId openId;
}
