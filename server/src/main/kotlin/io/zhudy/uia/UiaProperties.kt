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

    var redisUri = ""
    var loginFormUri = ""
    val token = Token
    val refreshToken = RefreshToken

    /**
     *
     */
    object Token {
        var salt = ""
        var expiresIn = 6000L
    }

    object RefreshToken {
        var salt = ""
        var expiresIn = Duration.ofDays(3).seconds
    }
}
