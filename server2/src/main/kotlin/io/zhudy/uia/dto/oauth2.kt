package io.zhudy.uia.dto

/**
 *
 */
data class CodeAuthorizeInfo(
        val clientId: String,
        val redirectUri: String,
        val scope: String = ""
)

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
data class AuthorizationCodeGrantInfo(
        val clientId: String,
        val clientSecret: String,
        val redirectUri: String,
        val code: String,
        val scope: String = ""
)

/**
 *
 */
data class PasswordGrantInfo(
        val clientId: String,
        val clientSecret: String,
        val username: String,
        val password: String,
        val scope: String = ""
)

data class RefreshTokenGrantInfo(
        val clientId: String,
        val clientSecret: String,
        val refreshToken: String,
        val scope: String = ""
)