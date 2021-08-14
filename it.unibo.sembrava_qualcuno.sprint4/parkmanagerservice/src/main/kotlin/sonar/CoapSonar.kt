package sonar

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.coap.CoAP

class CoapSonar(url: String) : SonarInterface, CoapHandler {
    var client: CoapClient = CoapClient(url)
    lateinit var handler: (engaged: Boolean) -> Unit

    override fun isEngaged(): Boolean {
        return client.get().responseText.toBoolean()
    }

    override fun addObserver(lmbd: (engaged: Boolean) -> Unit) {
        handler = lmbd
        client.observe(this)
    }

    override fun onLoad(response: CoapResponse) {
        if (response.code == CoAP.ResponseCode.NOT_FOUND)
            return

        handler(response.responseText.toBoolean())
    }

    override fun onError() {
        return
    }
}
