package io.zhudy.uia.web

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.form.FormData
import io.undertow.server.handlers.form.FormParserFactory
import io.undertow.util.Headers
import io.undertow.util.StatusCodes
import io.zhudy.uia.JacksonUtils
import java.nio.ByteBuffer
import java.nio.channels.Channels
import kotlin.reflect.KClass

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
val parserFactory = {
    val f = FormParserFactory.builder()
    f.defaultCharset = "UTF-8"
    f.build()!!
}.invoke()

val typeRefMap = object : TypeReference<LinkedHashMap<String, Any>>() {}
var objectMapper = JacksonUtils.objectMapper

/**
 *
 */
inline fun HttpServerExchange.pathParam(name: String) = pathParameters[name]?.first

/**
 *
 */
inline fun HttpServerExchange.queryParam(name: String) = queryParameters[name]?.first

/**
 *
 */
inline fun HttpServerExchange.formData() = parserFactory.createParser(this)?.parseBlocking() ?: throw ResponseStatusException(415)

/**
 *
 */
inline fun HttpServerExchange.jsonBody(): Map<String, Any> {
    val mimeType = requestHeaders[Headers.CONTENT_TYPE].first
    if (mimeType == null || !mimeType.startsWith("application/json")) {
        throw ResponseStatusException(415)
    }
    return objectMapper.readValue(Channels.newInputStream(requestChannel), typeRefMap)
}

/**
 *
 */
inline fun <R : Any> HttpServerExchange.jsonBody(clazz: KClass<R>): R {
    val mimeType = requestHeaders[Headers.CONTENT_TYPE].first
    if (mimeType == null || !mimeType.startsWith("application/json")) {
        throw ResponseStatusException(415)
    }
    return objectMapper.readValue(Channels.newInputStream(requestChannel), clazz.java)
}

/**
 *
 */
inline fun FormData.param(name: String) = getFirst(name)?.value

/**
 *
 */
inline fun HttpServerExchange.sendRedirect(location: String) {
    this.statusCode = StatusCodes.FOUND
    this.responseHeaders.put(Headers.LOCATION, location)
    this.endExchange()
}

/**
 *
 */
inline fun HttpServerExchange.sendJson(o: Any) {
    responseHeaders.put(Headers.CONTENT_TYPE, "application/json; charset=UTF-8")
    if (o is String) {
        responseSender.send(ByteBuffer.wrap(o.toByteArray()))
        return
    }
    responseSender.send(ByteBuffer.wrap(objectMapper.writeValueAsBytes(o)))
}

/**
 *
 */
inline fun HttpServerExchange.sendJson(fields: String, o: Any) {
    if (fields.isEmpty()) {
        sendJson(o)
        return
    }

    responseHeaders.put(Headers.CONTENT_TYPE, "application/json; charset=UTF-8")

    val filter = SimpleBeanPropertyFilter.filterOutAllExcept(fields.split(",").toSet())
    val provider = SimpleFilterProvider()
    provider.defaultFilter = filter

    responseSender.send(ByteBuffer.wrap(objectMapper.writer(provider).writeValueAsBytes(o)))
}
