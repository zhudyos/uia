package io.zhudy.uia.web.v1

import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class LoginResource(
) {

    fun login(req: ServerRequest): Mono<ServerResponse> {
        return Mono.empty()
    }
}
