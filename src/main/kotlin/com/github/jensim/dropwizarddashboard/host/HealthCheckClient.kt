package com.github.jensim.dropwizarddashboard.host

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method.GET
import com.github.kittinunf.fuel.jackson.responseObject
import javax.inject.Singleton

@Singleton
class HealthCheckClient {


    fun check(host: Host): Pair<Host, HostHealthChecks?> {
        val responseObject = FuelManager.instance.removeAllResponseInterceptors()
                .request(GET, host.healthCheckUrl.toString())
                .responseObject<HostHealthChecks>()

        val c: HostHealthChecks? = responseObject.third.component1()

        return host to c
    }
}
