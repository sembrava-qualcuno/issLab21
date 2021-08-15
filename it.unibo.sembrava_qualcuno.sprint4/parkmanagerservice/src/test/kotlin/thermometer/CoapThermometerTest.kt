package thermometer

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CoapThermometerTest {
    private val server = CoapServer(8027)
    private lateinit var thermometerMock: ThermometerMock
    lateinit var thermometer: ThermometerInterface

    @BeforeAll
    fun setup() {
        thermometerMock = ThermometerMock(25)
        server.add(CoapResource("parkingarea").add(thermometerMock))
        server.start()
        thermometer = CoapThermometer("coap://localhost:8027/parkingarea/thermometer")
    }

    @AfterAll
    fun terminate() {
        server.stop()
    }

    @BeforeEach
    fun reset() {
        thermometerMock.updateResource(25)
    }

    @Test
    fun testGetTemperature() {
        assert(thermometer.getTemperature() == 25)
    }

    @Test
    fun testGetTemperatureAfterUpdate() {
        assert(thermometer.getTemperature() == 25)
        thermometerMock.updateResource(30)
        assert(thermometer.getTemperature() == 30)
    }

    @Test
    fun testAddObserver() {
        var finalTemperature = 25
        thermometer.addObserver { temperature -> finalTemperature = temperature }
        thermometerMock.updateResource(30)
        runBlocking { delay(10) }
        assert(finalTemperature == 30)
    }
}
