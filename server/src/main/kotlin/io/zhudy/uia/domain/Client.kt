package io.zhudy.uia.domain

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
data class Client(
        val clientId: String,
        val clientSecret: String,
        val redirectUri: String,
        val scope: String
)