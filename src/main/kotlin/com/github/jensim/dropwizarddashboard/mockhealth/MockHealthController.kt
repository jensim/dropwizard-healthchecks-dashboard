package com.github.jensim.dropwizarddashboard.mockhealth

import com.github.jensim.dropwizarddashboard.host.ErrorDescription
import com.github.jensim.dropwizarddashboard.host.HealthCheck
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import io.micronaut.http.HttpStatus.NOT_FOUND
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/mock-health")
class MockHealthController {

    private val healthy = HealthCheck("im ok, really!", true)
    private val unhealthy = HealthCheck("Im not fealing very well", false)
    private val err = HealthCheck("I think  im feeling sick", false, ErrorDescription("Im the sickness! !..!,", listOf("first", "second")))
    private val keys = listOf("Database", "Internet", "FileSystem")

    @Get("/500")
    fun get500(): HttpResponse<out Any> = HttpResponseFactory.INSTANCE.status(INTERNAL_SERVER_ERROR, toMap(healthy, unhealthy, err))

    @Get("/good")
    fun getGood(): HttpResponse<out Any> = HttpResponseFactory.INSTANCE.status(INTERNAL_SERVER_ERROR, toMap(healthy, healthy, healthy))

    @Get("/bad")
    fun getBad(): HttpResponse<out Any> = HttpResponseFactory.INSTANCE.status(INTERNAL_SERVER_ERROR, toMap(healthy, unhealthy, healthy))

    @Get("/404")
    fun get404(): HttpResponse<out Any> = HttpResponseFactory.INSTANCE.status(NOT_FOUND, "404 Not found")

    private fun toMap(vararg values: HealthCheck) = values
            .mapIndexed { i, t -> keys[i] to t }
            .toMap()
}
