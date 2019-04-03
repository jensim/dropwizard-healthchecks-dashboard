package testinggrounds.micronaut.healthweb.repo

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoCollection
import io.reactivex.Observable
import io.reactivex.Single
import org.bson.conversions.Bson

abstract class CrudRepo<T>(val collectionName: String, val mongoClient: MongoClient) {

    inline fun <reified T> getCollection(): MongoCollection<T> = mongoClient
            .getDatabase("healthweb")
            .getCollection(collectionName, T::class.java)

    inline fun <reified T> insert(t: T) = Single.fromPublisher(getCollection<T>().insertOne(t)).map { t }
    inline fun <reified T> delete(bson: Bson) = Single.fromPublisher(getCollection<T>().deleteOne(bson))
    inline fun <reified T> find(bson: Bson, limit: Int = 1) = Observable.fromPublisher(getCollection<T>().find(bson).limit(limit))
    inline fun <reified T> getAll() = Observable.fromPublisher(getCollection<T>().find())
}