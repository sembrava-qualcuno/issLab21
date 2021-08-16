package parkmanagerservice.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class BaseController {
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
        return "login.html"
    }

    // Login form with error
    @RequestMapping("/manager/loginError")
    fun loginError(model: Model): String {
        model.addAttribute("loginError", true)
        return "login.html"
    }

    // Logout
    @RequestMapping("/manager/logout")
    fun logout(model: Model): String {
        model.addAttribute("logOut", true)
        return "login.html"
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
    companion object {
        fun notifyHighTemperature(highTemperature: Boolean){

        }

        fun notifyOutdoorEngaged(){

        }
    }
}
