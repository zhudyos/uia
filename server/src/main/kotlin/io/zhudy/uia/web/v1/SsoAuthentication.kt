package io.zhudy.uia.web.v1

import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.CookieImpl
import io.zhudy.uia.RedisKeys
import io.zhudy.uia.UiaProperties
import io.zhudy.uia.domain.User
import io.zhudy.uia.helper.JedisHelper
import io.zhudy.uia.web.queryParam
import io.zhudy.uia.web.sendRedirect
import org.hashids.Hashids
import org.springframework.stereotype.Component

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Component
class SsoAuthentication(
        val jedisHelper: JedisHelper
) {

    val tokenGen = Hashids(UiaProperties.ssoToken.salt, UiaProperties.ssoToken.length)

    /**
     *
     */
    fun validate(exchange: HttpServerExchange): Boolean {
        val cookie = exchange.requestCookies[UiaProperties.ssoToken.cookie.name] ?: return false
        cookie.value ?: return false

        val uid: Long
        try {
            val arr = tokenGen.decode(cookie.value)
            if (arr.size <= 0) return false
            uid = arr[0]
        } catch (e: Exception) {
            return false
        }

        val jedis = jedisHelper.getJedis()
        val token = jedis[RedisKeys.sso_token.key(uid)]
        return token != null && token == cookie.value
    }

    /**
     *
     */
    fun complete(exchange: HttpServerExchange, user: User) {
        val token = tokenGen.encode(user.id, System.currentTimeMillis())

        // 设置单点登录 token
        val ssoToken = UiaProperties.ssoToken
        val c = CookieImpl(UiaProperties.ssoToken.cookie.name, token)

        // ====================== cookie 配置 ==========================
        if (ssoToken.cookie.domain.isNotEmpty()) {
            c.domain = ssoToken.cookie.domain
        }
        if (ssoToken.cookie.maxAge > 0) {
            c.maxAge = ssoToken.cookie.maxAge
        }
        if (ssoToken.cookie.path.isNotEmpty()) {
            c.path = ssoToken.cookie.path
        }
        if (ssoToken.cookie.httpOnly) {
            c.isHttpOnly = true
        }
        if (ssoToken.cookie.secure) {
            c.isSecure = true
        }
        exchange.setResponseCookie(c)

        // 缓存 sso_token
        jedisHelper.getJedis().setex(RedisKeys.sso_token.key(user.id), ssoToken.expiresIn, token)
        val redirect_uri = exchange.queryParam("redirect_uri") ?: ""
        if (redirect_uri.isNotEmpty()) {
            exchange.sendRedirect(redirect_uri)
        }
    }
}
