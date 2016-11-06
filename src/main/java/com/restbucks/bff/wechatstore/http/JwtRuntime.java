package com.restbucks.bff.wechatstore.http;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "signingKey")//to exclude credentials from logs
@EqualsAndHashCode
public class JwtRuntime {

    private String signingKey = "shouldBeARandomString";

    private int expiresInSeconds = 3600;
}
