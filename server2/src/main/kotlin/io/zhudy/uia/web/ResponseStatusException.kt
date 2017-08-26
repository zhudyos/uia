package io.zhudy.uia.web

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class ResponseStatusException : RuntimeException {

    val status: Int
    var reason: String = ""

    /**
     *
     */
    constructor(status: Int) : super("http status [$status]") {
        this.status = status
    }

    /**
     *
     */
    constructor(status: Int, reason: String) : super(reason) {
        this.status = status
        this.reason = reason
    }

    override fun fillInStackTrace() = this
}