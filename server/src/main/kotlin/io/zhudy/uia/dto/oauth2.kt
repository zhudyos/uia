package io.zhudy.uia.dto

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
data class OAuthToken(
        val accessToken: String,
        val refreshToken: String,
        val expiresIn: Int = 6000
)

/**
 *
 */
data class PasswordAuthInfo(
        val clientId: String,
        val clientSecret: String,
        val username: String,
        val password: String,
        val scope: String?
)