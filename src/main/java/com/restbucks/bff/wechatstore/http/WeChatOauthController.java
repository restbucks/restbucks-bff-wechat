package com.restbucks.bff.wechatstore.http;

import com.restbucks.bff.wechatstore.wechat.WeChatRuntime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@Controller
public class WeChatOauthController {

    @Autowired
    private ServerRuntime serverRuntime;

    @Autowired
    private WeChatRuntime weChatRuntime;

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

        URL raw = new URL(state);

        response.sendRedirect(raw.toString());
    }
}
