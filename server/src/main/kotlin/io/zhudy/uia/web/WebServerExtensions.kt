package io.zhudy.uia.web

import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */

// fun ServerRequest.form() = this.body(BodyExtractors.toFormData())!!
/**
 * POST 请求参数.
 */
fun <R> ServerRequest.form(f: (MultiValueMap<String, String>) -> Mono<R>) = this.body(BodyExtractors.toFormData()).flatMap { f(it) }

// fun ServerRequest.json(elementClass: KClass<*>) = this.bodyToMono(elementClass)
/**
 *  Request json body.
 *  @param T 输入参数类型
 *  @param R 输出参数类型
 */
fun <T : Any, R> ServerRequest.json(elementClass: KClass<T>, f: (T) -> Mono<R>) = this.bodyToMono(elementClass).flatMap { f(it) }

/**
 * Response json body.
 */
fun <T> ServerResponse.BodyBuilder.json(obj: T) = this.body(BodyInserters.fromObject(obj))!!