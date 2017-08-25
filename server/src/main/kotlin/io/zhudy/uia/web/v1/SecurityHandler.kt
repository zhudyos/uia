package io.zhudy.uia.web.v1

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.zhudy.uia.UiaProperties
import io.zhudy.uia.web.sendRedirect
import java.net.URLEncoder
import kotlin.reflect.KFunction1

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class SecurityHandler(private val ssoAuthentication: SsoAuthentication,
                      private val next: KFunction1<@ParameterName(name = "exchange") HttpServerExchange, Unit>) : HttpHandler {

    override fun handleRequest(exchange: HttpServerExchange) {
        if (!ssoAuthentication.validate(exchange)) {
            var redirectUri = exchange.requestURL
            if (exchange.queryString.isNotEmpty()) {
                redirectUri = "${exchange.requestURL}?${exchange.queryString}"
            }
            redirectUri = URLEncoder.encode(redirectUri, Charsets.UTF_8.name())

            exchange.sendRedirect("${UiaProperties.loginHtmlUri}?redirect_uri=$redirectUri")
            return
        }

        next(exchange)
    }
}