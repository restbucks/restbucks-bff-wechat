package com.restbucks.bff.wechatstore.http;

import com.restbucks.bff.wechatstore.http.csrf.CsrfTokenGenerator;
import com.restbucks.bff.wechatstore.http.jwt.JwtIssuer;
import com.restbucks.bff.wechatstore.time.Clock;
import com.restbucks.bff.wechatstore.wechat.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.time.ZonedDateTime;

import static java.lang.String.format;
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

    @MockBean
    private JwtIssuer jwtIssuer;

    @MockBean
    private WeChatClient weChatClient;

    @MockBean
    private Clock clock;

    @MockBean
    private CsrfTokenGenerator csrfTokenGenerator;


    @Test
    public void itShouldRedirectToWeChatToFinishOauthProtocol() throws Exception {

        String origin = "http://www.example.com/index.html?a=b#/route";
        String encodedOrigin = URLEncoder.encode(origin, "UTF-8");

        this.mvc.perform(get("/wechat/browser")
                .param("origin", origin)) // it seems that the controller will decode the parameter automatically only for browser request
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=%s#wechat_redirect",
                                weChatRuntime.getAppId(),
                                "http%3A%2F%2Flocalhost%3A8080%2Fwechat%2Fbrowser%2Fuser",
                                encodedOrigin)));

    }

    @Test
    public void itShouldRedirectUserToOriginUri_whenWeChatOauthIsFinished() throws Exception {

        String code = "codeToExchangeWeChatUserAccessToken";
        String state = "http://www.example.com/index.html?a=b#/route";
        WeChatOauthAccessToken accessToken = new WeChatOauthAccessTokenFixture().build();
        UserJwtFixture userJwtFixture = new UserJwtFixture();
        String userJwt = userJwtFixture.with(new WeChatUserFixture().with(accessToken.getOpenId())).build();
        WeChatUser user = userJwtFixture.user();
        String csrfToken = userJwtFixture.csrfToken();
        ZonedDateTime now = ZonedDateTime.now();

        when(weChatClient.exchangeAccessTokenWith(code))
                .thenReturn(accessToken);

        when(weChatClient.exchangeUserProfileWith(accessToken))
                .thenReturn(user);

        when(clock.now()).thenReturn(now);

        when(csrfTokenGenerator.generate())
                .thenReturn(csrfToken);

        when(jwtIssuer.buildUserJwt(user, csrfToken))
                .thenReturn(userJwt);

        MvcResult mvcResult = this.mvc.perform(get("/wechat/browser/user")
                .param("state", state) // it seems that the controller will decode the parameter automatically only for browser request
                .param("code", code))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(state))
                .andReturn();


        Cookie userCookie = mvcResult.getResponse().getCookie("wechatStoreUser");
        Cookie userIdentifiedCookie = mvcResult.getResponse().getCookie("wechatStoreUserIdentified");
        Cookie csrfTokenCookie = mvcResult.getResponse().getCookie("wechatStoreCsrfToken");

        // verify userCookie
        assertThat(userCookie.getValue(), is(userJwt));
        assertThat(userCookie.isHttpOnly(), is(true));
        assertThat(userCookie.getMaxAge(), is(jwtIssuer.getExpiresInSeconds()));

        // verify userIdentifiedCookie
        assertThat(userIdentifiedCookie.getValue(), is("true"));
        assertThat(userIdentifiedCookie.isHttpOnly(), is(false));
        assertThat(userIdentifiedCookie.getMaxAge(), is(jwtIssuer.getExpiresInSeconds()));

        // verify csrfTokenCookie
        assertThat(csrfTokenCookie.getValue(), is(csrfToken));
        assertThat(csrfTokenCookie.isHttpOnly(), is(false));
        assertThat(csrfTokenCookie.getMaxAge(), is(jwtIssuer.getExpiresInSeconds()));
    }
}