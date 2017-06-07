package io.zhudy.uia.web

import io.zhudy.uia.BizCodes
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
object ExceptionHandlerFilterFunction : HandlerFilterFunction<ServerResponse, ServerResponse> {

    override fun filter(request: ServerRequest, next: HandlerFunction<ServerResponse>): Mono<ServerResponse> {
        return next.handle(request).onErrorResume(RequestParamException::class.java, {
            ServerResponse.badRequest().body(BodyInserters.fromObject("""{"code": ${BizCodes.C_999.code}, "message": "${it.message}"}"""))
        }).onErrorResume(OAuth2Exception::class.java, {
            if (it.status <= 0) {
                ServerResponse.badRequest()
            } else {
                ServerResponse.status(HttpStatus.valueOf(it.status))
            }.body(BodyInserters.fromObject(OAuthErrorRS(it.error, it.description, it.state)))
        })
    }

    /**
     * OAuth2 错误结果.
     */
    data class OAuthErrorRS(val error: String, val errorDescription: String, val state: String)
}

