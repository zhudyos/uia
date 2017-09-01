package io.zhudy.uia.web.v1

import io.zhudy.uia.*
import io.zhudy.uia.domain.Client
import io.zhudy.uia.dto.*
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.web.Constants
import io.zhudy.uia.web.OAuth2Exception
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Controller
import spark.Request
import spark.Response
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class OAuth2Resource(
        val oauth2Service: OAuth2Service,
        @Qualifier("stringRedisTemplate")
        val redisTemplate: StringRedisTemplate
) {

    private val log = LoggerFactory.getLogger(OAuth2Resource::class.java)

    /**
     *
     */
    fun authorize(req: Request, resp: Response): Any {
        val responseType = req.queryParams("response_type") ?: ""
        val state = req.queryParams("state") ?: ""

        when (responseType) {
            "code" -> {
                val info = req.attribute<CodeAuthorizeInfo>(Constants.REQUEST_CODE_AUTHORIZE_INFO_KEY)
                val client = req.attribute<Client>(Constants.REQUEST_AUTH_CLIENT)

                var authId = req.cookie(Constants.COOKIE_AUTH_ID)
                if (!authId.isNullOrEmpty()) {
                    if (client.confirm) {
                        if (redisTemplate.getExpire(RedisKeys.oauth2_auth_id.key(authId)) <= 0) {
                            authId = UUID.randomUUID().toString()

                            // 5分钟后过期
                            redisTemplate.opsForValue().set(RedisKeys.oauth2_auth_id.key(authId), "1", 5, TimeUnit.MINUTES)
                            resp.cookie(Constants.COOKIE_AUTH_ID, authId)

                            val redirectUri = URLEncoder.encode(info.redirectUri, "UTF-8")
                            resp.redirect("${UiaProperties.confirmHtmlUri}?=client_id=${client.clientId}&redirect_uri=$redirectUri&scope=${info.scope}")
                            return ""
                        }
                    }

                    launch(CommonPool) {
                        redisTemplate.delete(RedisKeys.oauth2_auth_id.key(authId))
                    }
                }

                val userContext = req.attribute<UserContext>(Constants.REQUEST_USER_CONTEXT)
                val (code, redirectUri) = oauth2Service.authorizeCode(info, userContext)
                // redirect_uri
                resp.redirect("$redirectUri?code=$code&state=$state")
            }
        }
        return ""
    }

    fun token(req: Request, resp: Response): Any {
        try {
            val grantType = req.queryParams("grant_type")
            val oauthToken: OAuthToken = when (grantType) {
                "authorization_code" -> {
                    handleAuthorizationCode(req)
                }
                "password" -> {
                    handlePassword(req)
                }
                "refresh_token" -> {
                    handleRefreshToken(req)
                }
                else -> {
                    throw OAuth2Exception(error = "unsupported_grant_type")
                }
            }

            resp.header("cache-control", "no-store")
            resp.header("pragma", "no-cache")

            resp.header("content-type", "application/json; charset=UTF-8")
            return JacksonUtils.writeValueAsBytes(oauthToken)
        } catch (e: BizCodeException) {
            when (e.bizCode) {
                BizCodes.C_1000, BizCodes.C_1001 -> throw OAuth2Exception(error = "invalid_client", description = e.bizCode.msg)
                BizCodes.C_2000, BizCodes.C_2011 -> throw OAuth2Exception(error = "invalid_grant", status = 401, description = e.bizCode.msg)
                else -> throw OAuth2Exception(error = "invalid_request", description = e.bizCode.msg)
            }
        } catch (e: Exception) {
            log.error("授权失败", e)
            throw OAuth2Exception(error = "server_error", description = e.message ?: "")
        }

        return ""
    }

    fun handleAuthorizationCode(req: Request): OAuthToken {
        val c = extractClient(req)

        val redirectUri = req.queryParams("redirect_uri") ?: throw OAuth2Exception("invalid_request", "redirect_uri 不存在")
        val code = req.queryParams("code") ?: throw OAuth2Exception("code", "code 不存在")
        val scope = req.queryParams("scope") ?: ""

        return oauth2Service.grantCode(AuthorizationCodeGrantInfo(
                clientId = c.first,
                clientSecret = c.second,
                redirectUri = redirectUri,
                code = code,
                scope = scope
        ))
    }

    fun handlePassword(req: Request): OAuthToken {
        val c = extractClient(req)

        val username = req.queryParams("username") ?: throw OAuth2Exception("invalid_request", "username 不存在")
        val password = req.queryParams("password") ?: throw OAuth2Exception("invalid_request", "client_id 不存在")
        val scope = req.queryParams("scope") ?: ""

        return oauth2Service.grantPassword(PasswordGrantInfo(
                clientId = c.first,
                clientSecret = c.second,
                username = username,
                password = password,
                scope = scope
        ))
    }

    fun handleRefreshToken(req: Request): OAuthToken {
        val c = extractClient(req)

        val refreshToken = req.queryParams("refresh_token") ?: throw OAuth2Exception("invalid_request", "refresh_token 不存在")
        val scope = req.queryParams("scope") ?: ""

        return oauth2Service.grantRefreshToken(RefreshTokenGrantInfo(
                clientId = c.first,
                clientSecret = c.second,
                refreshToken = refreshToken,
                scope = scope
        ))
    }

    private fun extractClient(req: Request): Pair<String, String> {
        val clientId = req.queryParams("client_id") ?: throw OAuth2Exception("invalid_client", "client_id 不存在")
        val clientSecret = req.queryParams("client_secret") ?: throw OAuth2Exception("invalid_client", "client_secret 不存在")
        return Pair(clientId, clientSecret)
    }
}