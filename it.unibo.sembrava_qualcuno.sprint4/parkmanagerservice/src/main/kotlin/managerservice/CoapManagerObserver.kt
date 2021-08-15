package managerservice

import fan.CoapFan
import fan.FanInterface
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
                SonarController(CoapSonar("coap://localhost:8026/sonar"), 3).addObserver {
                    BaseController.notifyOutdoorEngaged()
                }
            }
            "ThermometerController" -> {
                ThermometerController(CoapThermometer("coap://localhost:8027/parkingarea/thermometer"), 30).addObserver{
                    ParkingAreaKb.highTemperature = !ParkingAreaKb.highTemperature
                    val fanResource : FanInterface = CoapFan("coap://localhost:8027/parkingarea/fan")
                    fanResource.updateResource(if(ParkingAreaKb.highTemperature) "start" else "stop")
                    BaseController.notifyHighTemperature(ParkingAreaKb.highTemperature)
                }
            }
        }
    }

    fun terminate() {
        println("%%%%%% CoapManagerObserver | terminate observer for $controller")
    }
}
