package io.zhudy.uia.domain

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
data class User(
        val id: Long,
        val email: String?,
        val password: String?,
        val createdTime: Long
)