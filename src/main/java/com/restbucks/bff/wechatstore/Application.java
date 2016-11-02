package com.restbucks.bff.wechatstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings("squid:S1118")
public class Application {

    //see http://stackoverflow.com/questions/37071032/sonarqube-close-this-configurableapplicationcontext-in-spring-boot-project
    @SuppressWarnings("squid:S2095")
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
