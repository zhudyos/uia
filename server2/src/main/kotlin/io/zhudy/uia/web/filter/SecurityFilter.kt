package io.zhudy.uia.web.filter

import io.zhudy.uia.UiaProperties
import io.zhudy.uia.web.v1.SsoAuthentication
import org.springframework.stereotype.Component
import spark.Filter
import spark.Request
import spark.Response
import spark.Spark.halt
import java.net.URLEncoder

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Component
class SecurityFilter(private val ssoAuthentication: SsoAuthentication) : Filter {

    override fun handle(req: Request, resp: Response) {
        if (!ssoAuthentication.validate(req, resp)) {
            var redirectUri = req.url()
            if (!req.queryString().isNullOrEmpty()) {
                redirectUri = "$redirectUri?${req.queryString()}"
            }
            redirectUri = URLEncoder.encode(redirectUri, Charsets.UTF_8.name())

            resp.redirect("${UiaProperties.loginHtmlUri}?redirect_uri=$redirectUri")
            halt(302)
        }
    }

}
