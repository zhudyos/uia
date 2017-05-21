package io.zhudy.uia.web

import io.zhudy.uia.resource.OAuthResource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@Configuration
class Routers(
        val oauthResource: OAuthResource
) {

    @Bean
    fun oauthRouter() = router {
        path("/api/v1/oauth").nest {
            GET("/token", oauthResource::token)
        }
    }

}