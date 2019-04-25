package com.github.jensim.dropwizarddashboard.host

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jensim.dropwizarddashboard.mockhealth.MockHealthController.Companion.RESPONSE
import com.github.jensim.dropwizarddashboard.mockhealth.MockHealthController.Companion.RESPONSE.BAD
import com.github.jensim.dropwizarddashboard.mockhealth.MockHealthController.Companion.RESPONSE.GOOD
import com.github.jensim.dropwizarddashboard.mockhealth.MockHealthController.Companion.RESPONSE.INTERNAL_SERVER_ERROR
import com.github.jensim.dropwizarddashboard.mockhealth.MockHealthController.Companion.RESPONSE.NOT_FOUND
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockserver.client.MockServerClient
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import kotlin.test.assertTrue

class HealthCheckClientTest {

    companion object {
        private val port = 8999
        private val mockServer: MockServerClient = startClientAndServer(port)

        @AfterAll
        fun tearDown() {
            mockServer.stop()
        }
    }

    enum class TestCase(val mockResponse: RESPONSE, private val isUnhealthy: Boolean?) {
        CASE_500(INTERNAL_SERVER_ERROR, true),
        CASE_GOOD(GOOD, false),
        CASE_BAD(BAD, true),
        CASE_404(NOT_FOUND, null);

        fun validate(checks: HostHealthChecks?): Boolean = checks?.isUnhealthy() == isUnhealthy
    }

    private val endpoint = "healthcheck"
    private val url get() = "http://localhost:$port/$endpoint"
    private val host get() = Host.fromUrl(url)
    private val healthCheckClient = HealthCheckClient(ObjectMapper())

    @BeforeEach
    internal fun setUp() {
        println("Getting $url")
    }

    @AfterEach
    internal fun reset() {
        mockServer.reset()
    }

    @ParameterizedTest
    @EnumSource(value = TestCase::class)
    internal fun `test all types`(tc: TestCase) {
        println("Testing ${tc.name} $url")
        assertTrue(mockServer.isRunning)
        mockServer.`when`(HttpRequest.request("/$endpoint"))
                .respond(HttpResponse.response(tc.mockResponse.data as String)
                        .withStatusCode(tc.mockResponse.statusCode.code))

        val checks: HostHealthChecks? = healthCheckClient.check(host)

        assertTrue(tc.validate(checks))
    }
}
