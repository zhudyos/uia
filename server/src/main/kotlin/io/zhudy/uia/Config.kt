package io.zhudy.uia

import java.time.Duration

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
object Config {

    var port: Int = 8080
    var loginHtmlUri = ""
    var confirmHtmlUri = ""

    val ssoToken = SsoToken
    val code = Code
    val token = Token
    val refreshToken = RefreshToken
    var weixin = Weixin

    /**
     *
     */
    object SsoToken {
        var salt = ""
        var length = 32
        var expiresIn = 2 * 60 * 60L
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

    object Code {
        var expiresIn = Duration.ofMinutes(5).seconds
    }

    /**
     *
     */
    object Token {
        var salt = ""
        var expiresIn = Duration.ofHours(2).seconds
    }

    object RefreshToken {
        var salt = ""
        var expiresIn = Duration.ofDays(3).seconds
    }

    object Weixin {
        var apps: Map<String, Map<String, String>> = HashMap()
    }
}
