package io.zhudy.uia.web.v1

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey
import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.dto.CodeAuthorizeInfo
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.web.OAuth2Exception
import io.zhudy.uia.web.queryParam
import org.slf4j.LoggerFactory

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class OAuth2AuthorizeBeforeHandler(
        private val oauth2Service: OAuth2Service,
        private val next: HttpHandler
) : HttpHandler {

    private val log = LoggerFactory.getLogger(OAuth2AuthorizeBeforeHandler::class.java)

    companion object {
        val CODE_ATTACHMENT_KEY = AttachmentKey.create(CodeAuthorizeInfo::class.java)
    }

    override fun handleRequest(exchange: HttpServerExchange) {
        val responseType = exchange.queryParam("response_type") ?: ""

        val clientId = exchange.queryParam("client_id") ?: throw OAuth2Exception(error = "invalid_client")
        val redirectUri = exchange.queryParam("redirect_uri") ?: ""
        val scope = exchange.queryParam("scope") ?: ""

        when (responseType) {
            "code" -> {
                val info = CodeAuthorizeInfo(clientId = clientId, redirectUri = redirectUri, scope = scope)
                exchange.putAttachment(CODE_ATTACHMENT_KEY, info)

                try {
                    oauth2Service.authorizeCheck(info)

                    // 调用
                    next.handleRequest(exchange)
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