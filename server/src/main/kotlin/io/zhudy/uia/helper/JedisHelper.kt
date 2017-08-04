package io.zhudy.uia.helper

import redis.clients.jedis.Jedis

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
interface JedisHelper {

    /**
     *
     */
    fun getJedis(): Jedis

}