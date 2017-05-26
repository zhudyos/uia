package io.zhudy.uia.token

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
interface TokenGenerator {

    /**
     *
     */
    fun generate(vararg args: Any): String

}