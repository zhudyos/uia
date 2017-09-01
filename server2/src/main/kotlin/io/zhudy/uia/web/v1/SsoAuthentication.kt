package io.zhudy.uia.web.v1

import io.zhudy.uia.RedisKeys
import io.zhudy.uia.UiaProperties
import io.zhudy.uia.UserContext
import io.zhudy.uia.domain.User
import io.zhudy.uia.web.Constants
import org.hashids.Hashids
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import spark.Request
import spark.Response
import java.net.URLDecoder
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.http.Cookie

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Component
class SsoAuthentication(
        @Qualifier("stringRedisTemplate")
        val redisTemplate: StringRedisTemplate
) {

    val tokenGen = Hashids(UiaProperties.SsoToken.salt, UiaProperties.SsoToken.length)

    /**
     *
     */
    fun validate(req: Request, resp: Response): Boolean {
        val ssoToken = req.cookie(UiaProperties.SsoToken.Cookie.name) ?: return false

        val uid: Long
        try {
            val arr = tokenGen.decode(ssoToken)
            if (arr.isEmpty()) return false
            uid = arr[0]
        } catch (e: Exception) {
            return false
        }

        val token = redisTemplate.opsForValue()[RedisKeys.sso_token.key(uid)]
        val r = token != null && token == ssoToken

        if (r) {
            // 封闭 UserContext
            req.attribute(Constants.REQUEST_USER_CONTEXT, object : UserContext {
                override val uid: Long = uid
            })
        }
        return r
    }

    /**
     *
     */
    fun complete(req: Request, resp: Response, user: User) {
        val token = tokenGen.encode(user.id, System.currentTimeMillis())

        // 设置单点登录 token
        val ssoToken = UiaProperties.ssoToken

        val rawResp = resp.raw()
        val c = Cookie(ssoToken.cookie.name, token)

        // ====================== cookie 配置 ==========================
        if (UiaProperties.SsoToken.Cookie.domain.isNotEmpty()) {
            c.domain = UiaProperties.SsoToken.Cookie.domain
        }
        if (UiaProperties.SsoToken.Cookie.maxAge > 0) {
            c.maxAge = UiaProperties.SsoToken.Cookie.maxAge
        }
        if (UiaProperties.SsoToken.Cookie.path.isNotEmpty()) {
            c.path = UiaProperties.SsoToken.Cookie.path
        }
        if (UiaProperties.SsoToken.Cookie.httpOnly) {
            c.isHttpOnly = true
        }
        if (UiaProperties.SsoToken.Cookie.secure) {
            c.secure = true
        }
        rawResp.addCookie(c)

        // 登录完成自动设置 AUTH_ID
        val authId = UUID.randomUUID().toString()
        resp.cookie(Constants.COOKIE_AUTH_ID, authId)
        // 5分钟后过期
        redisTemplate.opsForValue().set(RedisKeys.oauth2_auth_id.key(authId), "1", 5, TimeUnit.MINUTES)

        // 缓存 sso_token
        redisTemplate.opsForValue().set(RedisKeys.sso_token.key(user.id), token, UiaProperties.SsoToken.expiresIn, TimeUnit.SECONDS)

        val redirect_uri = req.queryParams("redirect_uri") ?: ""
        if (redirect_uri.isNotEmpty()) {
            resp.redirect(URLDecoder.decode(redirect_uri, Charsets.UTF_8.name()))
        }
    }
}