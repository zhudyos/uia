package io.zhudy.uia.domain

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
data class RefreshToken(
        val token: String,
        val clientId: String,
        val scope: String,
        val userId: Long
)