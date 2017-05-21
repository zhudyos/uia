package io.zhudy.uia

/**
 * 业务错误码定义枚举.
 * @author Kevin Zou <kevinz@weghst.com>
 */
enum class BizCodes(val code: Int, val msg: String) {

    // ====================================================== //
    // 公共错误码 0 - 999                                      //
    // ====================================================== //
    /**
     * 未知错误.
     */
    C_0(0, "未知错误"),

    // ====================================================== //
    // client 错误码 1000 - 1999                               //
    // ====================================================== //
    /**
     * 未发现指定 client_id 客户端记录.
     */
    C_1000(1000, "未发现指定 client_id 客户端记录");

    override fun toString(): String {
        return "[$code] $msg"
    }
}
