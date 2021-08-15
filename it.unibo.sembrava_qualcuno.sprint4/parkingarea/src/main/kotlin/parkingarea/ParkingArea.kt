package parkingarea

import fan.FanMock
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import thermometer.ThermometerMock

fun main() {
    val thermometerMock = ThermometerMock( 25)
    val fanMock = FanMock(false)

    val server = CoapServer(8027)
    server.add(CoapResource("parkingarea").add(thermometerMock, fanMock))

    server.start()
    //TODO Update also the fan from cmdline?
    while(true) {
        try {
            val temperature = readLine()!!.toInt()
            println("Temperature updated to $temperature")
            thermometerMock.updateResource(temperature)
        } catch (e: NumberFormatException) {
            println("Error: temperature must be an integer")
        }
    }
}
