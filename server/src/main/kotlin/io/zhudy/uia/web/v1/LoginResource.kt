package io.zhudy.uia.web.v1

import io.undertow.server.HttpServerExchange
import io.zhudy.uia.web.formData
import io.zhudy.uia.web.param
import io.zhudy.uia.web.sendJson
import org.springframework.stereotype.Controller

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class LoginResource() {

    fun login(exchange: HttpServerExchange) {
        val formData = exchange.formData()
        val username = formData.param("username")
        val password = formData.param("password")
        val requestId = formData.param("request_id")

        exchange.sendJson(mapOf("a" to "a", "b" to "b"))
    }
}
