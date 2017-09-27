package io.zhudy.uia.web

import io.zhudy.uia.*
import io.zhudy.uia.dto.CodeAuthorizeInfo
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.web.v1.LoginResource
import io.zhudy.uia.web.v1.OAuth2Resource
import io.zhudy.uia.web.v1.SsoAuthentication
import io.zhudy.uia.web.v1.UserResource
import org.slf4j.MDC
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import spark.Filter
import spark.Request
import spark.Response
import spark.Route
import spark.Spark.*
import java.net.URLEncoder
import java.util.*

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Component
class Routers(
        val redisTemplate: StringRedisTemplate,
        val oauth2Service: OAuth2Service,
        val ssoAuthentication: SsoAuthentication,
        // =============================================================
        loginResource: LoginResource,
        val oauth2Resource: OAuth2Resource,
        val userResource: UserResource
) {

    init {
        port(Config.port)

        staticFileLocation("/templates")
        post("/login", loginResource::login)

        // X-Request-ID 设置
        before(Filter { request, response ->
            var requestId = request.headers("X-Request-ID")
            if (requestId.isNullOrEmpty()) {
                requestId = UUID.randomUUID().toString().replace("-", "")
            }
            MDC.put("requestId", "[$requestId] ")
            response.header("X-Request-ID", requestId)
        })

        path("/api/v1") {
            path("/oauth") {
                get("/authorize", validateOAuthCode(oauth2Resource::authorize))
                post("/token", ssoSecurity(oauth2Resource::token))
            }

            // 获取当前认证用户信息
            get("/user", oauthSecurity(userResource::loadCurUserInfo))
        }

        // exception handlers
        exceptions()
    }

    private fun oauthSecurity(next: (Request, Response) -> String): Route {
        return Route { request, response ->
            val accessToken = request.queryParams("access_token")
            if (accessToken.isNullOrEmpty()) {
                throw OAuth2Exception(error = "invalid_token", status = 401)
            }

            val token = oauth2Service.decodeToken(accessToken)
            if (token.expireTime < System.currentTimeMillis()) {
                throw OAuth2Exception(error = "token_expired", status = 401)
            }

            val t = redisTemplate.opsForValue()[RedisKeys.oauth2_token.key(token.uid, token.cid)]
            if (t.isNullOrEmpty()) {
                throw OAuth2Exception(error = "token_expired", status = 401)
            }

            // set user context
            request.attribute(WebConstants.REQUEST_USER_CONTEXT, object : UserContext {
                override val uid: Long = token.uid
            })
            next(request, response)
        }
    }

    private fun validateOAuthCode(next: (Request, Response) -> String): Route {
        return Route { request, _ ->
            val responseType = request.queryParams("response_type") ?: throw OAuth2Exception(error = "unsupported_response_type")

            val clientId = request.queryParams("client_id") ?: throw OAuth2Exception(error = "invalid_client")
            val redirectUri = request.queryParams("redirect_uri") ?: ""
            val scope = request.queryParams("scope") ?: ""

            when (responseType) {
                "code" -> {
                    val info = CodeAuthorizeInfo(clientId = clientId, redirectUri = redirectUri, scope = scope)

                    try {
                        val client = oauth2Service.authorizeCheck(info)
                        request.attribute(WebConstants.REQUEST_AUTH_CLIENT, client)
                        request.attribute(WebConstants.REQUEST_CODE_AUTHORIZE_INFO_KEY, info)
                    } catch (e: BizCodeException) {
                        when (e.bizCode) {
                            BizCodes.C_1000 -> throw OAuth2Exception(error = "invalid_client", status = 401, description = e.bizCode.msg)
                            else -> throw OAuth2Exception(error = "server_error", description = e.bizCode.msg)
                        }
                    } catch (e: Exception) {
                        // log.error("oauth2 authorize 校验失败", e)
                        throw OAuth2Exception(error = "server_error", description = e.message ?: "")
                    }
                }
                else -> {
                    throw OAuth2Exception(error = "unsupported_response_type")
                }
            }
        }
    }

    private fun ssoSecurity(next: (Request, Response) -> String): Route {
        return Route { request, response ->
            if (!ssoAuthentication.validate(request, response)) {
                var redirectUri = request.url()
                if (!request.queryString().isNullOrEmpty()) {
                    redirectUri = "$redirectUri?${request.queryString()}"
                }
                redirectUri = URLEncoder.encode(redirectUri, Charsets.UTF_8.name())

                response.redirect("${Config.loginHtmlUri}?redirect_uri=$redirectUri")
                halt(302)
            }
            next(request, response)
        }
    }
}

