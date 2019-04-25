package com.github.jensim.dropwizarddashboard.host

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jensim.dropwizarddashboard.host.HostHealthStatus.UNKNOWN
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.net.URL

enum class HostHealthStatus {
    HEALTHY,
    UNHEALTHY,
    UNREACHABLE,
    UNKNOWN
}

data class ErrorDescription(
        @BsonProperty("message")
        @JsonProperty("message")
        val message: String,
        @BsonProperty("trace")
        @JsonProperty("trace")
        val trace: List<String>)

data class HealthCheck(
        @BsonProperty("message")
        @JsonProperty("message")
        val message: String,
        @JsonProperty("healthy")
        @BsonProperty("healthy")
        val healthy: Boolean,
        @JsonProperty("error", required = false)
        @BsonProperty("error")
        val error: ErrorDescription? = null
)

data class HostHealthChecks(
        @JsonProperty("checks")
        @BsonProperty("checks")
        val checks: Map<String, HealthCheck>) {

    fun isUnhealthy(): Boolean = checks.values.any { !it.healthy }
}

data class Host(
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
        val lastProbeTime: Long?,
        @BsonProperty("unreachableProbeStreak")
        @JsonProperty("unreachableProbeStreak")
        val unreachableProbeStreak: Int? = 0,
        @BsonProperty("healthStatus")
        @JsonProperty("healthStatus")
        val healthStatus: HostHealthStatus? = UNKNOWN,
        @BsonProperty("probeInterval")
        @JsonProperty("probeInterval")
        val probeInterval: Int? = null
) {

    companion object {
        fun fromUrl(url: String) = Host(null, URL(url), null, null, 0, UNKNOWN)
    }
}
