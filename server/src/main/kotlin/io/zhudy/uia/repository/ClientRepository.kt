package io.zhudy.uia.repository

import io.zhudy.uia.domain.Client

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
interface ClientRepository {

    fun findByClient(clientId: String): Client

}