package sonar

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SonarControllerTest {
    private lateinit var sonarMock: SonarMock
    lateinit var sonarController: SonarController

    @BeforeAll
    fun setup() {
        sonarMock = SonarMock(8026, false)
        sonarController = SonarController(CoapSonar("coap://localhost:8026/sonar"), 5)
    }

    @BeforeEach
    fun reset() {
        sonarMock.updateResource(false)
    }

    @Test
    fun testIsOutdoorFree() {
        assert(sonarController.isOutdoorFree())
    }

    @Test
    fun testIsOutdoorFreeAfterUpdate() {
        assert(sonarController.isOutdoorFree())
        sonarMock.updateResource(true)
        assert(!sonarController.isOutdoorFree())
    }

    @Test
    fun testNotify() {
        var observerNotified = false
        sonarController.addObserver { observerNotified = true }
        sonarMock.updateResource(true)
        runBlocking { delay(10) }
        //Check not notify immediately
        assert(!observerNotified)
        //Wait for the threshold
        runBlocking { delay(5000) }
        assert(observerNotified)
    }

    @Test
    fun testNotNotify() {
        var observerNotified = false
        sonarController.addObserver { observerNotified = true }
        sonarMock.updateResource(true)
        runBlocking { delay(10) }
        //Check not notify immediately
        assert(!observerNotified)
        sonarMock.updateResource(false)
        //Wait for the threshold
        runBlocking { delay(5500) }
        assert(!observerNotified)
    }
}
