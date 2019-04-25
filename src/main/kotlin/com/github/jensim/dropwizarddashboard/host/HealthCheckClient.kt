package com.github.jensim.dropwizarddashboard.host

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.async.Callback
import com.mashape.unirest.http.exceptions.UnirestException
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton

@Singleton
class HealthCheckClient(private val objectMapper: ObjectMapper) {

    val typeRef = object : TypeReference<Map<String, HealthCheck>>() {}

    val allowedStatuses = setOf(200, 500)

    init {
        Unirest.setTimeouts(1000, 1000)
        Unirest.setObjectMapper(object : com.mashape.unirest.http.ObjectMapper {
            override fun writeValue(p0: Any): String = objectMapper.writeValueAsString(p0)
            override fun <T : Any?> readValue(p0: String, p1: Class<T>): T = objectMapper.readValue(p0, p1)
        })
    }

    fun check(host: Host) = Single.create<Pair<Host, HostHealthChecks?>> {
        Unirest.get(host.healthCheckUrl.toString()).asStringAsync(object : Callback<String> {
            override fun cancelled() {
                it.onError(UnirestException("Request was canceled."))
            }

            override fun completed(p0: HttpResponse<String>) {
                try {
                    if (allowedStatuses.contains(p0.status)) {
                        val body = p0.rawBody.bufferedReader().use { reader -> reader.readText() }
                        it.onSuccess(host to HostHealthChecks(objectMapper.readValue(body, typeRef)))
                    } else {
                        it.onError(RuntimeException("Host ${host.healthCheckUrl} responded with status code ${p0.status}"))
                    }
                } catch (e: Exception) {
                    it.onError(e)
                }
            }

            override fun failed(p0: UnirestException) {
                it.onError(p0)
            }
        })
    }.timeout(2, SECONDS).subscribeOn(Schedulers.io())
}
