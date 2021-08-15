package thermometer

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.eclipse.californium.core.*
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ThermometerMockTest : CoapHandler {
    private val server = CoapServer(8027)
    lateinit var thermometer: ThermometerMock
    private lateinit var client: CoapClient
    private lateinit var handler : (CoapResponse) -> Unit

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

    @Test
    fun testObservable() {
        var temperature : Int = client.get().responseText.toInt()
        handler = {
            coapResponse ->
            temperature = coapResponse.responseText.toInt()
        }
        client.observe(this)
        thermometer.updateResource(30)

        runBlocking { delay(10) }
        assert(temperature == 30)
    }

    override fun onLoad(response: CoapResponse) {
        handler(response)
    }

    override fun onError() {
        fail("CoAP error")
    }
}
