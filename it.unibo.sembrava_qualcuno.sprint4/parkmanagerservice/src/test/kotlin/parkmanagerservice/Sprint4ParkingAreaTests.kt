package parkmanagerservice

import ctxparkmanagerservice.main
import fan.CoapFan
import fan.FanInterface
import fan.FanMock
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.QakContext
import sonar.SonarMock
import weightsensor.WeightSensorMock
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import parkmanagerservice.model.*
import thermometer.CoapThermometer
import thermometer.ThermometerController
import thermometer.ThermometerMock
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class Sprint4ParkingAreaTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    companion object {
        var systemStarted = false
        val channelSyncStart = Channel<String>()
        val actors: Array<String> = arrayOf("clientservice", "managerservice", "trolley")
        val weightSensor = WeightSensorMock(8025, 1000)
        val sonarMock = SonarMock(8026, false)
        val thermometerMock = ThermometerMock(25)
        val fanMock = FanMock(false)
        val server = CoapServer(8027).add(CoapResource("parkingarea").add(fanMock, thermometerMock)).start()

        @JvmStatic
        @BeforeAll
        fun init() {
            GlobalScope.launch {
                main() //keep the control
            }
            GlobalScope.launch {
                while (!actorsReady()) {
                    println("waiting for system startup ...")
                    delay(500)
                }
                delay(2000)
                channelSyncStart.send("starttesting")
            }
        }

        @JvmStatic
        @AfterAll
        fun terminate() {
            println("terminate the testing")
        }

        fun actorsReady(): Boolean {
            for (actor in actors) {
                if (QakContext.getActor(actor) == null)
                    return false
            }
            return true
        }
    }

    @BeforeEach
    fun checkSystemStarted() {
        println("\n=================================================================== ")
        println("+++++++++ BEFOREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE")

        if (!systemStarted) {
            runBlocking {
                channelSyncStart.receive()
                systemStarted = true
                println("+++++++++ checkSystemStarted resumed ")
            }
        }

        // Reset the knowledge base to assumption state
        ParkingAreaKb.slot = mutableMapOf(1 to "", 2 to "", 3 to "", 4 to "", 5 to "", 6 to "")
        // Reset the weight sensor
        weightSensor.updateResource(0)
        // Reset the sonar
        sonarMock.updateResource(false)
        // Reset the thermometer
        thermometerMock.updateResource(25)
        // Reset the fan
        fanMock.updateResource(false)
    }

    @AfterEach
    fun removeObs() {
        println("+++++++++ AFTERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR")

        runBlocking {
            delay(1000)
        }
    }
	
