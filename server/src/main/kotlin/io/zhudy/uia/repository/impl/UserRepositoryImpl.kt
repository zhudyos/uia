package io.zhudy.uia.repository.impl

import com.mongodb.MongoClient
import com.mongodb.client.model.Filters.eq
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.domain.User
import io.zhudy.uia.repository.UserRepository
import org.springframework.stereotype.Repository

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Repository
class UserRepositoryImpl(
        mongoClient: MongoClient
) : UserRepository {

    val coll = mongoClient.getDatabase("uia").getCollection("user")!!

    override fun findByEmail(email: String): User {
        val r = coll.find(eq("email", email)).first() ?: throw BizCodeException(BizCodes.C_2000)
        return User(
                id = r.getLong("_id"),
                email = r.getString("email") ?: "",
                password = r.getString("password") ?: "",
                createdTime = r.getLong("created_time")
        )
    }
}