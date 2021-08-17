package parkmanagerservice.controller

import fan.CoapFan
import fan.FanInterface
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
    private lateinit var template: SimpMessagingTemplate

    @Autowired
    constructor (template: SimpMessagingTemplate) : this() {
        this.template = template
    }

    //TODO Initialize temperature in ParkingAreaKb

    init {
        println("%%%%%% BaseController | START FOR OBSERVE: ThermometerController")
        ThermometerController(CoapThermometer("coap://localhost:8027/parkingarea/thermometer"), 30).addObserver{
            println("%%%%%% BaseController | OBSERVE: ThermometerController")
            ParkingAreaKb.highTemperature = !ParkingAreaKb.highTemperature
            val fanResource : FanInterface = CoapFan("coap://localhost:8027/parkingarea/fan")
            if(ParkingAreaKb.highTemperature) {
                template.convertAndSend("/manager/temperature", Message(1, "High Temperature!"))
                fanResource.updateResource("start")
            }
            else {
                template.convertAndSend("/manager/temperature", Message(2, "Low Temperature"))
                fanResource.updateResource("stop")
            }
        }
        println("%%%%%% BaseController | START FOR OBSERVE: SonarController")
        SonarController(CoapSonar("coap://localhost:8026/sonar"), 3).addObserver {
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

    // Logout
    @RequestMapping("/manager/logout")
    fun logout(model: Model): String {
        model.addAttribute("logOut", true)
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
