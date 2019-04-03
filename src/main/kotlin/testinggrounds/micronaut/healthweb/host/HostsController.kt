package testinggrounds.micronaut.healthweb.host

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.reactivex.Observable
import io.reactivex.Single
import org.slf4j.LoggerFactory
import java.util.UUID

@Controller("/hosts")
class HostsController(private val hostsRepo: HostsRepo) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Get("/")
    fun getHosts():Observable<Host> = hostsRepo.getAll()

    @Get("add-random")
    fun addRandom(): Single<Host> {
        log.info("Adding random host!..")
        return hostsRepo.insert(Host.fromUrl("http://${UUID.randomUUID()}:8080/healthcheck"))
    }
}