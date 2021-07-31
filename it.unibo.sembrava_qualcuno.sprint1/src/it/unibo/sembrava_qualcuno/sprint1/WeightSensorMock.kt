package it.unibo.sembrava_qualcuno.sprint1

import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.server.resources.CoapExchange
import org.eclipse.californium.core.CoapServer

class WeightSensorMock(port : Int) : CoapResource("weightSensor") {
	var weight : Int = 0
	
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