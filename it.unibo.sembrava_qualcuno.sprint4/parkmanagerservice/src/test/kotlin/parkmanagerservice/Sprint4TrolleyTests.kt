package parkmanagerservice

import ctxparkmanagerservice.main
import connQak.connQakBase
import connQak.connQakTcp
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.MsgUtil
import it.unibo.kactor.QakContext
import parkmanagerservice.model.ParkingSlot
import parkmanagerservice.model.TokenId
import parkmanagerservice.model.ParkingAreaKb
import weightsensor.WeightSensorMock
import sonar.SonarMock
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
class Sprint4TrolleyTests {

    @Autowired
    lateinit var mockMvc : MockMvc

    companion object {
        var testingObserver : CoapObserverForTesting ? = null
        var systemStarted = false
        val channelSyncStart = Channel<String>()
        val actors : Array<String> = arrayOf("clientservice", "trolley")
        var myactor : ActorBasic? = null
        var counter = 1
        val weightSensor = WeightSensorMock(8025, 1000)
        val sonarMock = SonarMock(8026, false)
        val connParkClientService: connQakBase = connQakTcp()

        @JvmStatic
        @BeforeAll
        fun init() {
            GlobalScope.launch {
                main() //keep the control
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
        // Reset the sonar
        sonarMock.updateResource(false)
    }

    @AfterEach
    fun removeObs(testInfo: TestInfo){
        println("+++++++++ AFTERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR  ${testingObserver!!.name}")

        if(!testInfo.tags.contains("NoObserve")) {
            testingObserver!!.terminate()
            testingObserver = null
        }

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

    @Test
    fun testTrolleyMovedToOutdoorArea() {
        ParkingAreaKb.slot.set(1, "TOKENID")

        //Send reqexit
        mockMvc.perform(MockMvcRequestBuilders.get("/client/reqexit?tokenid=TOKENID")).andDo(MockMvcResultHandlers.print()).andExpect(
            MockMvcResultMatchers.status().isOk)

        runBlocking {
            val channelForObserver = Channel<String>()
            testingObserver!!.addObserver(channelForObserver)

            assertEquals("trolley moveToPark(1)", channelForObserver.receive())
            assertEquals("trolley trip to park slot 1 end", channelForObserver.receive())

            assertTrue(plannerUtil.atPos(1, 1))

            assertEquals("trolley trip to OUTDOOR start", channelForObserver.receive())
            assertEquals("trolley trip to OUTDOOR end", channelForObserver.receive())

            assertTrue(plannerUtil.atPos(6, 4))

            assertEquals("trolley IDLE", channelForObserver.receive())
            assertEquals("trolley at HOME", channelForObserver.receive())

            assertTrue(plannerUtil.atPos(0, 0))
        }
    }

    @Test
    //@Tag("NoObserve")
    fun testAcceptOutdoorRequestOnAvailable() {
        connParkClientService.createConnection("localhost", 8024)

        ParkingAreaKb.slot.set(1, "TOKENID")

        //Send stop
        val stopDispatch = MsgUtil.buildDispatch("springcontroller", "stop", "stop()", "trolley")
        connParkClientService.forward(stopDispatch)

        runBlocking {
            val channelForObserver = Channel<String>()
            testingObserver!!.addObserver(channelForObserver)

            assertEquals("trolley at HOME", channelForObserver.receive())
            assertEquals("trolley STOPPED", channelForObserver.receive())
        }

        //Send reqexit
        mockMvc.perform(MockMvcRequestBuilders.get("/client/reqexit?tokenid=TOKENID")).andDo(MockMvcResultHandlers.print()).andExpect(
            MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun testAcceptInRequestOnAvailable() {
        connParkClientService.createConnection("localhost", 8024)

        //Send stop
        val stopDispatch = MsgUtil.buildDispatch("springcontroller", "stop", "stop()", "trolley")
        connParkClientService.forward(stopDispatch)

        runBlocking {
            val channelForObserver = Channel<String>()
            testingObserver!!.addObserver(channelForObserver)

            assertEquals("trolley at HOME", channelForObserver.receive())
            assertEquals("trolley STOPPED", channelForObserver.receive())
        }

        //Send reqenter
        mockMvc.perform(MockMvcRequestBuilders.get("/client/reqenter")).andDo(MockMvcResultHandlers.print()).andExpect(
            MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun testNoAcceptAfterResume() {
        connParkClientService.createConnection("localhost", 8024)

        ParkingAreaKb.slot.set(1, "TOKENID")

        //Send stop
        val stopDispatch = MsgUtil.buildDispatch("springcontroller", "stop", "stop()", "trolley")
        connParkClientService.forward(stopDispatch)

        val channelForObserver = Channel<String>()
        testingObserver!!.addObserver(channelForObserver)

        runBlocking {
            assertEquals("trolley STOPPED", channelForObserver.receive())
        }

        //Send reqexit
        mockMvc.perform(MockMvcRequestBuilders.get("/client/reqexit?tokenid=TOKENID")).andDo(MockMvcResultHandlers.print()).andExpect(
            MockMvcResultMatchers.status().isForbidden)

        //Send resume
        val resumeDispatch = MsgUtil.buildDispatch("springcontroller", "resume", "resume()", "trolley")
        connParkClientService.forward(resumeDispatch)

        runBlocking {
            assertEquals("trolley IDLE", channelForObserver.receive())
        }
    }

    @Test
    fun testStopAndResumeWorking() {
        connParkClientService.createConnection("localhost", 8024)
        val channelForObserver = Channel<String>()

        ParkingAreaKb.slot.set(1, "TOKENID")

        //Send reqexit
        mockMvc.perform(MockMvcRequestBuilders.get("/client/reqexit?tokenid=TOKENID")).andDo(MockMvcResultHandlers.print()).andExpect(
            MockMvcResultMatchers.status().isOk)

        testingObserver!!.addObserver(channelForObserver)
        runBlocking {
            assertEquals("trolley moveToPark(1)", channelForObserver.receive())
        }

        //Send stop
        val stopDispatch = MsgUtil.buildDispatch("springcontroller", "stop", "stop()", "trolley")
        connParkClientService.forward(stopDispatch)

        runBlocking {
            assertEquals("trolley trip to park slot 1 end", channelForObserver.receive())
            assertEquals("trolley STOPPED", channelForObserver.receive())

            delay(2000)
        }

        //Send resume
        val resumeDispatch = MsgUtil.buildDispatch("springcontroller", "resume", "resume()", "trolley")
        connParkClientService.forward(resumeDispatch)

        runBlocking {
            assertEquals("trolley trip to OUTDOOR start", channelForObserver.receive())
            assertEquals("trolley trip to OUTDOOR end", channelForObserver.receive())

            assertEquals("trolley IDLE", channelForObserver.receive())
            assertEquals("trolley at HOME", channelForObserver.receive())
        }
    }
}
