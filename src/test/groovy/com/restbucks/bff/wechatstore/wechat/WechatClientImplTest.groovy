package com.restbucks.bff.wechatstore.wechat

import com.github.dreamhead.moco.HttpServer
import com.github.dreamhead.moco.RequestHit
import com.restbucks.bff.wechatstore.time.Clock
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import java.time.ZonedDateTime
import java.util.concurrent.ThreadLocalRandom

import static com.github.dreamhead.moco.Moco.*
import static com.github.dreamhead.moco.MocoRequestHit.requestHit
import static com.github.dreamhead.moco.Runner.running

class WechatClientImplTest extends Specification {

    WeChatClientImpl subject = new WeChatClientImpl()
    WeChatRuntime weChatRuntime = new WeChatRuntime()
    int port = ThreadLocalRandom.current().nextInt(14000, 15000)
    RequestHit hit = requestHit()
    HttpServer server = httpServer(port, hit, log())
    RestTemplate restTemplate = new RestTemplate()
    Clock clock = Mock()

    def setup() {
        weChatRuntime.appId = "appId"
        weChatRuntime.appSecret = "appSecret"
        weChatRuntime.baseUrl = "http://localhost:" + port

        subject.weChatRuntime = weChatRuntime
        subject.restTemplate = restTemplate
        subject.clock = clock
    }

    def "should get wechat user access token by oauth code"() {

        given:
        def code = "codeProvidedByWeChatOauth"

        server.get(and(
                by(uri("/sns/oauth2/access_token")),
                eq(query("appid"), weChatRuntime.appId),
                eq(query("secret"), weChatRuntime.appSecret),
                eq(query("code"), code),
                eq(query("grant_type"), "authorization_code")
        )).response("""
            {
               "access_token":"ACCESS_TOKEN",
               "expires_in":7200,
               "refresh_token":"REFRESH_TOKEN",
               "openid":"OPENID",
               "scope":"SCOPE1,SCOPE2"
            }
        """)

        def now = ZonedDateTime.now()
        _ * clock.now() >> now

        running(server, { ->

            when:
            def accessToken = subject.exchangeAccessTokenWith(code)

            then:

            assert accessToken.accessToken == "ACCESS_TOKEN"
            assert accessToken.openId == OpenId.valueOf("OPENID")
            assert accessToken.refreshToken == "REFRESH_TOKEN"
            assert accessToken.expires == now.plusSeconds(7200)
            assert accessToken.scope == "SCOPE1,SCOPE2"
        })
    }

    def "should get wechat user profile by oauth access code"() {

        given:
        WeChatOauthAccessToken accessToken = new WeChatOauthAccessTokenFixture().build()

        server.get(and(
                by(uri("/sns/userinfo")),
                eq(query("access_token"), accessToken.accessToken),
                eq(query("openid"), accessToken.openId.value)
        )).response("""
            {
               "openid": "${accessToken.openId.value}",
               "nickname": "NICKNAME",
               "sex":"1",
               "province":"PROVINCE",
               "city":"CITY",
               "country":"COUNTRY",
               "headimgurl": "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46",
               "privilege": [
                    "PRIVILEGE1",
                    "PRIVILEGE2"
                ]
            }
        """)

        running(server, { ->

            when:
            def userProfile = subject.exchangeUserProfileWith(accessToken)

            then:

            assert userProfile.openId == accessToken.openId
            assert userProfile.nickname == "NICKNAME"
            assert userProfile.avatar == "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46"
        })
    }
}