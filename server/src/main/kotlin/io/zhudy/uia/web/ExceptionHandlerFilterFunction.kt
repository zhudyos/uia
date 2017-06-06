package io.zhudy.uia.web

import io.zhudy.uia.BizCodes
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
        return next.handle(request)
                .onErrorResume(RequestParamException::class.java, {
                    ServerResponse.badRequest().body(BodyInserters.fromObject("""{"code": ${BizCodes.C_999.code}, "message": "${it.message}"}"""))
                })
    }
}