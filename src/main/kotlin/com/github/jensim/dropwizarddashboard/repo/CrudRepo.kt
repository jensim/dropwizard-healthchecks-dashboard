package com.github.jensim.dropwizarddashboard.repo

import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoCollection
import io.micronaut.context.annotation.Value
import io.reactivex.Observable
import io.reactivex.Single
import org.bson.Document
import org.bson.conversions.Bson
import javax.inject.Inject

abstract class CrudRepo<T : Any> @Inject constructor(private val collectionName: String, val c: Class<T>) {

    @Inject
    private lateinit var mongo: MongoClient
    @Inject
    private lateinit var objectMapper: ObjectMapper
    @Value("\${mongodb.dbname}")
    private lateinit var dbName: String
    private val collection: MongoCollection<T> by lazy { constructCollection() }

    /**
     * Wanna add an index?
     * import org.litote.kmongo.reactivestreams.createIndex
     * collection.createIndex("healthCheckUrl")
     */
    open fun collectionModifier(collection: MongoCollection<T>) = Unit

    private fun constructCollection(): MongoCollection<T> {
        val a = mongo.getDatabase(dbName)
                .getCollection<T>(collectionName, c)
        collectionModifier(a)
        return a
    }

    fun raw() = collection
    fun insert(t: T): Single<T> = Single.fromPublisher(collection.insertOne(t)).map { t }
    fun updateOneById(id: Any, t: T) = Single.fromPublisher(collection.updateOne(Document(mapOf("_id" to id)), Document.parse(objectMapper.writeValueAsString(t))))
    fun deleteOne(bson: Bson) = Single.fromPublisher(collection.deleteOne(bson))
    fun find(bson: Bson, limit: Int = 1) = Observable.fromPublisher(collection.find(bson).limit(limit))
    fun getAll() = Observable.fromPublisher(collection.find())
    fun getAll(filter: Bson) = Observable.fromPublisher(collection.find(filter))
    fun count() = Single.fromPublisher(collection.count())
    fun count(filter: Bson) = Single.fromPublisher(collection.count(filter))
}
