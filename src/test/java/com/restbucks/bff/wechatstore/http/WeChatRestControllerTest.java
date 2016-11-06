package com.restbucks.bff.wechatstore.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbucks.bff.wechatstore.time.Clock;
import com.restbucks.bff.wechatstore.wechat.WeChatUser;
import com.restbucks.bff.wechatstore.wechat.WeChatUserFixture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.HashMap;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(WeChatRestController.class)
public class WeChatRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private JwtIssuer jwtIssuer;

    @Test
    public void itShouldReturnUserProfileByApi() throws Exception {

        WeChatUser user = new WeChatUserFixture().build();
        String csrfToken = "aCsrfToken";
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("csrfToken", csrfToken);

        JwtIssuer theJwtIssuer = new JwtIssuer();
        theJwtIssuer.setObjectMapper(new ObjectMapper());
        theJwtIssuer.setClock(new Clock());
        String userJwt = theJwtIssuer.buildUserJwt(user, claims);

        when(jwtIssuer.verified(userJwt, csrfToken))
                .thenReturn(user);

        this.mvc.perform(get("/wechat/me")
                .cookie(new Cookie("wechatStoreUser", userJwt))
                .header("x-csrf-token", csrfToken))
                .andDo(print())
                .andExpect(status().isOk())
                //.andExpect(jsonPath("openId").doesNotExist()) TODO make is invisible
                .andExpect(jsonPath("nickname", is(user.getNickname())));

    }
}