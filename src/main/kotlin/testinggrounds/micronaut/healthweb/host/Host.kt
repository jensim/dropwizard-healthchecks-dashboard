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

class Host {
    @BsonId
    @BsonProperty("_id")
    @JsonProperty("_id")
    var _id: ObjectId?
    @BsonProperty("healthCheckUrl")
    @JsonProperty("healthCheckUrl")
    var healthCheckUrl: URL?
    @BsonProperty("lastResponse")
    @JsonProperty("lastResponse")
    var lastResponse: HostHealthChecks?
    @BsonProperty("lastProbeTime")
    @JsonProperty("lastProbeTime")
    var lastProbeTime: LocalDateTime?
    @BsonProperty("unreachableProbeStreak")
    @JsonProperty("unreachableProbeStreak")
    var unreachableProbeStreak: Int? = 0
    @BsonProperty("healthStatus")
    @JsonProperty("healthStatus")
    var healthStatus: HostHealthStatus? = UNKNOWN

    constructor(
            _id: ObjectId?,
            healthCheckUrl: URL?,
            lastResponse: HostHealthChecks?,
            lastProbeTime: LocalDateTime?,
            unreachableProbeStreak: Int?,
            healthStatus: HostHealthStatus?
    ) {
        this._id = _id
        this.healthCheckUrl = healthCheckUrl
        this.lastResponse = lastResponse
        this.lastProbeTime = lastProbeTime
        this.unreachableProbeStreak = unreachableProbeStreak
        this.healthStatus = healthStatus
    }

    constructor() {
        _id = null
        healthCheckUrl = null
        lastResponse = null
        lastProbeTime = null
        unreachableProbeStreak = null
        healthStatus = null
    }

    companion object {
        fun fromUrl(url: String) = Host(null, URL(url), null, null, 0, UNKNOWN)
    }
}
