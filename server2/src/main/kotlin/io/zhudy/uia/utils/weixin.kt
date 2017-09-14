package io.zhudy.uia.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import java.util.*

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

object WeixinUtils {

    private val log = LoggerFactory.getLogger(WeixinUtils::class.java)
    private val httpClient = OkHttpClient.Builder()
            .build()

    /**
     *
     */
    fun getAccessToken(appId: String, appSecret: String, code: String): WeixinAccessToken {
        val requestId = UUID.randomUUID().toString()
        try {
            val req = Request.Builder().get()
                    .url("https://api.weixin.qq.com/sns/oauth2/access_token?appid=$appId&secret=$appSecret&code=$code&grant_type=authorization_code")
                    .build()
            log.debug("[{}] weixin getAccessToken: {}", requestId, req)

            val resp = httpClient.newCall(req).execute()
            if (!resp.isSuccessful) {
                log.error("[{}] weixin getAccessToken fail. http status {}", requestId, resp.code())
                throw ThirdException("ERROR")
            }

            val token = JacksonUtils.objectMapper.readValue(resp.body()?.bytes(), WeixinAccessToken::class.java)
            if (token.errcode != 0) {
                log.error("[{}] weixin getAccessToken error. response body: {}", requestId, token)
                throw ThirdException("${token.errcode}")
            }
            log.debug("[{}] weixin getAccessToken success. response body: {}", requestId, token)
            return token
        } catch (e: RuntimeException) {
            log.error("[{}] weixin getAccessToken fail.", requestId, e)
            throw ThirdException(e)
        }
    }

    /**
     *
     */
    fun getProfile(token: WeixinAccessToken): WeixinProfile {
        val requestId = UUID.randomUUID().toString()
        try {
            val req = Request.Builder().get()
                    .url("https://api.weixin.qq.com/sns/userinfo?access_token=${token.accessToken}&openid=${token.openid}")
                    .build()
            log.debug("[{}] weixin getProfile: {}", requestId, req)

            val resp = httpClient.newCall(req).execute()
            if (!resp.isSuccessful) {
                log.error("[{}] weixin getProfile fail. http status {}", requestId, resp.code())
                throw ThirdException("ERROR")
            }

            val profile = JacksonUtils.objectMapper.readValue(resp.body()?.bytes(), WeixinProfile::class.java)!!
            if (profile.errcode != 0) {
                log.error("[{}] weixin getProfile error. response body: {}", requestId, profile)
                throw ThirdException("${profile.errcode}")
            }
            log.debug("[{}] weixin getProfile success. response body: {}", requestId, profile)
            return profile
        } catch (e: RuntimeException) {
            log.error("[{}] weixin getProfile fail.", requestId, e)
            throw ThirdException(e)
        }
    }
}
