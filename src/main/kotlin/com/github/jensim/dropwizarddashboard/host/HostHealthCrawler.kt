package com.github.jensim.dropwizarddashboard.host

import com.github.jensim.dropwizarddashboard.host.HostHealthStatus.HEALTHY
import com.github.jensim.dropwizarddashboard.host.HostHealthStatus.UNHEALTHY
import com.github.jensim.dropwizarddashboard.host.HostHealthStatus.UNREACHABLE
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.lt
import com.mongodb.client.model.Filters.or
import com.mongodb.reactivestreams.client.FindPublisher
import io.micronaut.scheduling.annotation.Scheduled
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HostHealthCrawler @Inject constructor(
        private val hostHealthSocket: HostHealthSocket,
        private val hostsRepo: HostsRepo,
        private val client: HealthCheckClient) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val backoffDuration = Duration.ofSeconds(1).toMillis()
    private val timeWindow: Long
        get() = System.currentTimeMillis() - backoffDuration

    //@Scheduled(fixedDelay = "5s", initialDelay = "5s")
    fun crawl() {
        //log.info("Crawling suggested hosts")
        Observable.fromPublisher(getEligibleHosts())
                .flatMap { updateProbeTime(it) }
                .map { client.check(it) }
                .flatMap { updateProbedHost(it.first, it.second) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .timeout(3, SECONDS)
                .subscribe({
                    log.info("Good run ${it.healthCheckUrl}")
                }, {
                    log.error("Bad run", it)
                })
    }

    private fun getEligibleHosts(): FindPublisher<Host> {
        val findExpression = or(
                eq("lastProbeTime", null),
                lt("lastProbeTime", timeWindow)
        )
        //log.info(findExpression.toString())
        return hostsRepo.raw().find(findExpression).limit(5)
    }

    private fun updateProbeTime(host: Host): Observable<Host> =
            hostsRepo.update(host._id as Any, host.copy(lastProbeTime = System.currentTimeMillis()))
                    .map { host }
                    .toObservable()
                    .doOnNext { log.info("Host probe time updated ${host.healthCheckUrl.toURI().host}") }

    private fun updateProbedHost(host: Host, check: HostHealthChecks?): Observable<Host> {
        log.info("Updating host $host with result $check")
        val n: Host? = if (check == null) {
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
            null
        }
        return if (n != null) {
            hostHealthSocket.update(n)
            hostsRepo.update(host._id as Any, n).toObservable()
        } else {
            Observable.empty()
        }
    }
}
