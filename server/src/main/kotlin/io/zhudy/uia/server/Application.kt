package io.zhudy.uia.server

import com.lambdaworks.redis.RedisClient
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.zhudy.uia.UiaProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.embedded.undertow.UndertowWebServer
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer


/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties(UiaProperties::class, ServerProperties::class)
@ComponentScan(basePackages = arrayOf("io.zhudy.uia"))
class Application : ApplicationListener<ApplicationEvent> {

    @Bean
    fun propertyConfigurer() = PropertySourcesPlaceholderConfigurer().apply {
        setPlaceholderPrefix("%{")
    }

    @Bean
    fun undertowWebServer(serverProperties: ServerProperties, handler: HttpHandler): UndertowWebServer {
        val address = serverProperties.address?.hostName ?: "0.0.0.0"
        val builder = Undertow.builder().addHttpListener(serverProperties.port, address, handler)
        if (serverProperties.undertow.workerThreads != null) {
            builder.setWorkerThreads(serverProperties.undertow.workerThreads)
        }
        return UndertowWebServer(builder, true)
    }

    // =========================================================== //
    // Redis 配置                                                  //
    // =========================================================== //
    @Bean
    fun redisConn(uiaProperties: UiaProperties) = RedisClient.create(uiaProperties.redisUri).connect()

    override fun onApplicationEvent(event: ApplicationEvent) {
        if (event is ApplicationReadyEvent) {
            val webServer = event.applicationContext.getBean(UndertowWebServer::class.java)
            webServer.start()
        } else if (event is ContextClosedEvent) {
            val webServer = event.applicationContext.getBean(UndertowWebServer::class.java)
            webServer.stop()
        }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
