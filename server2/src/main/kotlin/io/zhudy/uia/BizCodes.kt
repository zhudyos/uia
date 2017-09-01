package io.zhudy.uia

/**
 * 业务错误码定义枚举.
 *
 * @author Kevin Zou <kevinz@weghst.com>
 */
enum class BizCodes(override val code: Int, override val msg: String) : BizCode {

    // ====================================================== //
    // 公共错误码 0 - 999                                     //
    // ====================================================== //
    /**
     * 未知错误.
     */
    C_0(0, "未知错误"),
    C_500(500, "服务器内部错误"),
    /**
     * HTTP Request 参数错误.
     */
    C_999(999, "HTTP Request 参数错误"),

    // ====================================================== //
    // client 错误码 1000 - 1999                              //
    // ====================================================== //
    /**
     * 未发现指定 client_id 客户端记录.
     */
    C_1000(1000, "未发现指定 client_id 客户端记录"),
    C_1001(1001, "client_secret 不匹配"),
    C_1002(1002, "redirect_uri 不在白名单内"),

    // ====================================================== //
    // user 错误码 2000 - 2999                                //
    // ====================================================== //

    /**
     * 未发现指定 email 的用户.
     */
    C_2000(2000, "未发现指定 email 的用户"),
    C_2001(2001, "未发现指定 uid 的用户"),
    C_2011(2011, "password 不匹配"),

    /**
     * oauth2 错误码.
     */
    C_3000(3000, "非法的 redirect_uri"),
    C_3001(3001, "refresh_token 过期/或不存在"),
    C_3002(3002, "refresh_token 的 client_id 非法"),
    C_3003(3003, "authorization_code 的 client_id 非法"),
    C_3004(3004, "code 不存在或者已使用")
    //
    ;

    override fun toString(): String {
        return "[$code] $msg"
    }
}