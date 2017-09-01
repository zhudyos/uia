package io.zhudy.uia.service.impl

import io.zhudy.uia.*
import io.zhudy.uia.domain.Client
import io.zhudy.uia.dto.*
import io.zhudy.uia.repository.ClientRepository
import io.zhudy.uia.repository.UserRepository
import io.zhudy.uia.service.OAuth2Service
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.hashids.Hashids
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.lang.management.ManagementFactory
import java.net.URL

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Service
class OAuth2ServiceImpl(
        val clientRepository: ClientRepository,
        val userRepository: UserRepository,
        val passwordEncoder: PasswordEncoder,
        val redisTemplate: StringRedisTemplate
) : OAuth2Service {

    val codeGen = Hashids("${ManagementFactory.getRuntimeMXBean().name}", 32)
    val accessTokenGen = Hashids(UiaProperties.token.salt, 32)
    val refreshTokenGen = Hashids(UiaProperties.refreshToken.salt, 32)

    override fun authorizeCheck(info: CodeAuthorizeInfo): Client {
        // client_id 是否存在
        val client = clientRepository.findByClient(info.clientId)

        // redirect_uri 是否在白名单内
        if (info.redirectUri.isNotEmpty()) {
            val a = URL(info.redirectUri)
            val b = URL(client.redirectUri)
            if (a.host != b.host || a.port != b.port) {
                throw BizCodeException(BizCodes.C_1002)
            }
        }
        return client
    }

    override fun authorizeCode(info: CodeAuthorizeInfo, userContext: UserContext): Pair<String, String> {
        val client = clientRepository.findByClient(info.clientId)
        var redirectUri = info.redirectUri
        if (redirectUri.isEmpty()) {
            redirectUri = client.redirectUri
        }

        // TODO 需要保证 code 生成是唯一的
        val code = codeGen.encode(client.id, System.nanoTime())
        if (code.isNullOrEmpty()) {
            throw IllegalStateException("生成的 oauth2 code 为空")
        }

        val k = RedisKeys.oauth2_code.key(code).toByteArray()

        // 将信息缓存在 redis 中
        redisTemplate.executePipelined {
            it.hSet(k, "redirect_uri".toByteArray(), redirectUri.toByteArray())
            it.hSet(k, "uid".toByteArray(), userContext.uid.toString().toByteArray())
            it.hSet(k, "client_id".toByteArray(), info.clientId.toByteArray())
            it.expire(k, UiaProperties.code.expiresIn)
        }

        return Pair(code, redirectUri)
    }

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
            val ops = redisTemplate.opsForValue()
            ops.set(RedisKeys.oauth2_token.key(uid, cid), token.accessToken, UiaProperties.token.expiresIn)
            ops.set(RedisKeys.oauth2_rtoken.key(uid, cid), token.refreshToken, UiaProperties.refreshToken.expiresIn)
        }
        return token
    }

    override fun grantCode(info: AuthorizationCodeGrantInfo): OAuthToken {
        val key = RedisKeys.oauth2_code.key(info.code)
        val hashOps = redisTemplate.opsForHash<String, String>()
        val codeFields = hashOps.entries(key)
        redisTemplate.delete(key)

        if (codeFields.isEmpty()) {
            throw BizCodeException(BizCodes.C_3004)
        }
        if (codeFields["client_id"] != info.clientId) {
            throw BizCodeException(BizCodes.C_3003)
        }
        if (codeFields["redirect_uri"] != info.redirectUri) {
            throw BizCodeException(BizCodes.C_3000)
        }

        val client = checkClient(info.clientId, info.clientSecret)
        // FIXME 增加 log
        return newOAuthToken(codeFields["uid"]!!.toLong(), client.id)
    }

    override fun grantPassword(info: PasswordGrantInfo): OAuthToken {
        val client = checkClient(info.clientId, info.clientSecret)

        val user = userRepository.findByEmail(info.username)
        if (!passwordEncoder.matches(info.password, user.password)) {
            throw BizCodeException(BizCodes.C_2011)
        }

        // FIXME 增加 log
        return newOAuthToken(user.id, client.id)
    }

    override fun grantRefreshToken(info: RefreshTokenGrantInfo): OAuthToken {
        val client = checkClient(info.clientId, info.clientSecret)
        val t = decodeRefreshToken(info.refreshToken)
        if (t.expireTime < System.currentTimeMillis()) { // refresh token 已经过期
            throw BizCodeException(BizCodes.C_3001)
        }

        if (t.cid != client.id) { // client id 与申请 refresh token 的不一致
            throw BizCodeException(BizCodes.C_3002)
        }

        val ttl = redisTemplate.getExpire(RedisKeys.oauth2_rtoken.key(t.uid))
        if (ttl <= 0) { // refresh token 不存在或者已经过期
            throw BizCodeException(BizCodes.C_3001)
        }

        // FIXME 增加 log
        return newOAuthToken(t.uid, client.id)
    }

    private fun checkClient(clientId: String, clientSecret: String): Client {
        val client = clientRepository.findByClient(clientId)
        if (clientSecret != client.clientSecret) {
            throw BizCodeException(BizCodes.C_1001)
        }
        return client
    }

    private fun decodeToken0(token: String, gen: Hashids): TokenInfo {
        val arr = gen.decode(token)
        return TokenInfo(uid = arr[0], cid = arr[1], expireTime = arr[2])
    }
}
