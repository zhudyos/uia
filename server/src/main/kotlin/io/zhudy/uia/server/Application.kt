package io.zhudy.uia.server

import com.lambdaworks.redis.RedisClient
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.zhudy.uia.UiaProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import kotlin.reflect.full.declaredMembers


/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties(UiaProperties::class)
@ComponentScan(basePackages = arrayOf("io.zhudy.uia"))
class Application : ApplicationListener<ApplicationEvent> {

    val log = LoggerFactory.getLogger(Application::class.java)

    @Bean
    fun propertyConfigurer() = PropertySourcesPlaceholderConfigurer().apply {
        setPlaceholderPrefix("%{")
    }

    @Bean
    fun undertow(router: HttpHandler): Undertow {
        return Undertow.builder()
                .addHttpListener(8080, "0.0.0.0", router)
                .build()!!
    }

    // =========================================================== //
    // Redis 配置                                                  //
    // =========================================================== //
    @Bean
    fun redisConn(uiaProperties: UiaProperties) = RedisClient.create(uiaProperties.redisUri).connect()

    override fun onApplicationEvent(event: ApplicationEvent) {
        if (event is ApplicationReadyEvent) {
            val undertow = event.applicationContext.getBean(Undertow::class.java)
            undertow.start()
            log.info("Undertow started")
            log.info("Undertow started on port(s) " + getPorts())
        } else if (event is ContextClosedEvent) {
            val undertow = event.applicationContext.getBean(Undertow::class.java)
            undertow.stop()
            log.info("Undertow stopped")
        }
    }

    private fun getPorts(): String {
        val members = Undertow::class.declaredMembers
        return "unknown"
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
