package com.restbucks.bff.wechatstore.http.proxy

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import org.apache.http.HttpEntity
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.mitre.dsmiley.httpproxy.ProxyServlet

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CatalogsProxyServlet extends ProxyServlet {

    @Override
    protected void copyResponseEntity(HttpResponse proxyResponse, HttpServletResponse servletResponse,
                                      HttpRequest proxyRequest, HttpServletRequest servletRequest)
            throws IOException {
        HttpEntity entity = proxyResponse.getEntity()

        JsonSlurper jsonSlurper = new JsonSlurper()

        def representation = jsonSlurper.parse(entity.getContent())

        representation._embedded.coffee.forEach { item ->
            item._links.put("addToCart", "abc")
        }

        new ObjectMapper().writeValue(servletResponse.getOutputStream(), representation)
    }
}
