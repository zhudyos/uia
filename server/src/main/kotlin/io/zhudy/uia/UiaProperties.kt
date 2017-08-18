package io.zhudy.uia

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Component
@ConfigurationProperties(prefix = "uia")
object UiaProperties {

    lateinit var redisUri: String
    lateinit var loginHtmlUri: String

    val ssoToken = SsoToken
    val token = Token
    val refreshToken = RefreshToken
    var weixin = Weixin

    /**
     *
     */
    object SsoToken {
        var salt = ""
        var length = 32
        var expiresIn = 2 * 60 * 60
        val cookie = Cookie

        object Cookie {
            var name = "sso_token"
            var domain = ""
            var maxAge = 0
            var path = "/"
            var httpOnly = true
            var secure = false
        }
    }

    /**
     *
     */
    object Token {
        var salt = ""
        var expiresIn = Duration.ofHours(2).seconds.toInt()
    }

    object RefreshToken {
        var salt = ""
        var expiresIn = Duration.ofDays(3).seconds.toInt()
    }

    object Weixin {
        var appids: Map<String, String> = HashMap()
    }
}
