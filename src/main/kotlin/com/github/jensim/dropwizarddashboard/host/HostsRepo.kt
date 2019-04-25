package com.github.jensim.dropwizarddashboard.host

import com.github.jensim.dropwizarddashboard.repo.CrudRepo
import com.mongodb.client.model.IndexOptions
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import org.litote.kmongo.reactivestreams.createIndex
import javax.inject.Singleton

@Singleton
open class HostsRepo(mongoDb: MongoDatabase) : CrudRepo<Host>("hosts", Host::class.java, mongoDb) {

    override fun collectionModifier(collection: MongoCollection<Host>) {
        collection.createIndex("{lastProbeTime:1}")
        collection.createIndex("{healthCheckUrl:-1}", object : IndexOptions() {
            override fun isUnique(): Boolean = true
        })
    }
}
