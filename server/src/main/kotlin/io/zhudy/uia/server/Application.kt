package io.zhudy.uia.server

import io.zhudy.uia.UiaProperties
import io.zhudy.uia.service.OAuth2Exception
import io.zhudy.uia.token.SimpleTokenGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import reactor.core.publisher.Mono


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
//    @Bean
//    fun jedisConnFactory() = JedisConnectionFactory(JedisShardInfo(UiaProperties.redis.host,
//            UiaProperties.redis.port))
//    @Bean
//    fun strRedisTemplate(jedisConnFactory: JedisConnectionFactory) = StringRedisTemplate(jedisConnFactory)

    @Bean
    fun tokenGenerator() = SimpleTokenGenerator()
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
