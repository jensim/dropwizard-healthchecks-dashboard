package com.github.jensim.dropwizarddashboard.mockhealth

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jensim.dropwizarddashboard.host.HealthCheck
import com.github.jensim.dropwizarddashboard.mockhealth.MockHealthController.Companion
import com.github.jensim.dropwizarddashboard.mockhealth.MockHealthController.Companion.RESPONSE.NOT_FOUND
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.emptyOrNullString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

internal class MockHealthControllerTest {

    private val om = ObjectMapper()
    private val typeRef = object : TypeReference<Map<String, HealthCheck>>() {}

    @Test
    internal fun name() {
        for (res in Companion.RESPONSE.values()) {
            assertThat(res.data, not(emptyOrNullString()))
            if (res != NOT_FOUND) {
                val data: Map<String, HealthCheck> = om.readValue(res.data, typeRef)
                assertThat(data.entries, not(empty()))
                for ((k, v) in data.entries) {
                    assertThat(k, not(emptyOrNullString()))
                    assertNotNull(v)
                }
            }
        }
    }
}
