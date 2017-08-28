package io.zhudy.uia.server

import io.zhudy.uia.UiaProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


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
    @ConfigurationProperties(prefix = "uia")
    fun uiaProperties(): UiaProperties {
        return UiaProperties
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }
}

fun main(args: Array<String>) {
    val ctx = SpringApplication.run(Application::class.java, *args)
    beans().invoke(ctx as GenericApplicationContext)
}

