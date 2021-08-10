package it.unibo.utils.it.unibo.sembrava_qualcuno.sonar

import org.eclipse.californium.core.CoapClient

class CoapSonar(url: String) : SonarInterface {
    var client: CoapClient = CoapClient(url)

    override fun isEngaged(): Boolean {
        return client.get().responseText.toBoolean()
    }
}