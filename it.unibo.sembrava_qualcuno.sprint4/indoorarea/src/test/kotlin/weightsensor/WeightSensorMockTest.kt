package weightsensor

import org.eclipse.californium.core.CoapClient
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WeightSensorMockTest {
    lateinit var weightSensor: WeightSensorMock
    lateinit var client: CoapClient

    @BeforeAll
    fun setup() {
        weightSensor = WeightSensorMock(8025, 0)
        client = CoapClient("coap://localhost:8025/weightSensor")
    }

    @BeforeEach
    fun reset() {
        weightSensor.updateResource(0)
    }

    @Test
    fun testGet() {
        assert(client.get().responseText.toInt() == 0)
    }

    @Test
    fun testUpdateResource() {
        assert(client.get().responseText.toInt() == 0)
        weightSensor.updateResource(100)
        assert(client.get().responseText.toInt() == 100)
    }
}
