package io.zhudy.uia.web.filter

import io.zhudy.uia.RedisKeys
import io.zhudy.uia.UserContext
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.web.Constants
import io.zhudy.uia.web.OAuth2Exception
import org.springframework.data.redis.core.StringRedisTemplate
import spark.Filter
import spark.Request
import spark.Response

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class OAuth2SecurityFilter(
        private val redisTemplate: StringRedisTemplate,
        private val oauth2Service: OAuth2Service,
        private val prefix: String,
        authUrls: Map<String, Any>
) : Filter {

    private val inAuthUrls = hashMapOf<String, Any>()

    init {
        authUrls.entries.forEach {
            inAuthUrls[prefix + it.key] = it.value
        }
    }

    override fun handle(request: Request, response: Response) {
        if (!inAuthUrls.containsKey(request.uri())) {
            return
        }

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
        request.attribute(Constants.REQUEST_USER_CONTEXT, object : UserContext {
            override val uid: Long = token.uid
        })
    }
}