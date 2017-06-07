package io.zhudy.uia.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.codec.*
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.server.HandlerStrategies
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.result.view.ViewResolver
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier


/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class SimpleHandlerStrategiesBuilder(objectMapper: ObjectMapper) : HandlerStrategies.Builder {

    val codecConfigurer = ServerCodecConfigurer.create()!!
    val viewResolvers = ArrayList<ViewResolver>()
    var localeResolver: Function<ServerRequest, Optional<Locale>>? = null

    init {
        codecConfigurer.registerDefaults(false)
        codecConfigurer.customCodecs().reader(FormHttpMessageReader())

        codecConfigurer.customCodecs().encoder(Jackson2JsonEncoder(objectMapper))
        codecConfigurer.customCodecs().decoder(Jackson2JsonDecoder(objectMapper))

        // 文件上传
        // val partReader = SynchronossPartHttpMessageReader()
        // codecConfigurer.customCodecs().reader(partReader)
        // codecConfigurer.customCodecs().reader(MultipartHttpMessageReader(partReader))
        // codecConfigurer.customCodecs().writer(MultipartHttpMessageWriter())
    }

    override fun viewResolver(viewResolver: ViewResolver): HandlerStrategies.Builder {
        viewResolvers.add(viewResolver)
        return this
    }

    override fun defaultCodecs(consumer: Consumer<ServerCodecConfigurer.ServerDefaultCodecsConfigurer>): HandlerStrategies.Builder {
        consumer.accept(this.codecConfigurer.defaultCodecs())
        return this
    }

    override fun customCodecs(consumer: Consumer<CodecConfigurer.CustomCodecsConfigurer>): HandlerStrategies.Builder {
        consumer.accept(this.codecConfigurer.customCodecs())
        return this
    }

    override fun localeResolver(localeResolver: Function<ServerRequest, Optional<Locale>>): HandlerStrategies.Builder {
        this.localeResolver = localeResolver
        return this
    }

    override fun build(): HandlerStrategies {
        return SimpleHandlerStrategies(codecConfigurer.readers, codecConfigurer.writers, viewResolvers, localeResolver)
    }

    class SimpleHandlerStrategies(
            val messageReaders: List<HttpMessageReader<*>>,
            val messageWriters: List<HttpMessageWriter<*>>,
            val viewResolvers: List<ViewResolver>,
            val localeResolver: Function<ServerRequest, Optional<Locale>>?
    ) : HandlerStrategies {

        override fun viewResolvers() = Supplier {
            viewResolvers.stream()
        }

        override fun messageReaders() = Supplier {
            messageReaders.stream()
        }

        override fun messageWriters() = Supplier {
            messageWriters.stream()
        }

        override fun localeResolver() = Supplier {
            localeResolver
        }
    }
}
