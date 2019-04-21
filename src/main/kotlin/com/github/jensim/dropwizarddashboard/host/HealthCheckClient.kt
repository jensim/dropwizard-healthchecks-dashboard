package com.github.jensim.dropwizarddashboard.host

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method.GET
import com.github.kittinunf.fuel.jackson.responseObject
import javax.inject.Singleton

@Singleton
class HealthCheckClient {

    private val fuelManager = FuelManager.instance.removeAllResponseInterceptors()

    fun check(host: Host): Pair<Host, HostHealthChecks?> {
        val req = fuelManager.request(GET, host.healthCheckUrl.toString())

        val c: HostHealthChecks? = Fuel.request(req).responseObject<HostHealthChecks>().third.component1()

        return host to c
    }
}
