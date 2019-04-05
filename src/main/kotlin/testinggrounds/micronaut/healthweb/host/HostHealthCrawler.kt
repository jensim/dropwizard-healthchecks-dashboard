package testinggrounds.micronaut.healthweb.host

import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
import testinggrounds.micronaut.healthweb.HostHealthSocket
import javax.inject.Singleton

@Singleton
class HostHealthCrawler(private val hostHealthSocket: HostHealthSocket) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = "30s", initialDelay = "30s")
    fun crawl() {
        hostHealthSocket.update(Host.fromUrl("http://localhost:8080/healthcheck"))
    }
}