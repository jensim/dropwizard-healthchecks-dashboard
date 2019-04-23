package com.github.jensim.dropwizarddashboard.host

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class HealthCheckClientTest {

    companion object {
        private val mockServer = WireMockServer(options().dynamicPort())
                .apply { start() }

        @AfterAll
        fun tearDown() {
            mockServer.stop()
        }
    }

    private val endpoint = "healthcheck"
    private val url get() = "${mockServer.baseUrl()}/$endpoint"
    private val host get() = Host.fromUrl(url)
    private val health = HostHealthChecks(listOf(HealthCheck("Uggh", false), HealthCheck("Mjau", true)))
    private val healthCheckClient = HealthCheckClient()

    @AfterEach
    internal fun reset() {
        mockServer.resetAll()
    }

    @Test
    internal fun `internal server error`() {
        println("hoooo hoooo $url")

        mockServer.stubFor(
                get(urlEqualTo("/$endpoint"))
                        .willReturn(aResponse().withStatus(500).withBody(
                                """
                    {
                      "DataSourceHealthIndicator": {
                        "healthy": true,
                        "message": "UP {database=H2, hello=1}"
                      },
                      "DiskSpaceHealthIndicator": {
                        "healthy": true,
                        "message": "UP {total=499963170816, free=321445916672, threshold=10485760}"
                      },
                      "FailingHealthcheckService": {
                        "healthy": false,
                        "message": "BOOOOOM",
                        "error": {
                          "message": "BOOOOOM",
                          "stack": [
                            "java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)",
                            "java.base/java.util.concurrent.ThreadPoolExecutor_Worker.run(ThreadPoolExecutor.java:641)",
                            "org.apache.tomcat.util.threads.TaskThread_WrappingRunnable.run(TaskThread.java:61)",
                            "java.base/java.lang.Thread.run(Thread.java:844)"
                          ]
                        }
                      },
                      "GarbageCollectionHealthCheck": {
                        "healthy": true,
                        "message": "no gc events. sample interval: 300000,00ms."
                      },
                      "HttpStatusHealthCheck": {
                        "healthy": true,
                        "message": "Last five minutes there has been 0 http 5XX responses"
                      },
                      "OracleObjectHealthCheck": {
                        "healthy": true,
                        "message": "No oracle database to check"
                      }
                    }
                """.trimIndent()
                        )))
        Thread.sleep(2000)

        val a = healthCheckClient.check(host)

        assertEquals(a.first, host)
        assertNotNull(a.second)
    }

    @Test
    internal fun `proxy error`() {
        /*
        val clientMock: RxHttpClient = mock {
            on { exchange(uri, HostHealthChecks::class.java) } doThrow HttpStatusException(BAD_GATEWAY, "502 Bad gateway error.")
        }
        val healthCheckClient = HealthCheckClient(clientMock)

        val a = healthCheckClient.check(host)

        val blockingGet = a.blockingGet()
        assertSame(blockingGet.first, host)
        assertNull(blockingGet.second)
        */
    }

    @Test
    internal fun `on ok`() {
        /*
        val clientMock: RxHttpClient = mock {
            on { exchange(uri, HostHealthChecks::class.java) } doReturn just(SimpleHttpResponseFactory.INSTANCE.ok(health) as HttpResponse<HostHealthChecks>)
        }
        val healthCheckClient = HealthCheckClient(clientMock)

        val a = healthCheckClient.check(host)

        val blockingGet = a.blockingGet()
        assertSame(blockingGet.first, host)
        assertSame(blockingGet.second, health)
         */
    }
}
