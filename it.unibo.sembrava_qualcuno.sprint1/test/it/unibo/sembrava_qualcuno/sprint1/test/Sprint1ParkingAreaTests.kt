package it.unibo.sembrava_qualcuno.sprint1.test

import it.unibo.kactor.ActorBasic
import it.unibo.kactor.QakContext
import it.unibo.sembrava_qualcuno.model.ApiError
import it.unibo.sembrava_qualcuno.model.ParkingSlot
import it.unibo.sembrava_qualcuno.model.TokenId
import it.unibo.sembrava_qualcuno.sprint1.ParkingAreaKb
import it.unibo.sembrava_qualcuno.weightsensor.WeightSensorMock
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class Sprint1ParkingAreaTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    companion object {
        var systemStarted = false
        val channelSyncStart = Channel<String>()
        val actors: Array<String> = arrayOf("clientservice")
        val weightSensor = WeightSensorMock(8025, 1000)

        @JvmStatic
        @BeforeAll
        fun init() {
            GlobalScope.launch {
                it.unibo.ctxparkmanagerservice.main() //keep the control
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
    }

    @AfterEach
    fun removeObs() {
        println("+++++++++ AFTERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR")

        runBlocking {
            delay(1000)
        }
    }

    @Test
    fun testNoParkingSlotsAvailable() {
        //Set all parking slots as occupied
        ParkingAreaKb.slot = mutableMapOf(1 to "1", 2 to "2", 3 to "3", 4 to "4", 5 to "5", 6 to "6")

        mockMvc.perform(get("/client/reqenter")).andDo(print()).andExpect(status().isOk)
    }

    @Test
    fun testGenerateTokenId() {
        //Send reqenter
        mockMvc.perform(get("/client/reqenter")).andDo(print()).andExpect(status().isOk)

        //The client moves the car to the indoor area
        weightSensor.updateResource(1000)

        //Send carenter
        val result =
            mockMvc.perform(get("/client/carenter?slotnum=1")).andDo(print()).andExpect(status().isOk).andReturn()
        val tokenId: TokenId = Json.decodeFromString(result.response.contentAsString)

        //example of tokenid: SLOTNUM-dd/MM/yyyy-hh:mm:ss
        val stringTokenizer = StringTokenizer(tokenId.tokenId, "-")

        //Test correct slotnum
        assertTrue(stringTokenizer.nextToken().equals("1"))

        //Test correct date format
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy-hh:mm:ss")
        sdf.parse(stringTokenizer.nextToken(""))
    }

    @Test
    fun testInvalidParkingSlot() {
        //Send reqenter
        mockMvc.perform(get("/client/reqenter")).andDo(print()).andExpect(status().isOk)

        //The client moves the car to the indoor area
        weightSensor.updateResource(1000)

        //Send carenter
        mockMvc.perform(get("/client/carenter?slotnum=-1")).andDo(print()).andExpect(status().isBadRequest)
    }

    @Test
    fun testIndoorAreaFree() {
        //Send reqenter
        mockMvc.perform(get("/client/reqenter")).andDo(print()).andExpect(status().isOk)

        //Send carenter
        mockMvc.perform(get("/client/carenter?slotnum=1")).andDo(print()).andExpect(status().isForbidden)
    }

    @Test
    fun testUniqueTokenId() {
        //Send reqenter
        mockMvc.perform(get("/client/reqenter")).andDo(print()).andExpect(status().isOk)

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

        //The client moves the car to the indoor area
        weightSensor.updateResource(1000)

        //Send carenter
        val tokenId2: TokenId = Json.decodeFromString(
            mockMvc.perform(get("/client/carenter?slotnum=2")).andDo(print()).andExpect(status().isOk)
                .andReturn().response.contentAsString
        )

        assertTrue(tokenId1 != tokenId2)
    }
}
