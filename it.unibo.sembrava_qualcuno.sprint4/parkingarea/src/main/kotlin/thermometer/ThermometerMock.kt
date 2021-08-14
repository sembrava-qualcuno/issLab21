package thermometer

import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.server.resources.CoapExchange

class ThermometerMock() : CoapResource("parkingarea/thermometer") {
    var temperature: Int = 25

    constructor(defaultValue: Int) : this() {
        temperature = defaultValue
    }

    fun updateResource(value: Int) {
        temperature = value
        changed()
    }

    override fun handleGET(exchange: CoapExchange) {
        exchange.respond("$temperature")
    }
}
