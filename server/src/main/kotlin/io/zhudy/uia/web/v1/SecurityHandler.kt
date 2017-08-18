package io.zhudy.uia.web.v1

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.zhudy.uia.UiaProperties
import io.zhudy.uia.web.sendRedirect
import kotlin.reflect.KFunction1

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class SecurityHandler(val ssoAuthentication: SsoAuthentication,
                      val next: KFunction1<@ParameterName(name = "exchange") HttpServerExchange, Unit>) : HttpHandler {

    override fun handleRequest(exchange: HttpServerExchange) {
        if (!ssoAuthentication.validate(exchange)) {
            exchange.sendRedirect("${UiaProperties.loginHtmlUri}?redirect_uri=${exchange.requestURL}")
            return
        }

        next(exchange)
    }
}