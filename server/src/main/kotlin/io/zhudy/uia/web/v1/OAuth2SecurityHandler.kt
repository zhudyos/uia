package io.zhudy.uia.web.v1

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.zhudy.uia.RedisKeys
import io.zhudy.uia.UserContextSetter
import io.zhudy.uia.helper.JedisHelper
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.web.OAuth2Exception
import io.zhudy.uia.web.queryParam
import kotlin.reflect.KFunction1

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class OAuth2SecurityHandler(
        private val jedisHelper: JedisHelper,
        private val oauth2Service: OAuth2Service,
        private val next: KFunction1<HttpServerExchange, Unit>) : HttpHandler {

    override fun handleRequest(exchange: HttpServerExchange) {
        val accessToken = exchange.queryParam("access_token")
        if (accessToken.isNullOrEmpty()) {
            throw OAuth2Exception(error = "invalid_token", status = 401)
        }

        val token = oauth2Service.decodeToken(accessToken!!)
        if (token.expireTime < System.currentTimeMillis()) {
            throw OAuth2Exception(error = "token_expired", status = 401)
        }
        val t = jedisHelper.jedis[RedisKeys.oauth2_token.key(token.uid, token.cid)]
        if (t.isNullOrEmpty()) {
            throw OAuth2Exception(error = "token_expired", status = 401)
        }

        // set user context
        UserContextSetter.uid.set(token.uid)

        next(exchange)
    }
}