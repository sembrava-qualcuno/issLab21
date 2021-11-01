package fan

import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.magenta
import com.andreapivetta.kolor.red
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.coap.CoAP
import org.eclipse.californium.core.server.resources.CoapExchange

class FanMock() : CoapResource("fan") {
    var state: Boolean = false

    constructor(defaultValue: Boolean) : this() {
        state = defaultValue
    }

    fun updateResource(value: Boolean) {
        state = value
        changed()
        println("Fan ".magenta() + if(value) "on".green() else "off".red())
    }

    override fun handleGET(exchange: CoapExchange) {
        exchange.respond("$state")
    }

    override fun handlePUT(exchange: CoapExchange) {
        when(exchange.requestText) {
            "start" -> {
                updateResource(true)
                exchange.respond(CoAP.ResponseCode.CHANGED, "$state")
            }
            "stop" -> {
                updateResource(false)
                exchange.respond(CoAP.ResponseCode.CHANGED, "$state")
            }
            else -> exchange.respond(CoAP.ResponseCode.BAD_REQUEST)
        }
    }
}
