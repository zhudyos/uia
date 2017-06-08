package io.zhudy.uia.repository.impl

import com.mongodb.client.model.Filters.eq
import com.mongodb.reactivestreams.client.MongoClient
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.domain.Client
import io.zhudy.uia.repository.ClientRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@Repository
class ClientRepositoryImpl(
        mongoClient: MongoClient
) : ClientRepository {

    val coll = mongoClient.getDatabase("uia").getCollection("client")!!

    override fun findByClient(clientId: String): Mono<Client> {
        val r = coll.find(eq("client_id", clientId)).first()
        return Mono.from(r).doOnSuccess {
            it ?: throw BizCodeException(BizCodes.C_1000)
        }.map {
            Client(
                    clientId = it.getString("client_id") ?: "",
                    clientSecret = it.getString("client_secret") ?: "",
                    redirectUri = it.getString("redirect_uri") ?: "",
                    scope = it.getString("scope") ?: ""
            )
        }
    }

}