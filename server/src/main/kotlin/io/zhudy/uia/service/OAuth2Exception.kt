package io.zhudy.uia.service

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class OAuth2Exception : RuntimeException {

    companion object {
        val UNABLE_TO_PARSE_FORM_DATA = "ERR12000"
        val UNSUPPORTED_GRANT_TYPE = "ERR12001"
        val MISSING_AUTHORIZATION_HEADER = "ERR12002"
        val INVALID_AUTHORIZATION_HEADER = "ERR12003"
        val INVALID_BASIC_CREDENTIALS = "ERR12004"
        val JSON_PROCESSING_EXCEPTION = "ERR12005"
        val CLIENT_NOT_FOUND = "ERR12014"
        val UNAUTHORIZED_CLIENT = "ERR12007"
        val INVALID_AUTHORIZATION_CODE = "ERR12008"
        val GENERIC_EXCEPTION = "ERR10014"
        val RUNTIME_EXCEPTION = "ERR10010"
        val USERNAME_REQUIRED = "ERR12022"
        val PASSWORD_REQUIRED = "ERR12023"
        val INCORRECT_PASSWORD = "ERR12016"
        val NOT_TRUSTED_CLIENT = "ERR12024"
        val MISSING_REDIRECT_URI = "ERR12025"
        val MISMATCH_REDIRECT_URI = "ERR12026"
        val MISMATCH_SCOPE = "ERR12027"
        val MISMATCH_CLIENT_ID = "ERR12028"
        val REFRESH_TOKEN_NOT_FOUND = "ERR12029"
    }

    val code: String

    /**
     *
     */
    constructor(code: String) : super(code) {
        this.code = code
    }

    override fun fillInStackTrace() = this
}