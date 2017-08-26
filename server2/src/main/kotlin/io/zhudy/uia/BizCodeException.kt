package io.zhudy.uia

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
class BizCodeException : RuntimeException {

    val bizCode: BizCode

    /**
     *
     */
    constructor(bizCode: BizCode) : super(bizCode.toString()) {
        this.bizCode = bizCode
    }

    /**
     *
     */
    constructor(bizCode: BizCode, e: Exception) : super(bizCode.toString(), e) {
        this.bizCode = bizCode
    }

    override fun fillInStackTrace() = this
}
