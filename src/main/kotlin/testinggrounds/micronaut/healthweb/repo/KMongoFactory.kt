package testinggrounds.micronaut.healthweb.repo

import com.mongodb.reactivestreams.client.MongoClients
import org.litote.kmongo.reactivestreams.withKMongo

object KMongoFactory {

    val mDb = MongoClients.create("mongodb://localhost:27017/healthweb")
            .getDatabase("healthweb")
            .withKMongo()
}