package io.zhudy.uia.repository

import io.zhudy.uia.domain.User

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
interface UserRepository {

    /**
     *
     */
    fun save(user: User): Long

    /**
     *
     */
    fun findByUid(uid: Long): User

    /**
     *
     */
    fun findByEmail(email: String): User
}