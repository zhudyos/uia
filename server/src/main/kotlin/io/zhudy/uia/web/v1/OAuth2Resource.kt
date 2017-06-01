package io.zhudy.uia.web.v1

import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.dto.PasswordAuthInfo
import io.zhudy.uia.service.OAuth2Service
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class OAuth2Resource(
        val oauth2Service: OAuth2Service
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
        val grantType = req.queryParam("grant_type").orElseThrow { BizCodeException(BizCodes.C_0) }
        if (grantType == "password") {
            oauth2Service.authorizePassword(PasswordAuthInfo(
                    clientId = req.queryParam("client_id").get(),
                    clientSecret = req.queryParam("client_secret").get(),
                    username = req.queryParam("username").get(),
                    password = req.queryParam("password").get(),
                    scope = req.queryParam("scope").get()
            ))
        }
        return Mono.empty()
    }

}
