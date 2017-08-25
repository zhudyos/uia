package io.zhudy.uia.web.v1

import io.undertow.server.HttpServerExchange
import io.zhudy.uia.UserContext
import io.zhudy.uia.service.UserService
import io.zhudy.uia.web.queryParam
import io.zhudy.uia.web.sendJson
import org.springframework.stereotype.Controller

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class UserResource(
        val userService: UserService
) {

    /**
     *
     */
    fun loadCurUserInfo(exchange: HttpServerExchange) {
        val fields = exchange.queryParam("fields") ?: ""
        val user = userService.findByUid(UserContext.uid)
        exchange.sendJson(fields, user)
    }
}