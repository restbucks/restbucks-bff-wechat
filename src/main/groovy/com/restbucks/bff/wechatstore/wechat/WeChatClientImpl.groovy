package com.restbucks.bff.wechatstore.wechat

import com.restbucks.bff.wechatstore.time.Clock
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

import javax.annotation.Resource

@Component
class WeChatClientImpl implements WeChatClient {

    private static final Logger log = LoggerFactory.getLogger(WeChatClientImpl.class)

    @Resource(name = "wechat.RestTemplate")
    RestTemplate restTemplate

    @Resource
    WeChatRuntime weChatRuntime

    @Resource
    Clock clock

    @Override
    WeChatOauthAccessToken exchangeAccessTokenWith(String code) {

        def representation = restTemplate
                .getForObject("/sns/oauth2/access_token?appid={appId}&secret={appSecret}&code={code}&grant_type=authorization_code",
                String.class,
                weChatRuntime.getAppId(),
                weChatRuntime.getAppSecret(),
                code);

        log.debug("Got access token: {}", representation)

        JsonSlurper jsonSlurper = new JsonSlurper()

        def accessToken = jsonSlurper.parseText(representation)

        new WeChatOauthAccessToken(OpenId.valueOf(accessToken.openid),
                accessToken.access_token,
                accessToken.refresh_token,
                clock.now().plusSeconds(accessToken.expires_in),
                accessToken.scope)
    }

    @Override
    WeChatUser exchangeUserProfileWith(WeChatOauthAccessToken accessToken) {
        def representation = restTemplate
                .getForObject("/sns/userinfo?openid={openId}&access_token={accessToken}",
                String.class,
                accessToken.openId.value,
                accessToken.accessToken);

        log.debug("Got user profile: {}", representation)

        JsonSlurper jsonSlurper = new JsonSlurper()

        def userProfile = jsonSlurper.parseText(representation)

        WeChatUser user = new WeChatUser()
        user.setOpenId(OpenId.valueOf(userProfile.openid))
        user.setNickname(userProfile.nickname)
        user.setAvatar(userProfile.headimgurl)

        user
    }
}
