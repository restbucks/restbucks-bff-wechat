package com.restbucks.bff.wechatstore.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.restbucks.bff.wechatstore.time.Clock;
import com.restbucks.bff.wechatstore.wechat.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.util.Date;

import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(WeChatOauthController.class)
public class WeChatOauthControllerTest {

    @Autowired
    private MockMvc mvc;

    @SpyBean
    private WeChatRuntime weChatRuntime;

    @SpyBean
    private ServerRuntime serverRuntime;

    @SpyBean
    private JwtRuntime jwtRuntime;

    @SpyBean
    private ObjectMapper objectMapper;

    @MockBean
    private WeChatClient weChatClient;

    @MockBean
    private Clock clock;

    @Test
    public void itShouldRedirectToWeChatToFinishOauthProtocol() throws Exception {

        String origin = "http://www.example.com/index.html?a=b#/route";
        String encodedOrigin = URLEncoder.encode(origin, "UTF-8");

        this.mvc.perform(get("/wechat/browser")
                .param("origin", origin)) // it seems that the controller will decode the parameter automatically only for browser request
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect",
                                weChatRuntime.getAppId(),
                                "http%3A%2F%2Flocalhost%3A8080%2Fwechat%2Fbrowser%2Fuser",
                                encodedOrigin)));

    }

    @Test
    public void itShouldRedirectUserToOriginUri_whenWeChatOauthIsFinished() throws Exception {

        String code = "codeToExchangeWeChatUserAccessToken";
        String state = "http://www.example.com/index.html?a=b#/route";
        WeChatOauthAccessToken accessToken = new WeChatOauthAccessTokenFixture().build();
        WeChatUser user = new WeChatUserFixture().with(accessToken.getOpenId()).build();
        ZonedDateTime now = ZonedDateTime.now();

        when(weChatClient.exchangeAccessTokenWith(code))
                .thenReturn(accessToken);

        when(weChatClient.exchangeUserWith(accessToken))
                .thenReturn(user);

        when(clock.now()).thenReturn(now);

        MvcResult mvcResult = this.mvc.perform(get("/wechat/browser/user")
                .param("state", state) // it seems that the controller will decode the parameter automatically only for browser request
                .param("code", code))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(state))
                .andReturn();


        String userJwt = mvcResult.getResponse().getCookie("wechatStoreUser").getValue();

        Jws<Claims> claims = Jwts.parser().setSigningKey(jwtRuntime.getSigningKey()).parseClaimsJws(userJwt);


        String subject = claims.getBody().getSubject();
        assertThat(JsonPath.read(subject, "openId"), is(user.getOpenId().getValue()));
        assertThat(claims.getBody().getIssuedAt(),
                equalTo(Date.from(now.withNano(0).toInstant()))); //Jwts wll remove the nano seconds when generating the jwt
        assertThat(claims.getBody().getExpiration(),
                equalTo(Date.from(now.plusSeconds(jwtRuntime.getExpiresInSeconds()).withNano(0).toInstant())));

    }

}