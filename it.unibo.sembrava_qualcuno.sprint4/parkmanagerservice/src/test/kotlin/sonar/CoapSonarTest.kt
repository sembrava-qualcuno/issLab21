package sonar

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CoapSonarTest {
    private lateinit var sonarMock: SonarMock
    lateinit var sonar: SonarInterface

    @BeforeAll
    fun setup() {
        sonarMock = SonarMock(8026, false)
        sonar = CoapSonar("coap://localhost:8026/sonar")
    }

    @BeforeEach
    fun reset() {
        sonarMock.updateResource(false)
    }

    @Test
    fun testIsEngaged() {
        assert(!sonar.isEngaged())
    }

    @Test
    fun testIsEngagedAfterUpdate() {
        assert(!sonar.isEngaged())
        sonarMock.updateResource(true)
        assert(sonar.isEngaged())
    }

    @Test
    fun testAddObserver() {
        var isEngaged = false
        sonar.addObserver { engaged ->  isEngaged = engaged }
        sonarMock.updateResource(true)
        runBlocking { delay(10) }
        assert(isEngaged)
    }
}
