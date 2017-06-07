package io.zhudy.uia.service

import io.zhudy.uia.dto.OAuthToken
import io.zhudy.uia.dto.PasswordAuthInfo
import reactor.core.publisher.Mono

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
interface OAuth2Service {

    /**
     * 创建用户 Token.
     */
    fun newOAuthToken(uid: Long): Mono<OAuthToken>

    /**
     * 简化模式 (implicit grant type).
     */
    fun authorizeImplicit()

    /**
     * 密码模式 (Resource Owner Password Credentials Grant).
     */
    fun authorizePassword(pai: PasswordAuthInfo): Mono<OAuthToken>
}