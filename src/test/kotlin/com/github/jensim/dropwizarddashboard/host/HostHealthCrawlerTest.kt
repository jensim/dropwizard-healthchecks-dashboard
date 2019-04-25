package com.github.jensim.dropwizarddashboard.host

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.net.URL
import kotlin.test.assertEquals

internal class HostHealthCrawlerTest {

    enum class TestCase(private val probeInterval: Int?, private val lastProbeTime: Long?, val isProbeable: Boolean) {
        IS_PROBEABLE_WHEN_NO_LASTPROBETIME(null, null, true),
        IS_PROBEABLE_WHEN_TIME_HAS_PASSED_FOR_OVERRIDE_INTERVAL(10_000, System.currentTimeMillis() - 11_000, true),
        IS_NOT_PROBEABLE_WHEN_TIME_HAS_NOT_PASSED_FOR_OVERRIDE_INTERVAL(10_000, System.currentTimeMillis(), false);

        val host by lazy {
            Host(_id = null, healthCheckUrl = URL("http://loclahost:8080/hc"), probeInterval = probeInterval, lastProbeTime = lastProbeTime, unreachableProbeStreak = null, healthStatus = null, lastResponse = null)
        }
    }

    @ParameterizedTest
    @EnumSource(value = TestCase::class)
    fun `is probeable when no lastProbeTime`(tc: TestCase) {
        // when
        val probeable = HostHealthCrawler.shouldProbe(tc.host)

        // then
        assertEquals(tc.isProbeable, probeable)
    }
}
