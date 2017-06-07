package io.zhudy.uia.web

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class OAuth2Exception : RuntimeException {


    val error: String
    val status: Int
    val description: String
    val state: String

    /**
     *
     */
    constructor(error: String, status: Int = 0, description: String = "", state: String = "")
            : super("[$error] description:$description, state:$state") {
        this.error = error
        this.status = status
        this.description = description
        this.state = state
    }
}