package managerservice

import parkmanagerservice.controller.BaseController
import parkmanagerservice.model.ParkingAreaKb
import sonar.CoapSonar
import sonar.SonarController
import thermometer.CoapThermometer
import thermometer.ThermometerController

class CoapManagerObserver(val controller: String) {
    fun start() {
        println("%%%%%% CoapManagerObserver | START FOR: $controller")
        when(controller){
            "SonarController" -> {
                SonarController(CoapSonar("coap://localhost:8026/sonar"), 60).addObserver {
                    BaseController.notifyOutdoorEngaged()
                }
            }
            "ThermometerController" -> {
                ThermometerController(CoapThermometer("coap://localhost:8027/parkingarea/thermometer"), 30).addObserver{
                    ParkingAreaKb.highTemperature = !ParkingAreaKb.highTemperature
                    BaseController.notifyHighTemperature(ParkingAreaKb.highTemperature)
                }
            }
        }
    }

    fun terminate() {
        println("%%%%%% CoapManagerObserver | terminate observer for $controller")
    }
}
