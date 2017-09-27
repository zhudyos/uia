package io.zhudy.uia.web.v1

import io.zhudy.uia.BizCodeException
import io.zhudy.uia.BizCodes
import io.zhudy.uia.Config
import io.zhudy.uia.domain.User
import io.zhudy.uia.domain.UserSource
import io.zhudy.uia.domain.Weixin
import io.zhudy.uia.service.OAuth2Service
import io.zhudy.uia.service.UserService
import io.zhudy.uia.utils.WeixinUtils
import io.zhudy.uia.web.RequestParamException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import spark.Request
import spark.Response

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Controller
class WeixinResource(
        val userService: UserService,
        val oauth2Service: OAuth2Service,
        val ssoAuthentication: SsoAuthentication
) {
    val log = LoggerFactory.getLogger(WeixinResource::class.java)

    fun handle(req: Request, resp: Response) {
        val label = req.params("label") ?: throw RequestParamException("label")
        val code = req.queryParams("code") ?: throw RequestParamException("code")

        val app = Config.weixin.apps[label]
        val appid = app?.get("app_id") ?: throw IllegalArgumentException("weixin 缺少标签")
        val appSecret = app?.get("app_secret") ?: throw IllegalArgumentException("")
        val token = WeixinUtils.getAccessToken(appid, appSecret, code)
        val profile = WeixinUtils.getProfile(token)

        log.debug("weixin profile: {}", profile)

        if (profile.unionid.isNotEmpty()) {
            var user: User
            try {
                user = userService.findByUnionid(profile.unionid)
            } catch (e: BizCodeException) {
                when (e.bizCode) {
                    BizCodes.C_2002 -> {
                        user = User(
                                source = UserSource.WEIXIN,
                                weixin = Weixin(
                                        appid = appid,
                                        openid = profile.openid,
                                        unionid = profile.unionid
                                )
                        )
                        val uid = userService.save(user)
                        log.debug("weixin save user success. uid: {}", uid)

                        user = userService.findByUid(uid)
                    }
                    else -> {
                        throw e
                    }
                }
            }

            ssoAuthentication.complete(req, resp, user)
            return
        }
        log.warn("weixin 用户没有 unionid - {}", profile)

//        ssoAuthentication.complete(req, resp)
//        val label = exchange.queryParam("label") ?: throw RequestParamException("label")
//        val code = exchange.queryParam("code") ?: throw RequestParamException("code")
//        val token = getAccessToken(code, label)
//
//        val info = getUserInfo(token)
//        exchange.sendJson(info)
    }
}