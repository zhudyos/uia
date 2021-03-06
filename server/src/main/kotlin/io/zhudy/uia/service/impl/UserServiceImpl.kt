package io.zhudy.uia.service.impl

import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.domain.User
import io.zhudy.uia.repository.UserRepository
import io.zhudy.uia.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Service
class UserServiceImpl(
        val userRepository: UserRepository,
        val passwordEncoder: PasswordEncoder
) : UserService {

    override fun save(user: User): Long {
        return userRepository.save(user)
    }

    override fun authenticate(username: String, password: String): User {
        val user = userRepository.findByEmail(username)
        if (!passwordEncoder.matches(password, user.password)) {
            throw BizCodeException(BizCodes.C_2011)
        }
        return user
    }

    override fun findByUid(uid: Long): User {
        return userRepository.findByUid(uid)
    }

    override fun findByUnionid(unionid: String): User {
        return userRepository.findByUnionid(unionid)
    }
}