// Test INDOOR use cases
	
    @Test
    fun testNoParkingSlotsAvailable() {
        //Set all parking slots as occupied
        ParkingAreaKb.slot = mutableMapOf(1 to "1", 2 to "2", 3 to "3", 4 to "4", 5 to "5", 6 to "6")

        mockMvc.perform(get("/client/reqenter")).andDo(print()).andExpect(status().isOk)
            .andExpect(content().json(Json.encodeToString(ParkingSlot(0))))
    }

    @Test
    fun testIndoorAreaEngaged() {
        //The indoor area is engaged
        weightSensor.updateResource(1000)

        mockMvc.perform(get("/client/reqenter")).andDo(print()).andExpect(status().isForbidden)
    }

    @Test
    fun testGenerateTokenId() {
        //Send reqenter
        mockMvc.perform(get("/client/reqenter")).andDo(print()).andExpect(status().isOk)
            .andExpect(content().json(Json.encodeToString(ParkingSlot(1))))

        //The client moves the car to the indoor area
        weightSensor.updateResource(1000)

        //Send carenter
        val result =
            mockMvc.perform(get("/client/carenter?slotnum=1")).andDo(print()).andExpect(status().isOk).andReturn()
        val tokenId: TokenId = Json.decodeFromString(result.response.contentAsString)
        //example of tokenid: SLOTNUMddMMyyyyhhmmss
        //Test correct slotnum
        assertEquals("1", tokenId.tokenId[0].toString())

        //Test correct date format
        val sdf = java.text.SimpleDateFormat("ddMMyyyyhhmmss")
        sdf.parse(tokenId.tokenId.substring(1))
    }

    @Test
    fun testInvalidParkingSlot() {
        //Send reqenter
        mockMvc.perform(get("/client/reqenter")).andDo(print()).andExpect(status().isOk)
            .andExpect(content().json(Json.encodeToString(ParkingSlot(1))))

        //The client moves the car to the indoor area
        weightSensor.updateResource(1000)

        //Send carenter
        mockMvc.perform(get("/client/carenter?slotnum=-1")).andDo(print()).andExpect(status().isBadRequest)
    }

    @Test
    fun testIndoorAreaFree() {
        //Send reqenter
        mockMvc.perform(get("/client/reqenter")).andDo(print()).andExpect(status().isOk)
            .andExpect(content().json(Json.encodeToString(ParkingSlot(1))))

        //Send carenter
        mockMvc.perform(get("/client/carenter?slotnum=1")).andDo(print()).andExpect(status().isForbidden)
    }

    @Test
    fun testUniqueTokenId() {
        //Send reqenter
        mockMvc.perform(get("/client/reqenter")).andDo(print()).andExpect(status().isOk)
            .andExpect(content().json(Json.encodeToString(ParkingSlot(1))))

        //The client moves the car to the indoor area
        weightSensor.updateResource(1000)

        //Send carenter
        val tokenId1: TokenId = Json.decodeFromString(
            mockMvc.perform(get("/client/carenter?slotnum=1")).andDo(print()).andExpect(status().isOk)
                .andReturn().response.contentAsString
        )

        //Simulate second client
        //Set the indoor area is free
        weightSensor.updateResource(0)

        //Send reqenter
        mockMvc.perform(get("/client/reqenter")).andDo(print()).andExpect(status().isOk)
            .andExpect(content().json(Json.encodeToString(ParkingSlot(2))))

        //The client moves the car to the indoor area
        weightSensor.updateResource(1000)

        //Send carenter
        val tokenId2: TokenId = Json.decodeFromString(
            mockMvc.perform(get("/client/carenter?slotnum=2")).andDo(print()).andExpect(status().isOk)
                .andReturn().response.contentAsString
        )

        assertTrue(tokenId1 != tokenId2)
    }
	
