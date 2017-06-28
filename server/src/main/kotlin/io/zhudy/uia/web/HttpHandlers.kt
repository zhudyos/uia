package io.zhudy.uia.web

import io.undertow.Handlers
import io.undertow.server.HttpHandler
import io.zhudy.uia.web.v1.OAuth2Resource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@DependsOn("uiaProperties")
@Configuration
class HttpHandlers(
        val oauth2Resource: OAuth2Resource
) {

    @Bean
    fun router(): HttpHandler {
        val routing = Handlers.routing()
        routing.get("/oauth/authorize", oauth2Resource::authorize)
        routing.post("/oauth/token", oauth2Resource::token)
        routing.post("/oauth/token2", oauth2Resource::token2)

        return Handlers.path().addPrefixPath("/api/v1", ExceptionHttpHandler(routing))
    }

//    @Bean
//    fun router() = router {
//        path("/api/v1/oauth").nest {
//            GET("/authorize", oauth2Resource::authorize)
//            POST("/token", oauth2Resource::token)
//        }
//    }.filter(ExceptionHandlerFilterFunction)!!
}