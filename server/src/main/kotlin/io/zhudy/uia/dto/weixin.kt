package io.zhudy.uia.dto

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
data class WeixinAccessToken(
        val accessToken: String = "",
        val refreshToken: String = "",
        val expiresIn: Int = 0,
        val openid: String = "",
        val scope: String = "",

        // =================================
        val errcode: Int = 0,
        val errmsg: String = ""
)