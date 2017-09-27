package io.zhudy.uia.utils

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class ThirdException : RuntimeException {

    val error: String

    /**
     *
     */
    constructor(error: String) : super(error) {
        this.error = error
    }

    /**
     *
     */
    constructor(e: Throwable) : super(e) {
        error = ""
    }
}