package io.zhudy.uia.web.v1

import io.zhudy.uia.dto.PasswordAuthInfo
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.web.OAuth2Exception
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters
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

    object ErrorConstants {
        val invalid_request = "invalid_request"
        val invalid_client = "invalid_client"
        val invalid_grant = "invalid_grant"
        val unauthorized_client = "unauthorized_client"
        val access_denied = "access_denied"
        val unsupported_response_type = "unsupported_response_type"
        val invalid_scope = "invalid_scope"
        val server_error = "server_error"
        val temporarily_unavailable = "temporarily_unavailable"
    }

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
            val clientId = it.getFirst("client_id") ?: throw OAuth2Exception("invalid_request", 400, "client_id 不存在")
            val clientSecret = it.getFirst("client_secret") ?: throw OAuth2Exception("invalid_request", 400, "client_secret 不存在")
            val username = it.getFirst("username") ?: throw OAuth2Exception("invalid_request", 400, "username 不存在")
            val password = it.getFirst("password") ?: throw OAuth2Exception("invalid_request", 400, "client_id 不存在")
            val scope = it.getFirst("scope")

            oauth2Service.authorizePassword(PasswordAuthInfo(
                    clientId = clientId,
                    clientSecret = clientSecret,
                    username = username,
                    password = password,
                    scope = scope
            ))
        } else {
            throw OAuth2Exception(error = "invalid_grant", description = "grant_type")
        }
    }.flatMap {
        ServerResponse.ok().body(BodyInserters.fromObject(it))
    }

}
