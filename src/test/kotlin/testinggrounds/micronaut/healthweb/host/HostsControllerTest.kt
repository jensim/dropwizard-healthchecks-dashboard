package testinggrounds.micronaut.healthweb.host

import com.mongodb.reactivestreams.client.MongoDatabase
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import io.reactivex.Single
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import testinggrounds.micronaut.healthweb.Application
import testinggrounds.micronaut.healthweb.host.HostsController.HostSuggestion
import javax.inject.Inject

@MicronautTest(application = Application::class, propertySources = ["application-test.yml"])
open class HostsControllerTest {

    private val log = LoggerFactory.getLogger(javaClass)

    @Inject
    private val embeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
    @Inject
    lateinit var mongoDb: MongoDatabase
    @Inject
    private lateinit var repo: HostsRepo

    private val client: RxHttpClient
    private val hostSuggestion = HostSuggestion("http://localhost:8080/healthcheck")

    init {
        client = RxHttpClient.create(embeddedServer.url)
    }

    @BeforeEach
    fun setUp() {
        Single.fromPublisher(mongoDb.drop())
                .doAfterSuccess { log.info("Dropped Mongo database") }
                .blockingGet()
    }

    @Test
    fun `getHosts zero`() {
        // when
        val responses = client.exchange("/api/hosts", Array<Host>::class.java)
                .blockingSingle()
                .body()!!

        // then
        assertTrue(responses.isEmpty())
    }

    @Test
    fun `getHosts one`() {
        // given
        repo.insert(Host.fromUrl(hostSuggestion.url))

        // when
        val responses = client.exchange("/api/hosts", Array<Host>::class.java)
                .blockingSingle()
                .body()!!

        // then
        assertTrue(responses.isEmpty())
    }

    @Test
    fun add() {
        // when
        client.exchange(HttpRequest.POST("/api/hosts", hostSuggestion)).blockingFirst()

        // then
        assertEquals(repo.count().blockingGet(), 1)
    }

    @Test
    internal fun `add same url twice`() {
        // when
        client.exchange(HttpRequest.POST("/api/hosts", hostSuggestion)).blockingFirst()
        client.exchange(HttpRequest.POST("/api/hosts", hostSuggestion)).blockingFirst()
    }
}