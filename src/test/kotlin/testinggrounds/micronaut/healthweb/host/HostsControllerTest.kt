package testinggrounds.micronaut.healthweb.host

import io.micronaut.context.ApplicationContext
import io.micronaut.http.client.RxHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import testinggrounds.micronaut.healthweb.Application
import javax.inject.Inject

@MicronautTest(application = Application::class)
open class HostsControllerTest {

    @Inject
    private val embeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
    @Inject
    private lateinit var repo: HostsRepo

    @Inject
    private lateinit var controller: HostsController

    private val client: RxHttpClient

    init {
        client = RxHttpClient.create(embeddedServer.url)
    }

    @BeforeEach
    fun setUp() {
        repo.insert(Host.fromUrl("http://localhost:8080/healthcheck")).blockingGet()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun getHosts() {
        // given

        // when
        println("Getting all hosts from server")
        val responses = client.exchange("/api/hosts", Array<Host>::class.java)
                .blockingSingle()
                .body()!!

        // then
        assertTrue(responses.isNotEmpty())
    }

    @Test
    fun add() {
        // given

        // when

        // then
    }
}