package fan

import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CoapFanTest {
    private val server = CoapServer(8027)
    private lateinit var fanMock: FanMock
    lateinit var fan: FanInterface

    @BeforeAll
    fun setup() {
        fanMock = FanMock(false)
        server.add(CoapResource("parkingarea").add(fanMock))
        server.start()
        fan = CoapFan("coap://localhost:8027/parkingarea/fan")
    }

    @AfterAll
    fun terminate() {
        server.stop()
    }

    @BeforeEach
    fun reset() {
        fanMock.updateResource(false)
    }

    @Test
    fun testGetStateOn() {
        assert(fan.getState() == "off")
    }

    @Test
    fun testGetStateOff() {
        fanMock.updateResource(true)
        assert(fan.getState() == "on")
    }

    @Test
    fun testUpdateResourceStart() {
        assert(fan.getState() == "off")
        fan.updateResource("start")
        assert(fan.getState() == "on")
    }

    @Test
    fun testUpdateResourceStop() {
        fanMock.updateResource(true)
        assert(fan.getState() == "on")
        fan.updateResource("stop")
        assert(fan.getState() == "off")
    }
}
