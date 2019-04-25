package com.github.jensim.dropwizarddashboard.json

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.jackson.JacksonConfiguration
import io.micronaut.jackson.ObjectMapperFactory

@Factory
@Replaces(ObjectMapperFactory::class)
class AnObjectMapperFactory : ObjectMapperFactory() {

    override fun objectMapper(jacksonConfiguration: JacksonConfiguration?, jsonFactory: JsonFactory?): ObjectMapper {
        return super.objectMapper(jacksonConfiguration, jsonFactory).apply {
            enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
            enable(JsonParser.Feature.ALLOW_MISSING_VALUES)
            enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            enable(JsonParser.Feature.ALLOW_TRAILING_COMMA)
            enable(JsonParser.Feature.IGNORE_UNDEFINED)
            enable(JsonParser.Feature.ALLOW_COMMENTS)
            enable(JsonParser.Feature.AUTO_CLOSE_SOURCE)
        }
    }
}
