package io.zhudy.uia.web.v1

import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.form.FormData
import io.undertow.util.Headers
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.dto.OAuthToken
import io.zhudy.uia.dto.PasswordAuthInfo
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.web.OAuth2Exception
import io.zhudy.uia.web.formData
import io.zhudy.uia.web.param
import io.zhudy.uia.web.sendJson
import org.springframework.stereotype.Controller

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class OAuth2Resource(
        val oauth2Service: OAuth2Service
) {

    fun authorize(exchange: HttpServerExchange) {
        exchange.sendJson(mapOf("a" to "a", "b" to "b"))
    }

    fun token(exchange: HttpServerExchange) {
        val formData = exchange.formData()
        val grantType = formData.param("grant_type")
        val oauthToken: OAuthToken
        when (grantType) {
            "password" -> {
                oauthToken = handlePassword(formData)
            }
            else -> {
                throw OAuth2Exception(error = "invalid_grant", description = "grant_type")
            }
        }
        exchange.responseHeaders.add(Headers.CACHE_CONTROL, "no-store").add(Headers.PRAGMA, "no-cache")
        exchange.sendJson(oauthToken)
    }

    fun handlePassword(formData: FormData): OAuthToken {
        val clientId = formData.param("client_id") ?: throw OAuth2Exception("invalid_request", "client_id 不存在")
        val clientSecret = formData.param("client_secret") ?: throw OAuth2Exception("invalid_request", "client_secret 不存在")
        val username = formData.param("username") ?: throw OAuth2Exception("invalid_request", "username 不存在")
        val password = formData.param("password") ?: throw OAuth2Exception("invalid_request", "client_id 不存在")
        val scope = formData.param("scope")

        try {
            return oauth2Service.authorizePassword(PasswordAuthInfo(
                    clientId = clientId,
                    clientSecret = clientSecret,
                    username = username,
                    password = password,
                    scope = scope
            ))
        } catch (e: BizCodeException) {
            when (e.bizCode) {
                BizCodes.C_1000, BizCodes.C_1001 -> throw OAuth2Exception(error = "invalid_client", status = 401, description = e.bizCode.msg)
                BizCodes.C_2000, BizCodes.C_2011 -> throw OAuth2Exception(error = "invalid_grant", status = 401, description = e.bizCode.msg)
                else -> throw e
            }
        }
    }
}
