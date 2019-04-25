package com.github.jensim.dropwizarddashboard.host

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.exceptions.UnirestException
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class HealthCheckClient(private val objectMapper: ObjectMapper) {

    private val log = LoggerFactory.getLogger(javaClass)
    val typeRef = object : TypeReference<Map<String, HealthCheck>>() {}
    val allowedStatuses = setOf(200, 500)

    init {
        Unirest.setTimeouts(1000, 1000)
    }

    fun check(host: Host): HostHealthChecks? {
        try {
            val p0 = Unirest.get(host.healthCheckUrl.toString()).asString()
            if (allowedStatuses.contains(p0.status)) {
                val body = p0.rawBody.bufferedReader().use { reader -> reader.readText() }
                return HostHealthChecks(objectMapper.readValue(body, typeRef))
            }
        } catch (e: UnirestException) {
            log.debug("BUUUH, someone added a bad URL maybe. Casue: ${e.cause?.message}")
        }
        return null
    }
}
