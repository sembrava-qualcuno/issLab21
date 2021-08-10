package it.unibo.sembrava_qualcuno.sprint1.test

import it.unibo.kactor.ActorBasic
import it.unibo.kactor.QakContext
import it.unibo.sembrava_qualcuno.model.ParkingSlot
import it.unibo.sembrava_qualcuno.model.TokenId
import it.unibo.sembrava_qualcuno.sprint1.ParkingAreaKb
import it.unibo.sembrava_qualcuno.weightsensor.WeightSensorMock
import itunibo.planner.plannerUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class Sprint1TrolleyTests {

    @Autowired
    lateinit var mockMvc : MockMvc

    companion object {
        var testingObserver : CoapObserverForTesting ? = null
        var systemStarted = false
        val channelSyncStart = Channel<String>()
        val actors : Array<String> = arrayOf("parkclientservice", "trolley")
        var myactor : ActorBasic? = null
        var counter = 1
        val weightSensor = WeightSensorMock(8025, 1000)

        @JvmStatic
        @BeforeAll
        fun init() {
            GlobalScope.launch {
                it.unibo.ctxparkmanagerservice.main() //keep the control
            }
            GlobalScope.launch {
                while(!actorsReady()) {
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

        fun actorsReady() : Boolean {
            for(actor in actors) {
                if(QakContext.getActor(actor) == null)
                    return false
            }
            return true
        }
    }

    @BeforeEach
    fun checkSystemStarted()  {
        println("\n=================================================================== ")
        println("+++++++++ BEFOREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE testingObserver=$testingObserver")

        if(!systemStarted) {
            runBlocking {
                channelSyncStart.receive()
                systemStarted = true
                println("+++++++++ checkSystemStarted resumed ")
            }
        }
        if(testingObserver == null) testingObserver = CoapObserverForTesting("obstesting${counter++}", "ctxtrolley", "trolley", "8024")

        // Reset the knowledge base to assumption state
        ParkingAreaKb.slot = mutableMapOf(1 to "", 2 to "", 3 to "", 4 to "", 5 to "", 6 to "")
        // Reset the weight sensor
        weightSensor.updateResource(0)
    }

    @AfterEach
    fun removeObs(){
        println("+++++++++ AFTERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR  ${testingObserver!!.name}")

        testingObserver!!.terminate()
        testingObserver = null

        runBlocking{
            delay(1000)
        }
    }

    @Test
    fun testTrolleyMovedToIndoorArea() {
        //Send reqenter
        mockMvc.perform(MockMvcRequestBuilders.get("/client/reqenter")).andExpect(
            MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(Json.encodeToString(ParkingSlot(1))))

        //The client moves the car to the indoor area
        weightSensor.updateResource(1000)

        //Send carenter
        val tokenId: TokenId = Json.decodeFromString(
            mockMvc.perform(MockMvcRequestBuilders.get("/client/carenter?slotnum=1")).andExpect(
                MockMvcResultMatchers.status().isOk)
                .andReturn().response.contentAsString
        )

        runBlocking {
            val channelForObserver = Channel<String>()
            testingObserver!!.addObserver(channelForObserver)

            assertEquals("trolley trip to INDOOR start", channelForObserver.receive())
            assertEquals("trolley trip to INDOOR end", channelForObserver.receive())

            assertTrue(plannerUtil.atPos(6, 0))

            assertEquals("trolley moveToPark(1)", channelForObserver.receive())
            assertEquals("trolley trip to park slot 1 end", channelForObserver.receive())

            assertTrue(plannerUtil.atPos(1, 1))

            assertEquals("trolley IDLE", channelForObserver.receive())
            assertEquals("trolley at HOME", channelForObserver.receive())

            assertTrue(plannerUtil.atPos(0, 0))
        }
    }
}
