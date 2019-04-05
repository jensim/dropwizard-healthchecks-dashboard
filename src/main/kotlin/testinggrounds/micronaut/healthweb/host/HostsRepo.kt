package testinggrounds.micronaut.healthweb.host

import com.mongodb.client.model.IndexOptions
import com.mongodb.reactivestreams.client.MongoCollection
import org.litote.kmongo.reactivestreams.createIndex
import testinggrounds.micronaut.healthweb.repo.CrudRepo
import javax.inject.Singleton

@Singleton
class HostsRepo : CrudRepo<Host>("hosts", Host::class.java) {

    override fun collectionModifier(collection: MongoCollection<Host>) {
        collection.createIndex("{healthCheckUrl:1}", object : IndexOptions() {
            override fun isUnique(): Boolean = true
        })
    }
}