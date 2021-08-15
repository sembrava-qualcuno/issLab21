package thermometer

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ThermometerControllerTest {
    private val server = CoapServer(8027)
    private lateinit var thermometerMock: ThermometerMock
    lateinit var thermometerController: ThermometerController

    @BeforeAll
    fun setup() {
        thermometerMock = ThermometerMock(25)
        server.add(CoapResource("parkingarea").add(thermometerMock))
        server.start()
        thermometerController = ThermometerController(CoapThermometer("coap://localhost:8027/parkingarea/thermometer"), 30)
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
        assert(thermometerController.getTemperature() == 25)
    }

    @Test
    fun testGetTemperatureAfterUpdate() {
        assert(thermometerController.getTemperature() == 25)
        thermometerMock.updateResource(30)
        assert(thermometerController.getTemperature() == 30)
    }

    @Test
    fun testNotifyHighTemperature() {
        var observerNotified = false
        thermometerController.addObserver { observerNotified = true }
        thermometerMock.updateResource(31)
        runBlocking { delay(10) }
        assert(observerNotified)
    }

    @Test
    fun testNotNotifyHighTemperature() {
        var observerNotified = false
        thermometerController.addObserver { observerNotified = true }
        thermometerMock.updateResource(30)
        runBlocking { delay(10) }
        assert(!observerNotified)
    }

    @Test
    fun testNotifyLowTemperature() {
        thermometerMock.updateResource(35)
        var observerNotified = false
        thermometerController.addObserver { observerNotified = true }
        thermometerMock.updateResource(25)
        runBlocking { delay(10) }
        assert(observerNotified)
    }

    @Test
    fun testNotNotifyLowTemperature() {
        thermometerMock.updateResource(35)
        var observerNotified = false
        thermometerController.addObserver { observerNotified = true }
        thermometerMock.updateResource(31)
        runBlocking { delay(10) }
        assert(!observerNotified)
    }
}
