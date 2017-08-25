package io.zhudy.uia.web.v1

import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.CookieImpl
import io.zhudy.uia.RedisKeys
import io.zhudy.uia.UiaProperties
import io.zhudy.uia.UserContextSetter
import io.zhudy.uia.domain.User
import io.zhudy.uia.helper.JedisHelper
import io.zhudy.uia.web.formData
import io.zhudy.uia.web.param
import io.zhudy.uia.web.queryParam
import io.zhudy.uia.web.sendRedirect
import org.hashids.Hashids
import org.springframework.stereotype.Component
import java.net.URLDecoder

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
            if (arr.isEmpty()) return false
            uid = arr[0]
        } catch (e: Exception) {
            return false
        }

        val jedis = jedisHelper.jedis
        val token = jedis[RedisKeys.sso_token.key(uid)]
        val r = token != null && token == cookie.value

        if (r) {
            // 将 uid 设置至上下文中
            UserContextSetter.uid.set(uid)
        }
        return r
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
        jedisHelper.jedis.setex(RedisKeys.sso_token.key(user.id), ssoToken.expiresIn, token)

        val redirect_uri = exchange.formData()?.param("redirect_uri") ?: ""
        if (redirect_uri.isNotEmpty()) {
            exchange.sendRedirect(URLDecoder.decode(redirect_uri, Charsets.UTF_8.name()))
        }
    }
}
