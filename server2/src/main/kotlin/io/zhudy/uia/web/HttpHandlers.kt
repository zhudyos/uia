package io.zhudy.uia.web

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import io.zhudy.uia.web.v1.LoginResource
import org.springframework.context.annotation.Configuration

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@Configuration
class HttpHandlers(
        val loginResource: LoginResource
) {

    val SNAKE_CASE = PropertyNamingStrategy.SNAKE_CASE as PropertyNamingStrategy.PropertyNamingStrategyBase


    /**
     * OAuth2 错误结果.
     */
    data class OAuthErrorRS(val error: String, val errorDescription: String, val state: String)
}