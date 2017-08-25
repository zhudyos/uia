package io.zhudy.uia.web.v1

import io.undertow.server.HttpServerExchange
import io.zhudy.uia.JacksonUtils
import io.zhudy.uia.UiaProperties
import io.zhudy.uia.dto.WeixinAccessToken
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.web.RequestParamException
import io.zhudy.uia.web.queryParam
import io.zhudy.uia.web.sendJson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class WeixinResource(
        val oauth2Service: OAuth2Service
) {
    val log = LoggerFactory.getLogger(WeixinResource::class.java)
    val httpClient = OkHttpClient.Builder().build()

    fun handle(exchange: HttpServerExchange) {
        val label = exchange.queryParam("label") ?: throw RequestParamException("label")
        val code = exchange.queryParam("code") ?: throw RequestParamException("code")
        val token = getAccessToken(code, label)

        val info = getUserInfo(token)
        exchange.sendJson(info)
    }

    fun getAccessToken(code: String, label: String): WeixinAccessToken {
        val app = UiaProperties.weixin.apps[label]
        val appId = app!!["app_id"]
        val appSecret = app["app_secret"]
        val req = Request.Builder().get()
                .url("https://api.weixin.qq.com/sns/oauth2/access_token?appid=$appId&secret=$appSecret&code=$code&grant_type=authorization_code")
                .build()
        val resp = httpClient.newCall(req).execute()
        if (!resp.isSuccessful) {

        }
        val token = JacksonUtils.objectMapper.readValue(resp.body()!!.string(), WeixinAccessToken::class.java)

        // ==
        return token
    }

    private fun getUserInfo(token: WeixinAccessToken): String {
        val req = Request.Builder().get()
                .url("https://api.weixin.qq.com/sns/userinfo?access_token=${token.accessToken}&openid=${token.openid}")
                .build()
        val resp = httpClient.newCall(req).execute()
        if (!resp.isSuccessful) {

        }
        val info = resp.body()!!.string()
        return info
    }
}
