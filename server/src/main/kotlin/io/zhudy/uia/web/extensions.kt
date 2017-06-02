package io.zhudy.uia.web

import org.springframework.web.reactive.function.server.ServerRequest

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */

fun ServerRequest.requiredQueryParam(name: String): String {
    return this.queryParam(name).orElseThrow { RequestParamException(name) }
}