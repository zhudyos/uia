package io.zhudy.uia

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@ConfigurationProperties(prefix = "uia")
object UiaProperties {

    var loginFormUri: String = ""
}