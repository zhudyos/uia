package io.zhudy.uia.dto

/**
 * 令牌解码信息.
 *
 * @param uid 用户 ID
 * @param cid 客户端 ID
 * @param expireTime 令牌过期时间
 */
data class TokenInfo(
        val uid: Long,
        val cid: Long,
        val expireTime: Long
)

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
data class OAuthToken(
        val accessToken: String,
        val refreshToken: String,
        val expiresIn: Int = 7000
)

/**
 *
 */
data class AuthorizationCodeAuthInfo(
        val clientId: String,
        val clientSecret: String,
        val redirectUri: String,
        val code: String,
        val scope: String = ""
)

/**
 *
 */
data class PasswordAuthInfo(
        val clientId: String,
        val clientSecret: String,
        val username: String,
        val password: String,
        val scope: String = ""
)

data class RefreshTokenAuthInfo(
        val clientId: String,
        val clientSecret: String,
        val refreshToken: String,
        val scope: String = ""
)