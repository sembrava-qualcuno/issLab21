package it.unibo.sembrava_qualcuno.sprint1

import it.unibo.kactor.QakContext
import it.unibo.sembrava_qualcuno.weightsensor.WeightSensorMock
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.lang.NumberFormatException

@SpringBootApplication
class Sprint1Application

@ObsoleteCoroutinesApi
fun main(args: Array<String>) {
    runBlocking {
        launch(newSingleThreadContext("QakThread")) {
            QakContext.createContexts("localhost", this, "carparking.pl", "sysRules.pl")
        }
        launch(newSingleThreadContext("SpringThread")) {
            runApplication<Sprint1Application>(*args)
        }
    }
}
