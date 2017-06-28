package io.zhudy.uia.service.impl

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
//@Service
//class OAuth2ServiceImpl(
//        val clientRepository: ClientRepository,
//        val userRepository: UserRepository,
//        val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
//) : OAuth2Service {
//
//    val valueOps = reactiveRedisTemplate.opsForValue()
//    val accessTokenGen = Hashids(UiaProperties.token.accessTokenSalt, UiaProperties.token.minLength)
//    val refreshTokenGen = Hashids(UiaProperties.token.refreshTokenSalt, UiaProperties.token.minLength)
//
//    override fun newOAuthToken(uid: Long, cid: Long): Mono<OAuthToken> {
//        val time = System.currentTimeMillis()
//        val token = OAuthToken(accessToken = accessTokenGen.encode(uid, cid, time),
//                refreshToken = refreshTokenGen.encode(uid, cid, time))
//
//        // 添加 redis 缓存
//        val e1 = Duration.ofSeconds(UiaProperties.token.accessTokenExpiresIn)
//        val e2 = Duration.ofSeconds(UiaProperties.token.refreshTokenExpiresIn)
//        Flux.merge(valueOps.set(RedisKeys.token.key(uid), token.accessToken, e1),
//                valueOps.set(RedisKeys.rtoken.key(uid), token.refreshToken, e2)
//        ).subscribeOn(Schedulers.single()).subscribe()
//
//        return token.toMono()
//    }
//
//    override fun authorizeImplicit() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun authorizePassword(pai: PasswordAuthInfo) = clientRepository.findByClient(pai.clientId).flatMap { client ->
//        if (!validateClientSecret(pai.clientSecret, client.clientSecret)) {
//            throw BizCodeException(BizCodes.C_1001)
//        }
//
//        userRepository.findByEmail(pai.username).flatMap { user ->
//            if (!validateUserPwd(pai.password, user.password)) {
//                throw BizCodeException(BizCodes.C_2011)
//            }
//
//            // 返回 Token
//            newOAuthToken(user.id, client.id)
//        }
//    }
//
//    private fun validateClientSecret(clientSecret1: String, clientSecret2: String): Boolean {
//        return clientSecret1 == clientSecret2
//    }
//
//    private fun validateUserPwd(pwd1: String, pwd2: String): Boolean {
//        return pwd1 == pwd2
//    }
//}
