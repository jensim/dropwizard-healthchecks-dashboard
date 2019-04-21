package com.github.jensim.dropwizarddashboard.host

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.reactivex.Observable
import io.reactivex.Single
import java.util.UUID
import javax.inject.Inject

@Controller("/api/hosts")
class HostsController @Inject constructor(private val hostsRepo: HostsRepo,
        private val hostHealthSocket: HostHealthSocket) {

    @Get("/")
    fun getHosts(): Observable<Host> = hostsRepo.getAll()

    @Post("/")
    fun add(hostSuggestion: HostSuggestion): Single<Host> = hostsRepo
            .insert(Host.fromUrl(hostSuggestion.url))
            .doOnSuccess { hostHealthSocket.update(it) }

    @Get("/rnd")
    fun addRandom() = hostsRepo.insert(Host.fromUrl("http://${UUID.randomUUID()}:8080/healthcheck"))

    data class HostSuggestion(val url: String)
}