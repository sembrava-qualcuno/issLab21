package weightsensor

import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.red
import com.andreapivetta.kolor.yellow
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import org.eclipse.californium.core.server.resources.CoapExchange
import kotlin.system.exitProcess

class WeightSensorMock(port: Int) : CoapResource("weightSensor") {
    var weight: Int = 0

    constructor(port: Int, defaultValue: Int) : this(port) {
        weight = defaultValue
    }

    init {
        val server: CoapServer = CoapServer(port)
        server.add(this)
        server.start()
    }

    fun updateResource(value: Int) {
        weight = value
        changed()
    }

    override fun handleGET(exchange: CoapExchange) {
        exchange.respond("$weight")
    }
}

fun main() {
    try {
        val START_VALUE= (System.getenv("WEIGHTSENSOR_START_VALUE") ?: "0").toInt()
        println("WeightSensorMock: Start with start value $START_VALUE".yellow())
        val mock = WeightSensorMock(8025, START_VALUE)

        while (true) {
            try {
                val weight = readLine()!!.toInt()
                println("Weight updated to $weight".green())
                mock.updateResource(weight)
            } catch (e: NumberFormatException) {
                println("Error: weight must be an integer".red())
            }
        }
    } catch (e: NumberFormatException) {
        println("START_VALUE must be an integer".red())
        exitProcess(1)
    }
}
