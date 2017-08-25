package io.zhudy.uia.helper

import redis.clients.jedis.Jedis

/**
 * Jedis 辅助接口定义.
 *
 * 使用方不能将 `jedisHelper.jedis` 缓存为全局变量. 应该在每个方法使用中通过 `jedisHelper.jedis` 获取 `Jedis` 实例.
 *
 * 因为当 `redis` *失效或不可用时*, 某些实现会*自动切换* `redis` 服务器, 此时你将会获取到一个*全新*的 `jedis` 实例.
 *
 * 示例:
 * ```
 * fun m() {
 *      val jedis = jedisHelper.jedis
 * }
 * ```
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
interface JedisHelper {

    /**
     * 返回当前可用 jedis 对象.
     */
    val jedis: Jedis
        get
}