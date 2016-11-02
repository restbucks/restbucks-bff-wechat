package com.restbucks.bff.wechatstore.http;

import com.restbucks.bff.wechatstore.wechat.WeChatRuntime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;

import static java.lang.String.format;
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

        this.mvc.perform(get("/wechat/browser/user")
                .param("state", state) // it seems that the controller will decode the parameter automatically only for browser request
                .param("code", code))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(state));
    }

}