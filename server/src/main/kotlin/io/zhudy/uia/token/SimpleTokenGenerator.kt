package io.zhudy.uia.token

import java.util.*

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class SimpleTokenGenerator : TokenGenerator {

    override fun generate(vararg args: Any): String {
        return Base64.getEncoder().encodeToString(args.joinToString(separator = ",").toByteArray())
    }

}