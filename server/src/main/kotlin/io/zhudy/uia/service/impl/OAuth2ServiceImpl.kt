package io.zhudy.uia.service.impl

import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.UiaProperties
import io.zhudy.uia.dto.OAuthToken
import io.zhudy.uia.dto.PasswordAuthInfo
import io.zhudy.uia.repository.ClientRepository
import io.zhudy.uia.repository.UserRepository
import io.zhudy.uia.service.OAuth2Service
import org.hashids.Hashids
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Service
class OAuth2ServiceImpl(
        val clientRepository: ClientRepository,
        val userRepository: UserRepository
) : OAuth2Service {

    val accessTokenGen = Hashids(UiaProperties.token.accessTokenSalt, UiaProperties.token.minLength)
    val refreshTokenGen = Hashids(UiaProperties.token.refreshTokenSalt, UiaProperties.token.minLength)

    override fun newOAuthToken(uid: Long): Mono<OAuthToken> {
        val token = OAuthToken(accessToken = accessTokenGen.encode(uid, System.currentTimeMillis()),
                refreshToken = refreshTokenGen.encode(uid, System.currentTimeMillis()))
        return token.toMono()
    }

    override fun authorizeImplicit() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun authorizePassword(pai: PasswordAuthInfo) = clientRepository.findByClient(pai.clientId).flatMap { client ->
        if (!validateClientSecret(pai.clientSecret, client.clientSecret)) {
            throw BizCodeException(BizCodes.C_1001)
        }

        userRepository.findByEmail(pai.username).flatMap { user ->
            if (!validateUserPwd(pai.password, user.password)) {
                throw BizCodeException(BizCodes.C_2011)
            }

            // 返回 Token
            newOAuthToken(user.id)
        }
    }

//    override fun authorizePassword(pai: PasswordAuthInfo) = Mono.create<String> { sink ->
//        clientRepository.findByClient(pai.clientId).and(userRepository.findByEmail(pai.username)).doOnError {
//            // FIXME 待修改
//            throw it
//        }.subscribe {
//            // 校验客户端
//            if (it.t1.clientSecret != pai.clientSecret) {
//                // sink.error(OAuth2Exception(OAuth2Exception.UNAUTHORIZED_CLIENT))
//                return@subscribe
//            }
//
//            // 校验用户密码
//            if (it.t2.password != pai.password) {
//                return@subscribe
//            }
//        }
//    }!!

    private fun validateClientSecret(clientSecret1: String, clientSecret2: String): Boolean {
        return clientSecret1 == clientSecret2
    }

    private fun validateUserPwd(pwd1: String, pwd2: String): Boolean {
        return pwd1 == pwd2
    }
}
