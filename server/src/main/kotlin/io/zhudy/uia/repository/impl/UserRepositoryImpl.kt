package io.zhudy.uia.repository.impl

import com.mongodb.client.model.Filters.eq
import com.mongodb.reactivestreams.client.MongoClient
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.domain.User
import io.zhudy.uia.repository.UserRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Repository
class UserRepositoryImpl(
        mongoClient: MongoClient
) : UserRepository {

    val coll = mongoClient.getDatabase("uia").getCollection("client")!!

    override fun findByEmail(email: String): Mono<User> {
        val r = coll.find(eq("email", email)).first()
        return Mono.from(r).map {
            if (it == null) {
                throw BizCodeException(BizCodes.C_1000)
            }
            User(
                    id = it["_id"] as Long,
                    email = it["email"] as String?,
                    password = it["password"] as String?,
                    createdTime = it["created_time"] as Long
            )
        }
    }
}