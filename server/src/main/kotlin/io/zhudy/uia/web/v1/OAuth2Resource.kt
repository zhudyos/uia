package io.zhudy.uia.web.v1

import io.zhudy.uia.dto.PasswordAuthInfo
import io.zhudy.uia.service.OAuth2Service
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
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

    fun token(req: ServerRequest) = req.body(BodyExtractors.toFormData()).map {
        val grantType = it.getFirst("grant_type")
        if (grantType == "password") {
            val clientId = it.getFirst("client_id") ?: throw IllegalArgumentException("client_id 参数错误")
            val clientSecret = it.getFirst("client_secret")
            val username = it.getFirst("username")
            val password = it.getFirst("password")
            val scope = it.getFirst("scope")

            oauth2Service.authorizePassword(PasswordAuthInfo(
                    clientId = clientId,
                    clientSecret = clientSecret,
                    username = username,
                    password = password,
                    scope = scope
            ))
        }
    }.flatMap {
        ServerResponse.badRequest().syncBody("GGGGGGGGGGGGG")
    }

}
