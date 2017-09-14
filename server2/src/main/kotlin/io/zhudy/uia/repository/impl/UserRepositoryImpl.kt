package io.zhudy.uia.repository.impl

import com.mongodb.MongoClient
import com.mongodb.client.model.Filters.eq
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.domain.User
import io.zhudy.uia.domain.UserSource
import io.zhudy.uia.domain.Weixin
import io.zhudy.uia.helper.MongoSeqHelper
import io.zhudy.uia.repository.UserRepository
import org.bson.BSON
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

    private fun map(d: Document): User {
        var weixin: Weixin? = null
        val w = d.get("weixin", Document::class.java)
        if (w != null) {
            weixin = Weixin(
                    appid = w.getString("appid") ?: "",
                    openid = w.getString("openid") ?: "",
                    unionid = w.getString("unionid") ?: ""
            )
        }

        return User(
                id = d.getLong("_id"),
                email = d.getString("email") ?: "",
                password = d.getString("password") ?: "",
                nickname = d.getString("nickname") ?: "",
                avatar = d.getString("avatar") ?: "",
                source = UserSource.forFlag(d.getInteger("source") ?: 0),
                createdAt = d.getLong("created_at"),
                updatedAt = d.getLong("updated_at") ?: 0,

                weixin = weixin
        )
    }

    override fun save(user: User): Long {
        val doc = Document()
        val _id = mongoSeqHelper.next("user_id")
        doc.put("_id", _id)
        doc.put("email", user.email)
        doc.put("password", user.password)
        doc.put("nickname", user.nickname)
        doc.put("avatar", user.avatar)
        doc.put("source", user.source.flag)
        doc.put("created_at", user.createdAt)

        if (user.weixin != null) {
            doc.put("weixin", user.weixin)
        }
        return _id
    }

    override fun findByUid(uid: Long): User {
        val r = coll.find(eq("_id", uid)).first() ?: throw BizCodeException(BizCodes.C_2001)
        return map(r)
    }

    override fun findByEmail(email: String): User {
        val r = coll.find(eq("email", email)).first() ?: throw BizCodeException(BizCodes.C_2000)
        return map(r)
    }

    override fun findByUnionid(unionid: String): User {
        val r = coll.find(eq("weixin.unionid", unionid)).first() ?: throw BizCodeException(BizCodes.C_2002)
        return map(r)
    }

    override fun existsUnionid(unionid: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findByOpenidAndAppid(openid: String, appid: String): User {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}