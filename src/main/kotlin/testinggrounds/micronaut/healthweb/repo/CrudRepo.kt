package testinggrounds.micronaut.healthweb.repo

import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import io.reactivex.Observable
import io.reactivex.Single
import org.bson.conversions.Bson
import org.litote.kmongo.reactivestreams.updateOneById
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

abstract class CrudRepo<T : Any>(private val collectionName: String, private val c: Class<T>) {

    @Inject
    private lateinit var mongoDb: MongoDatabase
    private val ref = AtomicReference<MongoCollection<T>?>().apply { set(null) }
    private val collection: MongoCollection<T>
        get() {
            ref.compareAndSet(null, constructCollection())
            return ref.get()!!
        }

    /**
     * Wanna add an index?
     * import org.litote.kmongo.reactivestreams.createIndex
     * collection.createIndex("healthCheckUrl")
     */
    open fun collectionModifier(collection: MongoCollection<T>) = Unit

    private fun constructCollection(): MongoCollection<T> {
        val a = mongoDb.getCollection<T>(collectionName, c)
        collectionModifier(a)
        return a
    }

    fun insert(t: T): Single<T> = Single.fromPublisher(collection.insertOne(t)).map { t }
    fun update(id: Any, t: T) = Single.fromPublisher(collection.updateOneById(id, t)).map { t }
    fun deleteOne(bson: Bson) = Single.fromPublisher(collection.deleteOne(bson))
    fun find(bson: Bson, limit: Int = 1) = Observable.fromPublisher(collection.find(bson).limit(limit))
    fun getAll() = Observable.fromPublisher(collection.find())
    fun getAll(filter: Bson) = Observable.fromPublisher(collection.find(filter))
    fun count() = Single.fromPublisher(collection.count())
    fun count(filter: Bson) = Single.fromPublisher(collection.count(filter))
}