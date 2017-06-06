package io.zhudy.uia

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@ConfigurationProperties(prefix = "uia")
object UiaProperties {

    var redis = Redis
    var loginFormUri = ""
    var token = Token

    /**
     *
     */
    object Token {
        var accessTokenSalt = ""
        var refreshTokenSalt = ""
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
