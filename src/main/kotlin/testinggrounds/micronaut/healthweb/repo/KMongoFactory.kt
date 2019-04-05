package testinggrounds.micronaut.healthweb.repo

import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import org.litote.kmongo.reactivestreams.withKMongo
import javax.inject.Singleton

@Factory
class KMongoFactory(
        @Property(name = "mongodb.uri") private val mongoUri: String,
        @Property(name = "mongodb.dbname") private val mongoDbName: String) {

    @Bean
    @Singleton
    fun mongoDb(): MongoDatabase {
        println("Connecting to $mongoUri $mongoDbName")
        return MongoClients.create(mongoUri)
                .getDatabase(mongoDbName)
                .withKMongo()
    }
}