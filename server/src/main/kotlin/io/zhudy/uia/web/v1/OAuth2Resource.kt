package io.zhudy.uia.web.v1

import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.form.FormData
import io.undertow.util.Headers
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.dto.AuthorizationCodeAuthInfo
import io.zhudy.uia.dto.OAuthToken
import io.zhudy.uia.dto.PasswordAuthInfo
import io.zhudy.uia.dto.RefreshTokenAuthInfo
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

        try {
            val oauthToken: OAuthToken
            when (grantType) {
                "authorization_code" -> {
                    oauthToken = handleAuthorizationCode(formData)
                }
                "password" -> {
                    oauthToken = handlePassword(formData)
                }
                "refresh_token" -> {
                    oauthToken = handleRefreshToken(formData)
                }
                else -> {
                    throw OAuth2Exception(error = "unsupported_grant_type")
                }
            }

            exchange.responseHeaders.add(Headers.CACHE_CONTROL, "no-store").add(Headers.PRAGMA, "no-cache")
            exchange.sendJson(oauthToken)
        } catch (e: BizCodeException) {
            when (e.bizCode) {
                BizCodes.C_1000, BizCodes.C_1001 -> throw OAuth2Exception(error = "invalid_client", status = 401, description = e.bizCode.msg)
                BizCodes.C_2000, BizCodes.C_2011 -> throw OAuth2Exception(error = "invalid_grant", status = 401, description = e.bizCode.msg)
                else -> throw e
            }
        }
    }

    fun handleAuthorizationCode(formData: FormData): OAuthToken {
        val c = extractClient(formData)

        val redirectUri = formData.param("redirect_uri") ?: throw OAuth2Exception("invalid_request", "redirect_uri 不存在")
        val code = formData.param("code") ?: throw OAuth2Exception("code", "code 不存在")
        val scope = formData.param("scope") ?: ""

        return oauth2Service.authorizeCode(AuthorizationCodeAuthInfo(
                clientId = c.first,
                clientSecret = c.second,
                redirectUri = redirectUri,
                code = code,
                scope = scope
        ))
    }

    fun handlePassword(formData: FormData): OAuthToken {
        val c = extractClient(formData)

        val username = formData.param("username") ?: throw OAuth2Exception("invalid_request", "username 不存在")
        val password = formData.param("password") ?: throw OAuth2Exception("invalid_request", "client_id 不存在")
        val scope = formData.param("scope") ?: ""

        return oauth2Service.authorizePassword(PasswordAuthInfo(
                clientId = c.first,
                clientSecret = c.second,
                username = username,
                password = password,
                scope = scope
        ))
    }

    fun handleRefreshToken(formData: FormData): OAuthToken {
        val c = extractClient(formData)

        val refreshToken = formData.param("refresh_token") ?: throw OAuth2Exception("invalid_request", "refresh_token 不存在")
        val scope = formData.param("scope") ?: ""

        return oauth2Service.refreshToken(RefreshTokenAuthInfo(
                clientId = c.first,
                clientSecret = c.second,
                refreshToken = refreshToken,
                scope = scope
        ))
    }

    private fun extractClient(formData: FormData): Pair<String, String> {
        val clientId = formData.param("client_id") ?: throw OAuth2Exception("invalid_client", "client_id 不存在")
        val clientSecret = formData.param("client_secret") ?: throw OAuth2Exception("invalid_client", "client_secret 不存在")
        return Pair(clientId, clientSecret)
    }
}
