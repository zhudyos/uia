package io.zhudy.uia.web

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import io.zhudy.uia.web.v1.LoginResource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@Configuration
class HttpHandlers(
        val loginResource: LoginResource
) {

    val SNAKE_CASE = PropertyNamingStrategy.SNAKE_CASE as PropertyNamingStrategy.PropertyNamingStrategyBase

    @Bean
    fun apis() = router {
        path("/api/v1").nest {

            POST("/login", loginResource::login)

        }
    }


    /**
     * OAuth2 错误结果.
     */
    data class OAuthErrorRS(val error: String, val errorDescription: String, val state: String)
}