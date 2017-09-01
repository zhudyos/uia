package io.zhudy.uia.service.impl

import io.zhudy.uia.domain.Client
import io.zhudy.uia.repository.ClientRepository
import io.zhudy.uia.service.ClientService
import org.springframework.stereotype.Service

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Service
class ClientServiceImpl(
        val clientRepository: ClientRepository
) : ClientService {

    override fun findByClientId(clientId: String): Client {
        return clientRepository.findByClient(clientId)
    }
}