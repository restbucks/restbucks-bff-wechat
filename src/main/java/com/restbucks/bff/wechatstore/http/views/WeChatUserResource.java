package com.restbucks.bff.wechatstore.http.views;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

@Data
@EqualsAndHashCode(callSuper = false)
public class WeChatUserResource extends ResourceSupport {
    private String nickname;
    private String avatar;
}
