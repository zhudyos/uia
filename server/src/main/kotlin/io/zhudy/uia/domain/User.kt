package io.zhudy.uia.domain

import com.fasterxml.jackson.annotation.JsonFilter

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */

/**
 * 用户实体.
 *
 * @property id 用户ID
 * @property email 用户邮箱
 * @property password 用户密码
 * @property nickname 用户昵称
 * @property avatar 用户头像
 * @property source 用户来源
 * @property createdAt 用户创建时间
 * @property updatedAt 用户修改时间
 * @property weixin 用户微信信息
 */
@JsonFilter("filterOutAllExcept")
data class User(
        val id: Long = 0,
        val email: String = "",
        val password: String = "",
        val nickname: String = "",
        val avatar: String = "",
        val source: UserSource = UserSource.LOCAL,
        val createdAt: Long = System.currentTimeMillis(),
        val updatedAt: Long = 0L,
        val weixin: Weixin? = null
)

/**
 * @property openid 用户微信 openid
 * @property unionid 用户微信 unionid
 */
data class Weixin(
        val appid: String = "",
        val openid: String = "",
        val unionid: String = ""
)

/**
 *
 */
enum class UserSource(val flag: Int) {
    LOCAL(0),
    WEIXIN(1);

    companion object {
        /**
         *
         */
        fun forFlag(flag: Int): UserSource {
            return UserSource.values().first { it.flag == flag }
        }
    }
}
