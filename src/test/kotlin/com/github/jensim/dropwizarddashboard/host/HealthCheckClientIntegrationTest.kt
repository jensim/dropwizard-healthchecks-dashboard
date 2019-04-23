package com.github.jensim.dropwizarddashboard.host

import io.micronaut.http.HttpStatus
import io.micronaut.http.HttpStatus.INTERNAL_SERVER_ERROR
import io.micronaut.http.exceptions.HttpStatusException
import io.reactivex.Flowable
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.UUID
import javax.inject.Inject

@Disabled
internal class HealthCheckClientIntegrationTest {

    @Inject
    private lateinit var healthCheckClient: HealthCheckClient

    @Test
    internal fun `missing host`() {
        healthCheckClient.check(Host.fromUrl("http://${UUID.randomUUID()}:41921/healthcheck"))
    }

    @Disabled
    @Test
    internal fun `flowable map before catch`() {
        val h = Host.fromUrl("http://localhost:8080/health")
        val err = HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, HostHealthChecks(emptyList()))
        val a = Flowable.error<HostHealthChecks?>(err)
                .onExceptionResumeNext { null }
                .onErrorReturn { mapThrown(it) }
                .map { h to it as HostHealthChecks? }
                .blockingFirst()
        assertNotNull(a)
    }

    fun mapThrown(t: Throwable): HostHealthChecks? {
        System.out.println("Mapping exception $t")
        return when (t) {
            is HttpStatusException -> mapHttpStatusException(t)
            else -> null
        }
    }

    fun mapHttpStatusException(e: HttpStatusException): HostHealthChecks? = when (INTERNAL_SERVER_ERROR) {
        e.status -> {
            val a = e.body.orElse(null)
            if (a is HostHealthChecks) {
                a
            } else {
                null
            }
        }
        else -> null
    }
}
