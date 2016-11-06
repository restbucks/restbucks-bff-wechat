package com.restbucks.bff.wechatstore.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbucks.bff.wechatstore.time.Clock;
import com.restbucks.bff.wechatstore.wechat.WeChatClient;
import com.restbucks.bff.wechatstore.wechat.WeChatOauthAccessToken;
import com.restbucks.bff.wechatstore.wechat.WeChatRuntime;
import com.restbucks.bff.wechatstore.wechat.WeChatUser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@Controller
public class WeChatOauthController {

    @Autowired
    private ServerRuntime serverRuntime;

    @Autowired
    private WeChatRuntime weChatRuntime;

    @Autowired
    private JwtRuntime jwtRuntime;

    @Autowired
    private Clock clock;

    @Autowired
    private WeChatClient weChatClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/wechat/browser", method = GET)
    public void askWeChatWhoTheUserIs(@RequestParam(name = "origin") String origin,
                                      HttpServletResponse response) throws IOException {

        final String endpointUrl = serverRuntime.getPublicUri("/wechat/browser/user");

        String redirect = format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                weChatRuntime.getAppId(),
                URLEncoder.encode(endpointUrl, "UTF-8"),
                "snsapi_base",
                URLEncoder.encode(origin, "UTF-8"));

        log.debug("We don't know who u are, redirecting you from {} to {}", origin, redirect);
        response.sendRedirect(redirect);
    }

    @RequestMapping(value = "/wechat/browser/user", method = GET)
    public void onWeChatTellingWhoTheUserIs(@RequestParam("code") final String code,
                                            @RequestParam("state") final String state,
                                            HttpServletResponse response) throws IOException {

        WeChatOauthAccessToken accessToken = weChatClient.exchangeAccessTokenWith(code);
        WeChatUser weChatUser = weChatClient.exchangeUserWith(accessToken);
        ZonedDateTime now = clock.now();

        String userJwt = Jwts.builder()
                .setSubject(objectMapper.writeValueAsString(weChatUser))
                .setIssuer("Restbucks")
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(now.plusSeconds(jwtRuntime.getExpiresInSeconds()).toInstant()))
                .signWith(HS512, jwtRuntime.getSigningKey())
                .compact();

        URL raw = new URL(state);
        response.addCookie(newServerCookie("wechatStoreUser", userJwt, jwtRuntime.getExpiresInSeconds()));
        response.addCookie(newClientCookie("wechatStoreUserIdentified", "true", jwtRuntime.getExpiresInSeconds()));
        response.sendRedirect(raw.toString());
    }

    private Cookie newServerCookie(String key, String value, int expiresInSeconds) {
        return newCookie(key, value, true, expiresInSeconds);
    }

    private Cookie newClientCookie(String key, String value, int expiresInSeconds) {
        return newCookie(key, value, false, expiresInSeconds);
    }

    private Cookie newCookie(String key, String value, boolean httpOnly, int expiresInSeconds) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(httpOnly); // against XSS attack
        cookie.setPath("/"); // spike on what is this
        cookie.setMaxAge(expiresInSeconds);
        return cookie;
    }


}
