package com.restbucks.bff.wechatstore.http;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CsrfTokenGenerator {
    public String generate() {
        byte[] r = new byte[256]; //Means 2048 bit
        new SecureRandom().nextBytes(r);
        return Base64.encodeBase64String(r);
    }
}
