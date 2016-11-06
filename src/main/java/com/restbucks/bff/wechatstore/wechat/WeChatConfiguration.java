package com.restbucks.bff.wechatstore.wechat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WeChatConfiguration {


    private WeChatRuntime weChatRuntime;

    @Autowired
    public WeChatConfiguration(WeChatRuntime weChatRuntime) {
        this.weChatRuntime = weChatRuntime;
    }

    @Bean(name = "wechat.RestTemplate")
    protected RestTemplate restTemplate() {
        DefaultUriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();
        uriTemplateHandler.setBaseUrl(weChatRuntime.getBaseUrl());

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(uriTemplateHandler);
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

}
