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
     * OAuth2 AccessToken.
     */
    val token = RedisKey("token")
    /**
     * OAuth2 RefreshToken.
     */
    val rtoken = RedisKey("rtoken")

}