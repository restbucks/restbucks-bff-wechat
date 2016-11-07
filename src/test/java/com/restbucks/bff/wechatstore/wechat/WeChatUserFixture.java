package com.restbucks.bff.wechatstore.wechat;

import java.util.UUID;

public class WeChatUserFixture {
    private WeChatUser target = new WeChatUser();

    public WeChatUserFixture() {
        target.setOpenId(OpenId.valueOf(UUID.randomUUID().toString()));
        target.setNickname("John Doe");
        target.setAvatar("https://avatar.com/johndoe");
    }

    public WeChatUserFixture with(OpenId openId) {
        target.setOpenId(openId);
        return this;
    }

    public WeChatUser build() {
        return target;
    }
}
