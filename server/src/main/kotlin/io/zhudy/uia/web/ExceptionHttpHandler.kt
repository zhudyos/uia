package io.zhudy.uia.web

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.zhudy.uia.BizCodes

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class ExceptionHttpHandler(val next: HttpHandler) : HttpHandler {

    val SNAKE_CASE = PropertyNamingStrategy.SNAKE_CASE as PropertyNamingStrategy.PropertyNamingStrategyBase

    override fun handleRequest(exchange: HttpServerExchange) {
        try {
            next.handleRequest(exchange)
        } catch (e: MissingKotlinParameterException) {
            exchange.statusCode = 400
            exchange.sendJson(mapOf("err_code" to BizCodes.C_999.code, "err_message" to "缺少参数 [${SNAKE_CASE.translate(e.parameter.name)}]"))
        } catch (e: ResponseStatusException) {
            exchange.statusCode = e.status
            if (e.reason != null) exchange.reasonPhrase = e.reason
        } catch (e: RequestParamException) {
            exchange.statusCode = 400
            exchange.sendJson(mapOf("err_code" to BizCodes.C_999.code, "err_message" to e.message))
        } catch (e: OAuth2Exception) {
            exchange.statusCode = e.status
            exchange.sendJson(OAuthErrorRS(e.error, e.description, e.state))
        }
    }

    /**
     * OAuth2 错误结果.
     */
    data class OAuthErrorRS(val error: String, val errorDescription: String, val state: String)
}