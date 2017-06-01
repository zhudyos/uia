package io.zhudy.uia.service.impl

import io.zhudy.uia.dto.PasswordAuthInfo
import io.zhudy.uia.repository.ClientRepository
import io.zhudy.uia.repository.UserRepository
import io.zhudy.uia.service.OAuth2Exception
import io.zhudy.uia.service.OAuth2Service
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

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

    override fun authorizePassword(pai: PasswordAuthInfo) = Mono.create<String> { sink ->
        clientRepository.findByClient(pai.clientId)
                .and(userRepository.findByEmail(pai.username)).doOnError {
            // FIXME 待修改
            throw it
        }.subscribe {
            // 校验客户端
            if (it.t1.clientSecret != pai.clientSecret) {
                sink.error(OAuth2Exception(OAuth2Exception.UNAUTHORIZED_CLIENT))
                return@subscribe
            }

            // 校验用户密码
            if (it.t2.password != pai.password) {
                return@subscribe
            }


        }
    }!!

    fun validateClientSecret(clientSecret1: String, clientSecret2: String): Boolean {
        return clientSecret1 == clientSecret2
    }
}
