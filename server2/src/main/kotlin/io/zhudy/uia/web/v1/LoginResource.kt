package io.zhudy.uia.web.v1

import io.zhudy.uia.helper.JedisHelper
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Controller
import spark.Request
import spark.Response
import spark.utils.IOUtils

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class LoginResource(
        val jedisHelper: JedisHelper
) {

    @Value("classpath:/templates/login.html")
    lateinit var loginHtml: Resource

    /**
     * login html.
     */
    fun loginView(req: Request, resp: Response): Any {
        return IOUtils.toString(loginHtml.inputStream)
    }

    /**
     *
     */
    fun login(req: Request, resp: Response): Any {
        return "success"
    }
}
