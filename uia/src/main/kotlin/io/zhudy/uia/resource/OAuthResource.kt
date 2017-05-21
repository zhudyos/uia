package io.zhudy.uia.resource

import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono


/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@Controller
class OAuthResource {

    fun token(req: ServerRequest):Mono<ServerResponse> {
        return ServerResponse.ok().syncBody(mapOf("a" to "a"))
    }

}
