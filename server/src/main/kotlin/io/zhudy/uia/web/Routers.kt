package io.zhudy.uia.web

import io.zhudy.uia.web.v1.OAuth2Resource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@Configuration
class Routers(
        val oauth2Resource: OAuth2Resource
) {

    @Bean
    fun router() = router {
        path("/api/v1/oauth").nest {
            GET("/authorize", oauth2Resource::authorize)
            GET("/token", oauth2Resource::token)
        }
    }
}