package com.restbucks.bff.wechatstore.time;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ConfigurationProperties("wechatstore.time")
@Component
@Getter
@Setter
@ToString
public class Clock {

    private String zoneId = "Asia/Shanghai";

    public ZonedDateTime now() {
        return ZonedDateTime.now(zoneId());
    }

    private ZoneId zoneId() {
        return ZoneId.of(zoneId);
    }
}
