package com.restbucks.bff.wechatstore.wechat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
public class OpenId {

    private String value;

    private OpenId(String value) {
        this.value = value;
    }

    public static OpenId valueOf(String value) {
        return new OpenId(value);
    }
}
