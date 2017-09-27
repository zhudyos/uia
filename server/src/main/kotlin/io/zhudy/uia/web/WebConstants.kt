package io.zhudy.uia.web

import io.zhudy.uia.dto.CodeAuthorizeInfo

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
object WebConstants {

    val REQUEST_USER_CONTEXT = "user_context"
    val REQUEST_CODE_AUTHORIZE_INFO_KEY = CodeAuthorizeInfo::class.qualifiedName
    val REQUEST_AUTH_CLIENT = "auth_client"
    val COOKIE_AUTH_ID = "auth_id"

}