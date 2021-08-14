package fan

import org.eclipse.californium.core.CoapClient

class CoapFan(url: String) : FanInterface {
    private val client: CoapClient = CoapClient(url)

    override fun getState(): String {
        return if (client.get().responseText.toBoolean())
            "on"
        else
            "off"
    }

    override fun updateResource(action: String) {
        client.put(action, 0)
    }
}
