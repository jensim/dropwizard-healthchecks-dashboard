package testinggrounds.micronaut.healthweb.host

import com.mongodb.reactivestreams.client.MongoClient
import testinggrounds.micronaut.healthweb.repo.CrudRepo
import javax.inject.Singleton

@Singleton
class HostsRepo(mongoClient: MongoClient) : CrudRepo<Host>("hosts", mongoClient)