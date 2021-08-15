package sonar

import org.eclipse.californium.core.CoapClient
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SonarMockTest {
    lateinit var sonar: SonarMock
    lateinit var client: CoapClient

    @BeforeAll
    fun setup() {
        sonar = SonarMock(8026, false)
        client = CoapClient("coap://localhost:8026/sonar")
    }

    @BeforeEach
    fun reset() {
        sonar.updateResource(false)
    }

    @Test
    fun testGet() {
        assert(!client.get().responseText.toBoolean())
    }

    @Test
    fun testUpdateResource() {
        assert(!client.get().responseText.toBoolean())
        sonar.updateResource(true)
        assert(client.get().responseText.toBoolean())
    }
}
