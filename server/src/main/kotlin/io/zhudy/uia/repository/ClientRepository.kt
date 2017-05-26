package io.zhudy.uia.repository

import io.zhudy.uia.domain.Client
import reactor.core.publisher.Mono

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
interface ClientRepository {

    fun findByClient(clientId: String): Mono<Client>

}