package io.zhudy.uia.web.v1

import io.zhudy.uia.JacksonUtils
import io.zhudy.uia.UserContext
import io.zhudy.uia.service.UserService
import io.zhudy.uia.web.Constants
import org.springframework.stereotype.Controller
import spark.Request
import spark.Response

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class UserResource(
        val userService: UserService
) {

    /**
     * 加载当前认证用户信息.
     */
    fun loadCurUserInfo(req: Request, resp: Response): Any {
        val fields = req.queryParams("fields") ?: ""

        val userContext = req.attribute<UserContext>(Constants.REQUEST_USER_CONTEXT)
        val user = userService.findByUid(userContext.uid)

        resp.header("content-type", "application/json; charset=UTF-8")
        return JacksonUtils.writeValueAsString(user, fields)
    }
}