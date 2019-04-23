package com.github.jensim.dropwizarddashboard.host

import com.github.jensim.dropwizarddashboard.Application
import com.github.jensim.dropwizarddashboard.host.HostsController.HostSuggestion
import com.mongodb.reactivestreams.client.MongoDatabase
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import io.reactivex.Single
import io.restassured.RestAssured.`when`
import io.restassured.RestAssured.given
import io.restassured.mapper.TypeRef
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import javax.inject.Inject

@Ignore
//@MicronautTest(application = Application::class, propertySources = ["application-test.yml"])
open class HostsControllerIntegrationTest {

    private val log = LoggerFactory.getLogger(javaClass)

    @Inject
    private val embeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
    @Inject
    lateinit var mongoDb: MongoDatabase
    @Inject
    private lateinit var repo: HostsRepo

    private val hostSuggestion = HostSuggestion("http://localhost:8080/healthcheck")

    @Before
    fun setUp() {
        Single.fromPublisher(mongoDb.drop())
                .doAfterSuccess { log.info("Dropped Mongo database") }
                .blockingGet()
    }

    @Test
    fun `getHosts zero`() {
        // when
        given()
                .port(embeddedServer.port)
                .`when`()
                .get("/api/hosts")
                .then()
                .statusCode(200)
                .body("size()", `is`(0))
    }

    @Test
    fun `getHosts one`() {
        // given
        repo.insert(Host.fromUrl(hostSuggestion.url))

        val host = given()
                .port(embeddedServer.port)
                .`when`()
                .get("/api/hosts")
                .then()
                .statusCode(200)
                .body("size()", `is`(1))
                .extract().body().`as`(object : TypeRef<Array<Host>>() {})[0]

        assertEquals(host.healthCheckUrl, hostSuggestion.url)
    }

    @Test
    fun add() {
        // when
        given()
                .port(embeddedServer.port)
                .body(hostSuggestion)
                .`when`()
                .post("/api/hosts")
                .then()
                .statusCode(200)

        // then
        assertEquals(repo.count().blockingGet(), 1)
    }

    @Test
    internal fun `add same url twice`() {
        // when
        repeat(2) {
            given()
                    .port(embeddedServer.port)
                    .body(hostSuggestion)
                    .`when`()
                    .post("/api/hosts")
                    .then()
                    .statusCode(200)
        }
    }
}
