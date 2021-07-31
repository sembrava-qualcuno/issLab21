package it.unibo.sembrava_qualcuno.weightsensor

import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.server.resources.CoapExchange
import org.eclipse.californium.core.CoapServer
import java.lang.NumberFormatException

class WeightSensorMock(port : Int) : CoapResource("weightSensor") {
	var weight : Int = 0

	constructor(port: Int, defaultValue : Int) : this(port) {
		weight = defaultValue
	}

	init {
		val server : CoapServer = CoapServer(port)
		server.add(this)
		server.start()
		//TODO Check if correct start the server in this way
	}
	
	fun updateResource(value : Int) {
		weight = value
	}
	
	override fun handleGET(exchange : CoapExchange) {
		exchange.respond("$weight")
	}
}

fun main() {
	val mock = WeightSensorMock(8020, 1000)

	while(true) {
		try {
			val weight = readLine()!!.toInt()
			println("Weight updated to $weight")
			mock.updateResource(weight)
		} catch (e : NumberFormatException) {
			println("Error: weight must be an integer")
		}
	}
}