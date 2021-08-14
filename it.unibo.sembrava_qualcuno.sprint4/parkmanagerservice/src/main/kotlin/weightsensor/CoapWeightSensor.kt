package weightsensor

import org.eclipse.californium.core.CoapClient

class CoapWeightSensor(url: String) : WeightSensorInterface {
    private val client: CoapClient = CoapClient(url)

    override fun getWeight(): Int {
        return client.get().responseText.toInt()
    }
}
