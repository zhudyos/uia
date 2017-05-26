package io.zhudy.uia.web.v1

import io.zhudy.uia.service.OAuth2Service
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class OAuth2Resource(
        oauth2Service: OAuth2Service
) {

    fun authorize(req: ServerRequest): Mono<ServerResponse> {
//        val clientId = req.queryParam("client_id")
//        val redirectUri = req.queryParam("redirect_uri")
        req.body({ inputMessage, _ ->
            println(inputMessage.cookies)
        })
        return ServerResponse.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "http://www.baidu.com").build()
    }

    fun token(req: ServerRequest): Mono<ServerResponse> {
        return Mono.empty()
    }

}
