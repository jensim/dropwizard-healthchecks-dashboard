package testinggrounds.micronaut.healthweb

import io.micronaut.websocket.WebSocketBroadcaster
import io.micronaut.websocket.annotation.ServerWebSocket
import testinggrounds.micronaut.healthweb.host.Host

@ServerWebSocket("/ws/hosthealth")
class HostHealthSocket(private val broadcaster: WebSocketBroadcaster) {

    fun update(host: Host) {
        broadcaster.broadcast(host)
    }
}