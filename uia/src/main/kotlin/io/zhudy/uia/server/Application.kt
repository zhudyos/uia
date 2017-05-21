package io.zhudy.uia.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.reactive.config.EnableWebFlux


/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@SpringBootApplication
@EnableWebFlux
class Application {

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
