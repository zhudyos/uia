package io.zhudy.uia.repository.impl

import com.mongodb.MongoClient
import com.mongodb.client.model.Filters.eq
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.JacksonUtils
import io.zhudy.uia.RedisKeys
import io.zhudy.uia.domain.Client
import io.zhudy.uia.repository.ClientRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@Repository
class ClientRepositoryImpl(
        mongoClient: MongoClient,
        val redisTemplate: StringRedisTemplate
) : ClientRepository {

    val coll = mongoClient.getDatabase("uia").getCollection("client")!!

    override fun findByClient(clientId: String): Client {
        val ckey = RedisKeys.client_repo.key(clientId)
        val valOps = redisTemplate.opsForValue()
        val cacheClient = valOps[ckey]

        if (cacheClient != null && cacheClient.isNotEmpty()) {
            return JacksonUtils.objectMapper.readValue(cacheClient, Client::class.java)
        }

        val doc = coll.find(eq("client_id", clientId)).first() ?: throw BizCodeException(BizCodes.C_1000)
        val client = Client(
                id = doc.getLong("_id") ?: 0,
                clientId = doc.getString("client_id") ?: "",
                clientSecret = doc.getString("client_secret") ?: "",
                redirectUri = doc.getString("redirect_uri") ?: "",
                scope = doc.getString("scope") ?: "",
                confirm = doc.getBoolean("confirm") ?: true
        )

        // 将 client 加入缓存
        valOps.set(ckey, JacksonUtils.writeValueAsString(client), 7, TimeUnit.DAYS)
        return client
    }
}