package io.zhudy.uia.service

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
interface OAuth2Service {

    /**
     * 简化模式 (implicit grant type).
     */
    fun authorizeImplicit()

    /**
     * 密码模式 (Resource Owner Password Credentials Grant).
     */
    fun authorizePassword(username: String, password: String)
}