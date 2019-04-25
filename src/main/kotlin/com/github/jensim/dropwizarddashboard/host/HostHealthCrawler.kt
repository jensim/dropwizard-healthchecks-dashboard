package com.github.jensim.dropwizarddashboard.host

import com.github.jensim.dropwizarddashboard.host.HostHealthStatus.HEALTHY
import com.github.jensim.dropwizarddashboard.host.HostHealthStatus.UNHEALTHY
import com.github.jensim.dropwizarddashboard.host.HostHealthStatus.UNREACHABLE
import io.micronaut.scheduling.annotation.Scheduled
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HostHealthCrawler @Inject constructor(
        private val hostHealthSocket: HostHealthSocket,
        private val hostsRepo: HostsRepo,
        private val client: HealthCheckClient) {

    companion object {
        private const val defaultProbeInterval = 60_000
        fun shouldProbe(host: Host) = (host.lastProbeTime ?: 0) +
                (host.probeInterval ?: defaultProbeInterval) < System.currentTimeMillis()
    }

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = "5s", initialDelay = "5s")
    fun crawl() {
        try {
            runBlocking {
                hostsRepo.getAll()
                        .filter(::shouldProbe)
                        .map {
                            async {
                                updateProbeTime(it)
                                val checks = client.check(it)
                                updateProbedHost(it, checks)
                            }
                        }.blockingIterable()
            }
        } catch (e: Exception) {
            log.error("Whoops!", e)
        }
    }

    private fun updateProbeTime(host: Host) {
        log.debug("Updating probe time for host ${host.healthCheckUrl}")
        hostsRepo.update(host._id as Any, host.copy(lastProbeTime = System.currentTimeMillis())).blockingGet()
    }

    private fun updateProbedHost(host: Host, check: HostHealthChecks?) {
        log.debug("Updating host ${host.healthCheckUrl} with result ${check?.isUnhealthy()}")
        if (check == null) {
            host.copy(lastResponse = null,
                    healthStatus = UNREACHABLE,
                    unreachableProbeStreak = (host.unreachableProbeStreak ?: 0) + 1)
        } else if (host.healthStatus?.equals(check) != true) {
            if (check.isUnhealthy()) {
                host.copy(lastResponse = check, healthStatus = UNHEALTHY, unreachableProbeStreak = 0)
            } else {
                host.copy(lastResponse = check, healthStatus = HEALTHY, unreachableProbeStreak = 0)
            }
        } else {
            log.debug("Nothing to update for host ${host.healthCheckUrl}")
            null
        }?.run {
            hostHealthSocket.update(this)
            hostsRepo.update(host._id as Any, this).subscribe({
                log.debug("Updated Host ${host.healthCheckUrl} with checks ${check.isUnhealthy()}")
            }, {
                log.error("Failed updating host ${host.healthCheckUrl} with checks ${check.isUnhealthy()}", it)
            })
        }
    }
}
