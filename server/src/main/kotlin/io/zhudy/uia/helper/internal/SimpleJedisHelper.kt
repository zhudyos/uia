package io.zhudy.uia.helper.internal

import io.zhudy.uia.helper.JedisHelper
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import redis.clients.jedis.Jedis
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * 基本 [JedisHelper] 实现.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@ConfigurationProperties("jedis")
@ConditionalOnProperty(name = arrayOf("jedis.helper"), havingValue = "simple", matchIfMissing = true)
@Component
class SimpleJedisHelper : JedisHelper {

    var uri: String = ""
    lateinit var redisClient: Jedis

    @PostConstruct
    fun start() {
        redisClient = Jedis(uri)
    }

    @PreDestroy
    fun stop() {
        redisClient.close()
    }

    /**
     * 返回 jedis 对象.
     */
    override val jedis: Jedis
        get() {
            return redisClient
        }
}