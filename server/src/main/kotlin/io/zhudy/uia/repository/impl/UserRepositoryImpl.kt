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

    val coll = mongoClient.getDatabase("uia").getCollection("user")!!

    override fun findByEmail(email: String): Mono<User> {
        val r = coll.find(eq("email", email)).first()
        return Mono.from(r).doOnSuccess {
            it ?: throw BizCodeException(BizCodes.C_2000)
        }.map {
            User(
                    id = it.getLong("_id"),
                    email = it.getString("email") ?: "",
                    password = it.getString("password") ?: "",
                    createdTime = it.getLong("created_time")
            )
        }
    }
}