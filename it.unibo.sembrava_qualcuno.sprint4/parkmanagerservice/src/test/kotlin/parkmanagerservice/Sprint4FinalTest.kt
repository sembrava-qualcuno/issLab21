package parkmanagerservice

import connQak.connQakBase
import connQak.connQakTcp
import fan.FanMock
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.QakContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
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

    @Test
    @Tag("NoObserve")
    fun productOwnerTest() {
        ParkingAreaKb.slot[1] = "117082021120000"
        ParkingAreaKb.slot[3] = "317082021130000"
        ParkingAreaKb.slot[5] = "517082021140000"

        //Send reqenter
        mockMvc.perform(MockMvcRequestBuilders.get("/client/reqenter")).andDo(MockMvcResultHandlers.print()).andExpect(
            MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json(Json.encodeToString(ParkingSlot(2))))

        runBlocking { delay(2000) }
        weightSensor.updateResource(1000)

        //Send carenter
        val result =
            mockMvc.perform(MockMvcRequestBuilders.get("/client/carenter?slotnum=2")).andDo(MockMvcResultHandlers.print()).andExpect(
                MockMvcResultMatchers.status().isOk).andReturn()
        val tokenId: TokenId = Json.decodeFromString(result.response.contentAsString)

        weightSensor.updateResource(0)
        runBlocking { delay(2000) }

        //Send reqexit
        mockMvc.perform(MockMvcRequestBuilders.get("/client/reqexit?tokenid=${tokenId.tokenId}")).andDo(MockMvcResultHandlers.print()).andExpect(
            MockMvcResultMatchers.status().isOk)

        runBlocking { delay(15000) }
    }
}