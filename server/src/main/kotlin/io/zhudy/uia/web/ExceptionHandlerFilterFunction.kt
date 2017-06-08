package io.zhudy.uia.web

import io.zhudy.uia.BizCodes
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.publisher.onErrorResume

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
object ExceptionHandlerFilterFunction : HandlerFilterFunction<ServerResponse, ServerResponse> {

    val log = LoggerFactory.getLogger(ExceptionHandlerFilterFunction::class.java)

    override fun filter(request: ServerRequest, next: HandlerFunction<ServerResponse>): Mono<ServerResponse> {
        return next.handle(request).onErrorResume(RequestParamException::class.java, {
            ServerResponse.badRequest().json("""{"code": ${BizCodes.C_999.code}, "message": "${it.message}"}""")
        }).onErrorResume(OAuth2Exception::class, {
            if (it.status <= 0) {
                ServerResponse.badRequest()
            } else {
                ServerResponse.status(HttpStatus.valueOf(it.status))
            }.json(OAuthErrorRS(it.error, it.description, it.state))
        }).onErrorResume(Throwable::class, {
            log.error("", it)
            ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        })
    }

    /**
     * OAuth2 错误结果.
     */
    data class OAuthErrorRS(val error: String, val errorDescription: String, val state: String)
}

