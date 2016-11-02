package com.restbucks.bff.wechatstore.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wechatstore")
@ToString
@Getter
@Setter
public class ServerRuntime {
    private String publicBaseUri = "http://localhost:8080";

    public String getPublicUri(String relativePath) {
        return publicBaseUri + relativePath;
    }
}
