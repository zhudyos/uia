package io.zhudy.uia.service.impl

import io.zhudy.uia.repository.ClientRepository
import io.zhudy.uia.repository.UserRepository
import io.zhudy.uia.service.OAuth2Service
import org.springframework.stereotype.Service

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Service
class OAuth2ServiceImpl(
        val clientRepository: ClientRepository,
        val userRepository: UserRepository
) : OAuth2Service {

    override fun authorizeImplicit() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun authorizePassword(username: String, password: String) {
        val user = userRepository.findByEmail(username)
        user.filter {
            it.password != password
        }
    }

}
