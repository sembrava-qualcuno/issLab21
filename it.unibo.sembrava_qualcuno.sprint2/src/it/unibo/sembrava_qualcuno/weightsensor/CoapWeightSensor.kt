package it.unibo.sembrava_qualcuno.weightsensor

import org.eclipse.californium.core.CoapClient

class CoapWeightSensor(url: String) : WeightSensorInterface {
    val client: CoapClient = CoapClient(url)

    override fun getWeight(): Int {
        return client.get().getResponseText().toInt()
    }
}
