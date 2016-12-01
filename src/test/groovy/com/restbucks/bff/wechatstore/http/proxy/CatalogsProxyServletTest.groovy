package com.restbucks.bff.wechatstore.http.proxy

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification

class CatalogsProxyServletTest extends Specification {

    def subject = new CatalogsProxyServlet()

    def "it should proxy catalogs api and customize the links"() {

        given: ""

        MockHttpServletRequest servletRequest = new MockHttpServletRequest()
        MockHttpServletResponse servletResponse = new MockHttpServletResponse()

        MockHttpServletRequest proxyRequest = new MockHttpServletRequest()
        MockHttpServletResponse proxyReponse = new MockHttpServletResponse()


        when: ""

        //subject.copyResponseEntity(proxyReponse, servletResponse, proxyRequest, servletResponse)

        then: ""

//        assert """
//              {
//                "_embedded": {
//                  "coffee": [
//                    {
//                      "attributes but we don't care": "omitted",
//                      "_links": {
//                        "links but we don't care": { "href": "http://catalogs.restbucks.com/rel/coffe/latte-medium" },
//                        "addToCart": {
//                            "href": "http://wechatstore.restbucks.com/rel/cart/me/items"
//                        }
//                      }
//                    }
//                  ]
//                }
//              }
//               """, servletResponse.getContentAsString()
    }
}