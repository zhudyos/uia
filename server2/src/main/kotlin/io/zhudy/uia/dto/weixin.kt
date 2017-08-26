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

/**
 * @property openid 与 APP 绑定的唯一 ID
 * @property nickname
 * @property sex
 * @property language
 * @property city
 * @property province
 * @property country
 * @property headimgurl
 * @property unionid
 */
data class WeixinProfile(
        val openid: String = "",
        val nickname: String = "",
        val sex: Int = 0,
        val language: String = "",
        val city: String = "",
        val province: String = "",
        val country: String = "",
        val headimgurl: String = "",
        val unionid: String = "",

        // =================================
        val errcode: Int = 0,
        val errmsg: String = ""
)
