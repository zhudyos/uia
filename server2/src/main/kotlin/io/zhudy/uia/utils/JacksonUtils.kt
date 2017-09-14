package io.zhudy.uia.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.InputStream
import kotlin.reflect.KClass

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
object JacksonUtils {

    val objectMapper = ObjectMapper()

    init {
        objectMapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
        objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        objectMapper.registerKotlinModule()
    }

    /**
     *
     */
    fun <T : Any> readValue(content: String, valueType: KClass<T>) = objectMapper.readValue(content, valueType.java)

    /**
     *
     */
    fun <T : Any> readValue(content: ByteArray, valueType: KClass<T>) = objectMapper.readValue(content, valueType.java)

    /**
     *
     */
    fun <T : Any> readValue(content: InputStream, valueType: KClass<T>) = objectMapper.readValue(content, valueType.java)

    /**
     *
     */
    fun writeValueAsBytes(o: Any) = objectMapper.writeValueAsBytes(o)

    /**
     * @param fields filterOutAllExcept
     */
    fun writeValueAsBytes(o: Any, fields: String): ByteArray {
        if (fields.isEmpty()) {
            return writeValueAsBytes(o)
        }
        return filterOutAllExceptWriter(fields).writeValueAsBytes(o)
    }

    /**
     *
     */
    fun writeValueAsString(o: Any) = objectMapper.writeValueAsString(o)

    /**
     * @param fields filterOutAllExcept
     */
    fun writeValueAsString(o: Any, fields: String): String {
        if (fields.isEmpty()) {
            return writeValueAsString(o)
        }
        return filterOutAllExceptWriter(fields).writeValueAsString(o)
    }

    private fun filterOutAllExceptWriter(fields: String): ObjectWriter {
        val filter = SimpleBeanPropertyFilter.filterOutAllExcept(fields.split(",").toSet())
        val provider = SimpleFilterProvider()
        provider.addFilter("filterOutAllExcept", filter)
        return objectMapper.writer(provider)
    }
}