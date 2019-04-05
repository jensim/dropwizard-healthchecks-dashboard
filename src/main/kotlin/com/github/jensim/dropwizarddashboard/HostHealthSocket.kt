package com.github.jensim.dropwizarddashboard

import io.micronaut.websocket.WebSocketBroadcaster
import io.micronaut.websocket.annotation.ServerWebSocket
import com.github.jensim.dropwizarddashboard.host.Host

@ServerWebSocket("/ws/hosthealth")
class HostHealthSocket(private val broadcaster: WebSocketBroadcaster) {

    fun update(host: Host) {
        broadcaster.broadcast(host)
    }
}