package io.zhudy.uia.web

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.undertow.Handlers.*
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.ExceptionHandler
import io.undertow.server.handlers.resource.ClassPathResourceManager
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.web.v1.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import kotlin.reflect.KFunction1

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@DependsOn("uiaProperties")
@Configuration
class HttpHandlers(
        val loginResource: LoginResource,
        val oauth2Resource: OAuth2Resource,
        val weixinResource: WeixinResource,
        val ssoAuthentication: SsoAuthentication
) {

    val SNAKE_CASE = PropertyNamingStrategy.SNAKE_CASE as PropertyNamingStrategy.PropertyNamingStrategyBase

    @Bean
    fun router(): HttpHandler {
        val a = routing()
        a.post("/login", loginResource::login)
        a.get("/weixin/callback", weixinResource::handle)

        a.get("/oauth/authorize", security(oauth2Resource::authorize))
        a.post("/oauth/token", oauth2Resource::token)

        // =============================================================================================================
        val dh = resource(ClassPathResourceManager(HttpHandlers::class.java.classLoader, "templates"))
        val root = path(dh).addPrefixPath("/api/v1", a)
        return gracefulShutdown(errorHandler(root))
    }

    private fun security(next: KFunction1<HttpServerExchange, Unit>): HttpHandler {
        return SecurityHandler(ssoAuthentication, next)
    }

    /**
     * 错误处理.
     */
    private fun errorHandler(next: HttpHandler): HttpHandler {
        val h = exceptionHandler(next)
        h.addExceptionHandler(MissingKotlinParameterException::class.java, {
            val e = it.getAttachment(ExceptionHandler.THROWABLE) as MissingKotlinParameterException
            it.statusCode = 400
            it.sendJson(mapOf("err_code" to BizCodes.C_999.code, "err_message" to "缺少参数 [${SNAKE_CASE.translate(e.parameter.name)}]"))
        })

        h.addExceptionHandler(ResponseStatusException::class.java, {
            val e = it.getAttachment(ExceptionHandler.THROWABLE) as ResponseStatusException
            it.statusCode = e.status
            if (e.reason != null) it.reasonPhrase = e.reason
        })

        h.addExceptionHandler(RequestParamException::class.java, {
            val e = it.getAttachment(ExceptionHandler.THROWABLE) as RequestParamException
            it.statusCode = 400
            it.sendJson(mapOf("err_code" to BizCodes.C_999.code, "err_message" to e.message))
        })

        h.addExceptionHandler(OAuth2Exception::class.java, {
            val e = it.getAttachment(ExceptionHandler.THROWABLE) as OAuth2Exception
            it.statusCode = e.status
            it.sendJson(OAuthErrorRS(e.error, e.description, e.state))
        })

        h.addExceptionHandler(BizCodeException::class.java, {
            val e = it.getAttachment(ExceptionHandler.THROWABLE) as BizCodeException
            it.statusCode = 400
            it.sendJson(mapOf("err_code" to e.bizCode.code, "err_message" to e.bizCode.msg))
        })

        // =============================================================================================================
        h.addExceptionHandler(Throwable::class.java, {
            it.statusCode = 500
            it.sendJson(mapOf("err_code" to BizCodes.C_500, "err_message" to BizCodes.C_500.msg))
        })
        return h
    }

    /**
     * OAuth2 错误结果.
     */
    data class OAuthErrorRS(val error: String, val errorDescription: String, val state: String)
}