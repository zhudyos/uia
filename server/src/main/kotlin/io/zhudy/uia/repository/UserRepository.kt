package io.zhudy.uia.repository

import io.zhudy.uia.domain.User
import reactor.core.publisher.Mono

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
interface UserRepository {

    fun findByEmail(email: String): Mono<User>

}