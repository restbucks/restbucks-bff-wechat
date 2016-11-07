package com.restbucks.bff.wechatstore.http.views;

import com.restbucks.bff.wechatstore.http.WeChatUserRestController;
import com.restbucks.bff.wechatstore.wechat.WeChatUser;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class WeChatUserResourceAssembler extends ResourceAssemblerSupport<WeChatUser, WeChatUserResource> {


    public WeChatUserResourceAssembler() {
        super(WeChatUserRestController.class, WeChatUserResource.class);
    }

    @Override
    public WeChatUserResource toResource(WeChatUser entity) {
        WeChatUserResource representation = new ModelMapper().map(entity, WeChatUserResource.class);
        representation.add(linkTo(methodOn(WeChatUserRestController.class).me(null, null)).withSelfRel());
        return representation;
    }
}
