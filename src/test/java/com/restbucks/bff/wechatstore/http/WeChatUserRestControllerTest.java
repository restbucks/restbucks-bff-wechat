package com.restbucks.bff.wechatstore.http;

import com.restbucks.bff.wechatstore.http.jwt.JwtIssuer;
import com.restbucks.bff.wechatstore.http.views.WeChatUserResourceAssembler;
import com.restbucks.bff.wechatstore.wechat.WeChatUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest({WeChatUserRestController.class, WeChatUserResourceAssembler.class})
public class WeChatUserRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private JwtIssuer jwtIssuer;

    @Test
    public void itShouldReturnUserProfileByApi() throws Exception {

        UserJwtFixture userJwtFixture = new UserJwtFixture();
        String userJwt = userJwtFixture.build();
        WeChatUser user = userJwtFixture.user();
        String csrfToken = userJwtFixture.csrfToken();

        when(jwtIssuer.verified(userJwt, csrfToken))
                .thenReturn(user);

        this.mvc.perform(get("/wechat/me")
                .cookie(new Cookie("wechatStoreUser", userJwt))
                .header("x-csrf-token", csrfToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("openId").doesNotExist()) // hide internal state
                .andExpect(jsonPath("nickname", is(user.getNickname())))
                .andExpect(jsonPath("avatar", is(user.getAvatar())))
                .andExpect(jsonPath("_links.self.href", is("http://localhost/wechat/me")));

    }
}