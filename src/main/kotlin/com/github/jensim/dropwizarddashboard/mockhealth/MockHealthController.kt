package com.github.jensim.dropwizarddashboard.mockhealth

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/mock-health")
class MockHealthController{

    private val response500 = MockHealthController::class.java.getResource("/mock-healthchecks/health-check-1error.json").readText()
    private val response200good = MockHealthController::class.java.getResource("/mock-healthchecks/health-check-happy.json").readText()
    private val response200bad = MockHealthController::class.java.getResource("/mock-healthchecks/health-check-3sad.json").readText()
    private val response404 = MockHealthController::class.java.getResource("/mock-healthchecks/").readText()


    @Get("/500") fun get500(): HttpResponse<out Any> = HttpResponseFactory.INSTANCE.status(INTERNAL_SERVER_ERROR, response500)
    @Get("/good") fun getGood(): HttpResponse<out Any> = HttpResponseFactory.INSTANCE.status(INTERNAL_SERVER_ERROR, response200good)
    @Get("/bad") fun getBad(): HttpResponse<out Any> = HttpResponseFactory.INSTANCE.status(INTERNAL_SERVER_ERROR, response200bad)
    @Get("/404") fun get404(): HttpResponse<out Any> = HttpResponseFactory.INSTANCE.status(INTERNAL_SERVER_ERROR, response404)

}
