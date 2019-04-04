package testinggrounds.micronaut.healthweb.host

import testinggrounds.micronaut.healthweb.repo.CrudRepo
import javax.inject.Singleton

@Singleton
class HostsRepo : CrudRepo<Host>("hosts")