package com.restbucks.bff.wechatstore.wechat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("wechat")
@Setter
@Getter
@ToString(exclude = "appSecret")//to avoid print secret in logs
public class WeChatRuntime {

    private String appId = "wechatAppId";
    private String appSecret = "wechatAppSecret";
    private String baseUrl = "https://api.wechat.com";
}
