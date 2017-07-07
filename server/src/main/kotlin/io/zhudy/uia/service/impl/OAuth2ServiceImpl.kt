package io.zhudy.uia.service.impl

import com.lambdaworks.redis.api.StatefulRedisConnection
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.RedisKeys
import io.zhudy.uia.UiaProperties
import io.zhudy.uia.dto.OAuthToken
import io.zhudy.uia.dto.PasswordAuthInfo
import io.zhudy.uia.repository.ClientRepository
import io.zhudy.uia.repository.UserRepository
import io.zhudy.uia.service.OAuth2Service
import org.hashids.Hashids
import org.springframework.stereotype.Service

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Service
class OAuth2ServiceImpl(
        val clientRepository: ClientRepository,
        val userRepository: UserRepository,
        val redisConn: StatefulRedisConnection<String, String>
) : OAuth2Service {

    //    val valueOps = reactiveRedisTemplate.opsForValue()
    val accessTokenGen = Hashids(UiaProperties.token.salt, 32)
    val refreshTokenGen = Hashids(UiaProperties.refreshToken.salt, 32)

    override fun newOAuthToken(uid: Long, cid: Long): OAuthToken {
        val time = System.currentTimeMillis()
        val token = OAuthToken(accessToken = accessTokenGen.encode(uid, cid, time),
                refreshToken = refreshTokenGen.encode(uid, cid, time))

        // 添加 redis 缓存
        redisConn.async().setex(RedisKeys.token.key(uid), UiaProperties.token.expiresIn, token.accessToken)
        redisConn.async().setex(RedisKeys.rtoken.key(uid), UiaProperties.refreshToken.expiresIn, token.refreshToken)
        return token
    }

    override fun authorizeImplicit() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun authorizePassword(pai: PasswordAuthInfo): OAuthToken {
        val client = clientRepository.findByClient(pai.clientId)
        if (!validateClientSecret(pai.clientSecret, client.clientSecret)) {
            throw BizCodeException(BizCodes.C_1001)
        }

        val user = userRepository.findByEmail(pai.username)
        if (!validateUserPwd(pai.password, user.password)) {
            throw BizCodeException(BizCodes.C_2011)
        }

        // 返回 Token
        return newOAuthToken(user.id, client.id)
    }

    private fun validateClientSecret(clientSecret1: String, clientSecret2: String): Boolean {
        return clientSecret1 == clientSecret2
    }

    private fun validateUserPwd(pwd1: String, pwd2: String): Boolean {
        return pwd1 == pwd2
    }
}
