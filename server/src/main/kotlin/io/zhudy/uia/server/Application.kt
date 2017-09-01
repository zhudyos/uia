package io.zhudy.uia.server

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.zhudy.uia.UiaProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.embedded.undertow.UndertowWebServer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties(UiaProperties::class, ServerProperties::class)
@ComponentScan("io.zhudy.uia")
class Application {

    @Bean
    fun propertyConfigurer() = PropertySourcesPlaceholderConfigurer().apply {
        setPlaceholderPrefix("%{")
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    fun undertowWebServer(serverProperties: ServerProperties, handler: HttpHandler): UndertowWebServer {
        val address = serverProperties.address?.hostName ?: "0.0.0.0"
        val builder = Undertow.builder().addHttpListener(serverProperties.port, address, handler)
        if (serverProperties.undertow.workerThreads != null) {
            builder.setWorkerThreads(serverProperties.undertow.workerThreads)
        }
        return UndertowWebServer(builder, true)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }
}

fun main(args: Array<String>) {
    // SpringApplication.run(Application::class.java, *args)
    println((500 * 23) + (1000 * 23) + (2000 * 67))
}

