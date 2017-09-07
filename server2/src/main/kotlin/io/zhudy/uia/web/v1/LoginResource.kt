package io.zhudy.uia.web.v1

import io.zhudy.uia.BizCodeException
import io.zhudy.uia.UiaProperties
import io.zhudy.uia.domain.User
import io.zhudy.uia.service.UserService
import io.zhudy.uia.web.RequestParamException
import org.springframework.stereotype.Controller
import spark.Request
import spark.Response
import java.net.URLEncoder

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class LoginResource(
        val userService: UserService,
        val ssoAuthentication: SsoAuthentication
) {

    /**
     *
     */
    fun login(req: Request, resp: Response): Any {
        val username = req.queryParams("username") ?: throw RequestParamException("username")
        val password = req.queryParams("password") ?: throw RequestParamException("password")
        val redirectUri = req.queryParams("redirect_uri") ?: ""

        val user: User
        try {
            user = userService.authenticate(username, password)
        } catch (e: BizCodeException) {
            var location = "${UiaProperties.loginHtmlUri}?username=$username"
            if (redirectUri.isNotEmpty()) {
                location += "&redirect_uri=${URLEncoder.encode(redirectUri, "UTF-8")}"
            }
            location += "&err_code=${e.bizCode.code}"
            resp.redirect(location)
            return ""
        }

        ssoAuthentication.complete(req, resp, user)
        return ""
    }
}
