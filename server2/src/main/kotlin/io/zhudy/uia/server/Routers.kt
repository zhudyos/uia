package io.zhudy.uia.server

import io.zhudy.uia.web.v1.LoginResource
import org.springframework.stereotype.Component
import spark.Spark.*

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Component
class Routers(
        val loginResource: LoginResource
) {

    init {

        get("/login.html", loginResource::loginView)
        post("/login", loginResource::login)

        path("/api/v1") {
            path("/oauth") {
                get("/authorize", loginResource::login)
                post("/token", loginResource::login)
            }
        }

    }
}

