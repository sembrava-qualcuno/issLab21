package it.unibo.sembrava_qualcuno.sprint2

import it.unibo.kactor.QakContext
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Sprint2Application

@ObsoleteCoroutinesApi
fun main(args: Array<String>) {
    runBlocking {
        launch(newSingleThreadContext("QakThread")) {
            QakContext.createContexts("localhost", this, "carparking.pl", "sysRules.pl")
        }
        launch(newSingleThreadContext("SpringThread")) {
            runApplication<Sprint2Application>(*args)
        }
    }
}
