package io.zhudy.uia.server

import org.springframework.context.support.beans
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
fun beans() = beans {

    /**
     *
     */
    bean<PasswordEncoder> {
        NoOpPasswordEncoder.getInstance()
    }

}