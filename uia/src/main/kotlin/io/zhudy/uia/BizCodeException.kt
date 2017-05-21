package io.zhudy.uia

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
class BizCodeException : RuntimeException {

    val bizCode: BizCodes

    /**
     *
     */
    constructor(bizCode: BizCodes) : super(bizCode.toString()) {
        this.bizCode = bizCode
    }

    /**
     *
     */
    constructor(bizCode: BizCodes, e: Exception) : super(bizCode.toString(), e) {
        this.bizCode = bizCode
    }

    override fun fillInStackTrace() = this
}
