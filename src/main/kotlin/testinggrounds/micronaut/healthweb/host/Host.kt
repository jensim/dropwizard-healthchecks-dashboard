package testinggrounds.micronaut.healthweb.host

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import testinggrounds.micronaut.healthweb.host.HostHealthStatus.UNKNOWN
import java.net.URL
import java.time.LocalDateTime

enum class HostHealthStatus {
    HEALTHY,
    UNHEALTHY,
    UNREACHABLE,
    UNKNOWN
}

data class HealthCheck(
        @BsonProperty("message")
        @JsonProperty("message")
        val message: String,
        @JsonProperty("healthy")
        @BsonProperty("healthy")
        val healthy: Boolean
)

data class HostHealthChecks(
        @JsonProperty("checks")
        @BsonProperty("checks")
        val checks: List<HealthCheck>)

data class Host (
    @BsonId
    @BsonProperty("_id")
    @JsonProperty("_id")
    val _id: ObjectId?,
    @BsonProperty("healthCheckUrl")
    @JsonProperty("healthCheckUrl")
    val healthCheckUrl: URL,
    @BsonProperty("lastResponse")
    @JsonProperty("lastResponse")
    val lastResponse: HostHealthChecks?,
    @BsonProperty("lastProbeTime")
    @JsonProperty("lastProbeTime")
    val lastProbeTime: LocalDateTime?,
    @BsonProperty("unreachableProbeStreak")
    @JsonProperty("unreachableProbeStreak")
    val unreachableProbeStreak: Int? = 0,
    @BsonProperty("healthStatus")
    @JsonProperty("healthStatus")
    val healthStatus: HostHealthStatus? = UNKNOWN
){
    companion object {
        fun fromUrl(url: String) = Host(null, URL(url), null, null, 0, UNKNOWN)
    }
}
