package io.zhudy.uia

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class RedisKey {
    private val prefix: String

    constructor(prefix: String) {
        this.prefix = "$prefix:"
    }

    fun key(vararg segments: Any) = segments.joinToString(prefix = prefix, separator = ":")
}

object RedisKeys {

    /**
     * Client Repository 对象.
     */
    val client_repo = RedisKey("client_repo")
    /**
     * oauth2 authorization code.
     */
    val oauth2_code = RedisKey("oauth2:code")
    /**
     * oauth2 access token.
     */
    val oauth2_token = RedisKey("oauth2:token")
    /**
     * oauth2 refresh token.
     */
    val oauth2_rtoken = RedisKey("oauth2:rtoken")

}