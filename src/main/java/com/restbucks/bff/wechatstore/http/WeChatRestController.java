package com.restbucks.bff.wechatstore.http;

import com.restbucks.bff.wechatstore.http.jwt.JwtIssuer;
import com.restbucks.bff.wechatstore.wechat.WeChatUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@RestController
public class WeChatRestController {

    @Autowired
    private JwtIssuer jwtIssuer;

    @RequestMapping(value = "/wechat/me", method = GET)
    public Resource<WeChatUser> me(@CookieValue("wechatStoreUser") String userJwt,
                                   @RequestHeader("x-csrf-token") String csrfToken) {
        WeChatUser user = jwtIssuer.verified(userJwt, csrfToken);
        return new Resource<>(user,
                linkTo(methodOn(WeChatRestController.class).me(userJwt, csrfToken)).withSelfRel());
    }


}
