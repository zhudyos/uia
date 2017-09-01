package io.zhudy.uia.web.filter

import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.dto.CodeAuthorizeInfo
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.web.Constants
import io.zhudy.uia.web.OAuth2Exception
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import spark.Filter
import spark.Request
import spark.Response

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Component
class OAuth2CodeValidationFilter(
        private val oauth2Service: OAuth2Service
) : Filter {

    private val log = LoggerFactory.getLogger(OAuth2CodeValidationFilter::class.java)

    override fun handle(req: Request, resp: Response) {
        val responseType = req.queryParams("response_type") ?: throw OAuth2Exception(error = "unsupported_response_type")

        val clientId = req.queryParams("client_id") ?: throw OAuth2Exception(error = "invalid_client")
        val redirectUri = req.queryParams("redirect_uri") ?: ""
        val scope = req.queryParams("scope") ?: ""

        when (responseType) {
            "code" -> {
                val info = CodeAuthorizeInfo(clientId = clientId, redirectUri = redirectUri, scope = scope)

                try {
                    val client = oauth2Service.authorizeCheck(info)
                    req.attribute(Constants.REQUEST_AUTH_CLIENT, client)
                    req.attribute(Constants.REQUEST_CODE_AUTHORIZE_INFO_KEY, info)
                } catch (e: BizCodeException) {
                    when (e.bizCode) {
                        BizCodes.C_1000 -> throw OAuth2Exception(error = "invalid_client", status = 401, description = e.bizCode.msg)
                        else -> throw OAuth2Exception(error = "server_error", description = e.bizCode.msg)
                    }
                } catch (e: Exception) {
                    log.error("oauth2 authorize 校验失败", e)
                    throw OAuth2Exception(error = "server_error", description = e.message ?: "")
                }
            }
            else -> {
                throw OAuth2Exception(error = "unsupported_response_type")
            }
        }
    }
}