package io.zhudy.uia.server

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.utils.JacksonUtils
import io.zhudy.uia.web.OAuth2Exception
import io.zhudy.uia.web.RequestParamException
import io.zhudy.uia.web.ResponseStatusException
import spark.Spark.exception

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
private val SNAKE_CASE = PropertyNamingStrategy.SNAKE_CASE as PropertyNamingStrategy.PropertyNamingStrategyBase

fun exceptions() {

    exception(MissingKotlinParameterException::class.java, { e, request, response ->
        response.status(400)
        response.body(JacksonUtils.writeValueAsString(mapOf(
                "err_code" to BizCodes.C_999.code,
                "err_message" to "缺少参数 [${SNAKE_CASE.translate(e.parameter.name)}]"
        )))
    })

    exception(ResponseStatusException::class.java, { e, request, response ->
        response.status(e.status)
    })

    exception(RequestParamException::class.java, { e, request, response ->
        response.status(400)
        response.body(JacksonUtils.writeValueAsString(mapOf(
                "err_code" to BizCodes.C_999.code,
                "err_message" to e.message
        )))
    })

    exception(OAuth2Exception::class.java, { e, request, response ->
        response.status(e.status)
        response.body(JacksonUtils.writeValueAsString(OAuthErrorRS(e.error, e.description, e.state)))
    })

    exception(BizCodeException::class.java, { e, request, response ->
        response.status(400)
        response.body(JacksonUtils.writeValueAsString(mapOf(
                "err_code" to e.bizCode.code,
                "err_message" to e.bizCode.msg
        )))
    })

    exception(Exception::class.java, { e, request, response ->
        response.status(500)
        response.body(JacksonUtils.writeValueAsString(mapOf(
                "err_code" to BizCodes.C_500,
                "err_message" to BizCodes.C_500.msg
        )))
    })
}

/**
 * OAuth2 错误结果.
 */
data class OAuthErrorRS(val error: String, val errorDescription: String, val state: String)
