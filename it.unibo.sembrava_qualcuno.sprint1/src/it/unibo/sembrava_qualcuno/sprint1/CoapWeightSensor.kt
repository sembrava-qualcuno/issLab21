package it.unibo.sembrava_qualcuno.sprint1

import org.eclipse.californium.core.CoapClient

class CoapWeightSensor(url : String) : WeightSensorInterface {
	val client : CoapClient = CoapClient(url)
	
	override fun getWeight() : Int {
		return client.get().getResponseText().toInt()
	}
}

