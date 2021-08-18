package sonar

import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.CoapServer
import org.eclipse.californium.core.server.resources.CoapExchange
import kotlin.system.exitProcess

class SonarMock(port: Int) : CoapResource("sonar") {
    var engaged: Boolean = false

    constructor(port: Int, defaultValue: Boolean) : this(port) {
        engaged = defaultValue
        this.isObservable = true
        this.setObserveType(null)
    }

    init {
        val server: CoapServer = CoapServer(port)
        server.add(this)
        server.start()
    }

    fun updateResource(value: Boolean) {
        engaged = value
        changed()
    }

    override fun handleGET(exchange: CoapExchange) {
        exchange.respond("$engaged")
    }
}

fun main() {
    val START_VALUE= (System.getenv("SONAR_START_VALUE")).toBoolean()
    println("SonarMock: Start with start value $START_VALUE")
    val mock = SonarMock(8026, START_VALUE)

    while (true) {
        val engaged = readLine().toBoolean()
        println("Sonar engaged: $engaged")
        mock.updateResource(engaged)
    }
}
