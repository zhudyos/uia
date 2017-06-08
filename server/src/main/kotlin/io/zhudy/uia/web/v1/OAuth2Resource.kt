package io.zhudy.uia.web.v1

import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.dto.OAuthToken
import io.zhudy.uia.dto.PasswordAuthInfo
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.web.OAuth2Exception
import io.zhudy.uia.web.form
import io.zhudy.uia.web.json
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.publisher.onErrorMap

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

    fun token(req: ServerRequest) = req.form {
        val grantType = it.getFirst("grant_type")
        when (grantType) {
            "password" -> {
                handlePassword(it)
            }
            else -> {
                throw OAuth2Exception(error = "invalid_grant", description = "grant_type")
            }
        }
    }.flatMap {
        val bodyBuilder = ServerResponse.ok().header("Cache-Control", "no-store").header("Pragma", "no-cache")
        bodyBuilder.json(it)
    }.onErrorMap(BizCodeException::class, {
        when (it.bizCode) {
            BizCodes.C_1000, BizCodes.C_1001 -> OAuth2Exception(error = "invalid_client", status = 401, description = it.bizCode.msg)
            BizCodes.C_2000, BizCodes.C_2011 -> OAuth2Exception(error = "invalid_grant", status = 401, description = it.bizCode.msg)
            else -> it
        }
    })

    fun handlePassword(formData: MultiValueMap<String, String>): Mono<OAuthToken> {
        val clientId = formData.getFirst("client_id") ?: throw OAuth2Exception("invalid_request", 400, "client_id 不存在")
        val clientSecret = formData.getFirst("client_secret") ?: throw OAuth2Exception("invalid_request", 400, "client_secret 不存在")
        val username = formData.getFirst("username") ?: throw OAuth2Exception("invalid_request", 400, "username 不存在")
        val password = formData.getFirst("password") ?: throw OAuth2Exception("invalid_request", 400, "client_id 不存在")
        val scope = formData.getFirst("scope")

        return oauth2Service.authorizePassword(PasswordAuthInfo(
                clientId = clientId,
                clientSecret = clientSecret,
                username = username,
                password = password,
                scope = scope
        ))
    }
}
