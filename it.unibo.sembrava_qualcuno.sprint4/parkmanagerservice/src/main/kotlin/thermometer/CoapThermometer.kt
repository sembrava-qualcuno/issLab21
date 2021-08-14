package thermometer

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.coap.CoAP

class CoapThermometer(url: String) : ThermometerInterface, CoapHandler {
    private val client: CoapClient = CoapClient(url)
    lateinit var handler: (temperature: Int) -> Unit

    override fun getTemperature(): Int {
        return client.get().responseText.toInt()
    }

    override fun addObserver(lmbd: (temperature: Int) -> Unit) {
        handler = lmbd
        client.observe(this)
    }

    override fun onLoad(response: CoapResponse) {
        if (response.code == CoAP.ResponseCode.NOT_FOUND)
            return

        handler(response.responseText.toInt())
    }

    override fun onError() {
        return
    }
}
