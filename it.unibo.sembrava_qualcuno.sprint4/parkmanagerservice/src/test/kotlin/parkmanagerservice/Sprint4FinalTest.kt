package parkmanagerservice

import ch.qos.logback.core.util.OptionHelper.getEnv
import connQak.connQakBase
import connQak.connQakTcp
import fan.FanMock
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.QakContext
import itunibo.planner.plannerUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import org.junit.Assert
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import parkmanagerservice.model.ParkingAreaKb
import parkmanagerservice.model.ParkingSlot
import parkmanagerservice.model.TokenId
import sonar.SonarMock
import thermometer.ThermometerMock
import weightsensor.WeightSensorMock

@SpringBootTest
@AutoConfigureMockMvc
class Sprint4FinalTest {

    @Autowired
    lateinit var mockMvc : MockMvc

    companion object {
        var testingObserver : CoapObserverForTesting ? = null
        var systemStarted = false
        val channelSyncStart = Channel<String>()
        val actors : Array<String> = arrayOf("clientservice", "managerservice", "trolley")
        var myactor : ActorBasic? = null
        var counter = 1
        val weightSensor = WeightSensorMock(8025, 1000)
        val sonarMock = SonarMock(8026, false)
        val thermometerMock = ThermometerMock(25)
        val fanMock = FanMock(false)
        val server = CoapServer(8027).add(CoapResource("parkingarea").add(fanMock, thermometerMock)).start()
        val connParkClientService: connQakBase = connQakTcp()

        @JvmStatic
        @BeforeAll
        fun init() {
            GlobalScope.launch {
                ctxparkmanagerservice.main() //keep the control
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
        // Reset the thermometer
        thermometerMock.updateResource(25)
        // Reset the fan
        fanMock.updateResource(false)
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

    @OptIn(ObsoleteCoroutinesApi::class)
    @Test
    fun productOwnerTest() {
        ParkingAreaKb.slot[1] = "117082021120000"
        ParkingAreaKb.slot[3] = "317082021130000"
        ParkingAreaKb.slot[5] = "517082021140000"
        val channelForObserver = Channel<String>()
        testingObserver!!.addObserver(channelForObserver)
       runBlocking {
           launch(newSingleThreadContext("SpringThread")) {
               mockMvc.perform(MockMvcRequestBuilders.get("/client/reqenter")).andDo(MockMvcResultHandlers.print()).andExpect(
                       MockMvcResultMatchers.status().isOk)
                       .andExpect(MockMvcResultMatchers.content().json(Json.encodeToString(ParkingSlot(2))))

               delay(2000)
               weightSensor.updateResource(1000)

               //Send carenter
               val result =
                       mockMvc.perform(MockMvcRequestBuilders.get("/client/carenter?slotnum=2")).andDo(MockMvcResultHandlers.print()).andExpect(
                               MockMvcResultMatchers.status().isOk).andReturn()

               weightSensor.updateResource(0)
               delay(2000)

               val tokenId = TokenId("117082021120000")
               //Send reqexit
               mockMvc.perform(MockMvcRequestBuilders.get("/client/reqexit?tokenid=${tokenId.tokenId}")).andDo(MockMvcResultHandlers.print()).andExpect(
                       MockMvcResultMatchers.status().isOk)
           }
           launch {
               Assert.assertEquals("trolley at HOME", channelForObserver.receive())
               Assert.assertEquals("trolley trip to INDOOR start", channelForObserver.receive())
               Assert.assertEquals("trolley trip to INDOOR end", channelForObserver.receive())

               Assert.assertTrue(plannerUtil.atPos(6, 0))

               Assert.assertEquals("trolley moveToPark(2)", channelForObserver.receive())
               Assert.assertEquals("trolley trip to park slot 2 end", channelForObserver.receive())

               Assert.assertTrue(plannerUtil.atPos(1, 2))

               Assert.assertEquals("trolley moveToPark(1)", channelForObserver.receive())
               Assert.assertEquals("trolley trip to park slot 1 end", channelForObserver.receive())

               Assert.assertTrue(plannerUtil.atPos(1, 1))

               Assert.assertEquals("trolley trip to OUTDOOR start", channelForObserver.receive())
               Assert.assertEquals("trolley trip to OUTDOOR end", channelForObserver.receive())

               Assert.assertTrue(plannerUtil.atPos(6, 4))

               Assert.assertEquals("trolley IDLE", channelForObserver.receive())
               Assert.assertEquals("trolley at HOME", channelForObserver.receive())
           }
       }
    }
}