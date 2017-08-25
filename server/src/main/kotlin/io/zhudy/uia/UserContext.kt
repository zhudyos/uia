package io.zhudy.uia

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
object UserContext {

    val uid: Long
        get() {
            return UserContextSetter.uid.get()
        }
}
