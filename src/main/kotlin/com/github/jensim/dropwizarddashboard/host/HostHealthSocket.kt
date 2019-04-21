package com.github.jensim.dropwizarddashboard.host

import io.micronaut.websocket.WebSocketBroadcaster
import io.micronaut.websocket.WebSocketSession
import io.micronaut.websocket.annotation.OnClose
import io.micronaut.websocket.annotation.OnMessage
import io.micronaut.websocket.annotation.OnOpen
import io.micronaut.websocket.annotation.ServerWebSocket
import java.util.function.Predicate
import javax.inject.Inject

@ServerWebSocket("/ws/hosthealth")
class HostHealthSocket @Inject constructor(private val broadcaster: WebSocketBroadcaster,
        private val hostsRepo: HostsRepo) {

    @OnOpen
    fun onOpen(session: WebSocketSession) = hostsRepo.getAll().map { session.send(it) }.blockingLast()

    @OnMessage
    fun onMessage(message: String, session: WebSocketSession) {

    }

    @OnClose
    fun onClose(session: WebSocketSession) = Unit

    fun update(host: Host) {
        broadcaster.broadcast(host)
    }

    private fun isValid(topic: String) = Predicate { s: WebSocketSession ->
        topic.equals(s.uriVariables.get("topic", String::class.java, null), ignoreCase = true)
    }
}