package parkingarea

import com.andreapivetta.kolor.magenta
import com.andreapivetta.kolor.red
import com.andreapivetta.kolor.yellow
import fan.FanMock
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import thermometer.ThermometerMock
import kotlin.system.exitProcess

fun main() {
    try {
        val THERMOMETER_START_VALUE = (System.getenv("THERMOMETER_START_VALUE") ?: "25").toInt()
        val FAN_START_VALUE = System.getenv("FAN_START_VALUE").toBoolean()

        println("ThermometerMock: Start with start value $THERMOMETER_START_VALUE".yellow())
        println("FanMock: Start with start value $FAN_START_VALUE".magenta())

        val thermometerMock = ThermometerMock(THERMOMETER_START_VALUE)
        val fanMock = FanMock(FAN_START_VALUE)

        val server = CoapServer(8027)
        server.add(CoapResource("parkingarea").add(thermometerMock, fanMock))
        server.start()

        while(true) {
            try {
                val temperature = readLine()!!.toInt()
                println("Temperature updated to $temperature".yellow())
                thermometerMock.updateResource(temperature)
            } catch (e: NumberFormatException) {
                println("Error: temperature must be an integer".red())
            }
        }
    } catch (e: NumberFormatException) {
        println("THERMOMETER_START_VALUE must be an integer".red())
        exitProcess(1)
    }

}
