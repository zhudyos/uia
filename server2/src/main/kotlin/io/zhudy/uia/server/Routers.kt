package io.zhudy.uia.server

import io.zhudy.uia.UiaProperties
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.web.filter.OAuth2CodeValidationFilter
import io.zhudy.uia.web.filter.OAuth2SecurityFilter
import io.zhudy.uia.web.filter.SecurityFilter
import io.zhudy.uia.web.v1.LoginResource
import io.zhudy.uia.web.v1.OAuth2Resource
import io.zhudy.uia.web.v1.UserResource
import org.slf4j.MDC
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import spark.Filter
import spark.Spark.*
import java.util.*

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Component
class Routers(
        securityFilter: SecurityFilter,
        oauth2CodeValidationFilter: OAuth2CodeValidationFilter,
        val redisTemplate: StringRedisTemplate,
        val oauth2Service: OAuth2Service,
        // =============================================================
        val loginResource: LoginResource,
        val oauth2Resource: OAuth2Resource,
        val userResource: UserResource
) {

    init {
        port(UiaProperties.port)

        staticFileLocation("/templates")
        post("/login", loginResource::login)

        // X-Request-ID 设置
        before(Filter { request, response ->
            var requestId = request.headers("X-Request-ID")
            if (requestId.isNullOrEmpty()) {
                requestId = UUID.randomUUID().toString().replace("-", "")
            }
            MDC.put("requestId", "[$requestId] ")
            response.header("X-Request-ID", requestId)
        })

        // 获取 authorization_code
        before("/api/v1/oauth/authorize", oauth2CodeValidationFilter)
        before("/api/v1/oauth/authorize", securityFilter)

        //
        before("/api/v1/*", oauth2SecurityFilter())

        path("/api/v1") {
            path("/oauth") {
                get("/authorize", oauth2Resource::authorize)
                post("/token", oauth2Resource::token)
            }

            // 获取当前认证用户信息
            get("/user", userResource::loadCurUserInfo)
        }


        // exception handlers
        exceptions()
    }

    private fun oauth2SecurityFilter(): OAuth2SecurityFilter {
        return OAuth2SecurityFilter(redisTemplate = redisTemplate,
                oauth2Service = oauth2Service,
                prefix = "/api/v1",
                authUrls = mapOf(
                        "/user" to 1
                )
        )
    }
}

