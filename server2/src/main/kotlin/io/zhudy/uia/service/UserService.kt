package io.zhudy.uia.service

import io.zhudy.uia.domain.User

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
interface UserService {

    /**
     *
     */
    fun authenticate(username: String, password: String): User

    /**
     *
     */
    fun findByUid(uid: Long): User

}