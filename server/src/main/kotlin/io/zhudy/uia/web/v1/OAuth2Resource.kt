package io.zhudy.uia.web.v1

import io.undertow.server.HttpServerExchange
import io.zhudy.uia.domain.Client
import io.zhudy.uia.web.formData
import io.zhudy.uia.web.jsonData
import io.zhudy.uia.web.sendJson
import org.springframework.stereotype.Controller

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class OAuth2Resource {

    fun authorize(exchange: HttpServerExchange) {
        exchange.sendJson(mapOf("a" to "a", "b" to "b"))
    }

    fun token(exchange: HttpServerExchange) {
        val formData = exchange.formData()
        println(formData)

        exchange.sendJson(mapOf("a" to "a", "b" to "b"))
    }

    fun token2(exchange: HttpServerExchange) {
        val client = exchange.jsonData(Client::class)
        println(client)

        exchange.sendJson(mapOf("a" to "a", "b" to "b"))
    }

//    fun token(req: ServerRequest) = req.form {
//        val grantType = it.getFirst("grant_type")
//        when (grantType) {
//            "password" -> {
//                handlePassword(it)
//            }
//            else -> {
//                throw OAuth2Exception(error = "invalid_grant", description = "grant_type")
//            }
//        }
//    }.flatMap {
//        val bodyBuilder = ServerResponse.ok().header("Cache-Control", "no-store").header("Pragma", "no-cache")
//        bodyBuilder.json(it)
//    }.onErrorMap(BizCodeException::class, {
//        when (it.bizCode) {
//            BizCodes.C_1000, BizCodes.C_1001 -> OAuth2Exception(error = "invalid_client", status = 401, description = it.bizCode.msg)
//            BizCodes.C_2000, BizCodes.C_2011 -> OAuth2Exception(error = "invalid_grant", status = 401, description = it.bizCode.msg)
//            else -> it
//        }
//    })
//
//    fun handlePassword(formData: MultiValueMap<String, String>): Mono<OAuthToken> {
//        val clientId = formData.getFirst("client_id") ?: throw OAuth2Exception("invalid_request", 400, "client_id 不存在")
//        val clientSecret = formData.getFirst("client_secret") ?: throw OAuth2Exception("invalid_request", 400, "client_secret 不存在")
//        val username = formData.getFirst("username") ?: throw OAuth2Exception("invalid_request", 400, "username 不存在")
//        val password = formData.getFirst("password") ?: throw OAuth2Exception("invalid_request", 400, "client_id 不存在")
//        val scope = formData.getFirst("scope")
//
//        return oauth2Service.authorizePassword(PasswordAuthInfo(
//                clientId = clientId,
//                clientSecret = clientSecret,
//                username = username,
//                password = password,
//                scope = scope
//        ))
//    }
}
