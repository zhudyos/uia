package io.zhudy.uia.server

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.zhudy.uia.UiaProperties
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


/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties(UiaProperties::class)
@ComponentScan(basePackages = arrayOf("io.zhudy.uia"))
class Application : ApplicationListener<ApplicationEvent> {

    @Bean
    fun propertyConfigurer() = PropertySourcesPlaceholderConfigurer().apply {
        setPlaceholderPrefix("%{")
    }

    @Bean
    fun undertow(router: HttpHandler) = Undertow.builder()
            .addHttpListener(8080, "0.0.0.0", router)
            .build()!!


    override fun onApplicationEvent(event: ApplicationEvent) {
        if (event is ApplicationReadyEvent) {
            val undertow = event.applicationContext.getBean(Undertow::class.java)
            undertow.start()
            println("start.........................")
        } else if (event is ContextClosedEvent) {
            val undertow = event.applicationContext.getBean(Undertow::class.java)
            undertow.stop()
            println("stop.........................")
        }
    }

    // =========================================================== //
    // Redis 配置                                                  //
    // =========================================================== //
//    @Bean
//    fun redisConnFactory() = LettuceConnectionFactory()
//
//    @Bean
//    fun reactiveRedisTemplate(recf: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, String> {
//        val serializer = StringRedisSerializer()
//        val sc = RedisSerializationContext.newSerializationContext<String, String>()
//                .key(serializer).value(serializer)
//                .hashKey(serializer).hashValue(serializer).build()
//        return ReactiveRedisTemplate<String, String>(recf, sc)
//    }


}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
