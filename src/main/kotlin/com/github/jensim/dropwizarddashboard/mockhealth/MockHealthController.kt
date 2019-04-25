package com.github.jensim.dropwizarddashboard.mockhealth

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jensim.dropwizarddashboard.host.ErrorDescription
import com.github.jensim.dropwizarddashboard.host.HealthCheck
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.slf4j.LoggerFactory

@Controller("/mock-health")
class MockHealthController {

    companion object {

        private val om = ObjectMapper()
        private val healthy = HealthCheck("im ok, really!", true)
        private val unhealthy = HealthCheck("Im not fealing very well", false)
        private val err = HealthCheck("I think  im feeling sick", false, ErrorDescription("Im the sickness! !..!,", listOf("first", "second")))
        private val keys = listOf("Database", "Internet", "FileSystem")

        enum class RESPONSE(val statusCode: HttpStatus, val data: Any) {
            INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, toMap(healthy, unhealthy, err)),
            GOOD(HttpStatus.OK, toMap(healthy, healthy, healthy)),
            BAD(HttpStatus.OK, toMap(healthy, unhealthy, healthy)),
            NOT_FOUND(HttpStatus.NOT_FOUND, "404 Not found")
        }

        private fun toMap(vararg values: HealthCheck): String {
            val map = values
                    .mapIndexed { i, t -> keys[i] to t }
                    .toMap()
            return om.writeValueAsString(map)
        }
    }

    private val log = LoggerFactory.getLogger(javaClass)

    @Get("/500")
    fun get500(): HttpResponse<out Any> {
        log.debug("Someone asked for the \"500\" healthcheck")
        return HttpResponseFactory.INSTANCE.status(RESPONSE.INTERNAL_SERVER_ERROR.statusCode, RESPONSE.INTERNAL_SERVER_ERROR.data)
    }

    @Get("/good")
    fun getGood(): HttpResponse<out Any> {
        log.debug("Someone asked for the \"good\" healthcheck")
        return HttpResponseFactory.INSTANCE.status(RESPONSE.GOOD.statusCode, RESPONSE.GOOD.data)
    }

    @Get("/bad")
    fun getBad(): HttpResponse<out Any> {
        log.debug("Someone asked for the \"bad\" healthcheck")
        return HttpResponseFactory.INSTANCE.status(RESPONSE.GOOD.statusCode, RESPONSE.BAD.data)
    }

    @Get("/404")
    fun get404(): HttpResponse<out Any> {
        log.debug("Someone asked for the \"404\" healthcheck")
        return HttpResponseFactory.INSTANCE.status(RESPONSE.NOT_FOUND.statusCode, RESPONSE.NOT_FOUND.data)
    }
}
