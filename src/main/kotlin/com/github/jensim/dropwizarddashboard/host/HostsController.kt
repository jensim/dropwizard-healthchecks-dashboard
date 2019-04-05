package com.github.jensim.dropwizarddashboard.host

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.reactivex.Observable
import io.reactivex.Single
import org.slf4j.LoggerFactory
import com.github.jensim.dropwizarddashboard.HostHealthSocket
import javax.inject.Inject

@Controller("/api/hosts")
class HostsController {

    private val log = LoggerFactory.getLogger(javaClass)

    @Inject
    private lateinit var hostsRepo: HostsRepo
    @Inject
    private lateinit var hostHealthSocket: HostHealthSocket

    @Get("/")
    fun getHosts(): Observable<Host> = hostsRepo.getAll()

    @Post("/")
    fun add(hostSuggestion: HostSuggestion): Single<Host> = hostsRepo
            .insert(Host.fromUrl(hostSuggestion.url))
            .doOnSuccess { hostHealthSocket.update(it) }

    data class HostSuggestion(val url: String)
}