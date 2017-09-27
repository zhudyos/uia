package io.zhudy.uia.service

import io.zhudy.uia.domain.User

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
interface UserService {

    /**
     *
     */
    fun save(user: User): Long

    /**
     *
     */
    fun authenticate(username: String, password: String): User

    /**
     *
     */
    fun findByUid(uid: Long): User

    /**
     * 根据微信 unionid 查询用户信息.
     */
    fun findByUnionid(unionid: String): User

}