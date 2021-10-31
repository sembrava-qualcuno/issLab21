package it.unibo.sembrava_qualcuno.sprint2.test

import it.unibo.sembrava_qualcuno.sonar.CoapSonar
import it.unibo.sembrava_qualcuno.sonar.SonarController
import it.unibo.sembrava_qualcuno.sonar.SonarInterface
import it.unibo.sembrava_qualcuno.sonar.SonarMock
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SonarTests {
    companion object {
        val sonarMock = SonarMock(8026, false)
        val sonarController = SonarController(CoapSonar("coap://localhost:8026/sonar"))
    }

    @BeforeEach
    fun reset() {
        sonarMock.updateResource(false)
    }

    @Test
    fun testSonarController() {
        assert(sonarController.isOutdoorFree())

        sonarMock.updateResource(true)

        assert(!sonarController.isOutdoorFree())
    }
}
