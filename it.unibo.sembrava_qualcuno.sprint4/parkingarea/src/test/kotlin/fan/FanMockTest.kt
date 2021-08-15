package fan

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import org.eclipse.californium.core.coap.CoAP
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FanMockTest {
    private val server = CoapServer(8027)
    lateinit var fan: FanMock
    private lateinit var client: CoapClient

    @BeforeAll
    fun setup() {
        fan = FanMock(false)
        server.add(CoapResource("parkingarea").add(fan))
        server.start()
        client = CoapClient("coap://localhost:8027/parkingarea/fan")
    }

    @AfterAll
    fun terminate() {
        server.stop()
    }

    @BeforeEach
    fun reset() {
        fan.updateResource(false)
    }

    @Test
    fun testGet() {
        assert(!client.get().responseText.toBoolean())
    }

    @Test
    fun testUpdateResource() {
        assert(!client.get().responseText.toBoolean())
        fan.updateResource(true)
        assert(client.get().responseText.toBoolean())
    }

    @Test
    fun testPutStart() {
        assert(client.put("start", 0).code == CoAP.ResponseCode.CHANGED)
        assert(client.get().responseText.toBoolean())
    }

    @Test
    fun testPutStop() {
        assert(client.put("stop", 0).code == CoAP.ResponseCode.CHANGED)
        assert(!client.get().responseText.toBoolean())
    }

    @Test
    fun testPutBadRequest() {
        assert(client.put("", 0).code == CoAP.ResponseCode.BAD_REQUEST)
    }
}
