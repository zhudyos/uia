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
    fun findByEmail(email: String): User

}