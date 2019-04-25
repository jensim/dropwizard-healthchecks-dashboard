package com.github.jensim.dropwizarddashboard.host

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jensim.dropwizarddashboard.mockhealth.MockHealthController.Companion.RESPONSE.GOOD
import com.github.jensim.dropwizarddashboard.mockhealth.MockHealthController.Companion.RESPONSE.INTERNAL_SERVER_ERROR
import com.github.jensim.dropwizarddashboard.mockhealth.MockHealthController.Companion.RESPONSE.NOT_FOUND
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HealthCheckClientTest {

    companion object {
        private val mockServer: MockServerClient = startClientAndServer(0)

        @AfterAll
        fun tearDown() {
            mockServer.stop()
        }
    }

    private val endpoint = "healthcheck"
    private val url get() = "http://localhost:${(mockServer as ClientAndServer).localPort}/$endpoint"
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

    @Test
    internal fun `internal server error`() {
        mockServer.`when`(HttpRequest.request("/$endpoint"))
                .respond(HttpResponse.response(INTERNAL_SERVER_ERROR.data.toString())
                        .withStatusCode(500))
        val (host, checks) = healthCheckClient.check(host).blockingGet()

        assertEquals(host, host)
        assertNotNull(checks)
        assertTrue(checks!!.isUnhealthy())
    }

    @Test
    internal fun `Not found`() {
        mockServer.`when`(HttpRequest.request("/$endpoint"))
                .respond(HttpResponse.response(NOT_FOUND.data.toString())
                        .withStatusCode(404))
        val (host, checks) = healthCheckClient.check(host).blockingGet()

        assertEquals(host, this.host)
        assertNull(checks)
    }

    @Test
    internal fun `on ok`() {
        mockServer.`when`(HttpRequest.request("/$endpoint"))
                .respond(HttpResponse.response(GOOD.data.toString())
                        .withStatusCode(200))
        val (host, checks) = healthCheckClient.check(host).blockingGet()

        assertEquals(host, this.host)
        assertNotNull(checks)
        assertFalse(checks!!.isUnhealthy())
    }
}
