package io.zhudy.uia.service.impl

import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.RedisKeys
import io.zhudy.uia.UiaProperties
import io.zhudy.uia.domain.Client
import io.zhudy.uia.dto.*
import io.zhudy.uia.helper.JedisHelper
import io.zhudy.uia.repository.ClientRepository
import io.zhudy.uia.repository.UserRepository
import io.zhudy.uia.service.OAuth2Service
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.hashids.Hashids
import org.springframework.stereotype.Service

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Service
class OAuth2ServiceImpl(
        val clientRepository: ClientRepository,
        val userRepository: UserRepository,
        val jedisHelper: JedisHelper
) : OAuth2Service {

    val accessTokenGen = Hashids(UiaProperties.token.salt, 32)
    val refreshTokenGen = Hashids(UiaProperties.refreshToken.salt, 32)

    override fun decodeToken(token: String): TokenInfo {
        return decodeToken0(token, accessTokenGen)
    }

    override fun decodeRefreshToken(refreshToken: String): TokenInfo {
        return decodeToken0(refreshToken, refreshTokenGen)
    }

    override fun newOAuthToken(uid: Long, cid: Long): OAuthToken {
        val time = System.currentTimeMillis()
        val token = OAuthToken(accessToken = accessTokenGen.encode(uid, cid, time + UiaProperties.token.expiresIn * 1000),
                refreshToken = refreshTokenGen.encode(uid, cid, time + UiaProperties.refreshToken.expiresIn * 1000))

        // 添加 redis 缓存
        launch(CommonPool) {
            val jedis = jedisHelper.getJedis()
            jedis.setex(RedisKeys.oauth2_token.key(uid), UiaProperties.token.expiresIn, token.accessToken)
            jedis.setex(RedisKeys.oauth2_rtoken.key(uid), UiaProperties.refreshToken.expiresIn, token.refreshToken)
        }
        return token
    }

    override fun authorizeCode(info: AuthorizationCodeAuthInfo): OAuthToken {
        val codeFields = jedisHelper.getJedis().hgetAll(RedisKeys.oauth2_code.key(info.code))
        if (codeFields["redirect_uri"] != info.redirectUri) {
            throw BizCodeException(BizCodes.C_3000)
        }
        val client = checkClient(info.clientId, info.clientSecret)
        return newOAuthToken(codeFields["uid"] as Long, client.id)
    }

    override fun authorizePassword(info: PasswordAuthInfo): OAuthToken {
        val client = checkClient(info.clientId, info.clientSecret)

        val user = userRepository.findByEmail(info.username)
        if (!validateUserPwd(info.password, user.password)) {
            throw BizCodeException(BizCodes.C_2011)
        }

        // 返回 Token
        return newOAuthToken(user.id, client.id)
    }

    override fun refreshToken(info: RefreshTokenAuthInfo): OAuthToken {
        val client = checkClient(info.clientId, info.clientSecret)
        val t = decodeRefreshToken(info.refreshToken)
        if (t.expireTime > System.currentTimeMillis()) { // refresh token 已经过期
            throw BizCodeException(BizCodes.C_3001)
        }

        if (t.cid != client.id) { // client id 与申请 refresh token 的不一致
            throw BizCodeException(BizCodes.C_3002)
        }

        val ttl = jedisHelper.getJedis().ttl(RedisKeys.oauth2_rtoken.key(t.uid)) ?: 0
        if (ttl <= 0) { // refresh token 不存在或者已经过期
            throw BizCodeException(BizCodes.C_3001)
        }

        return newOAuthToken(t.uid, client.id)
    }

    private fun checkClient(clientId: String, clientSecret: String): Client {
        val client = clientRepository.findByClient(clientId)
        if (clientSecret != client.clientSecret) {
            throw BizCodeException(BizCodes.C_1001)
        }
        return client
    }

    private fun validateUserPwd(pwd1: String, pwd2: String): Boolean {
        return pwd1 == pwd2
    }

    private fun decodeToken0(token: String, gen: Hashids): TokenInfo {
        val arr = gen.decode(token)
        return TokenInfo(uid = arr[0], cid = arr[1], expireTime = arr[2])
    }
}
