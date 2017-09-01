package io.zhudy.uia.service

import io.zhudy.uia.UserContext
import io.zhudy.uia.domain.Client
import io.zhudy.uia.dto.*

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
interface OAuth2Service {

    /**
     *
     */
    fun authorizeCheck(info: CodeAuthorizeInfo): Client

    /**
     *
     */
    fun authorizeCode(info: CodeAuthorizeInfo, userContext: UserContext): Pair<String, String>

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
    fun grantCode(info: AuthorizationCodeGrantInfo): OAuthToken

    /**
     * 密码模式 (Resource Owner Password Credentials Grant).
     */
    fun grantPassword(info: PasswordGrantInfo): OAuthToken

    /**
     * 刷新令牌(refresh token).
     */
    fun grantRefreshToken(info: RefreshTokenGrantInfo): OAuthToken
}