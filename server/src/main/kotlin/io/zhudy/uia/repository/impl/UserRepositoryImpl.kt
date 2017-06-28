package io.zhudy.uia.repository.impl

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
//@Repository
//class UserRepositoryImpl(
//        mongoClient: MongoClient
//) : UserRepository {
//
//    val coll = mongoClient.getDatabase("uia").getCollection("user")!!
//
//    override fun findByEmail(email: String): Mono<User> {
//        val r = coll.find(eq("email", email)).first()
//        return Mono.from(r).doOnSuccess {
//            it ?: throw BizCodeException(BizCodes.C_2000)
//        }.map {
//            User(
//                    id = it.getLong("_id"),
//                    email = it.getString("email") ?: "",
//                    password = it.getString("password") ?: "",
//                    createdTime = it.getLong("created_time")
//            )
//        }
//    }
//}