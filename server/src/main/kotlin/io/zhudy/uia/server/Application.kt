package io.zhudy.uia.server

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import io.zhudy.uia.Config
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import spark.Spark


/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("io.zhudy.uia")
class Application {

    @Bean
    fun propertyConfigurer() = PropertySourcesPlaceholderConfigurer().apply {
        setPlaceholderPrefix("%{")
    }

    @Bean
    @ConfigurationProperties(prefix = "app")
    fun config(): Config {
        return Config
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }

    @Bean
    fun uiaMongodbDatabase(mongoClient: MongoClient): MongoDatabase {
        return mongoClient.getDatabase("uia")
    }

    @Bean
    fun stringRedisTemplate(redisConnectionFactory: RedisConnectionFactory): StringRedisTemplate {
        return StringRedisTemplate(redisConnectionFactory)
    }
}

fun main(args: Array<String>) {
    // ===========================================================================
    SpringApplication.run(Application::class.java, *args).addApplicationListener {
        if (it is ContextClosedEvent) {
            Spark.stop()
            Spark.awaitInitialization()
        }
    }
}

