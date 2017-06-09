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

    var loginFormUri = ""
    var token = Token

    /**
     *
     */
    object Token {
        var accessTokenSalt = ""
        var accessTokenExpiresIn = 6000L
        var refreshTokenSalt = ""
        var refreshTokenExpiresIn = Duration.ofDays(3).seconds
        var minLength = 32
    }

    /**
     *
     */
    object Redis {
        var host = "localhost"
        var port = 6379
    }
}
