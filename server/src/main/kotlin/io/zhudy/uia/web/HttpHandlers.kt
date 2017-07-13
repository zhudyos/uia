package io.zhudy.uia.web

import io.undertow.Handlers.*
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
        val handler = path()
        handler.addPrefixPath("/oauth", routing()
                .get("/authorize", oauth2Resource::authorize)
                .post("/token", oauth2Resource::token)
        )

        return gracefulShutdown(
                path().addPrefixPath("/api/v1", ExceptionHttpHandler(handler))
        )
    }
}