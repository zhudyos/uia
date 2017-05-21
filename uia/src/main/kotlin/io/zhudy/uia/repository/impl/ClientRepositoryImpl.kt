package io.zhudy.uia.repository.impl

import com.mongodb.client.model.Filters.eq
import com.mongodb.reactivestreams.client.MongoClient
import io.zhudy.uia.domain.Client
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
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

    val clientColl = mongoClient.getDatabase("uia").getCollection("client")!!

    override fun findByClient(clientId: String): Mono<Client> {
        val r = clientColl.find(eq("client_id", clientId)).first()
        return Mono.from(r).map({
            if (it == null) {
                throw BizCodeException(BizCodes.C_1000)
            }

            Client(
                    clientId = it["client_id"] as String,
                    clientSecret = it["client_secret"] as String
            )
        })
    }

}