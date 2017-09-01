package io.zhudy.uia.repository.impl

import com.mongodb.MongoClient
import com.mongodb.client.model.Filters.eq
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.domain.User
import io.zhudy.uia.helper.MongoSeqHelper
import io.zhudy.uia.repository.UserRepository
import org.bson.Document
import org.springframework.stereotype.Repository

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Repository
class UserRepositoryImpl(
        val mongoClient: MongoClient,
        val mongoSeqHelper: MongoSeqHelper
) : UserRepository {

    val coll = mongoClient.getDatabase("uia").getCollection("user")!!

    override fun save(user: User): Long {
        val doc = Document()
        val _id = mongoSeqHelper.next("user_id")
        doc.put("_id", _id)
        doc.put("email", user.email)
        doc.put("password", user.password)
        doc.put("created_time", user.createdTime)

        return _id
    }

    override fun findByUid(uid: Long): User {
        val r = coll.find(eq("_id", uid)).first() ?: throw BizCodeException(BizCodes.C_2001)
        return User(
                id = r.getLong("_id"),
                email = r.getString("email") ?: "",
                password = r.getString("password") ?: "",
                createdTime = r.getLong("created_time")
        )
    }

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