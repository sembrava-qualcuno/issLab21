package parkmanagerservice.controller

import fan.CoapFan
import fan.FanInterface
import it.unibo.actor0.sysUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import parkmanagerservice.model.Message
import parkmanagerservice.model.ParkingAreaKb
import sonar.CoapSonar
import sonar.SonarController
import thermometer.CoapThermometer
import thermometer.ThermometerController

@Controller
class BaseController() {
    val PARKINGAREA_HOSTNAME = System.getenv("PARKINGAREA_HOSTNAME") ?: "parkingarea"
    val PARKINGAREA_PORT = System.getenv("PARKINGAREA_PORT") ?: "8027"
    val SONAR_HOSTNAME = System.getenv("SONAR_HOSTNAME") ?: "outdoorarea"
    val SONAR_PORT = System.getenv("SONAR_PORT") ?: "8026"

    var THERMOMETER_THRESHOLD = 30
    var TIMER_THRESHOLD = 60

    private lateinit var template: SimpMessagingTemplate

    @Autowired
    constructor (template: SimpMessagingTemplate) : this() {
        this.template = template

        try {
            val tmax = System.getenv("TMAX")
            if(tmax != null)
                THERMOMETER_THRESHOLD = tmax.toInt()
            else
                println("BaseController: Use default value TMAX=$THERMOMETER_THRESHOLD")
        } catch (e: NumberFormatException) {
            println("BaseController: TMAX must be an integer. Use default value $THERMOMETER_THRESHOLD")
        }
        try {
            val dtfree = System.getenv("DTFREE")
            if(dtfree != null)
                TIMER_THRESHOLD = dtfree.toInt()
            else
                println("BaseController: Use default value DTFREE=$TIMER_THRESHOLD")
        } catch (e: NumberFormatException) {
            println("BaseController: DTFREE must be an integer. Use default value $TIMER_THRESHOLD")
        }
    }

    //TODO Initialize temperature in ParkingAreaKb

    init {
        println("%%%%%% BaseController | START FOR OBSERVE: ThermometerController at $PARKINGAREA_HOSTNAME:$PARKINGAREA_PORT")
        ThermometerController(CoapThermometer("coap://$PARKINGAREA_HOSTNAME:$PARKINGAREA_PORT/parkingarea/thermometer"), THERMOMETER_THRESHOLD).addObserver{
            println("%%%%%% BaseController | OBSERVE: ThermometerController")
            ParkingAreaKb.highTemperature = !ParkingAreaKb.highTemperature
            val fanResource : FanInterface = CoapFan("coap://$PARKINGAREA_HOSTNAME:$PARKINGAREA_PORT/parkingarea/fan")
            if(ParkingAreaKb.highTemperature) {
                template.convertAndSend("/manager/temperature", Message(1, "High Temperature!"))
                fanResource.updateResource("start")
            }
            else {
                template.convertAndSend("/manager/temperature", Message(2, "Low Temperature"))
                fanResource.updateResource("stop")
            }
        }
        println("%%%%%% BaseController | START FOR OBSERVE: SonarController at $SONAR_HOSTNAME:$SONAR_PORT")
        SonarController(CoapSonar("coap://$SONAR_HOSTNAME:$SONAR_PORT/sonar"), TIMER_THRESHOLD).addObserver {
            println("%%%%%% BaseController | OBSERVE: ThermometerController")
            template.convertAndSend("/manager/sonar", Message(3, "Sonar engaged!"))
        }
    }

    @Value("\${human.logo}")
    var appName: String? = null
    @GetMapping("/")
    fun homePage(model: Model): String {
        println("------------------- BaseController homePage $model")
        model.addAttribute("arg", appName)
        return "welcome"
    }

    @GetMapping("/client")
    fun clientInHomepage(): String {
        return "clientInHomepage"
    }

    @GetMapping("/clientOut")
    fun clientOutHomepage(): String {
        return "clientOutHomepage"
    }

    //Login form
    @RequestMapping("/manager/login")
    fun login(): String {
        return "login"
    }

    // Login form with error
    @RequestMapping("/manager/loginError")
    fun loginError(model: Model): String {
        model.addAttribute("loginError", true)
        return "login"
    }

    @GetMapping("/manager")
    fun managerHomepage(): String {
        return "managerHomepage"
    }

    @ExceptionHandler
    fun handle(ex: Exception): ResponseEntity<*> {
        val responseHeaders = HttpHeaders()
        return ResponseEntity(
            "BaseController ERROR ${ex.message}",
            responseHeaders, HttpStatus.CREATED
        )
    }
}
