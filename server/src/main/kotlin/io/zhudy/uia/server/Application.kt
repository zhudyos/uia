package io.zhudy.uia.server

import io.zhudy.uia.UiaProperties
import io.zhudy.uia.token.SimpleTokenGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties(UiaProperties::class)
@ComponentScan(basePackages = arrayOf("io.zhudy.uia"))
class Application {

    @Bean
    fun propertyConfigurer() = PropertySourcesPlaceholderConfigurer().apply {
        setPlaceholderPrefix("%{")
    }

    // =========================================================== //
    // Redis 配置                                                  //
    // =========================================================== //
    @Bean
    fun tokenGenerator() = SimpleTokenGenerator()

    @Bean
    fun redisConnFactory() = LettuceConnectionFactory()

    @Bean
    fun reactiveRedisTemplate(recf: ReactiveRedisConnectionFactory) = {
        val serializer = StringRedisSerializer()
        val sc = RedisSerializationContext.newSerializationContext<String, String>().key(serializer).value(serializer)
                .hashKey(serializer).hashValue(serializer).build()
        ReactiveRedisTemplate<String, String>(recf, sc)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
