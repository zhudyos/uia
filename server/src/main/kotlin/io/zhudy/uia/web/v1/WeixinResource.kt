package io.zhudy.uia.web.v1

import io.undertow.server.HttpServerExchange
import io.zhudy.uia.JacksonUtils
import io.zhudy.uia.UiaProperties
import io.zhudy.uia.dto.WeixinAccessToken
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.web.queryParam
import io.zhudy.uia.web.sendJson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import javax.json.JsonObject

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
        val code = exchange.queryParam("code")
        val state = exchange.queryParam("state")
        val stateJson = JacksonUtils.objectMapper.readValue(state, JsonObject::class.java)
        val appid = stateJson.getString("appid")

        exchange.sendJson(mapOf("a" to "a", "b" to "b"))
    }

    fun getAccessToken(code: String, appid: String): WeixinAccessToken {
        val appSecret = UiaProperties.weixin.appids[appid]
        val req = Request.Builder().get()
                .url("https://api.weixin.qq.com/sns/oauth2/access_token?appid=$appid&secret=$appSecret&code=$code&grant_type=authorization_code")
                .build()
        val resp = httpClient.newCall(req).execute()
        if (!resp.isSuccessful) {

        }
        val token = JacksonUtils.objectMapper.readValue(resp.body()!!.string(), WeixinAccessToken::class.java)

        // ==
        return token
    }
}
