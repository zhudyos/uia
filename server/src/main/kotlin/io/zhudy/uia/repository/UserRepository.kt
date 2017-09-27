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

    /**
     * 根据微信 unionid 查询用户信息.
     */
    fun findByUnionid(unionid: String): User

    /**
     * 判断微信 unionid 是否存在.
     */
    fun existsUnionid(unionid: String): Boolean

    /**
     * 根据微信 openid 查询用户信息.
     */
    fun findByOpenidAndAppid(openid: String, appid: String): User
}