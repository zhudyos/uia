package io.zhudy.uia.service

import io.zhudy.uia.dto.*

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
interface OAuth2Service {

    /**
     *
     */
    fun decodeToken(token: String): TokenInfo

    /**
     *
     */
    fun decodeRefreshToken(refreshToken: String): TokenInfo

    /**
     * 创建用户 Token.
     */
    fun newOAuthToken(uid: Long, cid: Long): OAuthToken

    /**
     * 授权码模式(authorization code).
     */
    fun authorizeCode(info: AuthorizationCodeAuthInfo): OAuthToken

    /**
     * 密码模式 (Resource Owner Password Credentials Grant).
     */
    fun authorizePassword(info: PasswordAuthInfo): OAuthToken

    /**
     * 刷新令牌(refresh token).
     */
    fun refreshToken(info: RefreshTokenAuthInfo): OAuthToken
}