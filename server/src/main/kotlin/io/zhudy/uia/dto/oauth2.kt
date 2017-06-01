package io.zhudy.uia.dto

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
data class PasswordAuthInfo(
        val clientId: String,
        val clientSecret: String,
        val username: String,
        val password: String,
        val scope: String?
)