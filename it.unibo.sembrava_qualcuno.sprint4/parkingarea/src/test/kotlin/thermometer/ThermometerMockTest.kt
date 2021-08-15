package thermometer

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ThermometerMockTest {
    private val server = CoapServer(8027)
    lateinit var thermometer: ThermometerMock
    private lateinit var client: CoapClient

    @BeforeAll
    fun setup() {
        thermometer = ThermometerMock(25)
        server.add(CoapResource("parkingarea").add(thermometer))
        server.start()
        client = CoapClient("coap://localhost:8027/parkingarea/thermometer")
    }

    @AfterAll
    fun terminate() {
        server.stop()
    }

    @BeforeEach
    fun reset() {
        thermometer.updateResource(25)
    }

    @Test
    fun testGet() {
        assert(client.get().responseText.toInt() == 25)
    }

    @Test
    fun testUpdateResource() {
        assert(client.get().responseText.toInt() == 25)
        thermometer.updateResource(30)
        assert(client.get().responseText.toInt() == 30)
    }
}
