package sonar

import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import org.eclipse.californium.core.server.resources.CoapExchange

class SonarMock(port: Int) : CoapResource("sonar") {
    var engaged: Boolean = false

    constructor(port: Int, defaultValue: Boolean) : this(port) {
        engaged = defaultValue
    }

    init {
        val server: CoapServer = CoapServer(port)
        server.add(this)
        server.start()
    }

    fun updateResource(value: Boolean) {
        engaged = value
    }

    override fun handleGET(exchange: CoapExchange) {
        exchange.respond("$engaged")
    }
}

fun main() {
    val mock = SonarMock(8026, false)

    while (true) {
        val engaged = readLine().toBoolean()
        println("Sonar engaged: $engaged")
        mock.updateResource(engaged)
    }
}