// Test OUTDOOR use cases
	
    @Test
    fun testExitRequest() {
        ParkingAreaKb.slot.set(1, "TOKENID")

        //Send reqexit
        mockMvc.perform(get("/client/reqexit?tokenid=TOKENID")).andDo(print()).andExpect(status().isOk)
    }

    @Test
    fun testInvalidTokenid() {
        ParkingAreaKb.slot.set(1, "TOKENID")

        //Send reqexit
        mockMvc.perform(get("/client/reqexit?tokenid=BADTOKENID")).andDo(print()).andExpect(status().isBadRequest)
    }

    @Test
    fun testOutdoorAreaEngaged() {
        ParkingAreaKb.slot.set(1, "TOKENID")

        //Set the outdoor area engaged
        sonarMock.updateResource(true)

        //Send reqexit
        mockMvc.perform(get("/client/reqexit?tokenid=TOKENID")).andDo(print()).andExpect(status().isForbidden)
    }

    //TODO Complete the test with the web socket interaction
    @Test
    fun testOutdoorEngaged() {
        sonarMock.updateResource(true)
        runBlocking { delay(5000) }
    }

    @Test
    fun testFanOnHighTemperature() {
        thermometerMock.updateResource(35)
        runBlocking { delay(50) }
        assert(fanMock.state)
    }

    @Test
    fun testFanOffLowTemperature() {
        val fan: FanInterface = CoapFan("coap://localhost:8027/parkingarea/fan")

        thermometerMock.updateResource(35)
        runBlocking { delay(50) }
        assertEquals("on", fan.getState())
        thermometerMock.updateResource(25)
        runBlocking { delay(50) }
        assertEquals("off", fan.getState())
    }

    @Test
    fun testFanStayOn() {
        val fan: FanInterface = CoapFan("coap://localhost:8027/parkingarea/fan")

        thermometerMock.updateResource(35)
        runBlocking { delay(50) }
        assertEquals("on", fan.getState())
        thermometerMock.updateResource(32)
        runBlocking { delay(50) }
        assertEquals("on", fan.getState())
    }

    @Test
    fun testFanStayOff() {
        val fan: FanInterface = CoapFan("coap://localhost:8027/parkingarea/fan")

        assertEquals("off", fan.getState())
        thermometerMock.updateResource(28)
        runBlocking { delay(50) }
        assertEquals("off", fan.getState())
    }

    @Test
    fun testGetParkingArea() {
        val thermometerController = ThermometerController(CoapThermometer("coap://localhost:8027/parkingarea/thermometer"), 30)
        val fan: FanInterface = CoapFan("coap://localhost:8027/parkingarea/fan")
        val trolleyCoapClient = CoapClient("coap://localhost:8024/ctxtrolley/trolley")

        val result = mockMvc.perform(get("/parkingArea")).andDo(print()).andExpect(status().isOk).andReturn()
        val parkingArea: ParkingArea = Json.decodeFromString(result.response.contentAsString)

        println("ParkingArea $parkingArea")
        assertEquals(thermometerController.getTemperature(), parkingArea.temperature)
        assertEquals(fan.getState(), parkingArea.fan)
        assertEquals(getTrolleyStateFromCoap(trolleyCoapClient.get().responseText), parkingArea.trolley)
    }

    @Test
    fun testUpdateTrolleyStop() {
        thermometerMock.updateResource(35)
        val trolleyCoapClient = CoapClient("coap://localhost:8024/ctxtrolley/trolley")
        assertEquals("idle", getTrolleyStateFromCoap(trolleyCoapClient.get().responseText))
        mockMvc.perform(put("/parkingArea/trolley").contentType("application/json").content(Json.encodeToString(Trolley("stop")))).andDo(print()).andExpect(
            status().isOk)
        assertEquals("stopped", getTrolleyStateFromCoap(trolleyCoapClient.get().responseText))
    }

    @Test
    fun testUpdateTrolleyStopAndResume() {
        thermometerMock.updateResource(35)
        val trolleyCoapClient = CoapClient("coap://localhost:8024/ctxtrolley/trolley")
        assertEquals("idle", getTrolleyStateFromCoap(trolleyCoapClient.get().responseText))
        mockMvc.perform(put("/parkingArea/trolley").contentType("application/json").content(Json.encodeToString(Trolley("stop")))).andDo(print()).andExpect(
            status().isOk)
        assertEquals("stopped", getTrolleyStateFromCoap(trolleyCoapClient.get().responseText))
        mockMvc.perform(put("/parkingArea/trolley").contentType("application/json").content(Json.encodeToString(Trolley("resume")))).andDo(print()).andExpect(
            status().isOk)
        assertEquals("idle", getTrolleyStateFromCoap(trolleyCoapClient.get().responseText))
    }

    @Test
    fun testUpdateTrolleyBadRequest() {
        thermometerMock.updateResource(35)
        val trolleyCoapClient = CoapClient("coap://localhost:8024/ctxtrolley/trolley")
        assertEquals("idle", getTrolleyStateFromCoap(trolleyCoapClient.get().responseText))
        mockMvc.perform(put("/parkingArea/trolley").contentType("application/json").content(Json.encodeToString(Trolley("foo")))).andDo(print()).andExpect(
            status().isBadRequest)
        assertEquals("idle", getTrolleyStateFromCoap(trolleyCoapClient.get().responseText))
    }

    @Test
    fun testUpdateTrolleyLowTemperature() {
        val trolleyCoapClient = CoapClient("coap://localhost:8024/ctxtrolley/trolley")
        assertEquals("idle", getTrolleyStateFromCoap(trolleyCoapClient.get().responseText))
        mockMvc.perform(put("/parkingArea/trolley").contentType("application/json").content(Json.encodeToString(Trolley("stop")))).andDo(print()).andExpect(
            status().isForbidden)
        assertEquals("idle", getTrolleyStateFromCoap(trolleyCoapClient.get().responseText))
    }

    fun getTrolleyStateFromCoap(trolleyCoapResponse: String): String {
        return when (trolleyCoapResponse) {
            "trolley IDLE", "trolley at HOME" -> {
                "idle"
            }
            "trolley STOPPED" -> {
                "stopped"
            }
            else -> {
                "working"
            }
        }
    }
}
