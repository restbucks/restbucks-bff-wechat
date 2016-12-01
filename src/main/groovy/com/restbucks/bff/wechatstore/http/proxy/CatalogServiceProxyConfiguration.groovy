package com.restbucks.bff.wechatstore.http.proxy;

import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class CatalogServiceProxyConfiguration implements EnvironmentAware {

    private RelaxedPropertyResolver propertyResolver;

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new CatalogsProxyServlet(),
                propertyResolver.getProperty("mapping"));
        servletRegistrationBean.addInitParameter("targetUri",
                propertyResolver.getProperty("target"));
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG,
                propertyResolver.getProperty("logging", "false"));
        return servletRegistrationBean;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "proxy.catalogService.");
    }
}