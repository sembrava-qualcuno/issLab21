package weightsensor

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CoapWeightSensorTest {
    lateinit var weightSensorMock: WeightSensorMock
    lateinit var weightSensor: WeightSensorInterface

    @BeforeAll
    fun setup() {
        weightSensorMock = WeightSensorMock(8025, 0)
        weightSensor = CoapWeightSensor("coap://localhost:8025/weightSensor")
    }

    @BeforeEach
    fun reset() {
        weightSensorMock.updateResource(0)
    }

    @Test
    fun testGetWeight() {
        assert(weightSensor.getWeight() == 0)
    }

    @Test
    fun testGetWeightAfterUpdate() {
        assert(weightSensor.getWeight() == 0)
        weightSensorMock.updateResource(100)
        assert(weightSensor.getWeight() == 100)
    }
}
