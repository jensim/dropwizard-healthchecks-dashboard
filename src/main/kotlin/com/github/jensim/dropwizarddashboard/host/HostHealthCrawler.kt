package com.github.jensim.dropwizarddashboard.host

import com.github.jensim.dropwizarddashboard.host.HostHealthStatus.HEALTHY
import com.github.jensim.dropwizarddashboard.host.HostHealthStatus.UNHEALTHY
import com.github.jensim.dropwizarddashboard.host.HostHealthStatus.UNREACHABLE
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.lt
import com.mongodb.client.model.Filters.or
import io.micronaut.scheduling.annotation.Scheduled
import io.reactivex.Observable
import org.slf4j.LoggerFactory
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HostHealthCrawler @Inject constructor(
        private val hostHealthSocket: HostHealthSocket,
        private val hostsRepo: HostsRepo,
        private val client: HealthCheckClient) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val backoffDuration = Duration.ofMinutes(1).toMillis()
    private val timeWindow: Long
        get() = System.currentTimeMillis() - backoffDuration

    @Scheduled(fixedDelay = "5s", initialDelay = "5s")
    fun crawl() {
        getEligibleHosts().forEach { updateProbeTime(it) }
    }

    private fun getEligibleHosts(): Observable<Host> {
        val findExpression = or(
                eq("lastProbeTime", null),
                lt("lastProbeTime", timeWindow)
        )
        //log.info(findExpression.toString())
        return hostsRepo.find(findExpression, 5)
    }

    private fun updateProbeTime(host: Host) {
        log.debug("Updating probe time for host ${host.healthCheckUrl}")
        hostsRepo.update(host._id as Any, host.copy(lastProbeTime = System.currentTimeMillis()))
                .map { host }
                .subscribe({ crawl(host) }, {
                    log.error("Failed updating probe time")
                })
    }

    fun crawl(host: Host) {
        log.debug("Crawling host check ${host.healthCheckUrl}")
        client.check(host).subscribe(
                { (host, checks) -> updateProbedHost(host, checks) },
                {
                    log.error("Failed crawling host ${host.healthCheckUrl}")
                    updateProbedHost(host, null)
                })
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
