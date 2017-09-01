package io.zhudy.uia.service

import io.zhudy.uia.domain.Client

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
interface ClientService {

    /**
     *
     */
    fun findByClientId(clientId: String): Client

}