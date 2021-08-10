package it.unibo.sembrava_qualcuno.sprint1.test

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.eclipse.californium.core.coap.CoAP

class UpdateHandler(val name: String, val channel: Channel<String>) : CoapHandler {

    override fun onLoad(response: CoapResponse) {
        val content = response.responseText
        println("%%%%%% $name | content=$content RESP-CODE=${response.code}")

        if (response.code == CoAP.ResponseCode.NOT_FOUND)
            return

        //DISCARD the content not related to testing
        if (content.contains("START") || content.contains("created"))
            return

        runBlocking {
            channel.send(content)
        }
    }

    override fun onError() {
        println("$name | FAILED")
    }
}

class CoapObserverForTesting(val name: String, val context: String, val observed: String, val port: String = "8020") {
    private val uriStr = "coap://localhost:$port/$context/$observed"
    private val client: CoapClient = CoapClient(uriStr)
    private lateinit var handler: CoapHandler

    fun addObserver(channel: Channel<String>) {
        println("%%%%%% $name | START uriStr: $uriStr")
        handler = UpdateHandler("h_$name", channel)
        client.observe(handler)
    }

    fun terminate() {
        println("%%%%%% $name | terminate $handler")
        client.delete(handler)
        client.shutdown()
    }
}
