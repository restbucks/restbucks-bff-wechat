package com.restbucks.bff.wechatstore.wechat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@Setter
public class WeChatUser {

    @JsonSerialize(using = OpenIdSerializer.class)
    private OpenId openId;

    private String nickname;

    private String avatar;
}
