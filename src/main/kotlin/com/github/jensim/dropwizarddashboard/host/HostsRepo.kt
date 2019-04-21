package com.github.jensim.dropwizarddashboard.host

import com.mongodb.client.model.IndexOptions
import com.mongodb.reactivestreams.client.MongoCollection
import org.litote.kmongo.reactivestreams.createIndex
import com.github.jensim.dropwizarddashboard.repo.CrudRepo
import com.mongodb.reactivestreams.client.MongoDatabase
import javax.inject.Singleton

@Singleton
class HostsRepo(mongoDb: MongoDatabase) : CrudRepo<Host>("hosts", Host::class.java, mongoDb) {

    override fun collectionModifier(collection: MongoCollection<Host>) {
        collection.createIndex("{lastProbeTime:1}")
        collection.createIndex("{healthCheckUrl:-1}", object : IndexOptions() {
            override fun isUnique(): Boolean = true
        })
    }
}