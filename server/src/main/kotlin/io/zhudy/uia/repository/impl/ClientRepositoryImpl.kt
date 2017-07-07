package io.zhudy.uia.repository.impl

import com.lambdaworks.redis.api.StatefulRedisConnection
import com.mongodb.MongoClient
import com.mongodb.client.model.Filters.eq
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.JacksonUtils
import io.zhudy.uia.RedisKeys
import io.zhudy.uia.domain.Client
import io.zhudy.uia.repository.ClientRepository
import org.springframework.stereotype.Repository
import java.time.Duration

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@Repository
class ClientRepositoryImpl(
        mongoClient: MongoClient,
        val redisConn: StatefulRedisConnection<String, String>
) : ClientRepository {

    val coll = mongoClient.getDatabase("uia").getCollection("client")!!
    val redisComm = redisConn.sync()!!

    override fun findByClient(clientId: String): Client {
        val ckey = RedisKeys.client_repo.key(clientId)
        val cacheClient = redisComm[ckey]
        if (cacheClient != null && cacheClient.isNotEmpty()) {
            return JacksonUtils.objectMapper.readValue(cacheClient, Client::class.java)
        }

        val doc = coll.find(eq("client_id", clientId)).first() ?: throw BizCodeException(BizCodes.C_1000)
        val client = Client(
                id = doc.getLong("_id") ?: 0,
                clientId = doc.getString("client_id") ?: "",
                clientSecret = doc.getString("client_secret") ?: "",
                redirectUri = doc.getString("redirect_uri") ?: "",
                scope = doc.getString("scope") ?: ""
        )

        // 将 client 加入缓存
        redisConn.async().setex(ckey, Duration.ofDays(7).seconds, JacksonUtils.objectMapper.writeValueAsString(client))
        return client
    }
}