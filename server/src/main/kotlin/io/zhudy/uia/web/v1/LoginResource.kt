package io.zhudy.uia.web.v1

import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.UiaProperties
import io.zhudy.uia.domain.User
import io.zhudy.uia.service.UserService
import io.zhudy.uia.web.RequestParamException
import io.zhudy.uia.web.formData
import io.zhudy.uia.web.param
import io.zhudy.uia.web.sendRedirect
import okhttp3.HttpUrl
import org.springframework.stereotype.Controller

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class LoginResource(
        val userService: UserService,
        val ssoAuthentication: SsoAuthentication
) {

    fun login(exchange: HttpServerExchange) {
        val formData = exchange.formData()
        val username = formData.param("username") ?: throw RequestParamException("username")
        val password = formData.param("password") ?: throw RequestParamException("password")

        // 登录模式 login mode
        val user: User
        try {
            user = userService.authenticate(username, password)
        } catch (e: BizCodeException) {
            val referer = exchange.requestHeaders.getFirst(Headers.REFERER) ?: UiaProperties.loginHtmlUri
            val location = HttpUrl.parse(referer)!!.newBuilder().addQueryParameter("err_code", "${e.bizCode.code}").toString()
            exchange.sendRedirect(location)
            return
        }

        ssoAuthentication.complete(exchange, user)
    }
}
