package io.zhudy.uia.web

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class OAuth2Exception : RuntimeException {


    val error: String
    val description: String
    val status: Int
    val state: String

    /**
     *
     */
    constructor(error: String, description: String = "", status: Int = 400, state: String = "")
            : super("[$error] description:$description, state:$state") {
        this.error = error
        this.description = description
        this.status = status
        this.state = state
    }
}