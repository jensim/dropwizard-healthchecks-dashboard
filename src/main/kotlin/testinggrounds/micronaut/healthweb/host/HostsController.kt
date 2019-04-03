package testinggrounds.micronaut.healthweb.host

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/hosts")
class HostsController{

    @Get("/")
    fun getHosts(){

    }
}