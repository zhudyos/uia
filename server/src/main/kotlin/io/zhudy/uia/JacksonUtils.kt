package io.zhudy.uia

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
object JacksonUtils {

    val objectMapper = ObjectMapper()

    init {
        objectMapper.propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
        objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        objectMapper.registerKotlinModule()
    }
}