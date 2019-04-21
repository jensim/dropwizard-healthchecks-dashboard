package com.github.jensim.dropwizarddashboard.host

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.BAD_GATEWAY
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.http.simple.SimpleHttpResponseFactory
import io.reactivex.Flowable
import io.reactivex.Flowable.just
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

internal class HealthCheckClientTest {

    private val uri = "http://localhost:8080/healthcheck"
    private val host = Host.fromUrl(uri)
    private val health = HostHealthChecks(listOf(HealthCheck("Uggh", false), HealthCheck("Mjau", true)))
    private val internalServerError = HttpStatusException(INTERNAL_SERVER_ERROR, health)

    @Test
    internal fun `internal server error`() {
        val clientMock: RxHttpClient = mock {
            on { exchange(uri, HostHealthChecks::class.java) } doThrow internalServerError
        }
        val healthCheckClient = HealthCheckClient(clientMock)

        val a = healthCheckClient.check(host)

        val blockingGet = a.blockingGet()
        assertSame(blockingGet.first, host)
        assertSame(blockingGet.second, health)
    }

    @Test
    internal fun `proxy error`() {
        val clientMock: RxHttpClient = mock {
            on { exchange(uri, HostHealthChecks::class.java) } doThrow HttpStatusException(BAD_GATEWAY, "502 Bad gateway error.")
        }
        val healthCheckClient = HealthCheckClient(clientMock)

        val a = healthCheckClient.check(host)

        val blockingGet = a.blockingGet()
        assertSame(blockingGet.first, host)
        assertNull(blockingGet.second)
    }

    @Test
    internal fun `on ok`() {
        val clientMock: RxHttpClient = mock {
            on { exchange(uri, HostHealthChecks::class.java) } doReturn just(SimpleHttpResponseFactory.INSTANCE.ok(health) as HttpResponse<HostHealthChecks>)
        }
        val healthCheckClient = HealthCheckClient(clientMock)

        val a = healthCheckClient.check(host)

        val blockingGet = a.blockingGet()
        assertSame(blockingGet.first, host)
        assertSame(blockingGet.second , health)
    }
}