package io.zhudy.uia.repository.impl

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
//@Repository
//class ClientRepositoryImpl(
//        mongoClient: MongoClient,
//        val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
//        val objectMapper: ObjectMapper
//) : ClientRepository {
//
//    val coll = mongoClient.getDatabase("uia").getCollection("client")!!
//    val valueOps = reactiveRedisTemplate.opsForValue()!!
//
//    override fun findByClient(clientId: String): Mono<Client> {
//        val ckey = RedisKeys.client_repo.key(clientId)
//
//        return valueOps.get(ckey).map {
//            objectMapper.readValue(it, Client::class.java)
//        }.switchIfEmpty(Mono.from(coll.find(eq("client_id", clientId)).first()).doOnSuccess {
//            it ?: throw BizCodeException(BizCodes.C_1000)
//        }.map {
//            val client = Client(
//                    id = it.getLong("_id") ?: 0,
//                    clientId = it.getString("client_id") ?: "",
//                    clientSecret = it.getString("client_secret") ?: "",
//                    redirectUri = it.getString("redirect_uri") ?: "",
//                    scope = it.getString("scope") ?: ""
//            )
//
//            // 将 client 加入缓存
//            valueOps.set(ckey, objectMapper.writeValueAsString(client), Duration.ofDays(7))
//                    .subscribeOn(Schedulers.single())
//                    .subscribe()
//            client
//        })
//    }
//
//}