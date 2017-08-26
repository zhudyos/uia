package io.zhudy.uia.web

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class RequestParamException : RuntimeException {

    val param: String

    /**
     *
     */
    constructor(param: String) : super("[$param] required") {
        this.param = param
    }

    /**
     *
     */
    constructor(param: String, message: String) : super("[$param] $message") {
        this.param = param
    }

    override fun fillInStackTrace() = this
}