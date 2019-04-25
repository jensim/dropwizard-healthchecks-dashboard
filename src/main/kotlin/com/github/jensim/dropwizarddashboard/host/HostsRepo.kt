package com.github.jensim.dropwizarddashboard.host

import com.github.jensim.dropwizarddashboard.repo.CrudRepo
import com.mongodb.client.model.IndexOptions
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document
import javax.inject.Singleton

@Singleton
open class HostsRepo : CrudRepo<Host>("hosts", Host::class.java) {

    override fun collectionModifier(collection: MongoCollection<Host>) {
        collection.createIndex(Document(mapOf("lastProbeTime" to 1)))
        collection.createIndex(Document(mapOf("healthCheckUrl" to -1)), object : IndexOptions() {
            override fun isUnique(): Boolean = true
        })
    }
}
